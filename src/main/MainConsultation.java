package src.main;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import src.DBConnection;
import src.produit.Produit;
import src.produit.ProduitDAO;

import java.util.Scanner;

public class MainConsultation {

    public static void main(String[] args) {

        /* ----- US 0.1 ----- */
        System.out.println("\n");
        System.out.println("----- US 0.1 -----");
        System.out.println("Visualiser les détails d'un produit par son ID");

        //recherche et affichage d'un produit par son ID
        //scanner pour choisir l'id du produit
        int idProduit;
        Produit produitId = null;
        ProduitDAO produitDAO = new ProduitDAO();

        //tant que le produit voulu n'existe pas
        try (Scanner scanner = new Scanner(System.in)) {
            while (produitId == null) {
                System.out.print("Veuillez entrer le numéro du produit souhaité : ");
                while (!scanner.hasNextInt()) {
                    System.out.print("Entrée invalide. Veuillez entrer un chiffre : ");
                    scanner.next();
                }
                idProduit = scanner.nextInt();

                // Recherche du produit via le DAO
                produitId = produitDAO.getProduitById(idProduit);

                if (produitId == null) {
                    System.out.println("Produit introuvable avec l'ID : " + idProduit);
                }
            }
        }

        // Affichage des détails du produit
        System.out.println(produitId.toString());
        System.out.println("\n");

        //recherche et affichage de produit.s par son libellé exact
        String libelle = "Jus d'orange";
        System.out.println("Visualiser les détails des " + libelle);
        List<Produit> produitsLibelle = produitDAO.getProduitsByLibelle(libelle, true);
        for (Produit produit : produitsLibelle) {
            System.out.println(produit.toString());
        }
        System.out.println("\n");

        /* ----- US 0.2 ----- */
        System.out.println("----- US 0.2 -----");

        //recherche de produits par mot clé
        String motCle = "Jus d'o";
        System.out.println("Recherche par mot clé : " + motCle);
        List<Produit> produitsMotCle = produitDAO.getProduitsByLibelle(libelle, false);
        for (Produit produit : produitsMotCle) {
            System.out.println(produit.toString());
        }
        System.out.println("\n");

        /* ----- US 0.3 ----- */
        System.out.println("----- US 0.3 -----");
        //consulter la liste des produits par catégorie
        String categorie = "Boissons";
        System.out.println("Recherche par catégorie : " + categorie);
        List<Produit> produitsBoissons = produitDAO.produitsParCategorie(categorie);
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