package src;
import java.sql.*;


public class Categorie {
    private int idCategorie;
    private String nomCategorie;

    // Constructeur
    public Categorie(int idCategorie, String nomCategorie) {
        this.idCategorie = idCategorie;
        this.nomCategorie = nomCategorie;
        ajouterOuVerifierCategorie();
    }

    // Méthode pour ajouter ou vérifier l'existence d'une catégorie
    public void ajouterOuVerifierCategorie() {
        try (Connection connection = DBConnection.getConnection()) {

            // Vérifier si la catégorie existe déjà
            String queryTest = "SELECT * FROM categorie WHERE idCategorie = ? OR nomCategorie = ?;";
            try (PreparedStatement pstmt = connection.prepareStatement(queryTest)) {
                pstmt.setInt(1, idCategorie);
                pstmt.setString(2, nomCategorie);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("La catégorie existe déjà : " + rs.getString("nomCategorie"));
                        return; // Arrête l'exécution si la catégorie existe déjà
                    }
                }
            }

            // Insérer une nouvelle catégorie dans la base de données
            String queryInsert = "INSERT INTO categorie (idCategorie, nomCategorie) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(queryInsert, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, idCategorie);
                pstmt.setString(2, nomCategorie);

                int rowsAffected = pstmt.executeUpdate();

                // Vérifier si l'insertion a réussi
                if (rowsAffected > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            int generatedId = rs.getInt(1);
                            this.idCategorie = generatedId; // Met à jour l'objet Java avec l'ID généré
                            System.out.println("Nouvelle catégorie créée avec succès : ID = " + this.idCategorie + ", Nom = " + this.nomCategorie);
                        } else {
                            System.out.println("Nouvelle catégorie créée sans ID auto-généré.");
                        }
                    }
                } else {
                    System.out.println("Erreur : La catégorie n'a pas pu être insérée.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la création de la catégorie : " + e.getMessage());
        }
    }

    // Getters et Setters
    public int getIdCategorie() {
        return idCategorie;
    }

    public String getNomCategorie() {
        return nomCategorie;
    }

    public void setIdCategorie(int idCategorie) {
        this.idCategorie = idCategorie;
    }

    public void setNomCategorie(String nomCategorie) {
        this.nomCategorie = nomCategorie;
    }

    // Méthode toString
    @Override
    public String toString() {
        return "Categorie [idCategorie=" + idCategorie + ", nomCategorie=" + nomCategorie + "]";
    }


}
