package src;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import src.client.Client;
import src.client.ClientDAO;
import src.produit.Produit;
import src.produit.ProduitDAO;

//classe pour les US 3, Marc - Gérer
public class GestionnaireDAO {

    ClientDAO clientDAO = new ClientDAO();
    ProduitDAO produitDAO = new ProduitDAO();

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

    public String getProduitPlusCommande() {
        String res = "Aucun produit commandé.";
    
        String query = """
            SELECT ppm.idProduit, SUM(ppm.quantiteVoulue) AS totalQuantite
            FROM panier_produit_magasin ppm, panier p, commande c
            WHERE ppm.idPanier = p.idPanier
            AND p.idPanier = c.idPanier
            GROUP BY ppm.idProduit
            ORDER BY totalQuantite DESC
            LIMIT 1
        """;
    
        try (Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {
    
            // Exécute la requête pour récupérer le produit le plus commandé
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int idProduit = resultSet.getInt("idProduit");
                    int totalQuantite = resultSet.getInt("totalQuantite");
    
                    // Récupérer les détails du produit correspondant
                    Produit produitPlusCommande = produitDAO.getProduitById(idProduit);
    
                    if (produitPlusCommande != null) {
                        res = produitPlusCommande.toString() + 
                        " (Commandé " + totalQuantite + " fois)";
                    }
                }
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public List<String> getTopCategories() {
        List<String> topCategories = new ArrayList<>();

        String query = """
            SELECT ca.nomCategorie, SUM(ppm.quantiteVoulue) AS totalQuantite
            FROM panier_produit_magasin ppm, panier p, commande c, produit prod, categorie ca, appartenir a
            WHERE ppm.idPanier = p.idPanier
            AND p.idPanier = c.idPanier
            AND ppm.idProduit = prod.idProduit
            AND prod.idProduit = a.idProduit
            AND a.idCategorie = ca.idCategorie
            GROUP BY ca.nomCategorie
            ORDER BY totalQuantite DESC
            LIMIT 5;        
            """;

        try (Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {

            // Exécuter la requête pour récupérer les catégories les plus commandées
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String categorie = resultSet.getString("nomCategorie");
                    int totalQuantite = resultSet.getInt("totalQuantite");

                    // Ajouter les détails au résultat
                    topCategories.add(categorie + " (Commandé " + totalQuantite + " fois)");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return topCategories;
    }

    /*
     * Permet de récupérer le client qui a le plus commandé
     */
    public List<String> getTopClientsNbCommandes() {
        List<String> topClients = new ArrayList<>();
    
        String query = """
            SELECT idClient, COUNT(*) AS nbCommandes
            FROM panier
            WHERE dateFinPanier IS NOT NULL
            GROUP BY idClient
            HAVING COUNT(*) = (
                SELECT MAX(nbCommandes) 
                FROM (
                    SELECT COUNT(*) AS nbCommandes
                    FROM panier 
                    WHERE dateFinPanier IS NOT NULL
                    GROUP BY idClient
                ) AS commandesParClient
            )
        """;
    
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
    
            // Exécute la requête pour récupérer les clients ayant le plus commandé
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int idClient = resultSet.getInt("idClient");
                    int nbCommandes = resultSet.getInt("nbCommandes");
    
                    // Charger les informations sur le client
                    Client client = clientDAO.getClientById(idClient);
                    topClients.add(client.toString() + " (Commandes: " + nbCommandes + ")");
                }
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return topClients;
    }

    public List<String> getTopClientsChiffreAffaires() {
        List<String> topClients = new ArrayList<>();
    
        String query = """
        SELECT p.idClient, SUM(ppm.quantiteVoulue * pr.prixUnitaire) AS totalCA
        FROM panier p, panier_produit_magasin ppm, produit pr, commande c
        WHERE p.idPanier = ppm.idPanier
        AND ppm.idProduit = pr.idProduit
        AND p.idPanier = c.idPanier
        AND p.dateFinPanier IS NOT NULL
        GROUP BY p.idClient
        HAVING SUM(ppm.quantiteVoulue * pr.prixUnitaire) = (
            SELECT MAX(totalCA)
            FROM (
                SELECT SUM(ppm.quantiteVoulue * pr.prixUnitaire) AS totalCA
                FROM panier p, panier_produit_magasin ppm, produit pr, commande c
                WHERE p.idPanier = ppm.idPanier
                AND ppm.idProduit = pr.idProduit
                AND p.idPanier = c.idPanier
                AND p.dateFinPanier IS NOT NULL
                GROUP BY p.idClient
            ) AS caParClient
        )
    """;

    
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
    
            // Exécute la requête pour récupérer les clients ayant généré le plus de chiffre d'affaires
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int idClient = resultSet.getInt("idClient");
                    double totalCA = resultSet.getDouble("totalCA");
    
                    // Charger les informations sur le client
                    Client client = clientDAO.getClientById(idClient);
                    topClients.add(client.toString() + " (Chiffre d'Affaires: " + totalCA + " euros)");
                }
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return topClients;
    }
    
    //US 3.2
    // Méthode pour calculer le temps moyen de réalisation des paniers terminés
    public double calculerTempsMoyenRealisation() {
        try (Connection connection = DBConnection.getConnection()){
        String queryTempsMoyen = "SELECT AVG(TIMESTAMPDIFF(HOUR, dateDebutPanier, dateFinPanier)) AS tempsMoyen " +
                                 "FROM panier " +
                                 "WHERE panierTermine = TRUE AND dateFinPanier IS NOT NULL";
        try (PreparedStatement pstmtTempsMoyen = connection.prepareStatement(queryTempsMoyen)) {
            try (ResultSet rs = pstmtTempsMoyen.executeQuery()) {
                if (rs.next()) {
                    double tempsMoyenHeures = rs.getDouble("tempsMoyen");
                    System.out.println("Temps moyen de réalisation des paniers (en heures) : ");
                    return tempsMoyenHeures;
                }
            }
        } } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du temps moyen de réalisation des paniers : " + e.getMessage());
        }
        return 0;
    }
    // Méthode pour calculer le temps moyen de préparation des commandes
    public double calculerTempsMoyenPreparation() {
     double tempsMoyen = 0;

     String query = "SELECT AVG(TIMESTAMPDIFF(HOUR, datePreparation, dateFinalisation)) AS temps_moyen_preparation " +
                   "FROM commande WHERE datePreparation IS NOT NULL"+
                   "AND dateFinalisation IS NOT NULL";
    
     try (Connection connection = DBConnection.getConnection();
         PreparedStatement pstmt = connection.prepareStatement(query);
         ResultSet rs = pstmt.executeQuery()) {
        
        if (rs.next()) {
            tempsMoyen = rs.getDouble("temps_moyen_preparation");
            System.out.println("Temps moyen de préparation des commandes (en heures) : ");
        } else {
            System.out.println("Aucune commande préparée trouvée.");
        }
     } catch (SQLException e) {
        System.out.println("Erreur lors du calcul du temps moyen de préparation : " + e.getMessage());
     }

     return tempsMoyen;
    }
}