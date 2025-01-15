package src.main;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

import src.PreparateurDAO;
import src.commande.Commande;

public class AppPreparation {

    public static void afficherCommandesAvecPagination(List<Commande> commandes, Scanner scanner, PreparateurDAO preparateurDAO, boolean preparationMode) {
        if (commandes.isEmpty()) {
            System.out.println("Aucune commande trouvée.");
            return;
        }

        boolean continuer = true;
        int pageIndex = 0;
        int commandesParPage = 5;
        int totalPages = (int) Math.ceil((double) commandes.size() / commandesParPage);

        //date lisible
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        
        while (continuer) {
            System.out.println("\n--- Page " + (pageIndex + 1) + "/" + totalPages + " ---");
            int start = pageIndex * commandesParPage;
            int end = Math.min(start + commandesParPage, commandes.size());

            for (int i = start; i < end; i++) {
                Commande commande = commandes.get(i);
                StringBuilder commandeDetails = new StringBuilder();
                
                commandeDetails.append((i + 1)).append(". Type : ").append(commande.getTypeCommande())
                               .append(" | Statut : ").append(commande.getStatutCommande());

                //affichage des dates en fonction du statut
                if (preparationMode) {
                    commandeDetails.append(" | Date Réception : ")
                                   .append(dateFormat.format(new Date(commande.getDateReception().getTime())));
                } else {
                    commandeDetails.append(" | Date Réception : ")
                                   .append(dateFormat.format(new Date(commande.getDateReception().getTime())));
                    if (commande.getDatePreparation() != null) {
                        commandeDetails.append(" | Date Préparation : ")
                                       .append(dateFormat.format(new Date(commande.getDatePreparation().getTime())));
                    }

                    if (commande.getDateFinalisation() != null) {
                        commandeDetails.append(" | Date Finalisation : ")
                                       .append(dateFormat.format(new Date(commande.getDateFinalisation().getTime())));
                    }
                }
                System.out.println(commandeDetails.toString());
            }

            System.out.println("\nOptions :");
            System.out.println("[s] Page suivante");
            System.out.println("[p] Page précédente");
            System.out.println("[v <numéroCommande>] Voir les détails d'une commande");
            System.out.println("[r] Retour");
            System.out.print("Votre choix : ");
            String choixUtilisateur = scanner.nextLine();

            switch (choixUtilisateur) {
                case "s":
                    if (pageIndex < totalPages - 1) {
                        pageIndex++;
                    } else {
                        System.out.println("Vous êtes déjà sur la dernière page.");
                    }
                    break;
                case "p":
                    if (pageIndex > 0) {
                        pageIndex--;
                    } else {
                        System.out.println("Vous êtes déjà sur la première page.");
                    }
                    break;
                case "r":
                    continuer = false;
                    break;
                default:
                    if (choixUtilisateur.startsWith("v")) {
                        try {
                            int numeroCommande = Integer.parseInt(choixUtilisateur.substring(2).trim()) - 1;
                            if (numeroCommande >= start && numeroCommande < end) {
                                Commande commande = commandes.get(numeroCommande);
                                System.out.println("\nDétails de la commande :");
                                System.out.println(commande);

                                System.out.println("\nOptions :");
                                if (preparationMode) {
                                    System.out.println("[p] Commencer à préparer cette commande");
                                } else {
                                    if (commande.getDateFinalisation() == null) {
                                        System.out.println("[f] Finaliser cette commande");
                                    }
                                }
                                System.out.println("[r] Retour");
                                System.out.print("Votre choix : ");
                                String choixCommande = scanner.nextLine();

                                if (preparationMode && choixCommande.equals("p")) {
                                    preparateurDAO.commencerAPreparer(commande.getIdCommande());
                                    return;
                                } else if (!preparationMode && choixCommande.equals("f") && commande.getDateFinalisation() == null) {
                                    preparateurDAO.finaliserCommande(commande.getIdCommande());
                                    return;
                                }
                            } else {
                                System.out.println("Numéro de commande invalide.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Format invalide. Utilisez 'v <numéro>'.");
                        }
                    } else {
                        System.out.println("Choix invalide.");
                    }
                    break;
            }
        }
    }

    public static void menuPreparation() {
        System.out.println("-----------------------------------------");
        System.out.println("| ~ Menu de préparation ~               |");
        System.out.println("|                                       |");
        System.out.println("| [1] Voir les commandes en attente     |");
        System.out.println("| [2] Voir les commandes en préparation |");
        System.out.println("| [3] Voir les commandes finalisées     |");
        System.out.println("| [0] Quitter                           |");
        System.out.println("|                                       |");
        System.out.println("----------------------------------------");
    }

    public static void main(String[] args) {
        PreparateurDAO preparateurDAO = new PreparateurDAO();
        Scanner scanner = new Scanner(System.in);
        int choix = -1;

        while (choix != 0) {
            menuPreparation();
            System.out.print("Veuillez choisir une option : ");
            while (!scanner.hasNextInt()) {
                System.out.print("Entrée invalide. Veuillez entrer un chiffre : ");
                scanner.next();
            }
            choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    List<Commande> commandesEnAttente = preparateurDAO.getCommandesEnAttente();
                    afficherCommandesAvecPagination(commandesEnAttente, scanner, preparateurDAO, true);
                    break;
                case 2:
                    List<Commande> commandesPreparees = preparateurDAO.getCommandesPreparees();
                    afficherCommandesAvecPagination(commandesPreparees, scanner, preparateurDAO, false);
                    break;
                case 3:
                    List<Commande> commandesFinalisees = preparateurDAO.getCommandesFinalisees();
                    afficherCommandesAvecPagination(commandesFinalisees, scanner, preparateurDAO, false);
                    break;
                case 0:
                    System.out.println("Fermeture du menu ...");
                    break;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }
            System.out.println("\n");
        }
        scanner.close();
    }
}