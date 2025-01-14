package src.main;

import java.util.Scanner;

import src.client.Client;
import src.client.ClientDAO;
import src.panier.Panier;
import src.panier.PanierDAO;

public class MainAchat {
    
    static ClientDAO clientDAO = new ClientDAO();
    static PanierDAO panierDAO = new PanierDAO();
    
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

            switch (choix) {
                case 1: //ajouter un produit au panier
                    System.out.print("Entrez l'ID du produit à ajouter : ");
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
                    break;

                case 2: //afficher le panier
                    System.out.println(panierDAO.afficherPanier(panierClient1.getIdPanier()));
                    break;

                case 3: //valider le panier
                    panierDAO.validerPanier(panierClient1);
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
