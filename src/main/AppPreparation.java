package src.main;

import java.util.Scanner;

import src.PreparateurDAO;

public class AppPreparation {

    public static void menuPreparation() {
        // Création du menu de gestion
        System.out.println("--------------------------------------");
        System.out.println("| ~ Menu de préparation ~            |");
        System.out.println("|                                    |");
        System.out.println("| [1] Voir les commandes en attente  |");
        System.out.println("| [2] Voir les commandes préparées   |");
        System.out.println("| [3] Voir les commandes finalisées  |");
        System.out.println("| [0] Quitter                        |");
        System.out.println("|                                    |");
        System.out.println("-------------------------------------");
    }

    public static void main(String[] args) {
        
        PreparateurDAO adam = new PreparateurDAO();
        Scanner scanner = new Scanner(System.in);
        int choix = -1;

        while (choix != 0) {
            menuGestion();
            System.out.print("Veuillez choisir une option : ");
            while (!scanner.hasNextInt()) {
                System.out.print("Entrée invalide. Veuillez entrer un chiffre : ");
                scanner.next();
            }
            choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    adam.afficherCommandesEnAttente();
                    break;
                case 2:
                    adam.afficherCommandesPreparees();
                    break;
                case 3:
                    adam.afficherCommandesFinalisees();
                    break;

                //US 4.1
                System.out.println("------US 4.1------");
                adam.afficherCommandesEnAttente();

                //US 4.2
                System.out.println("------US 4.2------");
                adam.commencerAPreparer(1);

                //US 4.3
                System.out.println("----- US 4.3 -----");
                System.out.println("Validation du panier en cours du client1.");
                adam.finaliserCommande(4);
            }
        }
    }
}