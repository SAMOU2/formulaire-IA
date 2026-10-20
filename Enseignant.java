public class Enseignant extends Personne {
    private String moduleEnseigne;
    public  Enseignant (String nom, String prenom, String sexe, String date_nai, String num_tl, String mail,String modules){
        this.moduleEnseigne=modules;
        super(nom, prenom, sexe, date_nai, num_tl, mail);

    }
    public String getModuleEnseigne() { return moduleEnseigne; }
    public void setModuleEnseigne(String moduleEnseigne) { this.moduleEnseigne = moduleEnseigne; }
}