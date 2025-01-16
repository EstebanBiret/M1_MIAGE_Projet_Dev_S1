package src.main;

import java.util.List;
import java.util.Scanner;

import src.GestionnaireDAO;
import src.categorie.CategorieDAO;
import src.produit.Produit;

public class AppGestion {

    public static void menuGestion() {
        // Création du menu de gestion
        System.out.println("------------------------------------------------");
        System.out.println("| ~ Menu de gestion ~                          |");
        System.out.println("|                                              |");
        System.out.println("| [1] Ajouter un produit au catalogue (saisie) |");
        System.out.println("| [2] Ajouter un produit au catalogue (auto)   |");
        System.out.println("| [3] Augmenter la quantité d'un produit       |");
        System.out.println("| [4] Baisser la quantité d'un produit         |");
        System.out.println("| [5] Voir les statistiques                    |");
        System.out.println("| [6] Profils de consommateurs                 |");
        System.out.println("| [0] Quitter                                  |");
        System.out.println("|                                              |");
        System.out.println("------------------------------------------------");
    }

    public static void menuStats() {
        // Création du menu de stats
        System.out.println("-----------------------------------------------");
        System.out.println("| ~ Menu de stats ~                            |");
        System.out.println("|                                              |");
        System.out.println("| [1] Produits les plus commandés              |");
        System.out.println("| [2] Catégories les plus commandées           |");
        System.out.println("| [3] Clients ayant le plus commandés          |");
        System.out.println("| [4] Clients ayant le plus haut CA            |");
        System.out.println("| [5] Temps moyen de réalisation des paniers   |");
        System.out.println("| [6] Temps moyen de préparation des commandes |");
        System.out.println("| [0] Retour au menu principal                 |");
        System.out.println("|                                              |");
        System.out.println("-----------------------------------------------");
    }

