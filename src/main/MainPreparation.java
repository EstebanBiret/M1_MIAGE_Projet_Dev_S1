package src.main;

import java.util.Scanner;

import src.commande.CommandeDAO;

public class MainPreparation {
    
    public static void main(String[] args) {
        
     CommandeDAO.afficherCommandesEnAttente();

        Scanner scanner = new Scanner(System.in);

    // Demander les informations de mise à jour
    System.out.println("US 4.2 Je marque une commande en préparation pour un retrait ou un envoi.");
    System.out.println("Entrez l'ID de la commande : ");
    int idCommande = scanner.nextInt();
    scanner.nextLine(); // Consommer la ligne restante

    System.out.println("Entrez le type de commande (retrait/livraison/mixte) : ");
    String typeCommande = scanner.nextLine();

    // Marquer la commande en préparation
    CommandeDAO.marquerEnPreparation(idCommande, typeCommande);
    }
}
