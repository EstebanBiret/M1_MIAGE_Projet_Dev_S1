package src.produit;

import src.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO {
    
    //écupérer un produit de la BD avec son ID
    public Produit getProduitById(int idProduit) {
        Produit produit = null;
        String query = "SELECT * FROM produit WHERE idProduit = ?";

        try (Connection connection = DBConnection.getConnection()){
             PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, idProduit);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Vérification si le nutriscore est null
                    String nutriscoreStr = resultSet.getString("nutriscore");
                    Character nutriscore = (nutriscoreStr != null && !nutriscoreStr.isEmpty()) ? nutriscoreStr.charAt(0) : 'N';

                    produit = new Produit(
                            resultSet.getInt("idProduit"),
                            resultSet.getString("libelleProduit"),
                            resultSet.getDouble("prixUnitaire"),
                            resultSet.getDouble("prixKilo"),
                            nutriscore,
                            resultSet.getDouble("poidsProduit"),
                            resultSet.getString("conditionnementProduit"),
                            resultSet.getString("marqueProduit")
                    );
                }
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produit;
    }

    //récupère tous les produits de la BD
    public List<Produit> getAllProduits() {
        List<Produit> produits = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection()) {
            String selectQuery = "SELECT * FROM produit";

            try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
                try (ResultSet rs = pstmt.executeQuery()) {

                    //tant qu'il y a des produits, on les ajoute à la liste
                    while (rs.next()) {
                        //vérifier si le nutriscore est null
                        String nutriscoreStr = rs.getString("nutriscore");
                        Character nutriscore = (nutriscoreStr != null && !nutriscoreStr.isEmpty()) ? nutriscoreStr.charAt(0) : 'N';

                        Produit produit = new Produit(
                            rs.getString("libelleProduit"),
                            rs.getDouble("prixUnitaire"),
                            rs.getDouble("prixKilo"),
                            nutriscore,
                            rs.getDouble("poidsProduit"),
                            rs.getString("conditionnementProduit"),
                            rs.getString("marqueProduit")
                        );
                        produit.setIdProduit(rs.getInt("idProduit"));
                        produits.add(produit);
                    } 
                }
            }
            connection.close();
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        if (produits.isEmpty()) {
            System.out.println("Aucun produit trouvé");
        }
        return produits;
    }

    //retourne la liste des produits de la catégorie fournie en paramètre
    public List<Produit> produitsParCategorie(String categorie) {
        List<Produit> produits = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection()) {
            //rechercher les produits appartenant à la catégorie en paramètre
            String selectQuery = "SELECT p.* FROM produit p, appartenir a, categorie c " 
            + "WHERE p.idProduit = a.idProduit AND a.idCategorie = c.idCategorie AND c.nomCategorie = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
                pstmt.setString(1, categorie);
                try (ResultSet rs = pstmt.executeQuery()) {

                    //tant qu'il y a des produits, on les ajoute à la liste
                    while (rs.next()) {

                        //vérifier si le nutriscore est null
                        String nutriscoreStr = rs.getString("nutriscore");
                        Character nutriscore = (nutriscoreStr != null && !nutriscoreStr.isEmpty()) ? nutriscoreStr.charAt(0) : 'N';

                        Produit produit = new Produit(
                            rs.getString("libelleProduit"),
                            rs.getDouble("prixUnitaire"),
                            rs.getDouble("prixKilo"),
                            nutriscore,
                            rs.getDouble("poidsProduit"),
                            rs.getString("conditionnementProduit"),
                            rs.getString("marqueProduit")
                        );
                        produit.setIdProduit(rs.getInt("idProduit"));
                        produits.add(produit);
                    } 
                }
            }
            connection.close();

        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        if (produits.isEmpty()) {
            System.out.println("Aucun produit trouvé dans cette catégorie (" + categorie + ")");
        }
        return produits;
    }

    //booléen en param pour savoir si on récupère un produit par son nom exact ou mot clé
    public List<Produit> getProduitsByLibelle(String libelleProduit, boolean nomExact) {
        List<Produit> produits = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection()) {
            String selectQuery;
            if(nomExact) {
                selectQuery = "SELECT * FROM produit WHERE libelleProduit = ?";
            } else {
                selectQuery = "SELECT * FROM produit WHERE libelleProduit LIKE ?";
                libelleProduit = "%" + libelleProduit + "%";
            }
            
            try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
                pstmt.setString(1, libelleProduit);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {

                        // Vérification si le nutriscore est null
                        String nutriscoreStr = rs.getString("nutriscore");
                        Character nutriscore = (nutriscoreStr != null && !nutriscoreStr.isEmpty()) ? nutriscoreStr.charAt(0) : 'N';

                        Produit produit = new Produit(
                            rs.getString("libelleProduit"),
                            rs.getDouble("prixUnitaire"),
                            rs.getDouble("prixKilo"),
                            nutriscore,
                            rs.getDouble("poidsProduit"),
                            rs.getString("conditionnementProduit"),
                            rs.getString("marqueProduit")
                        );
                        produit.setIdProduit(rs.getInt("idProduit"));
                        produits.add(produit);
                    } 
                }
            }
            connection.close();

        } 
        catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return produits;
    }

    //récupérer un produit par son nom partiel de marque
    public List<Produit> getProduitsByMarque(String marque) {
        List<Produit> produits = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection()) {
            String selectQuery = "SELECT * FROM produit WHERE marqueProduit LIKE ?";
            marque = "%" + marque + "%";
            
            try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
                pstmt.setString(1, marque);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {

                        // Vérification si le nutriscore est null
                        String nutriscoreStr = rs.getString("nutriscore");
                        Character nutriscore = (nutriscoreStr != null && !nutriscoreStr.isEmpty()) ? nutriscoreStr.charAt(0) : 'N';

                        Produit produit = new Produit(
                            rs.getString("libelleProduit"),
                            rs.getDouble("prixUnitaire"),
                            rs.getDouble("prixKilo"),
                            nutriscore,
                            rs.getDouble("poidsProduit"),
                            rs.getString("conditionnementProduit"),
                            rs.getString("marqueProduit")
                        );
                        produit.setIdProduit(rs.getInt("idProduit"));
                        produits.add(produit);
                    } 
                }
            }
            connection.close();

        } 
        catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return produits;
    }
}