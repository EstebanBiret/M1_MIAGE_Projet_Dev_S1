package src.main;

import src.Algorithmes;

public class MainAlgos {
    
    public static void main(String[] args) {
        
        int idNewProduit = Algorithmes.remplacementProduit(3, 1, 1201);  
        System.out.println("ID du produit de remplacement : " + idNewProduit);
    }
}
