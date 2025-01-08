package src;
//import java.sql.*;

public class Main {

    public static void main(String[] args) {

        /* ----- US 0.1 ----- */

        //recherche et affichage d'un produit par son ID
        Produit produitId = new Produit(5);
        System.out.println(produitId.toString());

        //recherche et affichage d'un produit par son libellé
        Produit produitLibelle = new Produit("Jus d'orange");
        System.out.println(produitLibelle.toString());

        /* ----- US 3.1 ----- */

        //création d'un produit et sauvegarde en BD
        Produit newProduit = new Produit("Poulet", 0.0, 0.0, 'A', 0.0, "1kg", "Carrefour");
        newProduit.save();

        //modification d'un produit en BD
        newProduit.setLibelleProduit("Test3");
        newProduit.update();

        //suppression d'un produit par son libellé et son ID
        Produit supprProduitLibelle = new Produit("Test");
        if(supprProduitLibelle != null) supprProduitLibelle.delete();

        Produit supprProduitId = new Produit(3);
        if(supprProduitId != null) supprProduitId.delete();
    }
}