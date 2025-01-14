package src.main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import src.categorie.CategorieDAO;
import src.produit.Produit;
import src.produit.ProduitDAO;

import java.util.Scanner;

public class AppConsultation {

    public static void menuConsultation() {
        //Création du menu de consultation
        System.out.println("------------------------------------------");
        System.out.println("| ~ Menu de consultation ~               |");
        System.out.println("|                                        |");
        System.out.println("| [1] Détails d'un produit               |");
        System.out.println("| [2] Recherche d'un produit             |");
        System.out.println("| [3] Produits par catégorie             |");
        System.out.println("| [4] Trier les produits                 |");
        System.out.println("| [0] Quitter                            |");
        System.out.println("|                                        |");
        System.out.println("------------------------------------------");
    }

    public static void menuCritères() {
        //Création du menu des critères
        System.out.println("------------------------------------------");
        System.out.println("| ~ Menu des critères ~                  |");
        System.out.println("|                                        |");
        System.out.println("| [1] Prix unitaire croissant            |");
        System.out.println("| [2] Ordre alphabétique                 |");
        System.out.println("| [3] Nutriscore                         |");
        System.out.println("| [4] Poids croissant                    |");
        System.out.println("| [0] Retour au menu principal           |");
        System.out.println("|                                        |");
        System.out.println("------------------------------------------");
    }

    public static void main(String[] args) {

        //variables
        ProduitDAO produitDAO = new ProduitDAO();
        CategorieDAO categorieDAO = new CategorieDAO();
        Scanner scanner = new Scanner(System.in);
        int choix = -1;

        //boucle pour interagir avec le menu tant que l'utilisateur ne quitte pas
        while (choix != 0) {
            menuConsultation();
            System.out.print("Veuillez choisir une option : ");
            while (!scanner.hasNextInt()) {
                System.out.print("Entrée invalide. Veuillez entrer un chiffre : ");
                scanner.next();
            }
            choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    System.out.print("Entrez l'ID du produit : ");

                    //tant que l'utilisateur ne renseigne pas un chiffre
                    while (!scanner.hasNextInt()) {
                        System.out.print("Entrée invalide. Veuillez entrer un chiffre : ");
                        scanner.next();
                    }
                    int idProduit = scanner.nextInt();
                    scanner.nextLine();
                    Produit produit = produitDAO.getProduitById(idProduit);

                    if (produit != null) {
                        System.out.println(produit.toString());
                    } else {
                        System.out.println("Produit introuvable avec l'ID : " + idProduit);
                    }
                    break;

                case 2:
                    System.out.print("Entrez un mot-clé pour rechercher des produits : ");
                    String motCle = scanner.nextLine();

                    //recherche sur libellé et marque
                    List<Produit> produitsMotCleNom = produitDAO.getProduitsByLibelle(motCle, false);
                    List<Produit> produitsMotCleMarque = produitDAO.getProduitsByMarque(motCle);

                    //on fusionne les 2 listes en supprimant les doublons
                    List<Produit> produitsResultats = new ArrayList<>(produitsMotCleNom);
                    for (Produit produitMotCleMarque : produitsMotCleMarque) {
                        if (!produitsResultats.contains(produitMotCleMarque)) {
                            produitsResultats.add(produitMotCleMarque);
                        }
                    }

                    if (produitsResultats.isEmpty()) {
                        System.out.println("Aucun produit trouvé avec le mot-clé : " + motCle);
                    } else {
                        produitsResultats.forEach(System.out::println);
                    }
                    break;

                case 3:
                    categorieDAO.gererMenuCategorie(produitDAO);
                    break;

                case 4:
                    List<Produit> produitsATrier = produitDAO.getAllProduits();
                    menuCritères();
                    int critere = scanner.nextInt();
                    scanner.nextLine();

                    switch (critere) {
                        case 1:
                            produitsATrier.sort(Comparator.comparingDouble(Produit::getPrixUnitaire));
                            break;
                        case 2:
                            produitsATrier.sort(Comparator.comparing(Produit::getLibelleProduit));
                            break;
                        case 3:
                            produitsATrier.sort(Comparator.comparing(Produit::getNutriscore));
                            break;
                        case 4:
                            produitsATrier.sort(Comparator.comparingDouble(Produit::getPoidsProduit));
                            break;
                        case 0:
                            System.out.println("Retour au menu principal.");
                            break;
                        default:
                            System.out.println("Option invalide. Veuillez réessayer.");
                            continue;
                    }
                    if(critere != 0) produitsATrier.forEach(System.out::println);
                    break;

                case 0:
                    System.out.println("Fermeture du menu ...");
                    break;

                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }

            System.out.println("\n");
        }

        scanner.close();
    }
}