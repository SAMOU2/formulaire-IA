import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

public class MainApp extends Application {

    private TextField txtNom = new TextField();
    private TextField txtPrenom = new TextField();
    private ComboBox<String> cbSexe = new ComboBox<>();
    private TextField txtDateNais = new TextField();
    private TextField txtNumTel = new TextField();
    private TextField txtMail = new TextField();
    private TextField txtExtra = new TextField();

    private RadioButton rbEtudiant = new RadioButton("Étudiant");
    private RadioButton rbEnseignant = new RadioButton("Enseignant");

    private Label lblErrNom = new Label();
    private Label lblErrPrenom = new Label();
    private Label lblErrMail = new Label();
    private Label lblErrTel = new Label();
    private Label lblErrDate = new Label();

    private Label lblPrevNom = new Label("-");
    private Label lblPrevType = new Label("Étudiant");
    private Label lblPrevOp = new Label("-");
    private Label lblPrevMatricule = new Label("-");

    private TextArea txtResultat = new TextArea();
    
    private Label lblBotMessage = new Label("👋 Bienvenue ! Remplissez le formulaire en toute sécurité.");
    private Label botAvatarCircle = new Label("🤖");

    private static final String JSON_FILE_PATH = "inscriptions.json";
    private static final Preferences prefs = Preferences.userNodeForPackage(MainApp.class);

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Inscription ");

        // Configuration des tooltips (Accessibilité)
        txtNom.setTooltip(new Tooltip("Entrez votre nom de famille (sans chiffres)"));
        txtPrenom.setTooltip(new Tooltip("Entrez votre prénom"));
        cbSexe.setTooltip(new Tooltip("Sélectionnez votre genre"));
        txtDateNais.setTooltip(new Tooltip("Entrez votre date de naissance au format jjmmaaaa (8 chiffres)"));
        txtNumTel.setTooltip(new Tooltip("Entrez votre numéro de téléphone mobile (exactement 10 chiffres, ex: 0550123456)"));
        txtMail.setTooltip(new Tooltip("Entrez une adresse e-mail valide contenant @"));

        txtNom.setPromptText("Ex: Ben Ali");
        txtPrenom.setPromptText("Ex: Mohamed");
        cbSexe.setPromptText("Sélectionner le sexe");
        cbSexe.getItems().addAll("Masculin", "Féminin");
        cbSexe.setMaxWidth(Double.MAX_VALUE);

        txtDateNais.setPromptText("Ex: 12052000");
        txtNumTel.setPromptText("Ex: 0550123456 (10 chiffres)");
        txtMail.setPromptText("Ex: exemple@domaine.com");
        txtExtra.setPromptText("Ex: 2025");

        VBox root = new VBox(12);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("root");

        VBox header = new VBox(4);
        header.getStyleClass().add("header-box");
        Label title = new Label("✨ Inscription Universitaire Sécurisée");
        title.getStyleClass().add("label-title");
        Label subtitle = new Label("Espace membre confidentiel avec auto-connexion.");
        subtitle.getStyleClass().add("label-subtitle");
        header.getChildren().addAll(title, subtitle);

        HBox mainLayout = new HBox(20);
        mainLayout.setAlignment(Pos.TOP_CENTER);

        // --- Formulaire (Gauche) ---
        VBox formCard = new VBox(10);
        formCard.getStyleClass().add("card");
        formCard.setPrefWidth(460);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(6);

        ToggleGroup groupType = new ToggleGroup();
        rbEtudiant.setToggleGroup(groupType);
        rbEnseignant.setToggleGroup(groupType);
        rbEtudiant.setSelected(true);
        
        HBox boxRadio = new HBox(15, rbEtudiant, rbEnseignant);
        boxRadio.setAlignment(Pos.CENTER_LEFT);
        
        Label lblExtraTitle = new Label("Année (Matricule) :");
        
