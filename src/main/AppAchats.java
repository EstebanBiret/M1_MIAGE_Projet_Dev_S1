package src.main;

import java.util.List;
import java.util.Scanner;
import src.client.Client;
import src.client.ClientDAO;
import src.panier.Panier;
import src.panier.PanierDAO;
import src.produit.Produit;
import src.produit.ProduitDAO;

public class AppAchats {
    
    static ClientDAO clientDAO = new ClientDAO();
    static PanierDAO panierDAO = new PanierDAO();
    static ProduitDAO produitDAO = new ProduitDAO();
    
    public static void menuAchat(int idClient) {

        String magasinFavori = clientDAO.getMagasinFavori(idClient);
        String prenomClient = clientDAO.getPrenomClient(idClient);
        String nomClient = clientDAO.getNomClient(idClient);

        // Menu principal pour l'achat
        System.out.println("------------------------------------------");
        System.out.println("| Bonjour " + prenomClient + " " + nomClient + " !");
        System.out.println("|                                        |");
        System.out.println("| Votre magasin favori : " + magasinFavori);
        System.out.println("|                                        |");
        System.out.println("| [1] Ajouter un produit au panier       |");
        System.out.println("| [2] Afficher le panier                 |");
        System.out.println("| [3] Valider le panier                  |");
        System.out.println("| [4] Annuler le panier                  |");
        System.out.println("| [0] Quitter                            |");
        System.out.println("|                                        |");
        System.out.println("------------------------------------------");
    }
    
