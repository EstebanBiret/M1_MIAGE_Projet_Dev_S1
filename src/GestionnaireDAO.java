package src;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
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
    public void majStockProduit(int idProduit, int quantite, int idMagasin, boolean augmenter) {

        if(quantite <= 0) {
            System.out.println("La quantité doit être supérieure à 0 !");
            return;
        }
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

        String testProduit = "SELECT * FROM produit WHERE idProduit = ?;";

        String testMagasin = "SELECT * FROM magasin WHERE idMagasin = ?;";

        try (Connection connection = DBConnection.getConnection()) {
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            PreparedStatement testProduitStatement = connection.prepareStatement(testProduit);
            PreparedStatement testMagasinStatement = connection.prepareStatement(testMagasin);

            //test si le produit existe
            testProduitStatement.setInt(1, idProduit);
            ResultSet resultSetTestProduit = testProduitStatement.executeQuery();
            if (!resultSetTestProduit.next()) {
                System.out.println("Le produit " + idProduit + " n'existe pas.");
                return;
            }

            //test si le magasin existe
            testMagasinStatement.setInt(1, idMagasin);
            ResultSet resultSetTestMagasin = testMagasinStatement.executeQuery();
            if (!resultSetTestMagasin.next()) {
                System.out.println("Le magasin " + idMagasin + " n'existe pas.");
                return;
            }

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
                System.out.println("Stock mis à jour pour le produit " + idProduit + " dans le magasin " + idMagasin + " : " + nouvelleQuantite);
            } else {
                //produit inexistant -> insérer une nouvelle ligne si augmenter
                if (augmenter) {
                    insertStatement.setInt(1, idMagasin);
                    insertStatement.setInt(2, idProduit);
                    insertStatement.setInt(3, quantite);
                    insertStatement.executeUpdate();
                    System.out.println("Stock ajouté pour le produit " + idProduit + " dans le magasin " + idMagasin + " : " + quantite);
                } else {
                    System.out.println("Impossible de réduire le stock : produit " + idProduit + " introuvable dans le magasin " + idMagasin);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la mise à jour des stocks.");
        }
    }

    //permet de récupérer les 5 produits les + commandés
    public String getProduitPlusCommandes() {
        StringBuilder res = new StringBuilder("Top 5 des produits les plus commandés :\n");

        String query = """
            SELECT ppm.idProduit, SUM(ppm.quantiteVoulue) AS totalQuantite
            FROM panier_produit_magasin ppm, panier p, commande c
            WHERE ppm.idPanier = p.idPanier
            AND p.idPanier = c.idPanier
            GROUP BY ppm.idProduit
            ORDER BY totalQuantite DESC
            LIMIT 5
        """;
    
        int rank = 1;

        try (Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {
    
            // Exécute la requête pour récupérer le produit le plus commandé
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int idProduit = resultSet.getInt("idProduit");
                    int totalQuantite = resultSet.getInt("totalQuantite");
        
                    // Récupérer les détails du produit correspondant
                    Produit produit = produitDAO.getProduitById(idProduit);
        
                    if (produit != null) {
                        res.append(rank).append(". ")
                           .append(produit.toString())
                           .append(" (Commandé ").append(totalQuantite).append(" fois)\n");
                    } else {
                        res.append(rank).append(". Produit inconnu (ID: ")
                           .append(idProduit).append(") - ")
                           .append(totalQuantite).append(" fois commandé\n");
                    }
        
                    rank++;
                }
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res.toString();
    }

    //permet de récupérer les 3 catégories les + commandées
    public String getTopsCategories() {
        StringBuilder res = new StringBuilder("Aucune catégorie commandée.");
    
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
            LIMIT 3;        
            """;
    
            try (Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery()) {
   
           if (resultSet.next()) {
               res = new StringBuilder("Top 3 des catégories les plus commandées :\n");
               int rank = 1;
   
               // Parcourir les résultats
               do {
                   String categorie = resultSet.getString("nomCategorie");
                   int totalQuantite = resultSet.getInt("totalQuantite");
   
                   // Ajouter les détails de la catégorie avec un classement
                   res.append(rank++).append(". ").append(categorie)
                      .append(" (Commandé ").append(totalQuantite).append(" fois)\n");
               } while (resultSet.next());
           }
   
       } catch (SQLException e) {
           e.printStackTrace();
       }
   
       return res.toString();
    }
    

    /*
     * Permet de récupérer les clients qui ont le plus commandés
     */
    public String getTopClientsNbCommandes() {
        StringBuilder res = new StringBuilder("Aucun client trouvé.");
    
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
                if (resultSet.next()) {
                    res = new StringBuilder("Top 5 des clients ayant passé le plus de commandes :\n");
                    int rank = 1;
        
                    do {
                        int idClient = resultSet.getInt("idClient");
                        int nbCommandes = resultSet.getInt("nbCommandes");
        
                        // Charger les informations du client
                        Client client = clientDAO.getClientById(idClient);
        
                        // Ajouter les détails au résultat
                        res.append(rank++).append(". ").append(client.toString())
                           .append(" (Commandes: ").append(nbCommandes).append(")\n");
                    } while (resultSet.next());
                }
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return res.toString();
    }

    /*
     * Permet de récupérer les clients qui ont le plus généré le plus de chiffre d'affaires
     */
    public String getTopsClientsChiffreAffaires() {
        StringBuilder res = new StringBuilder("Aucun client trouvé.");
    
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
                if (resultSet.next()) {
                    res = new StringBuilder("Top 5 des clients ayant généré le plus de chiffre d'affaires :\n");
                    int rank = 1;
        
                    do {
                        int idClient = resultSet.getInt("idClient");
                        double totalCA = resultSet.getDouble("totalCA");
        
                        // Charger les informations du client
                        Client client = clientDAO.getClientById(idClient);
        
                        // Ajouter les détails au résultat
                        res.append(rank++).append(". ").append(client.toString())
                           .append(" (Chiffre d'affaires: ").append(totalCA).append(" euros)\n");
                    } while (resultSet.next());
                }
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return res.toString();
    }
    
    //US 3.2
    // Méthode pour calculer le temps moyen de réalisation des paniers terminés (en heure)
    public double calculerTempsMoyenRealisation() {
        try (Connection connection = DBConnection.getConnection()){
        String queryTempsMoyen = "SELECT AVG(TIMESTAMPDIFF(HOUR, dateDebutPanier, dateFinPanier)) AS tempsMoyen " +
                                 "FROM panier " +
                                 "WHERE panierTermine = TRUE AND dateFinPanier IS NOT NULL";
        try (PreparedStatement pstmtTempsMoyen = connection.prepareStatement(queryTempsMoyen)) {
            try (ResultSet rs = pstmtTempsMoyen.executeQuery()) {
                if (rs.next()) {
                    double tempsMoyenHeures = rs.getDouble("tempsMoyen");
                    return tempsMoyenHeures;
                }
            }
        } } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du temps moyen de réalisation des paniers : " + e.getMessage());
        }
        return 0;
    }
    // Méthode pour calculer le temps moyen de préparation des commandes (en heures)
    public double calculerTempsMoyenPreparation() {
        double tempsMoyen = 0;

        String query = "SELECT AVG(TIMESTAMPDIFF(HOUR, datePreparation, dateFinalisation)) AS temps_moyen_preparation " +
                    "FROM commande WHERE datePreparation IS NOT NULL " +
                    "AND dateFinalisation IS NOT NULL";
        
        try (Connection connection = DBConnection.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                tempsMoyen = rs.getDouble("temps_moyen_preparation");
                System.out.println(tempsMoyen);
            } else {
                System.out.println("Aucune commande préparée trouvée.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du calcul du temps moyen de préparation : " + e.getMessage());
        }

        return tempsMoyen;
    }

    /* 
    public Map<String, String> determineClientProfiles() {
        Map<String, String> clientProfiles = new HashMap<>();
    
        // Query for individual client profiles
        String clientProfileQuery = """
            SELECT c.nomClient, c.prenomClient, p.nomProfil
            FROM client c
            JOIN panier pn ON c.idClient = pn.idClient
            JOIN commande cm ON pn.idPanier = cm.idPanier
            JOIN panier_produit_magasin ppm ON cm.idPanier = ppm.idPanier
            JOIN produit pr ON ppm.idProduit = pr.idProduit
            JOIN Appartenir ap ON pr.idProduit = ap.idProduit
            JOIN categorie cat ON ap.idCategorie = cat.idCategorie
            JOIN client_profil cp ON cp.idClient = c.idClient
            JOIN profil p ON cp.idProfil = p.idProfil
        """;
    
        // Query for profile analytics
        String profileAnalyticsQuery = """
            SELECT 
                p.nomProfil,
                COUNT(DISTINCT c.idClient) AS nombreClients,
                COUNT(DISTINCT cm.idCommande) AS nombreCommandes,
                GROUP_CONCAT(DISTINCT cat.nomCategorie SEPARATOR ', ') AS categoriesAchats
            FROM client c
            JOIN panier pn ON c.idClient = pn.idClient
            JOIN commande cm ON pn.idPanier = cm.idPanier
            JOIN panier_produit_magasin ppm ON cm.idPanier = ppm.idPanier
            JOIN produit pr ON ppm.idProduit = pr.idProduit
            JOIN Appartenir ap ON pr.idProduit = ap.idProduit
            JOIN categorie cat ON ap.idCategorie = cat.idCategorie
            JOIN client_profil cp ON cp.idClient = c.idClient
            JOIN profil p ON cp.idProfil = p.idProfil
            GROUP BY p.nomProfil
            ORDER BY nombreClients DESC
        """;
    
        try (Connection connection = DBConnection.getConnection()) {
    
            // Process individual client profiles
            try (PreparedStatement statement = connection.prepareStatement(clientProfileQuery);
                 ResultSet resultSet = statement.executeQuery()) {
    
                Map<String, Map<String, Integer>> clientProfileCounts = new HashMap<>();
    
                while (resultSet.next()) {
                    String clientName = resultSet.getString("nomClient") + " " + resultSet.getString("prenomClient");
                    String profileName = resultSet.getString("nomProfil");
    
                    clientProfileCounts.putIfAbsent(clientName, new HashMap<>());
                    Map<String, Integer> profileCounts = clientProfileCounts.get(clientName);
    
                    profileCounts.put(profileName, profileCounts.getOrDefault(profileName, 0) + 1);
                }
    
                // Find the most frequent profile for each client
                for (Map.Entry<String, Map<String, Integer>> entry : clientProfileCounts.entrySet()) {
                    String clientName = entry.getKey();
                    Map<String, Integer> profileCounts = entry.getValue();
    
                    String mostFrequentProfile = profileCounts.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse("No Profile");
    
                    clientProfiles.put(clientName, mostFrequentProfile);
                }
            }
    
            // Process profile analytics
            try (PreparedStatement statement = connection.prepareStatement(profileAnalyticsQuery);
                 ResultSet resultSet = statement.executeQuery()) {
    
                System.out.println("Profil Analytics:");
                while (resultSet.next()) {
                    String nomProfil = resultSet.getString("nomProfil");
                    int nombreClients = resultSet.getInt("nombreClients");
                    int nombreCommandes = resultSet.getInt("nombreCommandes");
                    String categoriesAchats = resultSet.getString("categoriesAchats");
    
                    System.out.println("Profil: " + nomProfil);
                    System.out.println("Nombre de clients: " + nombreClients);
                    System.out.println("Nombre de commandes: " + nombreCommandes);
                    System.out.println("Catégories d'achats: " + categoriesAchats);
                    System.out.println("------------");
                }
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la détermination des profils des clients : " + e.getMessage());
        }
    
        return clientProfiles;
    }
    */
    

    
}
