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
    public Panier(int idClient, int idMagasin, boolean panierTermine, Date dateDebutPanier, Date dateFinPanier) {
        this.idClient = idClient;
        this.idMagasin = idMagasin;
        this.panierTermine = panierTermine;
        this.dateDebutPanier = dateDebutPanier;
        this.dateFinPanier = dateFinPanier;
    }

    // Getter et setter

    public int getIdPanier() {
        return idPanier;
    }

    public int getIdClient() {
        return idClient;
    }

    public int getIdMagasin() {
        return idMagasin;
    }

    public boolean isPanierTermine() {
        return panierTermine;
    }

    public void setPanierTermine(boolean panierTermine) {
        this.panierTermine = panierTermine;
    }

    public Date getDateDebutPanier() {
        return dateDebutPanier;
    }

    public void setDateDebutPanier(Date dateDebutPanier) {
        this.dateDebutPanier = dateDebutPanier;
    }

    public Date getDateFinPanier() {
        return dateFinPanier;
    }

    public void setDateFinPanier(Date dateFinPanier) {
        this.dateFinPanier = dateFinPanier;
    }

    // Méthode afficher un panier [US 1.2]
    public void afficherPanier(int idPanier) {
        String query = """
            SELECT p.idPanier, c.nomClient, c.prenomClient, pr.libelleProduit, pp.quantiteVoulue, pp.modeLivraison
            FROM panier p
            INNER JOIN client c ON p.idClient = c.idClient
            INNER JOIN panier_produit pp ON p.idPanier = pp.idPanier
            INNER JOIN produit pr ON pp.idProduit = pr.idProduit
            WHERE p.idPanier = ?;
        """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
             
            pstmt.setInt(1, idPanier);

            ResultSet rs = pstmt.executeQuery();
            System.out.println("Details of Panier ID: " + idPanier);
            while (rs.next()) {
                System.out.println("Nom Client: " + rs.getString("nomClient") + " " + rs.getString("prenomClient"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage du panier : " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Panier [id=" + idPanier + ", idClient=" + idClient + ", idMagasin=" + idMagasin
                + ", statut_panier=" + panierTermine + ", date_début=" + dateDebutPanier + ", date_fin=" + dateFinPanier
                + "]";
    }
}
