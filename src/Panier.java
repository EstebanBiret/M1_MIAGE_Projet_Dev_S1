package src;

import java.sql.*;

public class Panier {
    
    private int idPanier;
    private int idClient;
    private boolean panierTermine;
    private Timestamp dateDebutPanier;
    private Timestamp dateFinPanier;
    //private List<ArrayList<Integer, Integer, String>> produits;

    //créer un nouveau panier pour le client
    public Panier(int idClient) {
        this.idClient = idClient;
        this.panierTermine = false;
        this.dateDebutPanier = new Timestamp(System.currentTimeMillis());
        this.dateFinPanier = null;

        try (Connection connection = DBConnection.getConnection()) {

            //vérifier si le client n'a pas déjà un panier en cours
            String queryTest = "SELECT * FROM panier WHERE idClient = ? AND panierTermine = false;";
            try (PreparedStatement pstmt = connection.prepareStatement(queryTest)) {
                pstmt.setInt(1, idClient);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Le client a déjà un panier en cours.");
                        return;
                    }
                }
            }

            //création du panier en BD
            String query = "INSERT INTO panier (idClient, panierTermine, dateDebutPanier, dateFinPanier) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, idClient);
                pstmt.setBoolean(2, this.panierTermine);
                pstmt.setTimestamp(3, this.dateDebutPanier);
                pstmt.setTimestamp(4, this.dateFinPanier);

                int rowsAffected = pstmt.executeUpdate();

                //on récupère l'id auto increment de la ligne tout juste générée pour l'attribuer à notre panier java
                if (rowsAffected > 0) {

                    //retourne les clés automatiquement générés (autoincrement dans notre cas), pour assigner l'id à l'objet java
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            this.idPanier = rs.getInt(1);
                            System.out.println("Panier créé avec succès : " + this.toString());
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
    }
    
    //construire le panier en cours d'un client
    public Panier(int idPanier, int idClient, Timestamp dateDebutPanier) {
        this.idPanier = idPanier;
        this.idClient = idClient;
        this.panierTermine = false;
        this.dateDebutPanier = dateDebutPanier;
        this.dateFinPanier = null;
    }

    //getters & setters
    public int getIdPanier() {return idPanier;}
    public int getIdClient() {return idClient;}
    public Client getClient() {
        Client client = new Client(idClient);
        return client;
    }
    public boolean isPanierTermine() {return panierTermine;}
    public void setPanierTermine(boolean panierTermine) {this.panierTermine = panierTermine;}
    public Timestamp getDateDebutPanier() {return dateDebutPanier;}
    public Timestamp getDateFinPanier() {return dateFinPanier;}

    //savoir si le panier recherché existe bien en BD
    public boolean exists() {return this.idPanier != 0;}

