package src;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import src.produit.ProduitRemplacement;

public class Algorithmes {

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
                
                System.out.println("Requête : " + statement.toString());

                //exécution de la requête et ajout des résultats à la liste
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next() && produitsAlternatifs.size() < 5) {

                        //vérifier si le nutriscore est null
                        String nutriscoreStr = resultSet.getString("nutriscore");
                        Character nutriscore = (nutriscoreStr != null && !nutriscoreStr.isEmpty()) ? nutriscoreStr.charAt(0) : 'N';

                        ProduitRemplacement produit = new ProduitRemplacement (
                        resultSet.getInt("idProduit"),
                        resultSet.getString("libelleProduit"),
                        resultSet.getDouble("prixUnitaire"),
                        resultSet.getDouble("prixKilo"),
                        nutriscore,                        
                        resultSet.getDouble("poidsProduit"),
                        resultSet.getString("conditionnementProduit"),
                        resultSet.getString("marqueProduit"),
                        resultSet.getInt("quantiteEnStock"),
                        resultSet.getInt("idMagasin"),
                        resultSet.getString("nomMagasin")
                        );

                        //vérifier la quantité disponible
                        if (produit.getQuantiteDisponible() > 0) {
                            // on regarde grâce à un stream si un produit dans la liste a déjà le même id que le produit que l'on veut ajouter à celle-ci
                            boolean produitExistant = produitsAlternatifs.stream().anyMatch(p -> p.getIdProduit() == produit.getIdProduit());

                            if (!produitExistant) {
                                produitsAlternatifs.add(produit);
                            }
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

        System.out.print("Veuillez entrer le numéro du produit souhaité : ");
        int choix = -1;
        while (choix < 1 || choix > produitsAlternatifs.size()) {
            if (scanner.hasNextInt()) {
                choix = scanner.nextInt();
                if (choix < 1 || choix > produitsAlternatifs.size()) {
                    System.out.print("Numéro invalide. Veuillez entrer un chiffre entre 1 et " + produitsAlternatifs.size() + " : ");
                }
            } else {
                System.out.print("Entrée invalide. Veuillez entrer un chiffre : ");
                scanner.next();
            }
        }

        ProduitRemplacement produitChoisi = produitsAlternatifs.get(choix - 1);

        //gestion de la quantité à retourner (même si la quantité demandée est supérieure à la quantité disponible)
        int quantiteRetournee = Math.min(quantiteDemandee, produitChoisi.getQuantiteDisponible());
        System.out.printf(
            "Produit choisi : %s (Quantité demandée : %d, Quantité retournée : %d)%n",
            produitChoisi.getLibelleProduit(), quantiteDemandee, quantiteRetournee
        );

        produitChoisi.setQuantiteChoisie(quantiteRetournee);
        return produitChoisi;
    }
    
    //permet de construire la requête en fonction du nombre d'itérations
    private static String construireQuery(int nbIterations, int idProduit) {
        String requete = "";
    
        switch (nbIterations) {
            case 0: // même libellé, catégorie, marque, nutriscore et magasin
                requete += """
                    SELECT p.idProduit, p.libelleProduit, p.prixUnitaire, p.prixKilo, p.nutriscore, 
                    p.poidsProduit, p.conditionnementProduit, p.marqueProduit, 
                    s.quantiteEnStock, s.idMagasin, m.nomMagasin
                    FROM produit p, appartenir a, stocker s, magasin m
                    WHERE p.idProduit = a.idProduit
                    AND s.idProduit = p.idProduit
                    AND s.idMagasin = m.idMagasin
                    AND p.libelleProduit = (SELECT libelleProduit FROM produit WHERE idProduit = ?)
                    AND a.idCategorie IN (SELECT idCategorie FROM appartenir WHERE idProduit = ?)
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
                    FROM produit p, appartenir a, stocker s, magasin m
                    WHERE p.idProduit = a.idProduit
                    AND s.idProduit = p.idProduit
                    AND s.idMagasin = m.idMagasin
                    AND p.libelleProduit = (SELECT libelleProduit FROM produit WHERE idProduit = ?)
                    AND a.idCategorie IN (SELECT idCategorie FROM appartenir WHERE idProduit = ?)
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
                    FROM produit p, appartenir a, stocker s, magasin m
                    WHERE p.idProduit = a.idProduit
                    AND s.idProduit = p.idProduit
                    AND s.idMagasin = m.idMagasin
                    AND p.libelleProduit = (SELECT libelleProduit FROM produit WHERE idProduit = ?)
                    AND a.idCategorie IN (SELECT idCategorie FROM appartenir WHERE idProduit = ?)
                    AND p.marqueProduit = (SELECT marqueProduit FROM produit WHERE idProduit = ?)
                    AND p.idProduit != ?;
                """;
                break;
    
            case 3: // même libellé et catégorie
                requete += """
                    SELECT p.idProduit, p.libelleProduit, p.prixUnitaire, p.prixKilo, p.nutriscore, 
                           p.poidsProduit, p.conditionnementProduit, p.marqueProduit, 
                           s.quantiteEnStock, s.idMagasin, m.nomMagasin
                    FROM produit p, appartenir a, stocker s, magasin m
                    WHERE p.idProduit = a.idProduit
                    AND s.idProduit = p.idProduit
                    AND s.idMagasin = m.idMagasin
                    AND p.libelleProduit = (SELECT libelleProduit FROM produit WHERE idProduit = ?)
                    AND a.idCategorie IN (SELECT idCategorie FROM appartenir WHERE idProduit = ?)
                    AND p.idProduit != ?;
                """;
                break;
    
            case 4: // même catégorie
                requete += """
                    SELECT p.idProduit, p.libelleProduit, p.prixUnitaire, p.prixKilo, p.nutriscore, 
                           p.poidsProduit, p.conditionnementProduit, p.marqueProduit, 
                           s.quantiteEnStock, s.idMagasin, m.nomMagasin
                    FROM produit p, appartenir a, stocker s, magasin m
                    WHERE p.idProduit = a.idProduit
                    AND s.idProduit = p.idProduit
                    AND s.idMagasin = m.idMagasin
                    AND a.idCategorie IN (SELECT idCategorie FROM appartenir WHERE idProduit = ?)
                    AND p.idProduit != ?;
                """;
                break;
    
            default:
                throw new IllegalArgumentException("Étape inconnue : " + nbIterations);
        }
        return requete;
    }
}