    public static void main(String[] args) {
        GestionnaireDAO marc = new GestionnaireDAO();
        CategorieDAO categorieDAO = new CategorieDAO();

        Scanner scanner = new Scanner(System.in);
        int choix = -1;

        while (choix != 0) {
            menuGestion();
            System.out.print("Veuillez choisir une option : ");
            while (!scanner.hasNextInt()) {
                System.out.print("Entrée invalide. Veuillez entrer un chiffre : ");
                scanner.next();
            }
            choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    System.out.println("Ajouter un produit au catalogue.");

                    System.out.print("Entrez le nom du produit : ");
                    String libelleProduit = scanner.nextLine();
                    while (libelleProduit.isEmpty() || libelleProduit.length() > 128) {
                        System.out.print("Nom invalide (1-128 caractères). Réessayez : ");
                        libelleProduit = scanner.nextLine();
                    }

                    System.out.print("Entrez le prix unitaire : ");
                    while (!scanner.hasNextDouble()) {
                        System.out.print("Entrée invalide. Veuillez entrer un nombre : ");
                        scanner.next();
                    }
                    double prixUnitaire = scanner.nextDouble();
                    scanner.nextLine(); // Consommer la fin de ligne restante

                    System.out.print("Entrez le prix au kilo (optionnel, ou 0 si non applicable) : ");
                    while (!scanner.hasNextDouble()) {
                        System.out.print("Entrée invalide. Veuillez entrer un nombre positif ou 0 : ");
                        scanner.next();
                    }

                    double prixKilo = scanner.nextDouble();
                    scanner.nextLine(); // Consommer la fin de ligne restante

                    if (prixKilo < 0) prixKilo = 0; // Prix non applicable

                    System.out.print("Entrez le nutriscore (A, B, C, D, E, ou autre si non applicable) : ");
                    String nutriscore = scanner.next().toUpperCase();
                    /*while (!nutriscore.matches("[A-E]")) {
                        System.out.print("Nutriscore invalide, veuillez recommencer : ");
                        nutriscore = scanner.next().toUpperCase();
                    }*/

                    System.out.print("Entrez le poids du produit (en kg) : ");
                    while (!scanner.hasNextDouble()) {
                        System.out.print("Entrée invalide. Veuillez entrer un nombre : ");
                        scanner.next();
                    }
                    double poidsProduit = scanner.nextDouble();
                    scanner.nextLine(); // Consommer la fin de ligne restante

                    System.out.print("Entrez le conditionnement du produit : ");
                    String conditionnementProduit = scanner.nextLine();
                    while (conditionnementProduit.isEmpty() || conditionnementProduit.length() > 128) {
                        System.out.print("Conditionnement invalide (1-128 caractères). Réessayez : ");
                        conditionnementProduit = scanner.nextLine();
                    }

                    System.out.print("Entrez la marque du produit : ");
                    String marqueProduit = scanner.nextLine();
                    while (marqueProduit.isEmpty() || marqueProduit.length() > 128) {
                        System.out.print("Marque invalide (1-128 caractères). Réessayez : ");
                        marqueProduit = scanner.nextLine();
                    }

                    Produit produit;
                    // Vérification si le nutriscore est null
                    if(!nutriscore.matches("[A-E]")) {
                        produit = new Produit(libelleProduit, prixUnitaire, prixKilo, 'N', poidsProduit, conditionnementProduit, marqueProduit);
                    }
                    else {
                        produit = new Produit(libelleProduit, prixUnitaire, prixKilo, nutriscore.charAt(0), poidsProduit, conditionnementProduit, marqueProduit);
                    }


                    //affichage des catégories disponibles
                    List<String> categories = categorieDAO.getCategoriesDisponibles();
                    System.out.println("Catégories disponibles :");
                    for (int i = 0; i < categories.size(); i++) {
                        System.out.printf("%d. %s%n", i + 1, categories.get(i));
                    }

                    //ajouter la catégorie au produit
                    System.out.print("Entrez l'ID de la catégorie de ce produit : ");
                    
                    int idCategorie = -1;
                    boolean categorieValide = false;

                    while (!categorieValide) {
                        if (scanner.hasNextInt()) {
                            idCategorie = scanner.nextInt();
                            scanner.nextLine(); // Consommer la fin de ligne restante

                            // Vérification que l'ID est dans les limites valides
                            if (idCategorie >= 1 && idCategorie <= categories.size()) {
                                categorieValide = true;
                            } else {
                                System.out.print("ID de catégorie invalide. Réessayez : ");
                            }
                        } else {
                            // Si l'utilisateur ne tape pas un entier
                            System.out.print("Entrée invalide. Veuillez entrer un chiffre : ");
                            scanner.nextLine(); // Consommer la fin de ligne restante
                        }
                    }

                    scanner.nextLine(); // Consommer la fin de ligne restante
                    marc.ajouterProduitCatalogue(produit, idCategorie);
                    break;

                case 2:
                    marc.importerProduitsDepuisCSV("src/produit/catalogue.csv");
                    break;

                case 3:
                    System.out.print("Entrez l'ID du produit à augmenter : ");
                    while (!scanner.hasNextInt()) {
                        System.out.print("Entrée invalide. Veuillez entrer un chiffre : ");
                        scanner.next();
                    }
                    int idProduitAug = scanner.nextInt();

                    System.out.print("Entrez le magasin concerné : ");
                    while (!scanner.hasNextInt()) {
                        System.out.print("Entrée invalide. Veuillez entrer un chiffre : ");
                        scanner.next();
                    }
                    int magasin = scanner.nextInt();

                    System.out.print("Entrez la quantité à ajouter : ");
                    while (!scanner.hasNextInt()) {
                        System.out.print("Entrée invalide. Veuillez entrer un chiffre : ");
                        scanner.next();
                    }
                    int quantiteAug = scanner.nextInt();

                    marc.majStockProduit(idProduitAug, quantiteAug, magasin, true);
                    break;

                case 4:
                    System.out.print("Entrez l'ID du produit à diminuer : ");
                    while (!scanner.hasNextInt()) {
                        System.out.print("Entrée invalide. Veuillez entrer un chiffre : ");
                        scanner.next();
                    }
                    int idProduitDim = scanner.nextInt();

                    System.out.print("Entrez le magasin concerné : ");
                    while (!scanner.hasNextInt()) {
                        System.out.print("Entrée invalide. Veuillez entrer un chiffre : ");
                        scanner.next();
                    }
                    int magasin2 = scanner.nextInt();

                    System.out.print("Entrez la quantité à retirer : ");
                    while (!scanner.hasNextInt()) {
                        System.out.print("Entrée invalide. Veuillez entrer un chiffre : ");
                        scanner.next();
                    }
                    int quantiteDim = scanner.nextInt();

                    marc.majStockProduit(idProduitDim, quantiteDim, magasin2, false);
                    break;

                case 5:
                    int choixStats = -1;
                    while (choixStats != 0) {
                        menuStats();
                        System.out.print("Veuillez choisir une option de statistiques : ");
                        choixStats = scanner.nextInt();
                        scanner.nextLine();

                        switch (choixStats) {
                            case 1:
                                System.out.println(marc.getProduitPlusCommandes());
                                break;
                            case 2:
                                System.out.println(marc.getTopsCategories());
                                break;
                            case 3:
                                System.out.println(marc.getTopClientsNbCommandes());
                                break;
                            case 4:
                                System.out.println(marc.getTopsClientsChiffreAffaires());
                                break;
                            case 5:
                                System.out.println("Temps moyen de réalisation des paniers : " + marc.calculerTempsMoyenRealisationPaniers() + " heures.");
                                break;
            
                            case 6:
                                System.out.println("Temps moyen de préparation des commandes : " + marc.calculerTempsMoyenPreparationCommandes() + " heures.");
                                break;
                            case 0:
                                System.out.println("Retour au menu principal.");
                                break;
                            default:
                                System.out.println("Option invalide. Veuillez réessayer.");
                        }
                        System.out.println("\n");
                    }
                    break;
                case 6:
                    //TODO
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