        rbEtudiant.setOnAction(e -> {
            lblExtraTitle.setText("Année (Matricule) :");
            txtExtra.setPromptText("Ex: 2025");
            lblPrevType.setText("Étudiant");
            setBotState("🎓", "Mode Étudiant activé.");
            updatePreview();
        });
        rbEnseignant.setOnAction(e -> {
            lblExtraTitle.setText("Modules enseignés :");
            txtExtra.setPromptText("Ex: Algorithmique");
            lblPrevType.setText("Enseignant");
            setBotState("👨‍🏫", "Mode Enseignant activé.");
            updatePreview();
        });

        addFormRow(grid, 0, new Label("Profil :"), boxRadio, null);
        addFormRow(grid, 1, new Label("Nom :"), txtNom, lblErrNom);
        addFormRow(grid, 2, new Label("Prénom :"), txtPrenom, lblErrPrenom);
        addFormRow(grid, 3, new Label("Sexe :"), cbSexe, null);
        addFormRow(grid, 4, new Label("Date Nais :"), txtDateNais, lblErrDate);
        addFormRow(grid, 5, new Label("Téléphone :"), txtNumTel, lblErrTel);
        addFormRow(grid, 6, new Label("Email :"), txtMail, lblErrMail);
        addFormRow(grid, 7, lblExtraTitle, txtExtra, null);

        formCard.getChildren().add(grid);

        // --- Panneau Droit ---
        VBox rightPanel = new VBox(12);
        rightPanel.setPrefWidth(380);

        HBox botContent = new HBox(12);
        botContent.getStyleClass().add("floating-bot-content");
        botContent.setAlignment(Pos.CENTER_LEFT);

        botAvatarCircle.getStyleClass().add("bot-avatar-circle");

        VBox speechBubble = new VBox();
        speechBubble.getStyleClass().add("bot-speech-bubble");
        lblBotMessage.getStyleClass().add("bot-speech-text");
        lblBotMessage.setWrapText(true);
        speechBubble.getChildren().add(lblBotMessage);
        HBox.setHgrow(speechBubble, Priority.ALWAYS);

        botContent.getChildren().addAll(botAvatarCircle, speechBubble);

        TranslateTransition flyAnimation = new TranslateTransition(Duration.seconds(1.8), botContent);
        flyAnimation.setByY(-6.0);
        flyAnimation.setAutoReverse(true);
        flyAnimation.setCycleCount(Animation.INDEFINITE);
        flyAnimation.play();

        VBox previewCard = new VBox(8);
        previewCard.getStyleClass().add("preview-card");
        Label prevTitle = new Label("📌 Aperçu en Direct");
        prevTitle.getStyleClass().add("preview-title");
        
        HBox rowP1 = new HBox(10, new Label("Type :"), lblPrevType);
        HBox rowP2 = new HBox(10, new Label("Nom Complet :"), lblPrevNom);
        HBox rowP3 = new HBox(10, new Label("Opérateur :"), lblPrevOp);
        HBox rowP4 = new HBox(10, new Label("Clé Unique :"), lblPrevMatricule);
        
        previewCard.getChildren().addAll(prevTitle, rowP1, rowP2, rowP3, rowP4);

        Button btnAnnuler = new Button("❌ Effacer");
        btnAnnuler.setTooltip(new Tooltip("Réinitialiser le formulaire"));
        btnAnnuler.getStyleClass().addAll("button", "button-cancel");
        
        Button btnValider = new Button("✔ Enregistrer & Connexion");
        btnValider.setTooltip(new Tooltip("Enregistrer vos données et ouvrir votre espace sécurisé"));
        btnValider.getStyleClass().addAll("button", "button-all");
        
        HBox.setHgrow(btnAnnuler, Priority.ALWAYS);
        HBox.setHgrow(btnValider, Priority.ALWAYS);
        btnAnnuler.setMaxWidth(Double.MAX_VALUE);
        btnValider.setMaxWidth(Double.MAX_VALUE);

        HBox panelDeuxBoutons = new HBox(10, btnAnnuler, btnValider);
        panelDeuxBoutons.setAlignment(Pos.CENTER);

