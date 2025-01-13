package src.main;

import src.client.Client;
import src.client.ClientDAO;
import src.panier.Panier;
import src.panier.PanierDAO;

public class MainAchat {
    
    public static void main(String[] args) {
     
        //création des instances de DAO
        ClientDAO clientDAO = new ClientDAO();
        PanierDAO panierDAO = new PanierDAO();

        // On récupère le premier client de la BD (par exemple avec l'ID 1)
        Client client1 = clientDAO.getClientById(1);


        if (client1 == null) {
            System.out.println("Client introuvable !");
            return;  // Si le client n'est pas trouvé, on arrête l'exécution.
        }

        //on récupère le panier en cours du client 1
        Panier panierClient1 = clientDAO.getPanierEnCours(client1.getIdClient());          
        //si le client n'a pas de panier en cours, on en crée un
        if(panierClient1 == null) panierClient1 = client1.creerPanier();

        /* ----- US 1.1 ----- */
        System.out.println("----- US 1.1 -----");
        //Cas d'un produit qui n'est pas disponible dans le magasin favori du client
        panierDAO.ajouterProduitPanier(panierClient1.getIdPanier(), panierClient1.getIdClient(), 6, 45);
        
        /* ----- US 1.2 ----- */
        System.out.println("----- US 1.2 -----");        
        System.out.println(panierDAO.afficherPanier(panierClient1.getIdPanier()));

        /* ----- US 1.3 ----- */
        System.out.println("----- US 1.3 -----");
        System.out.println("Validation du panier en cours du client1.");
        panierDAO.validerPanier(panierClient1);

        /* ----- US 1.4 ----- */
        System.out.println("----- US 1.4 -----");
        System.out.println("Annulation du panier en cours du client 1 ...");
        //panierDAO.annulerPanier(panierClient1);

        /* ----- US 1.5 ----- */
        System.out.println("----- US 1.5 -----");
        System.out.println("Récupération du panier en cours du client 1 ...");
        panierClient1 = clientDAO.getPanierEnCours(client1.getIdClient());

        //test algo de remplacement
        /*int idNewProduit = Algorithmes.remplacementProduit(1, 1, 1, 1000);  
        System.out.println("ID du produit de remplacement : " + idNewProduit);  */
    }
}
