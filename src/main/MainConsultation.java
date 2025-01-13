package src.main;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import src.DBConnection;
import src.Produit;

public class MainConsultation {
    
    //fonction qui retourne la liste des produits de la catégorie fournie en paramètre
    public static List<Produit> produitsParCategorie(String categorie) {
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
    public static List<Produit> getProduitsByLibelle(String libelleProduit, boolean nomExact) {
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
            connection.close();

        } 
        catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        if(produits.isEmpty()){
            if(nomExact) {
                System.out.println("Produit introuvable (" + libelleProduit + ")");
            } else {
                System.out.println("Aucun produit trouvé avec le mot clé " + "'"  + libelleProduit + "'.");
            }
        }
        return produits;
    }

    public static void main(String[] args) {

        /* ----- US 0.1 ----- */
        System.out.println("\n");
        System.out.println("----- US 0.1 -----");
        System.out.println("Visualiser les détails d'un produit par son ID");

        //recherche et affichage d'un produit par son ID
        Produit produitId = new Produit(5);
        if(produitId.exists()) 
        System.out.println("ID 5 --> " + produitId.toString());
        System.out.println("\n");

        //recherche et affichage de produit.s par son libellé

        String libelle = "Jus d'orange";
        System.out.println("Visualiser les détails des " + libelle);
        List<Produit> produitsLibelle = getProduitsByLibelle(libelle, true);
        for (Produit produit : produitsLibelle) {
            System.out.println(produit.toString());
        }
        System.out.println("\n");

        /* ----- US 0.2 ----- */
        System.out.println("----- US 0.2 -----");

        //recherche de produits par mot clé
        String motCle = "Jus d'o";
        System.out.println("Recherche par mot clé : " + motCle);
        List<Produit> produitsMotCle = getProduitsByLibelle(libelle, false);
        for (Produit produit : produitsMotCle) {
            System.out.println(produit.toString());
        }
        System.out.println("\n");

        /* ----- US 0.3 ----- */
        System.out.println("----- US 0.3 -----");
        //consulter la liste des produits par catégorie
        String categorie = "Boissons";
        System.out.println("Recherche par catégorie : " + categorie);
        List<Produit> produitsBoissons = produitsParCategorie(categorie);
        for (Produit produit : produitsBoissons) {
            System.out.println(produit.toString());
        }

        /* ----- 0.4 ----- */
        System.out.println("\n");
        System.out.println("----- US 0.4 -----");

        //tri de la liste des produits par prix, ordre alphabétique, nutriscore et poids
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