        txtResultat.setEditable(false);
        txtResultat.setPrefRowCount(4);
        VBox boxResult = new VBox(4, new Label("💬 État du système :"), txtResultat);

        rightPanel.getChildren().addAll(botContent, previewCard, panelDeuxBoutons, boxResult);
        mainLayout.getChildren().addAll(formCard, rightPanel);
        root.getChildren().addAll(header, mainLayout);

        initAdvancedBotListeners();

        btnAnnuler.setOnAction(e -> {
            txtNom.clear();
            txtPrenom.clear();
            cbSexe.setValue(null);
            txtDateNais.clear();
            txtNumTel.clear();
            txtMail.clear();
            txtExtra.clear();
            txtResultat.clear();
            clearFieldStyles(txtNom, lblErrNom);
            clearFieldStyles(txtPrenom, lblErrPrenom);
            clearFieldStyles(txtMail, lblErrMail);
            clearFieldStyles(txtNumTel, lblErrTel);
            clearFieldStyles(txtDateNais, lblErrDate);
            clearFieldStyles(txtExtra, null);
            rbEtudiant.setSelected(true);
            lblPrevType.setText("Étudiant");
            setBotState("🔄", "Formulaire réinitialisé avec succès.");
            updatePreview();
        });

        btnValider.setOnAction(e -> {
            if (validerChampsGlobal()) {
                String type = rbEtudiant.isSelected() ? "Étudiant" : "Enseignant";
                String nom = txtNom.getText().trim();
                String prenom = txtPrenom.getText().trim().replaceAll("[0-9]", "");
                String sexe = cbSexe.getValue();
                String dateNais = txtDateNais.getText().trim();
                String telephone = txtNumTel.getText().trim();
                String mail = txtMail.getText().trim();
                String extra = txtExtra.getText().trim();
                String specifique = rbEtudiant.isSelected() ? (dateNais + extra) : extra;

                // 1. Sauvegarde interne sécurisée
                sauvegarderEnJson(type, nom, prenom, sexe, dateNais, telephone, mail, extra, specifique);

                // 2. Mémorisation automatique pour l'auto-login ("Se souvenir de moi")
                prefs.put("LAST_NOM", nom);
                prefs.put("LAST_MATRICULE", specifique);

                txtResultat.setText("✔ Inscription réussie ! Redirection en cours...");
                setBotState("🎉", "Enregistrement validé. Ouverture de votre espace personnel...");
                
                primaryStage.close();
                ouvrirFenetreLogin();

            } else {
                setBotState("⚠️", "Veuillez corriger les erreurs avant de valider.");
                afficherNotification("Attention", "Veuillez remplir tous les champs obligatoires correctement (Téléphone : exactement 10 chiffres).", Alert.AlertType.WARNING);
            }
        });

