
import java.util.Date;
import java.util.*;

public class Personne {
    private String nom;
    private String prenom;
    private String sexe;
    private String date_nais;
    private String num_tel;
    private String mail;

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getSexe() {
        return sexe;
    }

    public String getDate_nais() {
        return date_nais;
    }
    

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public void setDate_nais(String date_nais) {
        this.date_nais = date_nais;
    }

    public void setNum_tel(String num_tel) {
        this.num_tel = num_tel;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getNum_tel() {
        return num_tel;
    }

    public String getMail() {
        return mail;
    }

    public Personne(String nom, String prenom, String sexe, String date_nai, String num_tl, String mail) {
        this.nom = nom;
        this.prenom = prenom;
        this.sexe = sexe;
        this.date_nais = date_nai;
        this.num_tel = num_tl;
        this.mail = mail;
    }

    public boolean Motcomposer(String nom) {
        return nom.contains(" ") || nom.contains("-");

    }

    public void Operateur(String num_tel) {
        String num = num_tel.trim();
      if (num.isEmpty() || num==null) {
        System.out.println("invalide");
      } 
      if (num.startsWith("05")) {
        System.out.println("ORD");
      }else if (num.startsWith("07")) {
        System.out.println("Dj");
      }
      else {
        System.out.println("mob");

        }
    }
    public boolean  VerifierMail(String mail){
        if (mail.isEmpty()||mail==null) {
            return false;
        }
        
        if (mail.contains("@")) {
            return true;
        }else{
         return  false;
        }
        
    }  
    public String NettoyerChiffre(String nom){
        return nom.replace("[0-9]", "");

    }
    


    



}
