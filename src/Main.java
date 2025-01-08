package src;
//import java.sql.*;

public class Main {

    public static void main(String[] args) {

        /* ----- US 0.1 ----- */
        System.out.println("----- US 0.1 -----");

        //recherche et affichage d'un produit par son ID
        Produit produitId = new Produit(5);
        if(produitId.exists()) System.out.println(produitId.toString());

        //recherche et affichage d'un produit par son libellé
        Produit produitLibelle = new Produit("Jus d'orange", true);
        if(produitLibelle.exists()) System.out.println(produitLibelle.toString() + '\n');

        /* ----- US 0.2 ----- */
        System.out.println("----- US 0.2 -----");

        //recherche d'un produit par mot clé
        Produit produitMotCle = new Produit("Jus d'o", false);
        if(produitMotCle.exists()) System.out.println(produitMotCle.toString() + "\n");

        /* ----- US 3.1 ----- */
        System.out.println("----- US 3.1 -----");

        //création d'un produit et sauvegarde en BD
        Produit newProduit = new Produit("Poulet", 0.0, 0.0, 'A', 0.0, "1kg", "Carrefour");
        newProduit.save();
 
        //modification d'un produit en BD
        newProduit.setLibelleProduit("Test3");
        newProduit.update();
 
        //suppression d'un produit par son libellé et son ID
        Produit supprProduitLibelle = new Produit("Test", true);
        if(supprProduitLibelle.exists()) supprProduitLibelle.delete();
 
        Produit supprProduitId = new Produit(3);
        if(supprProduitId.exists()) supprProduitId.delete();
        System.out.println("\n");

    }
}