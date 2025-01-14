package src;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import src.produit.ProduitRemplacement;

public class Algorithmes {

    /*public int remplacementProduitHabitudes(int idClient, int idProduit, int qte) {
        //en se basant sur les habitudes de conso du client et le produit désiré, on propose une liste de produits de remplacement, et on laisse le client choisir
    }*/

    //remplacement d'un produit avec gestion des alternatives
    public static ProduitRemplacement remplacementProduit(int idProduit, int idMagasin, int quantiteDemandee, Scanner scanner) {
        List<ProduitRemplacement> produitsAlternatifs = new ArrayList<>();
        int nbIterations = 0;
    
        //tant que la liste n'est pas pleine ou que l'on a pas encore fait tous les filtres
        while (produitsAlternatifs.size() < 5 && nbIterations < 5) {
            String query = construireQuery(nbIterations, idProduit);
    
            try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
    
                switch (nbIterations) {
                    case 0:
                        statement.setInt(1, idProduit);       
                        statement.setInt(2, idProduit);      
                        statement.setInt(3, idProduit);       
                        statement.setInt(4, idProduit);       
                        statement.setInt(5, idMagasin);       
                        statement.setInt(6, idProduit);      
                        break;

                    case 1:
                        statement.setInt(1, idProduit);       
                        statement.setInt(2, idProduit);       
                        statement.setInt(3, idProduit);       
                        statement.setInt(4, idProduit);      
                        statement.setInt(5, idProduit);       
                        break;

                    case 2:
                        statement.setInt(1, idProduit);       
                        statement.setInt(2, idProduit);       
                        statement.setInt(3, idProduit);      
                        statement.setInt(4, idProduit);      
                        break;

                    case 3:
                        statement.setInt(1, idProduit);    
                        statement.setInt(2, idProduit);
                        statement.setInt(3, idProduit);  
                        break;

                    case 4:
                        statement.setInt(1, idProduit);       
                        statement.setInt(2, idProduit);       
                        break;
                
                    default:
                        break;
                }
    
                // Exécution de la requête et ajout des résultats à la liste
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next() && produitsAlternatifs.size() < 5) {
                        ProduitRemplacement produit = new ProduitRemplacement (
                        resultSet.getInt("idProduit"),
                        resultSet.getString("libelleProduit"),
                        resultSet.getDouble("prixUnitaire"),
                        resultSet.getDouble("prixKilo"),
                        resultSet.getString("nutriscore").charAt(0),
                        resultSet.getDouble("poidsProduit"),
                        resultSet.getString("conditionnementProduit"),
                        resultSet.getString("marqueProduit"),
                        resultSet.getInt("quantiteEnStock"),
                        resultSet.getInt("idMagasin"),
                        resultSet.getString("nomMagasin")
                        );

                        //on regarde grâce à un stream si un produit dans la liste a déjà le même id que le produit que l'on veut ajouter à celle-ci
                        boolean produitExistant = produitsAlternatifs.stream().anyMatch(p -> p.getIdProduit() == produit.getIdProduit());
                        //System.out.println("test : " + produitExistant);

                        if (!produitExistant) {
                            produitsAlternatifs.add(produit);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
    
            nbIterations++;
        }
    
        //si aucun produit n'a été trouvé
        if (produitsAlternatifs.isEmpty()) {
            System.out.println("Aucun produit alternatif trouvé.");
            return null;
        }
    
        //proposition des produits à l'utilisateur
        System.out.println("Produits disponibles en remplacement :");
        for (int i = 0; i < produitsAlternatifs.size(); i++) {
            ProduitRemplacement produit = produitsAlternatifs.get(i);
            System.out.printf(
            "%d. %s (Nutriscore: %c, Prix: %.2f€, Prix/Kg: %.2f€/kg, Poids: %.2fkg, Conditionnement: %s, Marque: %s, Quantité Disponible: %d, Magasin: %s)%n",
            i + 1, produit.getLibelleProduit(), produit.getNutriscore(),
            produit.getPrixUnitaire(), produit.getPrixKilo(),
            produit.getPoidsProduit(), produit.getConditionnementProduit(),
            produit.getMarqueProduit(), produit.getQuantiteDisponible(),
            produit.getNomMagasin()
            );
        }
    
        // Choix de l'utilisateur
        int choix;
        do {
            System.out.print("Veuillez entrer le numéro du produit souhaité : ");
            while (!scanner.hasNextInt()) {
                System.out.print("Entrée invalide. Veuillez entrer un chiffre : ");
                scanner.next();
            }
            choix = scanner.nextInt();
        } while (choix < 1 || choix > produitsAlternatifs.size());  
    
        ProduitRemplacement produitChoisi = produitsAlternatifs.get(choix - 1);

        // Gestion de la quantité à retourner
        int quantiteRetournee = Math.min(quantiteDemandee, produitChoisi.getQuantiteDisponible());
        System.out.printf(
            "Produit choisi : %s (Quantité demandée : %d, Quantité retournée : %d)%n",
            produitChoisi.getLibelleProduit(), quantiteDemandee, quantiteRetournee
        );

        produitChoisi.setQuantiteChoisie(quantiteRetournee);
        return produitChoisi;
    }
    
    private static String construireQuery(int nbIterations, int idProduit) {
        String requete = "";
    
        switch (nbIterations) {
            case 0: // même libellé, catégorie, marque, nutriscore et magasin
                requete += """
                    SELECT p.idProduit, p.libelleProduit, p.prixUnitaire, p.prixKilo, p.nutriscore, 
                           p.poidsProduit, p.conditionnementProduit, p.marqueProduit, 
                           s.quantiteEnStock, s.idMagasin, m.nomMagasin
                    FROM produit p
                    JOIN appartenir a ON p.idProduit = a.idProduit
                    JOIN stocker s ON s.idProduit = p.idProduit
                    JOIN magasin m ON s.idMagasin = m.idMagasin
                    WHERE p.libelleProduit = (SELECT libelleProduit FROM produit WHERE idProduit = ?)
                    AND a.idCategorie = (SELECT idCategorie FROM appartenir WHERE idProduit = ?)
                    AND p.marqueProduit = (SELECT marqueProduit FROM produit WHERE idProduit = ?)
                    AND p.nutriscore = (SELECT nutriscore FROM produit WHERE idProduit = ?)
                    AND s.idMagasin = ?
                    AND p.idProduit != ?;
                """;
                break;
    
            case 1: // même libellé, catégorie, marque et nutriscore
                requete += """
                    SELECT p.idProduit, p.libelleProduit, p.prixUnitaire, p.prixKilo, p.nutriscore, 
                           p.poidsProduit, p.conditionnementProduit, p.marqueProduit, 
                           s.quantiteEnStock, s.idMagasin, m.nomMagasin
                    FROM produit p
                    JOIN appartenir a ON p.idProduit = a.idProduit
                    JOIN stocker s ON s.idProduit = p.idProduit
                    JOIN magasin m ON s.idMagasin = m.idMagasin
                    WHERE p.libelleProduit = (SELECT libelleProduit FROM produit WHERE idProduit = ?)
                    AND a.idCategorie = (SELECT idCategorie FROM appartenir WHERE idProduit = ?)
                    AND p.marqueProduit = (SELECT marqueProduit FROM produit WHERE idProduit = ?)
                    AND p.nutriscore = (SELECT nutriscore FROM produit WHERE idProduit = ?)
                    AND p.idProduit != ?;
                """;
                break;
    
            case 2: // même libellé, catégorie et marque
                requete += """
                    SELECT p.idProduit, p.libelleProduit, p.prixUnitaire, p.prixKilo, p.nutriscore, 
                           p.poidsProduit, p.conditionnementProduit, p.marqueProduit, 
                           s.quantiteEnStock, s.idMagasin, m.nomMagasin
                    FROM produit p
                    JOIN appartenir a ON p.idProduit = a.idProduit
                    JOIN stocker s ON s.idProduit = p.idProduit
                    JOIN magasin m ON s.idMagasin = m.idMagasin
                    WHERE p.libelleProduit = (SELECT libelleProduit FROM produit WHERE idProduit = ?)
                    AND a.idCategorie = (SELECT idCategorie FROM appartenir WHERE idProduit = ?)
                    AND p.marqueProduit = (SELECT marqueProduit FROM produit WHERE idProduit = ?)
                    AND p.idProduit != ?;
                """;
                break;
    
            case 3: // même libellé et catégorie
                requete += """
                    SELECT p.idProduit, p.libelleProduit, p.prixUnitaire, p.prixKilo, p.nutriscore, 
                           p.poidsProduit, p.conditionnementProduit, p.marqueProduit, 
                           s.quantiteEnStock, s.idMagasin, m.nomMagasin
                    FROM produit p
                    JOIN appartenir a ON p.idProduit = a.idProduit
                    JOIN stocker s ON s.idProduit = p.idProduit
                    JOIN magasin m ON s.idMagasin = m.idMagasin
                    WHERE p.libelleProduit = (SELECT libelleProduit FROM produit WHERE idProduit = ?)
                    AND a.idCategorie = (SELECT idCategorie FROM appartenir WHERE idProduit = ?)
                    AND p.idProduit != ?;
                """;
                break;
    
            case 4: // même catégorie
                requete += """
                    SELECT p.idProduit, p.libelleProduit, p.prixUnitaire, p.prixKilo, p.nutriscore, 
                           p.poidsProduit, p.conditionnementProduit, p.marqueProduit, 
                           s.quantiteEnStock, s.idMagasin, m.nomMagasin
                    FROM produit p
                    JOIN appartenir a ON p.idProduit = a.idProduit
                    JOIN stocker s ON s.idProduit = p.idProduit
                    JOIN magasin m ON s.idMagasin = m.idMagasin
                    WHERE a.idCategorie = (SELECT idCategorie FROM appartenir WHERE idProduit = ?)
                    AND p.idProduit != ?;
                """;
                break;
    
            default:
                throw new IllegalArgumentException("Étape inconnue : " + nbIterations);
        }
    
        return requete;
    }
}