package src;
import java.sql.*;
import java.util.List;

public class Client {
    private int idClient; //id en BD (auto increment)
    private String nomClient;
    private String prenomClient;
    private String adresseClient;
    private String telClient;
    private Panier panierEnCours;

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

                        //on ajoute le panier en cours du client s'il en a un, requête dans la table panier
                        String selectPanier = "SELECT * FROM panier WHERE idClient = ? AND panierTermine = false";
                        try (PreparedStatement pstmt2 = connection.prepareStatement(selectPanier)) {
                            pstmt2.setInt(1, idClient);
                            try (ResultSet rs2 = pstmt2.executeQuery()) {
                                if (rs2.next()) {
                                    this.panierEnCours = new Panier(rs2.getInt("idPanier"), idClient, rs2.getInt("idMagasin"), rs2.getDate("dateDebutPanier"));
                                }
                                else this.panierEnCours = null;
                            }
                        }
                    } else {
                        System.out.println("Client introuvable (" + idClient + ")");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    //retourne le panier en cours du client, null si aucun panier en cours
    public Panier getPanierEnCours() {        
        return panierEnCours;
    }

    public void creerPanier(int idMagasin) {
        //on gère dans la classe panier le cas où le client a déjà un panier en cours
        this.panierEnCours = new Panier(this.idClient, idMagasin);

        try (Connection connection = DBConnection.getConnection()) {

            //ajout du panier en BD
            String insertQuery = "INSERT INTO panier (idClient, idMagasin, panierTermine, dateDebutPanier, dateFinPanier) VALUES (?, ?, false, ?, null)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
                pstmt.setInt(1, this.idClient); 
                pstmt.setInt(2, idMagasin);
                pstmt.setDate(3, new Date(System.currentTimeMillis()));

                //exécution de la requête
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Panier ajouté avec succès !");
                } else {
                    System.out.println("Aucun panier ajouté.");
                }
                connection.commit();                

            } catch (SQLException e) {
                //rollback si erreur
                connection.rollback();
                System.out.println("Erreur lors de l'ajout : " + e.getMessage());
            }
        }
        catch (SQLException e) {
            System.out.println("Erreur lors de la création du panier : " + e.getMessage());
        }
    }

    /* 
    // Ajouter un produit au panier
    public void ajouterProduitAuPanier(Produit produit, int quantite) {
        panier.ajouterProduit(produit, quantite);
    }

    // Retirer un produit du panier
    public void retirerProduitDuPanier(Produit produit) {
        panier.retirerProduit(produit);
    }
    
    // Calculer le total du panier
    public void afficherTotalPanier() {
        double total = panier.calculerTotal();
        System.out.println("Total du panier : " + total + "€");
    }
    */

    //US 1.3
    public void validerPanier() {

        //transformer le panier en cours en commande
        //TODO

        //puis supprimer le panier en cours du client
        this.panierEnCours = null;
    }

    //US 1.4
    public void annulerPanier() {
        if(panierEnCours == null) {
            System.out.println("Aucun panier en cours à annuler.");
            return;
        }

        //supprimer dans la BD le panier en cours du client
        try (Connection connection = DBConnection.getConnection()){
            connection.setAutoCommit(false);

            //on remet les produits du panier en stock dans les magasins correspondants
            String supprPanierProduits = "DELETE FROM panier_produit WHERE idPanier = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(supprPanierProduits)) {
                pstmt.setInt(1, panierEnCours.getIdPanier());

                //exécution de la requête
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Les produits du panier ont bien été supprimés.");
                } else {
                    System.out.println("Ce client n'a pas de panier en cours.");
                }

                connection.commit();
            } catch (SQLException e) {
                //rollback si erreur
                connection.rollback();
                System.out.println("Erreur lors de la suppression : " + e.getMessage());
            }

            //et on supprime le panier
            String supprPanier = "DELETE FROM panier WHERE idClient = ? AND panierTermine = false;";

            try (PreparedStatement pstmt = connection.prepareStatement(supprPanier)) {
                pstmt.setInt(1, idClient);

                //exécution de la requête
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Le panier en cours du client a bien été annulé.");
                } else {
                    System.out.println("Ce client n'a pas de panier en cours.");
                }

                connection.commit();
            } catch (SQLException e) {
                //rollback si erreur
                connection.rollback();
                System.out.println("Erreur lors de la suppression : " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
        }
    this.panierEnCours = null;
    }

    @Override
    public String toString() {
        return "Client [id=" + idClient + ", nom=" + nomClient + ", prenom=" + prenomClient + ", adresse=" + adresseClient + ", tel=" + telClient + "]";
    }
}
