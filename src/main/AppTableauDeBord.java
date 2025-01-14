package src.main;

import java.util.List;
import java.util.Map;
import src.client.Client;
import src.client.ClientDAO;

public class AppTableauDeBord {
    
    public static void main(String[] args) {
        
        //Création du client
        ClientDAO clientDAO = new ClientDAO();
        Client client = clientDAO.getClientById(1);


        //produits les plus commandés
        List<String> produits = clientDAO.getProduitsPlusCommandes(client.getIdClient());
        for (String produit : produits) {
            System.out.println(produit);
        }

        //-----US 2.2-------//
    
       // Tester le calcul des habitudes de consommation pour le client
       Map<String, Integer> habitudes = clientDAO.calculerHabitudesConsommation(client.getIdClient());

       // Afficher les habitudes
       for (Map.Entry<String, Integer> habitude : habitudes.entrySet()) {
           System.out.println(habitude.getKey() + " : " + habitude.getValue());
        
       }
    }
}