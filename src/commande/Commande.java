package src.commande;
import java.sql.*;

import src.DBConnection; 

public class Commande {
    
    //propriétés
    private int idCommande;
    private int idPanier;
    private String typeCommande;
    private String statutCommande;
    private Timestamp dateReception;
    private Timestamp datePreparation;
    private Timestamp dateFinalisation;
    
    //récupérer la commande d'un client
    public Commande(int idCommande, int idPanier, String typeCommande, String statutCommande, Timestamp dateReception, Timestamp datePreparation, Timestamp dateFinalisation) {
        this.idCommande = idCommande;
        this.idPanier = idPanier;
        this.statutCommande = statutCommande;
        this.typeCommande = typeCommande;
        this.dateReception = dateReception;
        this.datePreparation = datePreparation;
        this.dateFinalisation = dateFinalisation;        
    }

    //constructeur pour creer une nouvelle commande.
    public Commande(int idPanier, String typeCommande, Timestamp dateReception){
        this.idPanier = idPanier;
        this.typeCommande = typeCommande;
        this.dateReception = dateReception;
        this.statutCommande = "en attente";
        this.datePreparation = null;
        this.dateFinalisation = null;
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
            return "Commande [idCommande=" + idCommande + ", idPanier=" + idPanier + ", statutCommande="
                    + statutCommande + ", typeCommande=" + typeCommande + ", dateReception=" + dateReception
                    + ", datePreparation=" + datePreparation + ", dateFinalisation=" + dateFinalisation + "]";
        }  
    
}
