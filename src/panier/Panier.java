package src.panier;

import java.sql.*;

import src.client.Client;
import src.client.ClientDAO;

public class Panier {
    
    //propriétés
    private int idPanier;
    private int idClient;
    private boolean panierTermine;
    private Timestamp dateDebutPanier;
    private Timestamp dateFinPanier;

    //créer un nouveau panier pour le client
    public Panier(int idClient) {
        this.idClient = idClient;
        this.panierTermine = false;
        this.dateDebutPanier = new Timestamp(System.currentTimeMillis());
        this.dateFinPanier = null;
    }
    
    //construire le panier en cours d'un client
    public Panier(int idPanier, int idClient, Timestamp dateDebutPanier) {
        this.idPanier = idPanier;
        this.idClient = idClient;
        this.panierTermine = false;
        this.dateDebutPanier = dateDebutPanier;
        this.dateFinPanier = null;
    }

    //getters & setters
    public int getIdPanier() {return idPanier;}
    public void setIdPanier(int idPanier) {this.idPanier = idPanier;}
    public int getIdClient() {return idClient;}
    public Client getClient() {ClientDAO clientdao = new ClientDAO();return clientdao.getClientById(idClient);}
    public boolean isPanierTermine() {return panierTermine;}
    public void setPanierTermine(boolean panierTermine) {this.panierTermine = panierTermine;}
    public Timestamp getDateDebutPanier() {return dateDebutPanier;}
    public Timestamp getDateFinPanier() {return dateFinPanier;}
    public void setDateFinPanier(Timestamp dateFinPanier) {this.dateFinPanier = dateFinPanier;}
}