package src.main;

import src.Client;
import src.Panier;

public class MainAchat {
    
    public static void main(String[] args) {
     
        Client client1 = new Client(1); //on récupère le premier client de la BD
        //System.out.println(client1.toString());

        //on récupère le panier en cours du client 1
        Panier panierClient1 = client1.getPanierEnCours();          
        //si le client n'a pas de panier en cours, on en crée un
        if(panierClient1 == null) panierClient1 = client1.creerPanier();

        /* ----- US 1.1 ----- */
        System.out.println("----- US 1.1 -----");

        //Cas d'un produit qui n'est pas disponible dans le magasin favori du client
        panierClient1.ajouterProduitPanier(100, 5);

        panierClient1.ajouterProduitPanier(14, 1);
        
        /* ----- US 1.2 ----- */
        System.out.println("----- US 1.2 -----");        
        System.out.println(panierClient1.toString());

        /* ----- US 1.3 ----- */
        System.out.println("----- US 1.3 -----");
        panierClient1.validerPanier();

        /* ----- US 1.4 ----- */
        System.out.println("----- US 1.4 -----");
        System.out.println("Annulation du panier en cours du client 1 ...");
        panierClient1.annulerPanier();

        /* ----- US 1.5 ----- */
        System.out.println("----- US 1.5 -----");
        System.out.println("Récupération du panier en cours du client 1 ...");
        panierClient1 = client1.getPanierEnCours();
        //faire des actions...

        //test algo de remplacement
        /*int idNewProduit = Algorithmes.remplacementProduit(1, 1, 1, 1000);  
        System.out.println("ID du produit de remplacement : " + idNewProduit);  */
    }
}
