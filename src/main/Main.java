package src.main;

import src.GestionnaireDAO;

public class Main {
    

    public static void main(String[] args) {
        // Exemple d'utilisation
        System.out.println("Changement automatique dans la BD");
    String cheminFichier = "src/produit/Catalogue.csv"; // Chemin relatif vers le fichier CSV
    GestionnaireDAO importer = new GestionnaireDAO();
    importer.importerProduitsDepuisCSV(cheminFichier);
    }
}
