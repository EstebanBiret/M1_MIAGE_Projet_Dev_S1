package src.main;

import java.util.List;
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

        // ----- US 2.2 : Habitudes de consommation ----- //
        System.out.println("\nHabitudes de consommation du client :");
        List<String> habitudes = clientDAO.getHabitudesCommandes(client.getIdClient());
        if (habitudes.isEmpty()) {
            System.out.println("Aucune habitude de consommation trouvée.");
        } else {
            for (String habitude : habitudes) {
                System.out.println(habitude);
            }
        }
    }
}