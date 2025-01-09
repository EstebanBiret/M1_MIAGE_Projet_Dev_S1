package src;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//classe pour les US 3, Marc - Gérer
public class Gestionnaire {

    /*
     * Permet de sauvegarder en BD un nouveau produit
     */
    public void ajouterProduitCatalogue(Produit p) {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            //vérifier qu'un produit n'existe pas déjà avec ce libellé
            String selectQuery = "SELECT * FROM produit WHERE libelleProduit = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
                pstmt.setString(1, p.getLibelleProduit());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Un produit existe déjà avec ce libellé (" + p.getLibelleProduit() + ").");
                        return;
                    }
                }
            }

            //ajout du produit en BD
            String insertQuery = "INSERT INTO produit (libelleProduit, prixUnitaire, prixKilo, nutriscore, poidsProduit, conditionnementProduit, marqueProduit) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
                pstmt.setString(1, p.getLibelleProduit()); 
                pstmt.setDouble(2, p.getPrixUnitaire());
                pstmt.setDouble(3, p.getPrixKilo());
                pstmt.setString(4, String.valueOf(p.getNutriscore()));
                pstmt.setDouble(5, p.getPoidsProduit());
                pstmt.setString(6, p.getConditionnementProduit());
                pstmt.setString(7, p.getMarqueProduit());

                //exécution de la requête
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Produit ajouté avec succès (" + p.getLibelleProduit() + ").");
                } else {
                    System.out.println("Aucun produit ajouté.");
                }
                connection.commit();

                //récupérer l'id du produit tout juste inséré
                String getId = "SELECT * FROM produit WHERE libelleProduit = ?";
                try (PreparedStatement pstmt2 = connection.prepareStatement(getId)) {
                    pstmt2.setString(1, p.getLibelleProduit());
                    try (ResultSet rs = pstmt2.executeQuery()) {
                        if (rs.next()) {
                            p.setIdProduit(rs.getInt("idProduit"));
                        } else {
                            System.out.println("Produit introuvable (" + p.getLibelleProduit() + ").");
                        }
                    }
                }

            } catch (SQLException e) {
                //rollback si erreur
                connection.rollback();
                System.out.println("Erreur lors de l'ajout : " + e.getMessage());
            }
            connection.close();

        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    /*
     * Permet de valider la modification d'un produit en BD
     */
    public void majProduitCatalogue(Produit p) {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            String updateQuery = "UPDATE produit SET libelleProduit = ?, prixUnitaire = ?, prixKilo = ?, nutriscore = ?, poidsProduit = ?, conditionnementProduit = ?, marqueProduit = ? WHERE idProduit = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
                pstmt.setString(1, p.getLibelleProduit()); 
                pstmt.setDouble(2, p.getPrixUnitaire());
                pstmt.setDouble(3, p.getPrixKilo());
                pstmt.setString(4, String.valueOf(p.getNutriscore()));
                pstmt.setDouble(5, p.getPoidsProduit());
                pstmt.setString(6, p.getConditionnementProduit());
                pstmt.setString(7, p.getMarqueProduit());
                pstmt.setInt(8, p.getIdProduit());
                
                //exécution de la requête
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Produit modifié avec succès (" + p.getIdProduit() + ").");
                } else {
                    System.out.println("Aucun produit modifié.");
                }

                connection.commit();

            } catch (SQLException e) {
                //rollback si erreur
                connection.rollback();
                System.out.println("Erreur lors de la modification : " + e.getMessage());
            }
            connection.close();
            
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

   /*
    * Permet de supprimer un produit en BD
    */
    public void supprProduitCatalogue(Produit p) {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            String deleteQuery = "DELETE FROM produit WHERE idProduit = ?";
            //TODO : supprimer les références de ce produit dans les tables concernées

            try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
                pstmt.setInt(1, p.getIdProduit());

                //exécution de la requête
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Produit supprimé avec succès (" + p.getIdProduit()+ ").");
                } else {
                    System.out.println("Aucun produit trouvé avec cet ID.");
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
    }

    //augmente les stocks d'un produit dans les magasins voulus
    public void augmenterStockProduit(int idProduit, int quantite, int magasin) {
    }
}
