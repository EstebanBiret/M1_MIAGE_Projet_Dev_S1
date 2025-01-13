package src.main;

import java.util.List;

import src.client.Client;
import src.client.ClientDAO;

public class MainTableauDeBord {
    
    public static void main(String[] args) {
        
        //Création du client
        ClientDAO clientDAO = new ClientDAO();
        Client client = clientDAO.getClientById(1);

        //produits les plus commandés
        List<String> produits = clientDAO.getProduitsPlusCommandes(client.getIdClient());
        for (String produit : produits) {
            System.out.println(produit);
        }
    }
}
