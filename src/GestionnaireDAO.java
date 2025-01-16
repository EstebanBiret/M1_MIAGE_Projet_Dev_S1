package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import src.client.Client;
import src.client.ClientDAO;
import src.produit.Produit;
import src.produit.ProduitDAO;

public class GestionnaireDAO {

    ClientDAO clientDAO = new ClientDAO();
    ProduitDAO produitDAO = new ProduitDAO();

    /*
     * Permet de sauvegarder en BD un nouveau produit
     */
    public void ajouterProduitCatalogue(Produit p, int idCategorie) {
        try (Connection connection = DBConnection.getConnection()) {

            //vérifier qu'un produit n'existe pas déjà avec ces paramètres exacts
            String selectQuery = """
            SELECT * FROM produit WHERE libelleProduit = ?
            AND prixUnitaire = ? AND prixKilo = ? AND poidsProduit = ? AND conditionnementProduit = ? AND marqueProduit = ?
            """;

            try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
                pstmt.setString(1, p.getLibelleProduit());
                pstmt.setDouble(2, p.getPrixUnitaire());
                pstmt.setDouble(3, p.getPrixKilo());
                pstmt.setDouble(4, p.getPoidsProduit());
                pstmt.setString(5, p.getConditionnementProduit());
                pstmt.setString(6, p.getMarqueProduit());

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Un produit avec ces paramètres existe déjà en base de données." + p.toString());
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
                if(p.getNutriscore() != 'A' && p.getNutriscore() != 'B' && p.getNutriscore() != 'C' && p.getNutriscore() != 'D' && p.getNutriscore() != 'E') {
                    pstmt.setNull(4, Types.CHAR);
                } else {
                    pstmt.setString(4, String.valueOf(p.getNutriscore()));
                }   
                pstmt.setDouble(5, p.getPoidsProduit());
                pstmt.setString(6, p.getConditionnementProduit());
                pstmt.setString(7, p.getMarqueProduit());

                int rowsAffected = pstmt.executeUpdate();

                //on récupère l'id auto increment de la ligne tout juste générée pour l'attribuer à l'id du produit
                if (rowsAffected > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            p.setIdProduit(rs.getInt(1));
                            System.out.println("Produit ajouté avec succès " + p.toString());
                        }
                    }
                } else {
                    System.out.println("Aucun produit ajouté.");
                }

            } catch (SQLException e) {
                System.out.println("Erreur lors de l'ajout : " + e.getMessage());
            }

            //liaison du produit à la catégorie
            String insertCategorie = "INSERT INTO appartenir (idCategorie, idProduit) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertCategorie)) {
                pstmt.setInt(1, idCategorie); 
                pstmt.setInt(2, p.getIdProduit());

                //exécution de la requête
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected <= 0) { 
                    System.out.println("Aucune liaison produit - catégorie ajoutée.");
                } 

            } catch (SQLException e) {
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
    /*public void majProduitCatalogue(Produit p) {
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
                System.out.println("Erreur lors de la modification : " + e.getMessage());
            }
            connection.close();
            
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }*/

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

            //récupérer la quantité actuelle en stock
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
    
        int rang = 1;

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
                        res.append(rang).append(". ")
                           .append(produit.toString())
                           .append(" (Commandé ").append(totalQuantite).append(" fois)\n");
                    } else {
                        res.append(rang).append(". Produit inconnu (ID: ")
                           .append(idProduit).append(") - ")
                           .append(totalQuantite).append(" fois commandé\n");
                    }
        
                    rang++;
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
   
           
                res = new StringBuilder("Top 3 des catégories les plus commandées :\n");
                int rank = 1;
   
                while (resultSet.next()) {
                    String categorie = resultSet.getString("nomCategorie");
                    int totalQuantite = resultSet.getInt("totalQuantite");
                
                    // Ajouter les détails de la catégorie avec un classement
                    res.append(rank++).append(". ").append(categorie)
                    .append(" (Commandé ").append(totalQuantite).append(" fois)\n");
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
            ORDER BY nbCommandes DESC
            LIMIT 5
        """;
    
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
    
            // Exécute la requête pour récupérer les clients ayant le plus commandé
            try (ResultSet resultSet = statement.executeQuery()) {
                res = new StringBuilder("Top 5 des clients ayant passé le plus de commandes :\n");
                int rank = 1;
    
                while (resultSet.next()) {
                    int idClient = resultSet.getInt("idClient");
                    int nbCommandes = resultSet.getInt("nbCommandes");
                    Client client = clientDAO.getClientById(idClient);
    
                    //ajouter les détails au résultat
                    res.append(rank++).append(". ").append(client.toString())
                        .append(" (Commandes: ").append(nbCommandes).append(")\n");
                }
            }
            connection.close();
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
            ORDER BY totalCA DESC
            LIMIT 5
        """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
    
            try (ResultSet resultSet = statement.executeQuery()) {
                res = new StringBuilder("Top 5 des clients ayant généré le plus de chiffre d'affaires :\n");
                int rank = 1;
    
                while (resultSet.next()) {
                    int idClient = resultSet.getInt("idClient");
                    double totalCA = resultSet.getDouble("totalCA");
                    Client client = clientDAO.getClientById(idClient);
    
                    //ajouter les détails au résultat
                    res.append(rank++).append(". ").append(client.toString())
                        .append(" (Chiffre d'affaires: ").append(totalCA).append(" euros)\n");
                } 
                
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res.toString();
    }
    
    //calculer le temps moyen de réalisation des paniers terminés (en heures)
    public double calculerTempsMoyenRealisationPaniers() {
        try (Connection connection = DBConnection.getConnection()){
            String queryTempsMoyen = "SELECT AVG(TIMESTAMPDIFF(HOUR, dateDebutPanier, dateFinPanier)) AS tempsMoyen " +
                                    "FROM panier " +
                                    "WHERE panierTermine = TRUE AND dateFinPanier IS NOT NULL";
            try (PreparedStatement pstmtTempsMoyen = connection.prepareStatement(queryTempsMoyen)) {
                try (ResultSet rs = pstmtTempsMoyen.executeQuery()) {
                    if (rs.next()) {
                        double tempsMoyenHeures = rs.getDouble("tempsMoyen");
                        //on arrondit
                        return Math.round(tempsMoyenHeures * 10.0) / 10.0;
                    }
                }
            } 
            connection.close();
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du temps moyen de réalisation des paniers : " + e.getMessage());
        }
        return 0;
    }

    //calculer le temps moyen de préparation des commandes (en heures)
    public double calculerTempsMoyenPreparationCommandes() {
        double tempsMoyen = 0;

        String query = "SELECT AVG(TIMESTAMPDIFF(HOUR, datePreparation, dateFinalisation)) AS temps_moyen_preparation " +
                    "FROM commande WHERE datePreparation IS NOT NULL " +
                    "AND dateFinalisation IS NOT NULL";
        
        try (Connection connection = DBConnection.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                tempsMoyen = rs.getDouble("temps_moyen_preparation");
                //on arrondit
                return Math.round(tempsMoyen * 10.0) / 10.0;            
            } 
            connection.close();
        } catch (SQLException e) {
            System.out.println("Erreur lors du calcul du temps moyen de préparation : " + e.getMessage());
        }
        return tempsMoyen;
    }

    //importer des produits dans le catalogue depuis un fichier CSV
    public void importerProduitsDepuisCSV(String cheminFichier) {
        String ligne;

        try (BufferedReader br = new BufferedReader(new FileReader(cheminFichier))) {
            br.readLine();

            while ((ligne = br.readLine()) != null) {
                String[] valeurs = ligne.split(";");

                //vérifier que toutes les infos sont mises
                if (valeurs.length == 8) { 
                    String libelleProduit = valeurs[0].trim();
                    double prixUnitaire = Double.parseDouble(valeurs[1].trim());
                    double prixKilo = Double.parseDouble(valeurs[2].trim());
                    char nutriscore = valeurs[3].trim().charAt(0);
                    double poidsProduit = Double.parseDouble(valeurs[4].trim());
                    String conditionnementProduit = valeurs[5].trim();
                    String marqueProduit = valeurs[6].trim();
                    int idCategorie = Integer.parseInt(valeurs[7].trim());
                    Produit produit = new Produit(libelleProduit, prixUnitaire, prixKilo, nutriscore, poidsProduit, conditionnementProduit, marqueProduit);
                    ajouterProduitCatalogue(produit, idCategorie);
                } else {
                    System.err.println("Format incorrect pour la ligne : " + ligne);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la lecture du fichier CSV : " + e.getMessage());
            e.printStackTrace();
        }
    }

    //afficher les profils avec le nb de clients
    public void analyserProfilsClients() {
        String requeteCategoriesClients = """
            SELECT c.idClient, c.nomClient, c.prenomClient, cat.nomCategorie, COUNT(pp.idProduit) AS productCount
            FROM CLIENT c
            JOIN PANIER p ON c.idClient = p.idClient
            JOIN panier_produit_magasin pp ON p.idPanier = pp.idPanier
            JOIN PRODUIT pr ON pp.idProduit = pr.idProduit
            JOIN Appartenir ap ON pr.idProduit = ap.idProduit
            JOIN CATEGORIE cat ON ap.idCategorie = cat.idCategorie
            GROUP BY c.idClient, c.nomClient, c.prenomClient, cat.nomCategorie
            ORDER BY c.idClient, productCount DESC;
        """;

        try (Connection connnexion = DBConnection.getConnection();
            PreparedStatement stmt = connnexion.prepareStatement(requeteCategoriesClients)) {

            ResultSet rs = stmt.executeQuery();
            Map<Integer, Map<String, Integer>> comptageCategoriesClients = new HashMap<>();

            //agréger le comptage des catégories pour chaque client
            while (rs.next()) {
                int idClient = rs.getInt("idClient");
                String categorie = rs.getString("nomCategorie");
                int nombreProduits = rs.getInt("productCount");

                comptageCategoriesClients
                    .computeIfAbsent(idClient, k -> new HashMap<>())
                    .merge(categorie, nombreProduits, Integer::sum);
            }

            //trouver les profils avec le nombre de clients
            Map<String, Integer> comptageCategories = new HashMap<>();
            for (Map.Entry<Integer, Map<String, Integer>> entry : comptageCategoriesClients.entrySet()) {
                Map<String, Integer> comptageCategoriesClient = entry.getValue();

                int totalProduits = comptageCategoriesClient.values().stream().mapToInt(Integer::intValue).sum();

                //vérifier si le client a une catégorie dominante
                for (Map.Entry<String, Integer> categorieEntry : comptageCategoriesClient.entrySet()) {
                    String categorie = categorieEntry.getKey();
                    int nombreProduits = categorieEntry.getValue();

                    //si 40% ou plus des produits du client sont dans cette catégorie
                    if (nombreProduits * 100 / totalProduits >= 40) {
                        comptageCategories.put(categorie, comptageCategories.getOrDefault(categorie, 0) + 1);
                    }
                }
            }

            //afficher les profils avec le nombre de clients
            System.out.println("\n Profils de consommateurs :");
            for (Map.Entry<String, Integer> entry : comptageCategories.entrySet()) {
                String categorie = entry.getKey();
                int nbClients = entry.getValue();
                System.out.println(categorie + " (" + nbClients + " clients)");
            }
            connnexion.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Une erreur est survenue lors de l'analyse des profils des clients.");
        }
    }
}   