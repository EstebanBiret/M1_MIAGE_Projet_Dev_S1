package src.panier;

import java.sql.*;
import java.util.Scanner;
import src.Algorithmes;
import src.DBConnection;
import src.client.Client;
import src.client.ClientDAO;
import src.produit.ProduitRemplacement;

public class PanierDAO {
    
    public Panier creerPanier(int idClient) {
        //Récupération du panier
        Panier panier = new Panier(idClient);

        try (Connection connection = DBConnection.getConnection()) {

            //vérifier si le client n'a pas déjà un panier en cours
            String queryTest = "SELECT * FROM panier WHERE idClient = ? AND panierTermine = false;";
            try (PreparedStatement pstmt = connection.prepareStatement(queryTest)) {
                pstmt.setInt(1, idClient);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Le client a déjà un panier en cours.");
                        return null;
                    }
                }
            }

            //création du panier en BD
            String query = "INSERT INTO panier (idClient, panierTermine, dateDebutPanier, dateFinPanier) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, idClient);
                pstmt.setBoolean(2, panier.isPanierTermine());
                pstmt.setTimestamp(3, panier.getDateDebutPanier());
                pstmt.setTimestamp(4, panier.getDateFinPanier());

                int rowsAffected = pstmt.executeUpdate();

                //on récupère l'id auto increment de la ligne tout juste générée pour l'attribuer à notre panier java
                if (rowsAffected > 0) {

                    //retourne les clés automatiquement générés (autoincrement dans notre cas), pour assigner l'id à l'objet java
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            panier.setIdPanier(rs.getInt(1));
                        }
                    }
                } else {
                    System.out.println("Aucun panier créé.");
                }
            }
            connection.close();
        } 
        catch (SQLException e) {
            System.out.println("Erreur lors de la création du panier : " + e.getMessage());
        }

        return panier;
    }

    //US 1.1
    public void ajouterProduitPanier(int idPanier, int idClient, int idProduit, int qteVoulue, Scanner scanner) {

        if(idPanier == 0) {
            System.out.println("Le panier n'existe pas.");
            return;
        }
        if(qteVoulue < 1) {
            System.out.println("La quantité doit être supérieure à 0.");
            return;
        }

        //panier en cours du client
        ClientDAO clientDAO = new ClientDAO();
        Client client = clientDAO.getClientById(idClient);
        int idMagasin = client.getIdMagasinFavori();

        //regarder si le produit pour le magasin favori n'est pas déjà présent dans le panier, et si oui, juste augmenter la qte en vérifiant si la qte n'est toujours pas dépassé
        if(checkProduitMagasinDejaPanier(idPanier, idProduit, idMagasin)) {

            //récupérer la quantité déjà en stock dans le panier pour ce produit
            int qtePanier = getQteProduitPanier(idPanier, idProduit, idMagasin);

            //qte bien en stock en prenant en compte la quantité déjà dans le panier pour ce produit
            if(checkQteStockMagasin(idProduit, idMagasin, qtePanier + qteVoulue)) {
                //ajout du produit au panier
                updateProduitPanier(idPanier, idProduit, idMagasin, qteVoulue);
            }
            else { //qte insuffisante, proposer produit de remplacement

                //on fait appel à l'algorithme de remplacement de produit
                ProduitRemplacement produitRemplacement = Algorithmes.remplacementProduit(idProduit, idMagasin, qteVoulue, scanner);
                idProduit = produitRemplacement.getIdProduit();
                idMagasin = produitRemplacement.getIdMagasin();
                qteVoulue = produitRemplacement.getQuantiteChoisie();

                if(checkProduitMagasinDejaPanier(idPanier, idProduit, idMagasin)) {
                    System.out.println("Pas assez de stock pour ce produit de remplacement !");
                    ProduitRemplacement produitRemplacement2 = Algorithmes.remplacementProduit(idProduit, idMagasin, qteVoulue, scanner);
                    idProduit = produitRemplacement2.getIdProduit();
                    idMagasin = produitRemplacement2.getIdMagasin();
                    qteVoulue = produitRemplacement2.getQuantiteChoisie();
                }

                //on insère le nouveau produit dans le panier
                insertProduitPanier(idPanier, idProduit, idMagasin, qteVoulue);
            }

        } else {
            if(checkQteStockMagasin(idProduit, idMagasin, qteVoulue)) {
                //ajout du produit au panier
                insertProduitPanier(idPanier, idProduit, idMagasin, qteVoulue);
            }
            else { //qte insuffisante, proposer produit de remplacement
                //on fait appel à l'algorithme de remplacement de produit
                ProduitRemplacement produitRemplacement = Algorithmes.remplacementProduit(idProduit, idMagasin, qteVoulue, scanner);
                idProduit = produitRemplacement.getIdProduit();
                idMagasin = produitRemplacement.getIdMagasin();
                qteVoulue = produitRemplacement.getQuantiteChoisie();

                if(checkProduitMagasinDejaPanier(idPanier, idProduit, idMagasin)) {

                    //récupérer la quantité déjà en stock dans le panier pour ce produit
                    int qtePanier = getQteProduitPanier(idPanier, idProduit, idMagasin);
        
                    //qte bien en stock en prenant en compte la quantité déjà dans le panier pour ce produit
                    if(checkQteStockMagasin(idProduit, idMagasin, qtePanier + qteVoulue)) {
                        //ajout du produit au panier
                        updateProduitPanier(idPanier, idProduit, idMagasin, qteVoulue);
                    }
                }
                else {
                    //ajout du produit au panier
                    insertProduitPanier(idPanier, idProduit, idMagasin, qteVoulue);
                }
                        
            }
        }
    }

    //méthode pour vérifier si le produit est déjà dans le panier pour ce magasin
    public boolean checkProduitMagasinDejaPanier(int idPanier, int idProduit, int idMagasin) {
        String query = "SELECT * FROM panier_produit_magasin WHERE idPanier = ? AND idProduit = ? AND idMagasin = ?;";

        try (Connection connection = DBConnection.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idPanier);
            pstmt.setInt(2, idProduit);
            pstmt.setInt(3, idMagasin);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification de la présence du produit dans le panier : " + e.getMessage());
        }
        return false;
    }

    public boolean checkQteStockMagasin(int idProduit, int idMagasin, int qte) {
        String queryTest = "SELECT quantiteEnStock FROM stocker WHERE idProduit = ? AND idMagasin = ?;";
        try (Connection connection = DBConnection.getConnection();
            PreparedStatement pstmtTest = connection.prepareStatement(queryTest)) {
            pstmtTest.setInt(1, idProduit);
            pstmtTest.setInt(2, idMagasin);

            try (ResultSet rs = pstmtTest.executeQuery()) {
                if (rs.next()) {
                    int stockDisponible = rs.getInt("quantiteEnStock");

                    if (stockDisponible < qte) {
                        System.out.println("Quantité insuffisante en stock dans votre magasin favori pour le produit " + idProduit);
                        return false;
                    }
                } else {
                    System.out.println("Le produit " + idProduit + " n'est pas disponible dans votre magasin favori.");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification du stock : " + e.getMessage());
            return false;
        }
        return true;
    }

    public int getQteProduitPanier(int idPanier, int idProduit, int idMagasin) {
        String query = "SELECT quantiteVoulue FROM panier_produit_magasin WHERE idPanier = ? AND idProduit = ? AND idMagasin = ?;";

        try (Connection connection = DBConnection.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idPanier);
            pstmt.setInt(2, idProduit);
            pstmt.setInt(3, idMagasin);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantiteVoulue");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de la quantité du produit dans le panier : " + e.getMessage());
        }
        return 0;
    }

    //ajout du produit au panier
    public void insertProduitPanier(int idPanier, int idProduit, int idMagasin, int qte) {
        String queryInsert = "INSERT INTO panier_produit_magasin (idPanier, idProduit, idMagasin, quantiteVoulue) VALUES (?, ?, ?, ?);";
        try (Connection connection = DBConnection.getConnection()) {

            try (PreparedStatement pstmtInsert = connection.prepareStatement(queryInsert)) {
                pstmtInsert.setInt(1, idPanier);
                pstmtInsert.setInt(2, idProduit);
                pstmtInsert.setInt(3, idMagasin);
                pstmtInsert.setInt(4, qte);

                int rowsAffected = pstmtInsert.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Produit " + idProduit + " ajouté au panier (" + qte + " exemplaire.s).");
                } else {
                    System.out.println("Échec de l'ajout du produit au panier.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du produit au panier : " + e.getMessage());
        }
    }

    //modif de la qte du produit dans le panier
    public void updateProduitPanier(int idPanier, int idProduit, int idMagasin, int qte) {
        String queryUpdate = "UPDATE panier_produit_magasin SET quantiteVoulue = quantiteVoulue + ? WHERE idPanier = ? AND idProduit = ? AND idMagasin = ?;";
        try (Connection connection = DBConnection.getConnection()) {

            try (PreparedStatement pstmtInsert = connection.prepareStatement(queryUpdate)) {
                pstmtInsert.setInt(1, qte);
                pstmtInsert.setInt(2, idPanier);
                pstmtInsert.setInt(3, idProduit);
                pstmtInsert.setInt(4, idMagasin);

                int rowsAffected = pstmtInsert.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Modification de la quantité du produit " + idProduit + " dans le panier (+"  + qte + " exemplaire.s).");
                } else {
                    System.out.println("Échec de l'ajout du produit au panier.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du produit au panier : " + e.getMessage());
        }
    }

    //US 1.2
    public String afficherPanier(int idPanier) {

        String details = "Contenu du panier :";

        String panier = """
            SELECT p.idPanier, c.nomClient, c.prenomClient, p.dateDebutPanier
            FROM panier p, client c
            WHERE p.idPanier = ?
            AND c.idClient = p.idClient;
        """;
        
        String produitsPanier = """
            SELECT p.idProduit, m.nomMagasin, p.libelleProduit, p.prixUnitaire, ppm.quantiteVoulue, p.marqueProduit
            FROM panier_produit_magasin ppm, produit p, magasin m
            WHERE ppm.idPanier = ?
            AND p.idProduit = ppm.idProduit
            AND m.idMagasin = ppm.idMagasin;
        """;
    
        try (Connection connection = DBConnection.getConnection();
            PreparedStatement pstmt1 = connection.prepareStatement(panier);
            PreparedStatement pstmt2 = connection.prepareStatement(produitsPanier)) {

            pstmt1.setInt(1, idPanier);
            pstmt2.setInt(1, idPanier);

            try (ResultSet rs1 = pstmt1.executeQuery()) {
                if (!rs1.next()) {
                    System.out.println("Aucn panier trouvé pour l'ID " + idPanier);
                }
            } catch (SQLException e) {
                System.out.println("Erreur lors de l'affichage du panier : " + e.getMessage());
            }
        
            try (ResultSet rs2 = pstmt2.executeQuery()) {
                boolean hasResults = false; // Pour vérifier si des résultats existent

                while (rs2.next()) {
                    hasResults = true;
                    String nomMagasin = rs2.getString("nomMagasin");
                    String libelleProduit = rs2.getString("libelleProduit");
                    double prixUnitaire = rs2.getDouble("prixUnitaire");
                    int quantite = rs2.getInt("quantiteVoulue");
                    String marqueProduit = rs2.getString("marqueProduit");

                    details += "\n" + libelleProduit + ", " + marqueProduit + ", " + nomMagasin + ", " + prixUnitaire + " euros, quantité : " + quantite;
                }

                if (!hasResults) {
                    details = "Votre panier est vide.";
                }

            }
            
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage du panier : " + e.getMessage());
        }
        return details + "\n";
    }
    
    public boolean estVide(int idPanier) {
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT 1 FROM panier_produit_magasin WHERE idPanier = ?";
            try (PreparedStatement pstmtCheckStock = connection.prepareStatement(query)) {
                pstmtCheckStock.setInt(1, idPanier);
                try (ResultSet rs = pstmtCheckStock.executeQuery()) {
                    // Retourne false si au moins un produit est trouvé
                    return !rs.next();
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification du panier : " + e.getMessage());
            return true;  // Par défaut, on considère le panier vide en cas d'erreur
        }
    }    

    //US 1.3
    public void validerPanier(Panier panier,int choix) {
        int idPanier = panier.getIdPanier();

        String deleteProduitPanier = "DELETE FROM panier_produit_magasin WHERE idPanier = ? AND idProduit = ? AND idMagasin = ?;";

        if (panier.isPanierTermine()) {
            System.out.println("Votre panier a déjà été annulé/validé.");
            return;
        }
        if(estVide(idPanier)){
            System.out.println("Votre panier est vide.");
            return;
        }

        String typeCommande;
        if (choix == 1) {
          typeCommande = "envoi";
        } else if (choix == 2) {
          typeCommande = "retrait";
        } else {
          System.out.println("Choix invalide. Opération annulée.");
          return;
        }
    
        try (Connection connection = DBConnection.getConnection()) {
            
            // Vérification des quantités pour chaque produit du panier
            String queryCheckStock = "SELECT ppm.idMagasin,ppm.idProduit, ppm.quantiteVoulue, s.quantiteEnStock " +
                                     "FROM panier_produit_magasin ppm " +
                                     "JOIN stocker s ON ppm.idProduit = s.idProduit AND ppm.idMagasin = s.idMagasin " +
                                     "WHERE ppm.idPanier = ?";
    
            try (PreparedStatement pstmtCheckStock = connection.prepareStatement(queryCheckStock)) {
                pstmtCheckStock.setInt(1, idPanier);
                try (ResultSet rs = pstmtCheckStock.executeQuery()) {
                    
                    while (rs.next()) {
                        int quantiteVoulue = rs.getInt("quantiteVoulue");
                        int quantiteEnStock = rs.getInt("quantiteEnStock");
                        int idProduit = rs.getInt("idProduit");
                        int idMagasin = rs.getInt("idMagasin");
                        if (quantiteVoulue > quantiteEnStock) {
                            System.out.println("Echec de la validation du panier en raison de stock insuffisant du produit ID : " + idProduit + " dans le magasin " + idMagasin);                            
                            
                            //supprimer le produit en question du panier, et proposer un produit de remplacement
                            try (PreparedStatement pstmtDeleteProduit = connection.prepareStatement(deleteProduitPanier)) {
                                pstmtDeleteProduit.setInt(1, idPanier);
                                pstmtDeleteProduit.setInt(2, idProduit);
                                pstmtDeleteProduit.setInt(3, idMagasin);
                                pstmtDeleteProduit.executeUpdate();

                                //proposer un nouveau produit
                                Scanner scanner = new Scanner(System.in);
                                ProduitRemplacement produitRemplacement = Algorithmes.remplacementProduit(idProduit, idMagasin, quantiteVoulue, scanner);
                                idProduit = produitRemplacement.getIdProduit();
                                idMagasin = produitRemplacement.getIdMagasin();
                                quantiteVoulue = produitRemplacement.getQuantiteChoisie();

                                if(checkProduitMagasinDejaPanier(idPanier, idProduit, idMagasin)) {

                                    //récupérer la quantité déjà en stock dans le panier pour ce produit
                                    int qtePanier = getQteProduitPanier(idPanier, idProduit, idMagasin);
                        
                                    //qte bien en stock en prenant en compte la quantité déjà dans le panier pour ce produit
                                    if(checkQteStockMagasin(idProduit, idMagasin, qtePanier + quantiteVoulue)) {
                                        //ajout du produit au panier
                                        updateProduitPanier(idPanier, idProduit, idMagasin, quantiteVoulue);
                                    }
                                }
                                else {
                                    //ajout du produit au panier
                                    insertProduitPanier(idPanier, idProduit, idMagasin, quantiteVoulue);
                                }
                            }
                            return;
                        }
                    }
                    
                }
            }
    
            // Mise à jour des quantités en stock
            String queryStockUpdate = "UPDATE stocker s " +
                                       "JOIN panier_produit_magasin ppm ON s.idProduit = ppm.idProduit AND s.idMagasin = ppm.idMagasin " +
                                       "SET s.quantiteEnStock = s.quantiteEnStock - ppm.quantiteVoulue " +
                                       "WHERE ppm.idPanier = ?";
    
            try (PreparedStatement pstmtStockUpdate = connection.prepareStatement(queryStockUpdate)) {
                pstmtStockUpdate.setInt(1, idPanier);
                pstmtStockUpdate.executeUpdate();
            }
    
            // Insertion de la commande dans la base de données
            Timestamp now = new Timestamp(System.currentTimeMillis());
            String insertCommandeQuery = "INSERT INTO commande (idPanier, typeCommande, statutCommande, dateReception) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmtInsertCommande = connection.prepareStatement(insertCommandeQuery, Statement.RETURN_GENERATED_KEYS)) {
                pstmtInsertCommande.setInt(1, idPanier);
                pstmtInsertCommande.setString(2, typeCommande);
                pstmtInsertCommande.setString(3, "en attente");
                pstmtInsertCommande.setTimestamp(4, now);
    
                int rowsInserted = pstmtInsertCommande.executeUpdate();
                if (rowsInserted > 0) {
                    try (ResultSet rs = pstmtInsertCommande.getGeneratedKeys()) {
                        if (rs.next()) {
                            int idCommande = rs.getInt(1);
                        }
                    }
                } else {
                    System.out.println("Échec de la création de la commande.");
                    connection.rollback();
                    return;
                }
            }
    
            // Marquer le panier comme terminé
            String queryUpdatePanier = "UPDATE panier SET panierTermine = true, dateFinPanier = ? WHERE idPanier = ?";
            try (PreparedStatement pstmtUpdatePanier = connection.prepareStatement(queryUpdatePanier)) {
                pstmtUpdatePanier.setTimestamp(1, now);
                pstmtUpdatePanier.setInt(2, idPanier);
                pstmtUpdatePanier.executeUpdate();
                panier.setPanierTermine(true);
                panier.setDateFinPanier(now);
                System.out.println("Le panier a été validé !");
            }
    
        } catch (SQLException e) {
            System.out.println("Erreur lors de la validation du panier : " + e.getMessage());
        }
    }
    
    

    //US 1.4
    public void annulerPanier(Panier panier) {
        int idPanier = panier.getIdPanier();
        int idClient = panier.getIdClient();

        if(panier.isPanierTermine()) {
            System.out.println("Le panier a déjà été annulé/validé.");
            return;
        }

        if(estVide(idPanier)) {
            System.out.println("Votre panier est vide.");
            return;
        }

        //supprimer dans la BD le panier en cours du client
        try (Connection connection = DBConnection.getConnection()){

            //on supprime les produits du panier
            String supprPanierProduits = "DELETE FROM panier_produit_magasin WHERE idPanier = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(supprPanierProduits)) {
                pstmt.setInt(1, idPanier);

                //exécution de la requête
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Les produits du panier ont bien été supprimés.");
                } else {
                    System.out.println("Ce client n'a pas de panier en cours.");
                }
            } catch (SQLException e) {
                System.out.println("Erreur lors de la suppression : " + e.getMessage());
            }

            //et on supprime le panier en cours
            String supprPanier = "DELETE FROM panier WHERE idClient = ? AND panierTermine = false;";

            try (PreparedStatement pstmt = connection.prepareStatement(supprPanier)) {
                pstmt.setInt(1, idClient);

                //exécution de la requête
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Le panier en cours a bien été annulé.");
                } else {
                    System.out.println("Vous n'a pas de panier en cours.");
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
        //enfin, on marque le panier actuel comme terminé
        panier.setPanierTermine(true);
    }
}
