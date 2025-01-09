package src;
import java.sql.*;
import java.util.List;

public class Client {
    private int idClient; //id en BD (auto increment)
    private String nomClient;
    private String prenomClient;
    private String adresseClient;
    private String telClient;
    //private Panier panierEnCours;

    //constructeur avec un paramètre (récupérer un client de la BD avec son ID)
    public Client(int idClient) {
        try (Connection connection = DBConnection.getConnection()) {
            String selectQuery = "SELECT * FROM client WHERE idClient = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {

                pstmt.setInt(1, idClient);

                try (ResultSet rs = pstmt.executeQuery()) {

                    if (rs.next()) {
                        this.idClient = rs.getInt("idClient");
                        this.nomClient = rs.getString("nomClient");
                        this.prenomClient = rs.getString("prenomClient");
                        this.adresseClient = rs.getString("adresseClient");
                        this.telClient = rs.getString("telClient");

                        //on ajoute le panier en cours du client s'il en a un, requête dans la table panier
                        /*String selectPanier = "SELECT * FROM panier WHERE idClient = ? AND panierTermine = false";
                        try (PreparedStatement pstmt2 = connection.prepareStatement(selectPanier)) {
                            pstmt2.setInt(1, idClient);
                            try (ResultSet rs2 = pstmt2.executeQuery()) {
                                if (rs2.next()) {
                                    this.panierEnCours = new Panier(rs2.getInt("idPanier"), idClient, rs2.getInt("idMagasin"), rs2.getDate("dateDebutPanier"));
                                }
                                else this.panierEnCours = null;
                            }
                        }*/
                    } else {
                        System.out.println("Client introuvable (" + idClient + ")");
                    }
                }
            }
        connection.close();

        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    //retourne le panier en cours du client, null si aucun panier en cours
    public Panier getPanierEnCours() {  

        Panier p = null;

        try (Connection connection = DBConnection.getConnection()) {

        String selectPanier = "SELECT * FROM panier WHERE idClient = ? AND panierTermine = false";
        try (PreparedStatement pstmt = connection.prepareStatement(selectPanier)) {
            pstmt.setInt(1, this.idClient);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    p = new Panier(rs.getInt("idPanier"), idClient, rs.getTimestamp("dateDebutPanier"));
                }
            }
        }
        connection.close();

        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return p;
    }

    public Panier creerPanier() {
        //on gère dans la classe panier le cas où le client a déjà un panier en cours
        return new Panier(this.idClient);
    }

    @Override
    public String toString() {
        return "Client [id=" + idClient + ", nom=" + nomClient + ", prenom=" + prenomClient + ", adresse=" + adresseClient + ", tel=" + telClient + "]";
    }
}
