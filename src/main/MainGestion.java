package src.main;

import java.util.ArrayList;
import java.util.List;

import src.GestionnaireDAO;
import src.produit.Produit;

public class MainGestion {
    
    public static void main(String[] args) {

        //création du gestionnaire
        GestionnaireDAO Marc = new GestionnaireDAO();

        /* ----- US 3.1 ----- */
        System.out.println("\n");
        System.out.println("----- US 3.1 -----");

        //création d'un produit et sauvegarde en BD
        Produit newProduit = new Produit("Poulet", 10, 10, 'A', 0.0, "1kg", "Carrefour");
        Marc.ajouterProduitCatalogue(newProduit);
 
        //modification d'un produit en BD
        newProduit.setLibelleProduit("Poulet modifié");
        newProduit.setMarqueProduit("Poulet modifié");
        Marc.majProduitCatalogue(newProduit);
 
        //suppression d'un produit par son libellé et son ID (TODO supprimer les références dans les tables associées)
        /*Produit supprProduitLibelle = new Produit("Test3", true);
        if(supprProduitLibelle.exists()) supprProduitLibelle.supprProduitCatalogue();
        
        Produit supprProduitId = new Produit(3);
        if(supprProduitId.exists()) supprProduitId.supprProduitCatalogue();*/

        //augmenter les stocks d'un produit
        List<Integer> magasins = List.of(1, 2, 3);
        Marc.majStockProduit(1, 5, magasins, true);
        //baisser les stocks d'un produit
        Marc.majStockProduit(1, 2, magasins, false);
        System.out.println("\n");

        /* ----- US 3.2 ----- */
        System.out.println("----- US 3.2 -----");
        System.out.println(Marc.calculerTempsMoyenRealisation());

        /* ----- US 3.3 ----- */
        System.out.println("----- US 3.3 -----");
        System.out.println("Produit les plus commandé :");
        System.out.println(Marc.getProduitPlusCommande());

        System.out.println("\n");
        System.out.println("Catégories les plus commandées :");
        System.out.println(Marc.getTopCategories());

        System.out.println("\n");
        System.out.println("Clients ayant effectués le plus de commandes :");
        List<String> topClients = Marc.getTopClientsNbCommandes();
        for (String client : topClients) {
            System.out.println(client);
        }

        System.out.println("\n");
        System.out.println("Clients ayant générés le plus d'argent :");
        List<String> topClients2 = Marc.getTopClientsChiffreAffaires();
        for (String client : topClients2) {
            System.out.println(client);
        }


    }
}
