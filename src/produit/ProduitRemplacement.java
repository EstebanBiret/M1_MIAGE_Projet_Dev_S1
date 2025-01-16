package src.produit;

public class ProduitRemplacement {

    //propriétés
    private int idProduit;
    private String libelleProduit;
    private double prixUnitaire;
    private double prixKilo;
    private char nutriscore;
    private double poidsProduit;
    private String conditionnementProduit;
    private String marqueProduit;
    private int quantiteDisponible;
    private int idMagasin;
    private String nomMagasin;
    private int quantiteChoisie;

    //constructeur
    public ProduitRemplacement(int idProduit, String libelleProduit, double prixUnitaire, double prixKilo, char nutriscore,
                              double poidsProduit, String conditionnementProduit, String marqueProduit,
                              int quantiteDisponible, int idMagasin, String nomMagasin) {
        this.idProduit = idProduit;
        this.libelleProduit = libelleProduit;
        this.prixUnitaire = prixUnitaire;
        this.prixKilo = prixKilo;
        this.nutriscore = nutriscore;
        this.poidsProduit = poidsProduit;
        this.conditionnementProduit = conditionnementProduit;
        this.marqueProduit = marqueProduit;
        this.quantiteDisponible = quantiteDisponible;
        this.idMagasin = idMagasin;
        this.nomMagasin = nomMagasin;
    }

    //getters & setters
    public int getIdProduit() { return idProduit; }
    public String getLibelleProduit() { return libelleProduit; }
    public double getPrixUnitaire() { return prixUnitaire; }
    public double getPrixKilo() { return prixKilo; }
    public char getNutriscore() { return nutriscore; }
    public double getPoidsProduit() { return poidsProduit; }
    public String getConditionnementProduit() { return conditionnementProduit; }
    public String getMarqueProduit() { return marqueProduit; }
    public int getQuantiteDisponible() { return quantiteDisponible; }
    public int getIdMagasin() { return idMagasin; }
    public String getNomMagasin() { return nomMagasin; }
    public int getQuantiteChoisie() { return quantiteChoisie; }
    public void setQuantiteChoisie(int quantiteChoisie) { this.quantiteChoisie = quantiteChoisie; }

    @Override
    public String toString() {
        return "ProduitRemplacement{" +
                "idProduit=" + idProduit +
                ", libelleProduit='" + libelleProduit + '\'' +
                ", prixUnitaire=" + prixUnitaire +
                ", prixKilo=" + prixKilo +
                ", nutriscore=" + nutriscore +
                ", poidsProduit=" + poidsProduit +
                ", conditionnementProduit='" + conditionnementProduit + '\'' +
                ", marqueProduit='" + marqueProduit + '\'' +
                ", quantiteDisponible=" + quantiteDisponible +
                ", idMagasin=" + idMagasin +
                ", nomMagasin='" + nomMagasin + '\'' +
                ", quantiteChoisie=" + quantiteChoisie +
                '}';
    }
}