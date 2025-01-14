package src.main;

import java.util.Scanner;
import src.PreparateurDAO;

public class AppPreparation {
    public static void main(String[] args) {
        
        PreparateurDAO Adam = new PreparateurDAO();
        Adam.afficherCommandesEnAttente();

        Scanner scanner = new Scanner(System.in);

    // Demander les informations de mise à jour
    System.out.println("US 4.2 Je marque une commande en préparation pour un retrait ou un envoi.");
    System.out.println("Entrez l'ID de la commande : ");
    int idCommande = scanner.nextInt();
    scanner.nextLine(); // Consommer la ligne restante

    System.out.println("Entrez le type de commande (retrait/livraison/mixte) : ");
    String typeCommande = scanner.nextLine();

    // Marquer la commande en préparation
    // CommandeDAO.marquerEnPreparation(idCommande, typeCommande);

    //US 4.2
    System.err.println("------US 4.2------");
    Adam.commencerAPreparer(1);
    }
}
