package src.main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import src.client.Client;
import src.client.ClientDAO;
import src.commande.Commande;
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

    public static void afficherMenuPrincipal(String prenom, String nom, String magasinFavori) {
        System.out.println("----------------------------------------------------");
        System.out.println("| Bonjour " + prenom + " " + nom + " !                          |");
        System.out.println("| Votre magasin favori : " + magasinFavori + " |");
        System.out.println("|                                                  |");
        System.out.println("| [1] Recherche de produits                        |");
        System.out.println("| [2] Visualiser et gérer le panier                |");
        System.out.println("| [3] Tableau de bord                              |");
        System.out.println("| [0] Quitter                                      |");
        System.out.println("----------------------------------------------------");
    }

    public static void afficherMenuRecherche() {
        System.out.println("-----------------------------------");
        System.out.println("| ~ Recherche de produits ~       |");
        System.out.println("|                                 |");
        System.out.println("| [1] Voir tout le catalogue      |");
        System.out.println("| [2] Mot-clé (nom ou catégorie ) |");
        System.out.println("| [3] Catégorie                   |");
        System.out.println("| [4] Tri par critères            |");
        System.out.println("| [0] Retour                      |");
        System.out.println("-----------------------------------");
    }

    public static void afficherMenuTri() {
        System.out.println("-------------------------------");
        System.out.println("| ~ Critères de tri ~         |");
        System.out.println("|                             |");
        System.out.println("| [1] Prix unitaire croissant |");
        System.out.println("| [2] Ordre alphabétique      |");
        System.out.println("| [3] Nutriscore              |");
        System.out.println("| [4] Poids croissant         |");
        System.out.println("| [0] Retour                  |");
        System.out.println("-------------------------------");
    }

    public static void afficherMenuPanier(Panier panier, Scanner scanner) {
        boolean continuer = true;

        while (continuer) {
            System.out.println("---------------------------");
            System.out.println("| ~ Gestion du panier ~   |");
            System.out.println("|                         |");
            System.out.println("| [1] Afficher le panier  |");
            System.out.println("| [2] Valider le panier   |");
            System.out.println("| [3] Annuler le panier   |");
            System.out.println("| [0] Retour              |");
            System.out.println("---------------------------");
            System.out.print("Votre choix : ");
            String choix = scanner.nextLine();

            switch (choix) {
                case "1":
                    System.out.println(panierDAO.afficherPanier(panier.getIdPanier()));
                    break;
                case "2":
                    //1 = retrait, 2 = livraison
                    int typeCommande = panierDAO.choisirModeRapide(panier.getIdPanier(), panier.getClient());

                    if(typeCommande != 0) {
                        System.out.println("Voulez-vous choisir ce mode ?");
                        System.out.println("[1] Oui  [2] Non");

                        String choixMode = scanner.nextLine();
                        //tant que choix incorrect
                        while (!choixMode.equals("1") && !choixMode.equals("2")) {
                            System.out.println("Veuillez taper 1 ou 2.");
                            choixMode = scanner.nextLine();
                        }
                        if (choixMode.equals("1")) {
                            panierDAO.validerPanier(panier, typeCommande);
                        } else {
                            int modeInverse = typeCommande == 1 ? 2 : 1;
                            panierDAO.validerPanier(panier, modeInverse);
                        }                                  
                    } 
                    else {
                        System.out.println(typeCommande);
                        System.out.println("Votre panier est vide.");
                    }
                    continuer = false;
                    break;
                case "3":
                    panierDAO.annulerPanier(panier);
                    continuer = false;
                    break;
                case "0":
                    continuer = false;
                    break;
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    public static void afficherProduitsAvecPagination(List<Produit> produits, Scanner scanner, Panier panierClient) {

        if(produits.isEmpty()) {
            System.out.println("Aucun produit trouvé.");
            return;
        }

        boolean continuer = true;
        int pageIndex = 0;
        int produitsParPage = 5;
        int totalPages = (int) Math.ceil((double) produits.size() / produitsParPage);

        while (continuer) {
            System.out.println("\n--- Page " + (pageIndex + 1) + "/" + totalPages + " ---");
            int start = pageIndex * produitsParPage;
            int end = Math.min(start + produitsParPage, produits.size());

            for (int i = start; i < end; i++) {
                Produit produit = produits.get(i);
                System.out.println((i + 1) + ". " + produit.getLibelleProduit() + " - " + produit.getMarqueProduit()
                        + " - Nutriscore " + produit.getNutriscore() + " - " + produit.getPrixUnitaire() + " euros - " + produit.getPoidsProduit() + "kg - " + produit.getConditionnementProduit());
            }

            System.out.println("\nOptions :");
            System.out.println("[s] Page suivante");
            System.out.println("[p] Page précédente");
            System.out.println("[v <numéroProduit>] Voir les détails d'un produit");
            System.out.println("[r] Retour");
            System.out.print("Votre choix : ");
            String choixUtilisateur = scanner.nextLine();

            switch (choixUtilisateur) {
                case "s":
                    if (pageIndex < totalPages - 1) {
                        pageIndex++;
                    } else {
                        System.out.println("Vous êtes déjà sur la dernière page.");
                    }
                    break;
                case "p":
                    if (pageIndex > 0) {
                        pageIndex--;
                    } else {
                        System.out.println("Vous êtes déjà sur la première page.");
                    }
                    break;
                case "r":
                    continuer = false;
                    break;
                default:
                    if (choixUtilisateur.startsWith("v")) {
                        try {
                            int numeroProduit = Integer.parseInt(choixUtilisateur.substring(2).trim()) - 1;
                            if (numeroProduit >= start && numeroProduit < end) {
                                Produit produit = produits.get(numeroProduit);
                                System.out.println("\nDétails du produit :");
                                System.out.println(produit);

                                System.out.println("\nOptions :");
                                System.out.println("[a] Ajouter au panier");
                                System.out.println("[r] Retour");
                                System.out.print("Votre choix : ");
                                String choixProduit = scanner.nextLine();

                                if (choixProduit.equals("a")) {
                                    System.out.print("Entrez la quantité : ");
                                    while (!scanner.hasNextInt()) {
                                        System.out.print("Entrée invalide. Veuillez entrer un chiffre : ");
                                        scanner.next();
                                    }
                                    int quantite = scanner.nextInt();
                                    scanner.nextLine();

                                    panierDAO.ajouterProduitPanier(
                                            panierClient.getIdPanier(),
                                            panierClient.getIdClient(),
                                            produit.getIdProduit(),
                                            quantite,
                                            scanner);
                                    //scanner.nextLine();
                                }
                            } else {
                                System.out.println("Numéro de produit invalide.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Format invalide. Utilisez 'v <numéro>'.");
                        }
                    } else {
                        System.out.println("Choix invalide.");
                    }
                    break;
            }
        }
    }

    public static void afficherMenuTableauDeBord(Client client, Scanner scanner) {
        boolean continuer = true;
    
        while (continuer) {
            System.out.println("-----------------------------------");
            System.out.println("| ~ Tableau de bord ~             |");
            System.out.println("|                                 |");
            System.out.println("| [1] Produits les plus commandés |");
            System.out.println("| [2] Habitudes de consommation   |");
            System.out.println("| [3] Mes commandes               |");
            System.out.println("| [0] Retour                      |");
            System.out.println("-----------------------------------");
            System.out.print("Votre choix : ");
            String choix = scanner.nextLine();
    
            switch (choix) {
                case "1":
                    System.out.println("\nProduits les plus commandés :\n");
                    List<String> produits = clientDAO.getProduitsPlusCommandes(client.getIdClient());
                    if (produits.isEmpty()) {
                        System.out.println("Aucun produit trouvé.");
                    } else {
                        for (String produit : produits) {
                            System.out.println(produit);
                        }
                    }
                    break;
                case "2":
                    System.out.println("\nHabitudes de consommation :");
                    List<String> habitudes = clientDAO.getHabitudesConsos(client.getIdClient());
                    if (habitudes.isEmpty()) {
                        System.out.println("Aucune habitude de consommation trouvée.");
                    } else {
                        for (String habitude : habitudes) {
                            System.out.println(habitude);
                        }
                    }
                    break;
                case "3":
                    List<Commande> commandes = clientDAO.getCommandes(client.getIdClient());
                    clientDAO.afficherCommandes(commandes);
                    break;
                
                case "0":
                    continuer = false;
                    break;
                default:
                    System.out.println("Choix invalide. Veuillez réessayer.");
            }
        }
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Client client = clientDAO.getClientById(1);

        if (client == null) {
            System.out.println("Client introuvable !");
            scanner.close();
            return;
        }

        boolean quitter = false;
        while (!quitter) {
            afficherMenuPrincipal(client.getPrenomClient(), client.getNomClient(), clientDAO.getMagasinFavori(client.getIdClient()));

            //on récupère le panier en cours du client 1
            Panier panierClient = clientDAO.getPanierEnCours(client.getIdClient());     

            //si le client n'a pas de panier en cours, on en crée un
            if(panierClient == null) panierClient = client.creerPanier();

            System.out.print("Votre choix : ");
            String choixPrincipal = scanner.nextLine();

            switch (choixPrincipal) {
                case "1":
                    afficherMenuRecherche();
                    String choixRecherche = scanner.nextLine();

                    switch (choixRecherche) {
                        case "1":
                            List<Produit> tousProduits = produitDAO.getAllProduits();
                            afficherProduitsAvecPagination(tousProduits, scanner, panierClient);
                            break;
                        case "2":
                            System.out.print("Entrez un mot-clé : ");
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

                            afficherProduitsAvecPagination(produitsResultats, scanner, panierClient);
                            
                            break;
                        case "3":
                            categorieDAO.gererMenuCategorie(produitDAO, scanner, panierClient);
                            break;
                        case "4":
                            afficherMenuTri();
                            int critere = scanner.nextInt();
                            scanner.nextLine();
                            List<Produit> produits = produitDAO.getAllProduits();
                            switch (critere) {
                                case 1:
                                    produits.sort(Comparator.comparingDouble(Produit::getPrixUnitaire));
                                    break;
                                case 2:
                                    produits.sort(Comparator.comparing(Produit::getLibelleProduit));
                                    break;
                                case 3:
                                    produits.sort(Comparator.comparing(Produit::getNutriscore));
                                    break;
                                case 4:
                                    produits.sort(Comparator.comparingDouble(Produit::getPoidsProduit));
                                    break;
                            }
                            afficherProduitsAvecPagination(produits, scanner, panierClient);
                            break;
                        case "0":
                            break;
                        default:
                            System.out.println("Choix invalide.");
                    }
                    break;
                case "2":
                    afficherMenuPanier(panierClient, scanner);
                    break;
                case "3":
                    afficherMenuTableauDeBord(client, scanner);
                    break;
                case "0":
                    quitter = true;
                    System.out.println("Déconnexion...");
                    break;
                default:
                    System.out.println("Choix invalide.");
            }
        }
        scanner.close();
    }
}