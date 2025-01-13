package src.main;

import src.Algorithmes;
import src.produit.ProduitRemplacement;

public class MainAlgos {
    
    public static void main(String[] args) {
        
        ProduitRemplacement newProduit = Algorithmes.remplacementProduit(3, 1, 21);  
        System.out.println("ID du produit de remplacement : " + newProduit.toString());
    }
}
