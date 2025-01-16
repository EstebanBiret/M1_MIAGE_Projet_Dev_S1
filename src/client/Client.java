package src.client;

import src.panier.Panier;
import src.panier.PanierDAO;

public class Client {
    private int idClient; //id en BD (auto increment)
    private String nomClient;
    private String prenomClient;
    private String adresseClient;
    private String telClient;
    private int idMagasinFavori;

    //constructeur utilisant les données récupérées par ClientDAO
    public Client(int idClient, String nomClient, String prenomClient, String adresseClient, String telClient, int idMagasinFavori) {
        this.idClient = idClient;
        this.nomClient = nomClient;
        this.prenomClient = prenomClient;
        this.adresseClient = adresseClient;
        this.telClient = telClient;
        this.idMagasinFavori = idMagasinFavori;
    }

    //getters & setters
    public int getIdClient() {return idClient;}
    public String getNomClient() {return nomClient;}
    public void setNomClient(String nom) {this.nomClient = nom;}
    public String getPrenomClient() {return prenomClient;}
    public void setPrenomClient(String prenom) {this.prenomClient = prenom;}
    public String getAdresseClient() {return adresseClient;}
    public void setAdresseClient(String adresse) {this.adresseClient = adresse;}
    public String getTelClient() {return telClient;}
    public void setTelClient(String tel) {this.telClient = tel;}
    public int getIdMagasinFavori() {return idMagasinFavori;}
    public void setIdMagasinFavori(int idMagasin) {this.idMagasinFavori = idMagasin;}

    public Panier creerPanier() {
        //on gère dans la classe PanierDAO le cas où le client a déjà un panier en cours
        PanierDAO panierDAO = new PanierDAO(); 
        return panierDAO.creerPanier(idClient);
    }

    @Override
    public String toString() {
        return "Client [id=" + idClient + ", nom=" + nomClient + ", prenom=" + prenomClient + ", adresse=" + adresseClient + ", tel=" + telClient + ", magasin favori=" + idMagasinFavori + "]";
    }
}