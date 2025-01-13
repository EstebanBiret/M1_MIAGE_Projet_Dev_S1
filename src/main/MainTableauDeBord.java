package src.main;

import java.util.List;

import src.Client;
import src.Produit;

public class MainTableauDeBord {
    
    public static void main(String[] args) {
        
        //Création du client
        Client client = new Client(1);

        List<Produit> produits = client.getProduitsPlusCommandes();
    }
}