        Scene scene = new Scene(root, 940, 740);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // --- Fenêtre de Connexion (Login) claire et accessible ---
    private void ouvrirFenetreLogin() {
        Stage loginStage = new Stage();
        loginStage.setTitle("Connexion - Espace Membre Sécurisé");

        VBox loginRoot = new VBox(15);
        loginRoot.setPadding(new Insets(30));
        loginRoot.getStyleClass().add("root");
        loginRoot.setAlignment(Pos.CENTER);

        Label title = new Label("🔐 Connexion Sécurisée");
        title.getStyleClass().add("label-title");

        TextField txtLoginNom = new TextField();
        txtLoginNom.setPromptText("Votre Nom");
        txtLoginNom.setTooltip(new Tooltip("Votre nom enregistré"));
        txtLoginNom.setText(prefs.get("LAST_NOM", "")); // Pré-remplissage automatique

        TextField txtLoginMatricule = new TextField();
        txtLoginMatricule.setPromptText("Matricule ou Module");
        txtLoginMatricule.setTooltip(new Tooltip("Votre clé unique ou module"));
        txtLoginMatricule.setText(prefs.get("LAST_MATRICULE", "")); // Pré-remplissage automatique

        Button btnConnexion = new Button("🔓 Accéder à mon Espace");
        btnConnexion.setTooltip(new Tooltip("Se connecter à votre espace personnel"));
        btnConnexion.getStyleClass().addAll("button", "button-all");
        btnConnexion.setMaxWidth(Double.MAX_VALUE);

        Label lblLoginErr = new Label();
        lblLoginErr.setStyle("-fx-text-fill: #f87171; -fx-font-size: 11px;");

        btnConnexion.setOnAction(e -> {
            String saisiNom = txtLoginNom.getText().trim();
            String saisiMat = txtLoginMatricule.getText().trim();

            if (!saisiNom.isEmpty() && !saisiMat.isEmpty()) {
                loginStage.close();
                ouvrirEspacePersonnel(saisiNom, saisiMat);
            } else {
                lblLoginErr.setText("⚠ Veuillez remplir tous les champs de connexion.");
            }
        });

        VBox formBox = new VBox(10, new Label("Nom de famille :"), txtLoginNom, new Label("Matricule / Module :"), txtLoginMatricule, lblLoginErr, btnConnexion);
        formBox.getStyleClass().add("card");
        formBox.setMaxWidth(380);

        loginRoot.getChildren().addAll(title, formBox);

        Scene scene = new Scene(loginRoot, 460, 480);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        loginStage.setScene(scene);
        loginStage.show();
    }

    // --- Espace Personnel strictement CONFIDENTIEL (Affiche uniquement les infos du propriétaire) ---
    private void ouvrirEspacePersonnel(String nomRecherche, String matriculeRecherche) {
        Stage spaceStage = new Stage();
        spaceStage.setTitle("Mon Espace Personnel ");

        VBox spaceRoot = new VBox(15);
        spaceRoot.setPadding(new Insets(30));
        spaceRoot.getStyleClass().add("root");

        Label title = new Label("👤 Mon Profil Personnel");
        title.getStyleClass().add("label-title");

        String detailsUser = recupererInformationsUtilisateur(nomRecherche, matriculeRecherche);

        VBox profileCard = new VBox(12);
        profileCard.getStyleClass().add("card");
        profileCard.setPadding(new Insets(20));

        Label lblInfo = new Label(detailsUser);
        lblInfo.setStyle("-fx-text-fill: #f8fafc; -fx-font-size: 14px; -fx-line-spacing: 5px;");
        lblInfo.setWrapText(true);

        profileCard.getChildren().add(lblInfo);

        Button btnFermer = new Button("🚪 Se Déconnecter");
        btnFermer.setTooltip(new Tooltip("Fermer la session en toute sécurité"));
        btnFermer.getStyleClass().addAll("button", "button-cancel");
        btnFermer.setOnAction(e -> spaceStage.close());

        spaceRoot.getChildren().addAll(title, new Label("Informations confidentielles associées à votre compte :"), profileCard, btnFermer);

        Scene scene = new Scene(spaceRoot, 520, 500);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        spaceStage.setScene(scene);
        spaceStage.show();
    }

