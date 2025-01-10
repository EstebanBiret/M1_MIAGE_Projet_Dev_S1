package src.main;

import java.util.ArrayList;
import java.util.List;

import src.Gestionnaire;
import src.Produit;

public class MainGestion {
    
    public static void main(String[] args) {

        //création du gestionnaire
        Gestionnaire Marc = new Gestionnaire();

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

        //modification du stock d'un produit
        //Marc.majStockProduit(1, 5, List.of(1, true));
        System.out.println("\n");

    }
}
