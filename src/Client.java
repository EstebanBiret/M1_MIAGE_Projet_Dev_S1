package src;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private int idClient; //id en BD (auto increment)
    private String nomClient;
    private String prenomClient;
    private String adresseClient;
    private String telClient;
    private int idMagasinFavori;

    //constructeur avec un paramètre (récupérer un client de la BD avec son ID)
    public Client(int idClient) {
        try (Connection connection = DBConnection.getConnection()) {
            String selectQuery = "SELECT * FROM client WHERE idClient = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {

                pstmt.setInt(1, idClient);

                try (ResultSet rs = pstmt.executeQuery()) {

                    if (rs.next()) {
                        this.idClient = rs.getInt("idClient");
                        this.nomClient = rs.getString("nomClient");
                        this.prenomClient = rs.getString("prenomClient");
                        this.adresseClient = rs.getString("adresseClient");
                        this.telClient = rs.getString("telClient");
                        this.idMagasinFavori = rs.getInt("idMagasin");
                    } else {
                        System.out.println("Client introuvable (" + idClient + ")");
                    }
                }
            }
        connection.close();

        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    //getters & setters
    public int getIdClient() {return idClient;}
    public int getIdMagasinFavori() {return idMagasinFavori;}

    //retourne le panier en cours du client, null si aucun panier en cours
    public Panier getPanierEnCours() {  

        Panier p = null;

        try (Connection connection = DBConnection.getConnection()) {

        String selectPanier = "SELECT * FROM panier WHERE idClient = ? AND panierTermine = false";
        try (PreparedStatement pstmt = connection.prepareStatement(selectPanier)) {
            pstmt.setInt(1, this.idClient);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    p = new Panier(rs.getInt("idPanier"), idClient, rs.getTimestamp("dateDebutPanier"));
                }
            }
        }
        connection.close();

        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return p;
    }

    public Panier creerPanier() {
        //on gère dans la classe panier le cas où le client a déjà un panier en cours
        return new Panier(this.idClient);
    }

    /*
     * Retourne une liste des produits les plus commandés du client actuel
     */
    public List<Produit> getProduitsPlusCommandes() {
        List<Produit> produits = new ArrayList<>();

        //récupérer les commandes de ce client
        List<Commande> commandes = getCommandes();
        for (Commande commande : commandes) {
            System.out.println(commande.toString());
        }
        return produits;
    }

    /*
     * Retourne une liste des commandes du client actuel
     */
    public List<Commande> getCommandes() {
        List<Commande> commandes = new ArrayList<>();

        //récupérer en BD les commandes de ce client
        try (Connection connection = DBConnection.getConnection()) {

            String commandesQuery = "SELECT * FROM commande c, panier p WHERE c.idPanier = p.idPanier AND p.idClient = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(commandesQuery)) {
                pstmt.setInt(1, this.idClient);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Commande commande = new Commande(
                            rs.getInt("idCommande"),
                            rs.getInt("idPanier"),
                            rs.getString("typeCommande"),
                            rs.getString("statutCommande"),
                            rs.getTimestamp("dateReception"),
                            rs.getTimestamp("datePreparation"),
                            rs.getTimestamp("dateFinalisation")
                        );
                        commandes.add(commande);
                    } 
                }
            }
            connection.close();
    
            } catch (SQLException e) {
                System.out.println("Erreur : " + e.getMessage());
            }

        if(commandes.isEmpty()) {
            System.out.println("Ce client n'a pas de commandes.");
        }
        return commandes;
    }

    @Override
    public String toString() {
        return "Client [id=" + idClient + ", nom=" + nomClient + ", prenom=" + prenomClient + ", adresse=" + adresseClient + ", tel=" + telClient + ", magasin favori=" + idMagasinFavori + "]";
    }
}