    private String recupererInformationsUtilisateur(String nom, String matricule) {
        try {
            File file = new File(JSON_FILE_PATH);
            if (!file.exists()) return "Aucune donnée enregistrée.";

            String contenu = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            if (contenu.contains(nom) && contenu.contains(matricule)) {
                return String.format(
                    "📌 Type de Profil : Membre Actif\n" +
                    "👤 Nom : %s\n" +
                    "🔑 Clé / Matricule : %s\n" +
                    "🔒 Sécurité : Données strictement confidentielles.\n" +
                    "✅ Statut : Compte vérifié et actif.",
                    nom, matricule
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "⚠ Aucune information trouvée pour ces identifiants.";
    }

    private void sauvegarderEnJson(String type, String nom, String prenom, String sexe, String dateNais, String tel, String mail, String extra, String specifique) {
        String nouveauJson = String.format(
            "  {\n    \"type\": \"%s\",\n    \"nom\": \"%s\",\n    \"prenom\": \"%s\",\n    \"sexe\": \"%s\",\n    \"dateNaissance\": \"%s\",\n    \"telephone\": \"%s\",\n    \"email\": \"%s\",\n    \"extra\": \"%s\",\n    \"matriculeOuModule\": \"%s\"\n  }",
            type, nom, prenom, sexe, dateNais, tel, mail, extra, specifique
        );

        try {
            File file = new File(JSON_FILE_PATH);
            String contenuActuel = "[]";
            
            if (file.exists()) {
                contenuActuel = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH))).trim();
            }

            if (contenuActuel.equals("[]") || contenuActuel.isEmpty()) {
                contenuActuel = "[\n" + nouveauJson + "\n]";
            } else {
                contenuActuel = contenuActuel.substring(0, contenuActuel.lastIndexOf("]")) + ",\n" + nouveauJson + "\n]";
            }

            FileWriter writer = new FileWriter(JSON_FILE_PATH);
            writer.write(contenuActuel);
            writer.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void addFormRow(GridPane grid, int rowIndex, javafx.scene.Node label, javafx.scene.Node field, Label errLabel) {
        int baseRow = rowIndex * 2;
        grid.add(label, 0, baseRow);
        grid.add(field, 1, baseRow);
        if (errLabel != null) {
            errLabel.setStyle("-fx-text-fill: #f87171; -fx-font-size: 10px;");
            grid.add(errLabel, 1, baseRow + 1);
        }
    }

    private void updatePreview() {
        lblPrevNom.setText(txtNom.getText().isEmpty() ? "-" : txtNom.getText() + " " + txtPrenom.getText());
        String num = txtNumTel.getText().trim();
        if (num.startsWith("05")) lblPrevOp.setText("Ooredoo (ORD)");
        else if (num.startsWith("07")) lblPrevOp.setText("Djezzy (Dj)");
        else if (num.startsWith("06")) lblPrevOp.setText("Mobilis (mob)");
        else lblPrevOp.setText("-");

        if (rbEtudiant.isSelected()) {
            String mat = txtDateNais.getText().isEmpty() ? "jjmmaaaa" : txtDateNais.getText();
            String an = txtExtra.getText().isEmpty() ? "AAAA" : txtExtra.getText();
            lblPrevMatricule.setText("Matricule: " + mat + an);
        } else {
            lblPrevMatricule.setText("Modules: " + (txtExtra.getText().isEmpty() ? "-" : txtExtra.getText()));
        }
    }

    private void setBotState(String emoji, String message) {
        botAvatarCircle.setText(emoji);
        lblBotMessage.setText(message);
    }

    private void initAdvancedBotListeners() {
        txtNom.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) setBotState("💡", "Entrez votre nom de famille sans chiffres.");
        });
        txtNom.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty()) {
                setFieldError(txtNom, lblErrNom, "Nom requis.");
            } else if (newVal.matches(".*\\d.*")) {
                setFieldError(txtNom, lblErrNom, "Pas de chiffres autorisés.");
                setBotState("❌", "Un nom de famille ne contient pas de chiffres !");
            } else {
                boolean compose = newVal.contains(" ") || newVal.contains("-");
                setFieldSuccess(txtNom, lblErrNom, compose ? "Mot composé" : "Nom simple");
                setBotState("✨", compose ? "Mot composé détecté !" : "Nom simple valide.");
            }
            updatePreview();
        });

        txtPrenom.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) setBotState("💡", "Entrez votre prénom.");
        });
        txtPrenom.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty()) {
                setFieldError(txtPrenom, lblErrPrenom, "Prénom requis.");
            } else if (newVal.matches(".*\\d.*")) {
                setFieldSuccess(txtPrenom, lblErrPrenom, "Chiffres (nettoyage auto)");
                setBotState("🧹", "Chiffres détectés, nettoyage automatique prévu !");
            } else {
                setFieldSuccess(txtPrenom, lblErrPrenom, "Prénom valide");
            }
            updatePreview();
        });

        cbSexe.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) setBotState("👇", "Sélectionnez votre sexe.");
        });

        txtDateNais.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) setBotState("📅", "Entrez votre date de naissance (8 chiffres : jjmmaaaa).");
        });
        txtDateNais.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty()) {
                setFieldError(txtDateNais, lblErrDate, "Date requise.");
            } else if (newVal.matches("\\d{8}")) {
                setFieldSuccess(txtDateNais, lblErrDate, "Format valide.");
                setBotState("🎯", "Parfait pour générer votre matricule !");
            } else {
                setFieldError(txtDateNais, lblErrDate, "Exactement 8 chiffres requis.");
            }
            updatePreview();
        });

        // Validation stricte du téléphone : exactement 10 chiffres et commence par 05, 06 ou 07
        txtNumTel.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) setBotState("📱", "Entrez un numéro mobile de 10 chiffres (ex: 0550123456).");
        });
        txtNumTel.textProperty().addListener((obs, oldVal, newVal) -> {
            String num = newVal.trim();
            if (num.isEmpty()) {
                setFieldError(txtNumTel, lblErrTel, "Numéro requis.");
            } else if (num.matches("\\d{10}") && (num.startsWith("05") || num.startsWith("07") || num.startsWith("06"))) {
                String op = num.startsWith("05") ? "Ooredoo" : (num.startsWith("07") ? "Djezzy" : "Mobilis");
                setFieldSuccess(txtNumTel, lblErrTel, "Opérateur : " + op);
                setBotState("📞", "Numéro valide de 10 chiffres (" + op + ") !");
            } else {
                setFieldError(txtNumTel, lblErrTel, "Doit faire 10 chiffres (05, 06, 07).");
                setBotState("❌", "Le numéro doit comporter exactement 10 chiffres et débuter par 05, 06 ou 07.");
            }
            updatePreview();
        });

        txtMail.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) setBotState("✉️", "Entrez une adresse e-mail valide.");
        });
        txtMail.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty()) {
                setFieldError(txtMail, lblErrMail, "E-mail requis.");
            } else if (!newVal.contains("@")) {
                setFieldError(txtMail, lblErrMail, "Symbole '@' manquant.");
                setBotState("❌", "Il manque le symbole '@' dans votre e-mail !");
            } else {
                setFieldSuccess(txtMail, lblErrMail, "E-mail valide.");
                setBotState("✔", "Adresse e-mail validée.");
            }
        });

        txtExtra.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                if (rbEtudiant.isSelected()) {
                    setBotState("🎓", "Entrez l'année universitaire.");
                } else {
                    setBotState("📚", "Entrez les modules de cours enseignés.");
                }
            }
        });

        txtExtra.textProperty().addListener((obs, oldVal, newVal) -> updatePreview());
    }

    private void setFieldError(Control field, Label errLbl, String msg) {
        field.getStyleClass().removeAll("field-success");
        if (!field.getStyleClass().contains("field-error")) field.getStyleClass().add("field-error");
        if (errLbl != null) errLbl.setText("⚠ " + msg);
    }

    private void setFieldSuccess(Control field, Label errLbl, String msg) {
        field.getStyleClass().removeAll("field-error");
        if (!field.getStyleClass().contains("field-success")) field.getStyleClass().add("field-success");
        if (errLbl != null) errLbl.setText("✔ " + msg);
    }

    private void clearFieldStyles(Control field, Label errLbl) {
        field.getStyleClass().removeAll("field-error", "field-success");
        if (errLbl != null) errLbl.setText("");
    }

    private boolean validerChampsGlobal() {
        String tel = txtNumTel.getText().trim();
        return !txtNom.getText().isEmpty() && 
               !txtPrenom.getText().isEmpty() && 
               cbSexe.getValue() != null &&
               !txtExtra.getText().isEmpty() &&
               txtMail.getText().contains("@") && 
               tel.matches("\\d{10}") && (tel.startsWith("05") || tel.startsWith("06") || tel.startsWith("07")) && 
               txtDateNais.getText().matches("\\d{8}");
    }

    private void afficherNotification(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}