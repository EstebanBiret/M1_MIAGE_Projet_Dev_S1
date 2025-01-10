package src.main;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import src.DBConnection;
import src.Panier;
import src.Produit;

public class MainConsultation {
    
    public static List<Produit> produitsParCategorie(String categorie) {
        List<Produit> produits = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection()) {
            //rechercher les produits appartenant à la catégorie en paramètre
            String selectQuery = "SELECT p.* FROM produit p, appartenir a, categorie c " 
            + "WHERE p.idProduit = a.idProduit AND a.idCategorie = c.idCategorie AND c.nomCategorie = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
                pstmt.setString(1, categorie);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        // Créer un produit pour chaque ligne de résultat
                        Produit produit = new Produit(
                            rs.getString("libelleProduit"),
                            rs.getDouble("prixUnitaire"),
                            rs.getDouble("prixKilo"),
                            rs.getString("nutriscore").charAt(0),
                            rs.getDouble("poidsProduit"),
                            rs.getString("conditionnementProduit"),
                            rs.getString("marqueProduit")
                        );
                        produit.setIdProduit(rs.getInt("idProduit"));
                        produits.add(produit);
                    } 
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        if (produits.isEmpty()) {
            System.out.println("Aucun produit trouvé dans cette catégorie (" + categorie + ")");
        }
        return produits;
    }

    public static void main(String[] args) {

        /* ----- US 0.1 ----- */
        System.out.println("----- US 0.1 -----");
        System.out.println("Visualiser les détails d'un produit par son ID et son libellé");

        //recherche et affichage d'un produit par son ID
        Produit produitId = new Produit(5);
        if(produitId.exists()) 
        System.out.println("ID 5 --> " + produitId.toString());

        //recherche et affichage d'un produit par son libellé
        Produit produitLibelle = new Produit("Jus d'orange", true);
        if(produitLibelle.exists()) System.out.println("Jus d'orange --> " + produitLibelle.toString() + '\n');

        /* ----- US 0.2 ----- */
        System.out.println("----- US 0.2 -----");

        //recherche d'un produit par mot clé
        System.out.println("Recherche par mot clé : Jus d'o");
        Produit produitMotCle = new Produit("Jus d'o", false);
        if(produitMotCle.exists()) System.out.println(produitMotCle.toString() + "\n");

        /* ----- US 0.3 ----- */
        System.out.println("----- US 0.3 -----");
        //consulter la liste des produits par catégorie
        System.out.println("Recherche par catégorie : Boissons");
        List<Produit> produitsBoissons = produitsParCategorie("Boissons");
        for (Produit produit : produitsBoissons) {
            System.out.println(produit.toString());
        }
        System.out.println("\n");

        /* ----- 0.4 ----- */
        System.out.println("----- US 0.4 -----");
        // Trier les produits par prix, alphabétique, nutriscore et poids

        produitsBoissons.sort(Comparator.comparingDouble(Produit::getPrixUnitaire));
        System.out.println("Tri par prix unitaire croissant :");
        produitsBoissons.forEach(System.out::println);

        produitsBoissons.sort(Comparator.comparing(Produit::getLibelleProduit));
        System.out.println("\nTri alphabétique par libellé :");
        produitsBoissons.forEach(System.out::println);

        produitsBoissons.sort(Comparator.comparing(Produit::getNutriscore));
        System.out.println("\nTri par nutriscore :");
        produitsBoissons.forEach(System.out::println);

        produitsBoissons.sort(Comparator.comparingDouble(Produit::getPoidsProduit));
        System.out.println("\nTri par poids croissant :");
        produitsBoissons.forEach(System.out::println);
        System.out.println("\n");

    }
}