    //US 1.1
    public void ajouterProduitPanier(int idProduit, int qte) {

        if(qte < 1) {
            System.out.println("La quantité doit être supérieure à 0.");
            return;
        }

        //regarder si le produit pour le magasin favori n'est pas déjà présent dans le panier, et si oui, juste augmenter la qte
        //TODO

        Client client = getClient();
        int idMagasin = client.getIdMagasinFavori();

        //TODO si produit déjà dans le panier, on modifie juste sa quantité 

        String queryTest = "SELECT quantiteEnStock FROM stocker WHERE idProduit = ? AND idMagasin = ?;";
        String queryInsert = "INSERT INTO panier_produit_magasin (idPanier, idProduit, idMagasin, quantiteVoulue) VALUES (?, ?, ?, ?);";
        String queryUpdate ="UPDATE panier_produit_magasin SET quantiteVoulue = ? WHERE idPanier = ? AND idProduit = ? AND idMagasin = ?;";

        try (Connection connection = DBConnection.getConnection()) {
            // Vérification du stock
            try (PreparedStatement pstmtTest = connection.prepareStatement(queryTest)) {
                pstmtTest.setInt(1, idProduit);
                pstmtTest.setInt(2, idMagasin);
    
                try (ResultSet rs = pstmtTest.executeQuery()) {
                    if (rs.next()) {
                        int stockDisponible = rs.getInt("quantiteEnStock");
    
                        if (stockDisponible < qte) {
                            System.out.println("Quantité insuffisante en stock dans votre magasin favori pour le produit " + idProduit);

                            //on fait appel à l'algorithme de remplacement de produit
                            /*Algorithmes algo = new Algorithmes();
                            int idNewProduit = algo.remplacementProduit(idClient, idProduit, qte);
                            idProduit = idNewProduit;*/

                            //on peut changer idProduit, idMagasin et qte.
                            //TODO + voir si ce produit pour nouveau magasin n'est pas déjà présent dans panier, sinon modif qte


                            
                        }
                    } else {
                        System.out.println("Le produit " + idProduit + " n'est pas disponible dans votre magasin favori.");
                        //on fait appel à l'algorithme de remplacement de produit
                        /*Algorithmes algo = new Algorithmes();
                        int idNewProduit = algo.remplacementProduit(idClient, idProduit, qte);
                        idProduit = idNewProduit;*/

                        //on peut changer idProduit, idMagasin et qte.
                        //TODO + voir si ce produit pour nouveau magasin n'est pas déjà présent dans panier, sinon modif qte
                        
                    }
                }
            }
    
            //ajout du produit au panier
            try (PreparedStatement pstmtInsert = connection.prepareStatement(queryInsert)) {
                pstmtInsert.setInt(1, this.idPanier);
                pstmtInsert.setInt(2, idProduit);
                pstmtInsert.setInt(3, idMagasin);
                pstmtInsert.setInt(4, qte);
    
                int rowsAffected = pstmtInsert.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Produit " + idProduit + " ajouté au panier avec succès (" + qte + " exemplaire.s).");
                } else {
                    System.out.println("Échec de l'ajout du produit au panier.");
                }
            }
            connection.close();
        } 
        catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du produit au panier : " + e.getMessage());
        }
    }   

    //US 1.2
    @Override
    public String toString() {

        String details = "";

        String query1 = """
            SELECT p.idPanier, c.nomClient, c.prenomClient, p.dateDebutPanier
            FROM panier p
            INNER JOIN client c ON p.idClient = c.idClient
            WHERE p.idPanier = ?;
        """;
        
        String query2 = """
            SELECT pr.idProduit, m.nomMagasin, pr.libelleProduit, pr.prixUnitaire, ppm.quantiteVoulue, ppm.modeLivraison
            FROM panier_produit_magasin ppm
            INNER JOIN produit pr ON ppm.idProduit = pr.idProduit
            INNER JOIN magasin m ON ppm.idMagasin = m.idMagasin
            WHERE ppm.idPanier = ?;
        """;
    
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement pstmt1 = connection.prepareStatement(query1);
                PreparedStatement pstmt2 = connection.prepareStatement(query2)) {

            pstmt1.setInt(1, idPanier);
    
            // Exécuter première requête
            try (ResultSet rs1 = pstmt1.executeQuery()) {
                if (rs1.next()) {
                    details += "Détails du panier " + idPanier + "\n";
                    details += "Client : " + rs1.getString("nomClient") + " " + rs1.getString("prenomClient") + "\n";
                    Timestamp dateDebutPanier = rs1.getTimestamp("dateDebutPanier");
                    if (dateDebutPanier != null) {
                        details += "Date Debut : " + dateDebutPanier.toLocalDateTime() + "\n";
                    } else {
                        System.out.println("La date de début du panier est nulle.");
                    }
                } else {
                    System.out.println("Aucun panier trouvé pour l'ID " + idPanier);
                }
            } catch (SQLException e) {
                System.out.println("Erreur lors de l'affichage du panier : " + e.getMessage());
            }
            

            pstmt2.setInt(1, idPanier);
    
            // Exécuter deuxième requête
            try (ResultSet rs2 = pstmt2.executeQuery()) {
                if (!rs2.next()) {
                    details +="Le panier ID " + idPanier + " est vide.";
                } else {
                    details += "Produits dans le panier : ";
                    while (rs2.next()) {
                        int idProduit = rs2.getInt("idProduit");
                        String nomMagasin = rs2.getString("nomMagasin");
                        String libelleProduit = rs2.getString("libelleProduit");
                        double prixUnitaire = rs2.getDouble("prixUnitaire");
                        int quantite = rs2.getInt("quantiteVoulue");
                        String modeLivraison = rs2.getString("modeLivraison");
    
                        details += "\nID : " + idProduit + ", Magasin : " + nomMagasin + ", Libellé : " + libelleProduit + ", Prix : " + prixUnitaire + ", Quantité : " + 
                        quantite + ", Mode de Livraison: " + modeLivraison;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage du panier : " + e.getMessage());
        }
        return details + "\n";
    }

    //US 1.3
    public void validerPanier() {

        if(this.panierTermine) {
            System.out.println("Le panier a déjà été annulé/validé.");
            return;
        }

        //actualiser la quantite en stock.
        //TODO

        //verifier la modeLivraison de chaque produit dans le panier afin de savoir typeCommande.
        //"envoi" ou "retrait" ou "mixt"
        String typeCommade = "";

        try (Connection connection = DBConnection.getConnection()) {
            String query = "Select * FROM produit_panier_magasin Where idmagasin =? And idPnaier = ? And idProduit =?;";
        } catch (SQLException e) {
            System.out.println("Erreur lors de la création du commande : " + e.getMessage());
        }

        //transformer le panier en cours en commande
        Commande commande = new Commande(this.idPanier, null, this.dateFinPanier);

        //puis supprimer le panier en cours du client
        this.panierTermine = true;
    }

    //US 1.4
    public void annulerPanier() {
        if(this.panierTermine) {
            System.out.println("Le panier a déjà été annulé/validé.");
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
                    System.out.println("Ce client n'a pas de panier en cours, ou aucun produit dans ce panier.");
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
                    System.out.println("Le panier en cours du client a bien été annulé.");
                } else {
                    System.out.println("Ce client n'a pas de panier en cours.");
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
        this.panierTermine = true;
    }
}