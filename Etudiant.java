import java.util.Date;

public class Etudiant extends Personne {
    private String AAAA;
    
    public  Etudiant(String nom, String prenom, String sexe, String date_nai, String num_tl, String mail ,String AnUn){
        this.AAAA=AnUn;        super(nom, prenom, sexe, date_nai, num_tl, mail);
    }
    public void AttribuerMatricule(String date_nais){
     String AnUn = this.AAAA; 

     System.out.println(date_nais + AnUn);


     
    }
}
