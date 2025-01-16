package src.commande;
import java.sql.*;

public class Commande {
    
    //propriétés
    private int idCommande;
    private int idPanier;
    private String typeCommande;
    private String statutCommande;
    private Timestamp dateReception;
    private Timestamp datePreparation;
    private Timestamp dateFinalisation;

    //constructeur
    public Commande(int idCommande, int idPanier, String typeCommande, String statutCommande, Timestamp dateReception, Timestamp datePreparation, Timestamp dateFinalisation) {
        this.idCommande = idCommande;
        this.idPanier = idPanier;
        this.statutCommande = statutCommande;
        this.typeCommande = typeCommande;
        this.dateReception = dateReception;
        this.datePreparation = datePreparation;
        this.dateFinalisation = dateFinalisation;        
    }

    //getters & setters
    public int getIdCommande() {return idCommande;}
    public void setIdCommande(int idCommande) {this.idCommande = idCommande;}
    public int getIdPanier() {return idPanier;}
    public String getStatutCommande() {return statutCommande;}
    public String getTypeCommande() {return typeCommande;}
    public Timestamp getDateReception() {return dateReception;}
    public Timestamp getDatePreparation() {return datePreparation;}
    public Timestamp getDateFinalisation() {return dateFinalisation;}
    public void setStatutCommande(String statutCommande) {this.statutCommande = statutCommande;}
    public void setTypeCommande(String typeCommande) {this.typeCommande = typeCommande;}
    public void setDateReception(Timestamp dateReception) {this.dateReception = dateReception;}
    public void setDatePreparation(Timestamp datePreparation) {this.datePreparation = datePreparation;}
    public void setDateFinalisation(Timestamp dateFinalisation) {this.dateFinalisation = dateFinalisation;}

    @Override
    public String toString() {
        return "Commande [idCommande=" + idCommande + ", idPanier=" + idPanier + ", typeCommande=" + typeCommande + ", statutCommande="
                + statutCommande + ", dateReception=" + dateReception
                + ", datePreparation=" + datePreparation + ", dateFinalisation=" + dateFinalisation + "]";
    }  
}