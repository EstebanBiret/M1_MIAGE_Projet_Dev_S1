package src.main;

import java.util.List;

import src.Client;
import src.Produit;

public class MainTableauDeBord {
    
    public static void main(String[] args) {
        
        //Création du client
        Client client = new Client(1);

        //produits les plus commandés
        List<String> produits = client.getProduitsPlusCommandes();
        for (String produit : produits) {
            System.out.println(produit);
        }
    }
}
