package src;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Panier {
    
    // Propriétés
    private int idPanier;
    private int idClient;

    private boolean panierTermine;
    private Date dateDebutPanier;
    private Date dateFinPanier;
    //private List<ArrayList<Integer, Integer, String>> produits;

    //construire un nouveau panier
    public Panier(int idClient) {

        //vérifier que le client n'a pas déjà un panier en cours
        String queryTest = "SELECT * FROM panier WHERE idClient = ? AND panierTermine = false;";
        try (Connection connection = DBConnection.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(queryTest)) {
            
            pstmt.setInt(1, idClient);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("Le client a déjà un panier en cours.");
                return;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification du panier en cours : " + e.getMessage());
        }

        //créer le panier en java
        this.idClient = idClient;
        this.panierTermine = false;
        this.dateDebutPanier = new Date(System.currentTimeMillis());
        this.dateFinPanier = null;

        //créer le panier en BD, et récupérer son ID
        String query = "INSERT INTO panier (idClient, panierTermine, dateDebutPanier, dateFinPanier) VALUES (?, ?, ?, ?);";
        try (Connection connection = DBConnection.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, idClient);
            pstmt.setBoolean(2, this.panierTermine);
            pstmt.setDate(3, this.dateDebutPanier);
            pstmt.setDate(4, this.dateFinPanier);

            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                this.idPanier = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la création du panier : " + e.getMessage());
        }
    }

    //construire un panier en cours d'un client
    public Panier(int idPanier, int idClient, Date dateDebutPanier) {
        this.idPanier = idPanier;
        this.idClient = idClient;
        this.panierTermine = false;
        this.dateDebutPanier = dateDebutPanier;
        this.dateFinPanier = null;
    }

    //getters & setters
    public int getIdPanier() {return idPanier;}
    public int getIdClient() {return idClient;}
    public boolean isPanierTermine() {return panierTermine;}
    public void setPanierTermine(boolean panierTermine) {this.panierTermine = panierTermine;}
    public Date getDateDebutPanier() {return dateDebutPanier;}
    public Date getDateFinPanier() {return dateFinPanier;}

    // Savoir si le panier recherché existe bien en BD
    public boolean exists() {return this.idPanier != 0;}

    @Override
    public String toString() {
        return "Panier [id=" + idPanier + ", idClient=" + idClient
                + ", statut_panier=" + panierTermine + ", date_début=" + dateDebutPanier + ", date_fin=" + dateFinPanier
                + "]";
    }

    //US 1.3
    public void validerPanier() {

        if(this.panierTermine) {
            System.out.println("Le panier a déjà été annulé/validé.");
            return;
        }

        //transformer le panier en cours en commande
        //TODO

        //puis supprimer le panier en cours du client
        this.panierTermine = true;
    }

    //US 1.4
    public void annulerPanier() {
        /*if(panierEnCours == null) {
            System.out.println("Aucun panier en cours à annuler.");
            return;
        }*/

        if(this.panierTermine) {
            System.out.println("Le panier a déjà été annulé/validé.");
            return;
        }

        //supprimer dans la BD le panier en cours du client
        try (Connection connection = DBConnection.getConnection()){
            connection.setAutoCommit(false);

            //on supprime les produits du panier
            String supprPanierProduits = "DELETE FROM panier_produit_magasin WHERE idPanier = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(supprPanierProduits)) {
                pstmt.setInt(1, idPanier);

                //exécution de la requête
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Les produits du panier ont bien été supprimés.");
                } else {
                    System.out.println("Ce client n'a pas de panier en cours, ou aucun produit dans ce panier.");
                }
                connection.commit();
            } catch (SQLException e) {
                //rollback si erreur
                connection.rollback();
                System.out.println("Erreur lors de la suppression : " + e.getMessage());
            }

            //et on supprime le panier en cours
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
            connection.close();

        } catch (SQLException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
        }

        this.panierTermine = true;
    }

    //afficher un panier (US 1.2)
    /*public void afficherPanier() {
        String query = """
            SELECT p.idPanier, c.nomClient, c.prenomClient, pr.libelleProduit, pp.quantiteVoulue, pp.modeLivraison
            FROM panier p, client c, panier_produit pp, produit pr
            WHERE p.idClient = c.idClient AND p.idPanier = pp.idPanier AND pp.idProduit = pr.idProduit AND p.idPanier = ?;
        """;

        try (Connection connection = DBConnection.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(query)) {
             
            pstmt.setInt(1, this.idPanier);

            ResultSet rs = pstmt.executeQuery();
            System.out.println("Détails du panier " + this.idPanier);
            while (rs.next()) {
                System.out.println(this.toString());
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage du panier : " + e.getMessage());
        }
    }*/

    public void ajouterProduit(int idProduit, int qte, int magasin, String modeLivraison) {
        //vérifier que le produit est disponible dans le magasin
        String queryTest = "SELECT * FROM stocker WHERE idProduit = ? AND idMagasin = ? AND quantiteEnStock >= ?;";
        try (Connection connection = DBConnection.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(queryTest);
            
            pstmt.setInt(1, idProduit);
            pstmt.setInt(2, magasin);
            pstmt.setInt(2, magasin);

            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {
                System.out.println("Le produit n'est pas disponible dans ce magasin.");
                return;
            }
            connection.close();

        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification du stock : " + e.getMessage());
        }

        //ajouter le produit au panier
        String query = "INSERT INTO panier_produit (idPanier, idProduit, quantiteVoulue, modeLivraison) VALUES (?, ?, ?, ?);";
        try (Connection connection = DBConnection.getConnection()){
            PreparedStatement pstmt = connection.prepareStatement(query);
            
            pstmt.setInt(1, this.idPanier);
            pstmt.setInt(2, idProduit);
            pstmt.setInt(3, qte);
            pstmt.setString(4, modeLivraison);

            pstmt.executeUpdate();
            connection.close();
        } 

        catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du produit au panier : " + e.getMessage());
        }
    }

    //permet de récupérer les produits du panier
    public Map<Integer, Integer> getProduits() {
        return null;
    }
}
