package src;

import java.sql.*;

public class Panier {
    
    // Propriétés
    private int idPanier;
    private int idClient;
    private int idMagasin;
    private boolean panierTermine;
    private Date dateDebutPanier;
    private Date dateFinPanier;

    // Constructeur
    public Panier(int idClient, int idMagasin) {

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
        this.idMagasin = idMagasin;
        this.panierTermine = false;
        this.dateDebutPanier = new Date(System.currentTimeMillis());
        this.dateFinPanier = null;

        //créer le panier en BD, et récupérer son ID
        String query = "INSERT INTO panier (idClient, idMagasin, panierTermine, dateDebutPanier, dateFinPanier) VALUES (?, ?, ?, ?, ?);";
        try (Connection connection = DBConnection.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, idClient);
            pstmt.setInt(2, idMagasin);
            pstmt.setBoolean(3, this.panierTermine);
            pstmt.setDate(4, this.dateDebutPanier);
            pstmt.setDate(5, this.dateFinPanier);

            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                this.idPanier = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la création du panier : " + e.getMessage());
        }
    }

    //getters & setters

    public int getIdPanier() {return idPanier;}
    public int getIdClient() {return idClient;}
    public int getIdMagasin() {return idMagasin;}
    public boolean isPanierTermine() {return panierTermine;}
    public void setPanierTermine(boolean panierTermine) {this.panierTermine = panierTermine;}
    public Date getDateDebutPanier() {return dateDebutPanier;}
    public Date getDateFinPanier() {return dateFinPanier;}

    // Savoir si le panier recherché existe bien en BD
    public boolean exists() {return this.idPanier != 0;}

    @Override
    public String toString() {
        return "Panier [id=" + idPanier + ", idClient=" + idClient + ", idMagasin=" + idMagasin
                + ", statut_panier=" + panierTermine + ", date_début=" + dateDebutPanier + ", date_fin=" + dateFinPanier
                + "]";
    }

    // afficher un panier (US 1.2)
    public void afficherPanier() {
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
                System.out.println("Nom Client: " + rs.getString("nomClient") + " " + rs.getString("prenomClient"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage du panier : " + e.getMessage());
        }
    }
}
