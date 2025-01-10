package src;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Algorithmes {

    /*public int remplacementProduit(int idClient, int idProduit, int qte) {
        //en se basant sur les habitudes de conso du client et le produit désiré, on propose une liste de produits de remplacement, et on laisse le client choisir
    }*/

    // Remplacement d'un produit avec gestion des alternatives, retourne l'ID du produit de remplacement
    public static int remplacementProduit(int idProduit, int idClient, int idMagasin, int quantiteDemandee) {
        List<Produit> produitsAlternatifs = new ArrayList<>();
        boolean libelleExact = true; // On commence avec un libellé exact
        int filtreEtape = 0; // Suivi de l'incrémentation des filtres
    
        while (produitsAlternatifs.size() < 5 && (libelleExact || filtreEtape <= 3)) {
            String query = construireQuery(libelleExact, filtreEtape);
    
            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
    
                int paramIndex = 1;
    
                //pas le même produit
                statement.setInt(paramIndex, idProduit);

                // Quantité demandée
                statement.setInt(paramIndex++, quantiteDemandee);
    
                // Libellé exact ou différent
                statement.setInt(paramIndex++, idProduit);
    
                // Filtres conditionnels
                if (filtreEtape <= 0) {
                    statement.setInt(paramIndex++, idProduit); // Nutriscore
                }
                if (filtreEtape <= 1) {
                    statement.setInt(paramIndex++, idProduit); // Marque
                }
                if (filtreEtape <= 2) {
                    statement.setInt(paramIndex++, idProduit); // Catégorie
                }
    
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next() && produitsAlternatifs.size() < 5) {
                        Produit produit = new Produit(
                                resultSet.getString("libelleProduit"),
                                resultSet.getDouble("prixUnitaire"),
                                resultSet.getDouble("prixKilo"),
                                resultSet.getString("nutriscore").charAt(0),
                                resultSet.getDouble("poidsProduit"),
                                resultSet.getString("conditionnementProduit"),
                                resultSet.getString("marqueProduit")
                        );
    
                        if (!produitsAlternatifs.contains(produit)) {
                            produitsAlternatifs.add(produit);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
    
            // Passer à l'étape suivante
            if (produitsAlternatifs.isEmpty()) {
                if (filtreEtape >= 3) {
                    libelleExact = false; // Passer à un libellé différent
                    filtreEtape = 0; // Réinitialiser les filtres
                } else {
                    filtreEtape++; // Relâcher un filtre
                }
            } else {
                break; // Arrêter si on a trouvé des produits
            }
        }
    
        // Affichage des produits trouvés
        if (produitsAlternatifs.isEmpty()) {
            System.out.println("Aucun produit alternatif trouvé.");
            return -1;
        }
    
        System.out.println("Produits disponibles en remplacement :");
        for (int i = 0; i < produitsAlternatifs.size(); i++) {
            Produit produit = produitsAlternatifs.get(i);
            System.out.printf("%d. %s (Nutriscore: %c, Prix: %.2f€, Prix/Kg: %.2f€/kg, Poids: %.2fkg, Conditionnement: %s, Marque: %s)%n",
                    i + 1, produit.getLibelleProduit(), produit.getNutriscore(),
                    produit.getPrixUnitaire(), produit.getPrixKilo(),
                    produit.getPoidsProduit(), produit.getConditionnementProduit(),
                    produit.getMarqueProduit());
        }
    
        // Choix du produit par l'utilisateur
        try (Scanner scanner = new Scanner(System.in)) {
            int choix;
            do {
                System.out.print("Veuillez entrer le numéro du produit souhaité : ");
                while (!scanner.hasNextInt()) {
                    System.out.print("Entrée invalide. Veuillez entrer un chiffre : ");
                    scanner.next();
                }
                choix = scanner.nextInt();
            } while (choix < 1 || choix > produitsAlternatifs.size());
    
            return produitsAlternatifs.get(choix - 1).getIdProduit();
        }
    }    

    // Construction dynamique de la requête SQL
    private static String construireQuery(boolean libelleExact, int filtreEtape) {
        StringBuilder query = new StringBuilder("""
            SELECT p.idProduit, p.libelleProduit, p.prixUnitaire, p.prixKilo, p.nutriscore,
                p.poidsProduit, p.conditionnementProduit, p.marqueProduit
            FROM produit p, appartenir a, stocker s
            WHERE p.idProduit = a.idProduit AND p.idProduit != ? AND p.idProduit = s.idProduit AND s.quantiteEnStock >= ?
        """);

        if (libelleExact) {
            query.append(" AND p.libelleProduit = (SELECT libelleProduit FROM produit WHERE idProduit = ?) ");
        }

        if (filtreEtape <= 0) {
            query.append(" AND p.nutriscore = (SELECT nutriscore FROM produit WHERE idProduit = ?) ");
        }
        if (filtreEtape <= 1) {
            query.append(" AND p.marqueProduit = (SELECT marqueProduit FROM produit WHERE idProduit = ?) ");
        }
        if (filtreEtape <= 2) {
            query.append(" AND a.idCategorie = (SELECT idCategorie FROM appartenir WHERE idProduit = ?) ");
        }
        return query.toString();
    }

}