    public static void main(String[] args) {
    
        Scanner scanner = new Scanner(System.in);
        int choix = -1;

        //on récupère le premier client de la BD (par exemple avec l'ID 1) pour cet exemple
        Client client1 = clientDAO.getClientById(1);

        if (client1 == null) {
            System.out.println("Client introuvable !");
            scanner.close();
            return;
        }    

        //boucle pour interagir avec le menu tant que l'utilisateur ne quitte pas
        while (choix != 0) {
            menuAchat(client1.getIdClient());

            //on récupère le panier en cours du client 1
            Panier panierClient1 = clientDAO.getPanierEnCours(client1.getIdClient());     

            //si le client n'a pas de panier en cours, on en crée un
            if(panierClient1 == null) panierClient1 = client1.creerPanier();

            System.out.print("Veuillez choisir une option : ");
            while (!scanner.hasNextInt()) {
                System.out.print("Entrée invalide. Veuillez entrer un chiffre : ");
                scanner.next();
            }
            choix = scanner.nextInt();
            scanner.nextLine();

            //TODO rajouter choix liste des produits avec système de page (5 par 5)
            switch (choix) {
                case 1: //ajouter un produit au panier

                    System.out.println("Notre catalogue (naviguez à travers les pages et visualisez les détails des produits) : \n");
                    List<Produit> produits = produitDAO.getAllProduits();
                    boolean continuer = true;
                    int pageIndex = 0;
                    int produitsParPage = 5;
                    int totalPages = (int) Math.ceil((double) produits.size() / produitsParPage);

                    /*System.out.print("Entrez l'ID du produit à ajouter : ");
                    //tant que l'utilisateur ne renseigne pas un chiffre
                    while (!scanner.hasNextInt()) {
                        System.out.print("Entrée invalide. Veuillez entrer un chiffre : ");
                        scanner.next();
                    }
                    int idProduit = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Entrez la quantité : ");
                    while (!scanner.hasNextInt()) {
                        System.out.print("Entrée invalide. Veuillez entrer un chiffre : ");
                        scanner.next();
                    }
                    int quantite = scanner.nextInt();
                    scanner.nextLine();

                    panierDAO.ajouterProduitPanier(panierClient1.getIdPanier(), panierClient1.getIdClient(), idProduit, quantite, scanner);
                    break;*/

                    while (continuer) {
                        // Affichage des produits de la page actuelle
                        System.out.println("\n--- Page " + (pageIndex + 1) + "/" + totalPages + " ---");
                        int start = pageIndex * produitsParPage;
                        int end = Math.min(start + produitsParPage, produits.size());
                        for (int i = start; i < end; i++) {
                            Produit produit = produits.get(i);
                            System.out.println((i + 1) + ". " + produit.getLibelleProduit() + " - " + produit.getMarqueProduit() + " - Nutriscore " + produit.getNutriscore() + " - " + produit.getPrixUnitaire() + " euros");
                        }
                
                        System.out.println("\nOptions :");
                        System.out.println("[s] Page suivante");
                        System.out.println("[p] Page précédente");
                        System.out.println("[v <numéroProduit>] Voir les détails d'un produit");
                        System.out.println("[r] Retour");
                        System.out.print("Votre choix : ");
                        String choixUtilisateur = scanner.nextLine();
                
                        switch (choixUtilisateur) {
                            case "s": // Page suivante
                                if (pageIndex < totalPages - 1) {
                                    pageIndex++;
                                } else {
                                    System.out.println("Vous êtes déjà sur la dernière page.");
                                }
                                break;
                
                            case "p": // Page précédente
                                if (pageIndex > 0) {
                                    pageIndex--;
                                } else {
                                    System.out.println("Vous êtes déjà sur la première page.");
                                }
                                break;
                
                            case "r": // Retour
                                continuer = false;
                                break;
                
                            default:
                                if (choixUtilisateur.startsWith("v")) {
                                    try {
                                        // Extraction du numéro du produit
                                        int numeroProduit = Integer.parseInt(choixUtilisateur.substring(2).trim()) - 1;
                
                                        if (numeroProduit >= start && numeroProduit < end) {
                                            Produit produitSelectionne = produits.get(numeroProduit);
                
                                            // Affichage des détails du produit
                                            System.out.println("\nDétails du produit :");
                                            System.out.println(produitSelectionne.toString());
                
                                            // Options pour ajouter le produit au panier
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
                                                    panierClient1.getIdPanier(),
                                                    panierClient1.getIdClient(),
                                                    produitSelectionne.getIdProduit(),
                                                    quantite,
                                                    scanner
                                                );
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
                    break;

                case 2: //afficher le panier
                    System.out.println(panierDAO.afficherPanier(panierClient1.getIdPanier()));
                    break;

                    case 3: //valider le panier
                    int typeCommande = 0;
                    typeCommande = panierDAO.choisirModeRapide(panierClient1.getIdPanier());
                    if(typeCommande != 0){
                    System.out.println("Voulez-vous choisir le mode de livraison le plus rapide possible?");
                    System.out.println("1. OUI  2. NON");
                    while (!scanner.hasNextInt()) {
                       System.out.print("Entrée invalide. Veuillez entrer un chiffre : ");
                       scanner.next();
                    }
                       int choixRapide  = scanner.nextInt();
                       scanner.nextLine();
                      
                       if(choixRapide == 1){                        
                           panierDAO.validerPanier(panierClient1, typeCommande);
                       }
                       else if(choixRapide == 2){
                           int choixInverse = 0;
                           if(typeCommande == 1){
                               choixInverse =2;
                           }else if(choixRapide == 2){
                               choixInverse = 1;
                           }  
                           panierDAO.validerPanier(panierClient1, choixInverse);                                        
                       }else{
                           System.out.println("Choix invalide. Opération annulée.");
                          
                       }
                   }
                       else{
                           System.out.println("Echec de validation.");
   
   
                       }          
                       break;   

                case 4: //annuler le panier
                    panierDAO.annulerPanier(panierClient1);
                    break;

                case 0: //quitter
                    System.out.println("Fermeture du menu ...");
                    break;

                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }

            System.out.println("\n");
        }
        scanner.close();

        
        /*System.out.println("----- US 1.1 -----");
        //Cas d'un produit qui n'est pas disponible dans le magasin favori du client
        panierDAO.ajouterProduitPanier(panierClient1.getIdPanier(), panierClient1.getIdClient(), 6, 45);
        
        System.out.println("----- US 1.2 -----");        
        System.out.println(panierDAO.afficherPanier(panierClient1.getIdPanier()));

        System.out.println("----- US 1.3 -----");
        System.out.println("Validation du panier en cours du client1.");
        panierDAO.validerPanier(panierClient1);

        System.out.println("----- US 1.4 -----");
        System.out.println("Annulation du panier en cours du client 1 ...");
        panierDAO.annulerPanier(panierClient1);

        System.out.println("----- US 1.5 -----");
        System.out.println("Récupération du panier en cours du client 1 ...");
        panierClient1 = clientDAO.getPanierEnCours(client1.getIdClient());*/

        //test algo de remplacement
        /*int idNewProduit = Algorithmes.remplacementProduit(1, 1, 1, 1000);  
        System.out.println("ID du produit de remplacement : " + idNewProduit);  */
    }
}
