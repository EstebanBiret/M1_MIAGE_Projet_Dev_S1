package src.main;

import java.util.List;
import java.util.Map;
import src.Client;

public class MainTableauDeBord {
    
    public static void main(String[] args) {
        
        //Création du client
        Client client = new Client(1);

        //produits les plus commandés
        List<String> produits = client.getProduitsPlusCommandes();
        for (String produit : produits) {
            System.out.println(produit);
        }

        // Tester le calcul des habitudes de consommation pour le client
        Map<String, Integer> habitudes = client.calculerHabitudesConsommation(client.getIdClient());

        // Afficher les habitudes
        for (Map.Entry<String, Integer> habitude : habitudes.entrySet()) {
            System.out.println(habitude.getKey() + " : " + habitude.getValue());
         
        }
    }
}
