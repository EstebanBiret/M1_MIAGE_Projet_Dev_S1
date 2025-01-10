package src;

import java.sql.*;
import java.util.List;

//classe pour les US 3, Marc - Gérer
public class Gestionnaire {

    /*
     * Permet de sauvegarder en BD un nouveau produit
     */
    public void ajouterProduitCatalogue(Produit p) {
        try (Connection connection = DBConnection.getConnection()) {

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
            try (PreparedStatement pstmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, p.getLibelleProduit()); 
                pstmt.setDouble(2, p.getPrixUnitaire());
                pstmt.setDouble(3, p.getPrixKilo());
                pstmt.setString(4, String.valueOf(p.getNutriscore()));
                pstmt.setDouble(5, p.getPoidsProduit());
                pstmt.setString(6, p.getConditionnementProduit());
                pstmt.setString(7, p.getMarqueProduit());

                //exécution de la requête
                int rowsAffected = pstmt.executeUpdate();

                //on récupère l'id auto increment de la ligne tout juste générée pour l'attribuer à l'id du produit
                if (rowsAffected > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            p.setIdProduit(rs.getInt(1));
                            System.out.println("Produit ajouté avec succès (" + p.toString() + ").");
                        }
                    }
                } else {
                    System.out.println("Aucun produit ajouté.");
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

                //connection.commit();

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

                //connection.commit();
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

    //maj le stock d'un produit dans les magasins spécifiés
    public void majStockProduit(int idProduit, int quantite, List<Integer> idsMagasins, boolean augmenter) {
        String selectQuery = """
            SELECT quantiteEnStock
            FROM stocker
            WHERE idProduit = ? AND idMagasin = ?
        """;

        String updateQuery = """
            UPDATE stocker
            SET quantiteEnStock = ?
            WHERE idProduit = ? AND idMagasin = ?
        """;

        String insertQuery = """
            INSERT INTO stocker (idMagasin, idProduit, quantiteEnStock)
            VALUES (?, ?, ?)
        """;

        try (Connection connection = DBConnection.getConnection()) {
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);

            for (int idMagasin : idsMagasins) {
                // Récupérer la quantité actuelle en stock
                selectStatement.setInt(1, idProduit);
                selectStatement.setInt(2, idMagasin);
                ResultSet resultSet = selectStatement.executeQuery();

                if (resultSet.next()) {
                    int quantiteActuelle = resultSet.getInt("quantiteEnStock");
                    int nouvelleQuantite = augmenter ? quantiteActuelle + quantite : Math.max(quantiteActuelle - quantite, 0); //pas en dessous de 0

                    updateStatement.setInt(1, nouvelleQuantite);
                    updateStatement.setInt(2, idProduit);
                    updateStatement.setInt(3, idMagasin);
                    updateStatement.executeUpdate();
                } else {
                    //produit inexistant -> insérer une nouvelle ligne si augmenter
                    if (augmenter) {
                        insertStatement.setInt(1, idMagasin);
                        insertStatement.setInt(2, idProduit);
                        insertStatement.setInt(3, quantite);
                        insertStatement.executeUpdate();
                    } else {
                        System.out.println("Impossible de réduire le stock : produit " + idProduit + " introuvable dans le magasin " + idMagasin);
                    }
                }
            }
            System.out.println("Stock mis à jour avec succès pour le produit " + idProduit + ".");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la mise à jour des stocks.");
        }
    }
}