package src.produit;

public class Produit {
    
    private int idProduit; //pas dans le 2ème constructeur car on prend l'id en BD (auto increment)
    private String libelleProduit;
    private double prixUnitaire;
    private double prixKilo;
    private char nutriscore;
    private double poidsProduit;
    private String conditionnementProduit;
    private String marqueProduit;

    /*
     * Constructeur de produit AVEC l'ID pour l'algo de recommandation, ne pas utiliser sinon
     */
    public Produit(int idProduit, String libelleProduit, double prixUnitaire, double prixKilo, char nutriscore, double poidsProduit, String conditionnementProduit, String marqueProduit) {
        this.idProduit = idProduit;
        this.libelleProduit = libelleProduit;
        this.prixUnitaire = prixUnitaire;
        this.prixKilo = prixKilo;
        this.nutriscore = nutriscore;
        this.poidsProduit = poidsProduit;
        this.conditionnementProduit = conditionnementProduit;
        this.marqueProduit = marqueProduit;
    }

    /*
     * US 3.1 : importation de produits pour maj le catalogue
     */
    public Produit(String libelleProduit, double prixUnitaire, double prixKilo, char nutriscore, double poidsProduit, String conditionnementProduit, String marqueProduit) {
        this.libelleProduit = libelleProduit;
        this.prixUnitaire = prixUnitaire;
        this.prixKilo = prixKilo;
        this.nutriscore = nutriscore;
        this.poidsProduit = poidsProduit;
        this.conditionnementProduit = conditionnementProduit;
        this.marqueProduit = marqueProduit;
    }

    //getters & setters
    public String getLibelleProduit() {return libelleProduit;}
    public void setLibelleProduit(String libelleProduit) {this.libelleProduit = libelleProduit;}
    public double getPrixUnitaire() {return prixUnitaire;}
    public void setPrixUnitaire(double prixUnitaire) {this.prixUnitaire = prixUnitaire;}
    public double getPrixKilo() {return prixKilo;}
    public void setPrixKilo(double prixKilo) {this.prixKilo = prixKilo;}
    public char getNutriscore() {return nutriscore;}
    public void setNutriscore(char nutriscore) {this.nutriscore = nutriscore;}
    public double getPoidsProduit() {return poidsProduit;}
    public void setPoidsProduit(double poidsProduit) {this.poidsProduit = poidsProduit;}
    public String getConditionnementProduit() {return conditionnementProduit;}
    public void setConditionnementProduit(String conditionnementProduit) {this.conditionnementProduit = conditionnementProduit;}
    public String getMarqueProduit() {return marqueProduit;}
    public void setMarqueProduit(String marqueProduit) {this.marqueProduit = marqueProduit;}

    /* getters/setters sur l'ID en BD */
    public int getIdProduit() {return idProduit;}
    public void setIdProduit(int idProduit) {this.idProduit = idProduit;}

    @Override
    public String toString() {
        return "Produit [id=" + idProduit + ", conditionnement=" + conditionnementProduit + ", libellé=" + libelleProduit
                + ", marque=" + marqueProduit + ", nutriscore=" + nutriscore + ", poids=" + poidsProduit
                + ", prixKilo=" + prixKilo + ", prixUnitaire=" + prixUnitaire + "]";
    }
}