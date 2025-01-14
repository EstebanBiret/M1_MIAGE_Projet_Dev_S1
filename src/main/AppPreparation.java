package src.main;

import java.util.Scanner;
import src.PreparateurDAO;

public class AppPreparation {
    public static void main(String[] args) {
        
        PreparateurDAO Adam = new PreparateurDAO();

    //US 4.2
    System.out.println("------US 4.3------");
    Adam.afficherCommandesEnAttente();


    //US 4.2
    System.out.println("------US 4.2------");
    Adam.commencerAPreparer(1);

    //US 4.3
    System.out.println("----- US 4.3 -----");
    System.out.println("Validation du panier en cours du client1.");
    Adam.finaliserCommande(4);
    }

    
}
