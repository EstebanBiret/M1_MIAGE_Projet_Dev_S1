package src.main;

import java.util.Scanner;

import src.Algorithmes;
import src.produit.ProduitRemplacement;

public class MainAlgos {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ProduitRemplacement newProduit = Algorithmes.remplacementProduit(3, 1, 21, scanner);  
        System.out.println("ID du produit de remplacement : " + newProduit.toString());
    }
}
