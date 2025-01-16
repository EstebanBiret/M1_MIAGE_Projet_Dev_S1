package src.categorie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import src.DBConnection;
import src.main.AppClient;
import src.panier.Panier;
import src.produit.Produit;
import src.produit.ProduitDAO;

public class CategorieDAO {

    private List<String> categoriesDisponibles;

    public CategorieDAO() {
        categoriesDisponibles = getCategories();
    }

    public List<String> getCategoriesDisponibles() {
        return categoriesDisponibles;
    }

    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        String query = "SELECT nomCategorie FROM categorie";

        try (Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                categories.add(resultSet.getString("nomCategorie"));

            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des catégories : " + e.getMessage());
        }

        return categories;
    }

    //pour chaque catégorie, on créé une option
    public void afficherMenuCategorie() {
        System.out.println("------------------------------------------");
        System.out.println("| ~ Catégories ~                         |");
        System.out.println("|                                        |");
        for (int i = 0; i < categoriesDisponibles.size(); i++) {
            System.out.println("| [" + (i + 1) + "] " + categoriesDisponibles.get(i));
        }
        System.out.println("| [0] Retour au menu principal           |");
        System.out.println("|                                        |");
        System.out.println("------------------------------------------");
    }

    public void gererMenuCategorie(ProduitDAO produitDAO, Scanner scanner, Panier panierClient) {
        int choix = -1;

        while (choix != 0) {
            afficherMenuCategorie();
            System.out.print("Veuillez choisir une catégorie (ou taper 0 pour revenir) : ");
            while (!scanner.hasNextInt()) {
                System.out.print("Entrée invalide. Veuillez entrer un chiffre : ");
                scanner.next();
            }
            choix = scanner.nextInt();
            scanner.nextLine();

            if (choix > 0 && choix <= categoriesDisponibles.size()) {
                String categorieChoisie = categoriesDisponibles.get(choix - 1);
                System.out.println("Vous avez choisi la catégorie : " + categorieChoisie);

                List<Produit> produitsCategorie = produitDAO.produitsParCategorie(categorieChoisie);

                if (!produitsCategorie.isEmpty()) {
                    AppClient.afficherProduitsAvecPagination(produitsCategorie, scanner, panierClient);
                } 
            } else if (choix != 0) {
                System.out.println("Choix invalide. Veuillez réessayer.");
            }
        }
        System.out.println("Retour au menu principal.");
    }
}
