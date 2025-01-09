package src;
//import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private int id;
    private String nomClient;
    private String adressClient;
    private String prenomClient;
    private int telClient;
    private Panier panier; // Le panier du client

    // Constructeurs
    public Client(int id, String nomClient, String adressClient, String prenomClient, int telClient) {
        this.id = id;
        this.nomClient = nomClient;
        this.adressClient = adressClient;
        this.prenomClient = prenomClient;
        this.telClient = telClient;
        this.panier = new Panier(); // Chaque client a un panier par défaut
    }

    public Client(String nom, String adress, String prenom, int tel) {
        this.nomClient = nom;
        this.adressClient = adress;
        this.prenomClient = prenom;
        this.telClient = tel;
        this.panier = new Panier();
    }

    // Getters et setters (inchangés)

    public Panier getPanier() {
        return panier;
    }

    public void setPanier(Panier panier) {
        this.panier = panier;
    }

    // Ajouter un produit au panier
    public void ajouterProduitAuPanier(Produit produit, int quantite) {
        panier.ajouterProduit(produit, quantite);
    }

    // Retirer un produit du panier
    public void retirerProduitDuPanier(Produit produit) {
        panier.retirerProduit(produit);
    }

    // Visualiser le contenu du panier
    public void afficherPanier() {
        panier.afficherContenu();
    }

    // Calculer le total du panier
    public void afficherTotalPanier() {
        double total = panier.calculerTotal();
        System.out.println("Total du panier : " + total + "€");
    }

    // Vider le panier
    public void viderPanier() {
        panier.viderPanier();
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", nom='" + nomClient + '\'' +
                ", email='" + adressClient + '\'' +
                '}';
    }
}
