package src.main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import src.client.Client;
import src.client.ClientDAO;
import src.panier.Panier;
import src.panier.PanierDAO;
import src.produit.Produit;
import src.produit.ProduitDAO;
import src.categorie.CategorieDAO;

public class AppClient {

    static ClientDAO clientDAO = new ClientDAO();
    static PanierDAO panierDAO = new PanierDAO();
    static ProduitDAO produitDAO = new ProduitDAO();
    static CategorieDAO categorieDAO = new CategorieDAO();

    public static void menuPrincipal(int idClient) {
        String magasinFavori = clientDAO.getMagasinFavori(idClient);
        String prenomClient = clientDAO.getPrenomClient(idClient);
        String nomClient = clientDAO.getNomClient(idClient);

        System.out.println("------------------------------------------");
        System.out.println("| Bonjour " + prenomClient + " " + nomClient + " !");
        System.out.println("| Votre magasin favori : " + magasinFavori);
        System.out.println("|                                        |");
        System.out.println("| [1] Rechercher des produits            |");
        System.out.println("| [2] Afficher et gérer le panier        |");
        System.out.println("| [0] Quitter                            |");
        System.out.println("------------------------------------------");
    }

    public static void menuRecherche() {
        System.out.println("------------------------------------------");
        System.out.println("| ~ Menu de recherche ~                  |");
        System.out.println("| [1] Détails d'un produit               |");
        System.out.println("| [2] Recherche par mot-clé              |");
        System.out.println("| [3] Produits par catégorie             |");
        System.out.println("| [4] Trier les produits                 |");
        System.out.println("| [0] Retour                             |");
        System.out.println("------------------------------------------");
    }

    public static void menuCritères() {
        System.out.println("------------------------------------------");
        System.out.println("| ~ Menu des critères ~                  |");
        System.out.println("| [1] Prix unitaire croissant            |");
        System.out.println("| [2] Ordre alphabétique                 |");
        System.out.println("| [3] Nutriscore                         |");
        System.out.println("| [4] Poids croissant                    |");
        System.out.println("| [0] Retour                             |");
        System.out.println("------------------------------------------");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choix = -1;

        Client client1 = clientDAO.getClientById(1);
        if (client1 == null) {
            System.out.println("Client introuvable !");
            scanner.close();
            return;
        }

        while (choix != 0) {
            menuPrincipal(client1.getIdClient());
            System.out.print("Veuillez choisir une option : ");
            while (!scanner.hasNextInt()) {
                System.out.print("Entrée invalide. Veuillez entrer un chiffre : ");
                scanner.next();
            }
            choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1: // Recherche de produits
                    int choixRecherche = -1;
                    while (choixRecherche != 0) {
                        menuRecherche();
                        System.out.print("Votre choix : ");
                        while (!scanner.hasNextInt()) {
                            System.out.print("Entrée invalide. Veuillez entrer un chiffre : ");
                            scanner.next();
                        }
                        choixRecherche = scanner.nextInt();
                        scanner.nextLine();

                        switch (choixRecherche) {
                            case 1: // Détails d'un produit
                                System.out.print("Entrez l'ID du produit : ");
                                while (!scanner.hasNextInt()) {
                                    System.out.print("Entrée invalide. Veuillez entrer un chiffre : ");
                                    scanner.next();
                                }
                                int idProduit = scanner.nextInt();
                                scanner.nextLine();
                                Produit produit = produitDAO.getProduitById(idProduit);
                                if (produit != null) {
                                    System.out.println(produit);
                                } else {
                                    System.out.println("Produit introuvable.");
                                }
                                break;

                            case 2: // Recherche par mot-clé
                                System.out.print("Entrez un mot-clé : ");
                                String motCle = scanner.nextLine();
                                List<Produit> produits = produitDAO.rechercherProduits(motCle);
                                if (produits.isEmpty()) {
                                    System.out.println("Aucun produit trouvé.");
                                } else {
                                    produits.forEach(System.out::println);
                                }
                                break;

                            case 3: // Produits par catégorie
                                categorieDAO.gererMenuCategorie(produitDAO);
                                break;

                            case 4: // Trier les produits
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
                                        continue;
                                    default:
                                        System.out.println("Option invalide.");
                                        continue;
                                }
                                produitsATrier.forEach(System.out::println);
                                break;

                            case 0:
                                System.out.println("Retour au menu principal.");
                                break;

                            default:
                                System.out.println("Option invalide.");
                        }
                    }
                    break;

                case 2: // Gérer le panier
                    Panier panierClient1 = clientDAO.getPanierEnCours(client1.getIdClient());
                    if (panierClient1 == null) {
                        panierClient1 = client1.creerPanier();
                    }
                    System.out.println(panierDAO.afficherPanier(panierClient1.getIdPanier()));
                    break;

                case 0: // Quitter
                    System.out.println("Fermeture du programme.");
                    break;

                default:
                    System.out.println("Option invalide.");
            }
        }
        scanner.close();
    }
}
