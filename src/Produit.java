package src;
import java.sql.*;

public class Produit {
    
    private int idProduit; //pas dans le constructeur car on prend l'id en BD
    private String libelleProduit;
    private double prixUnitaire;
    private double prixKilo;
    private char nutriscore;
    private double poidsProduit;
    private String conditionnementProduit;
    private String marqueProduit;

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

    // Constructeur avec un paramètre (récupérer un produit de la BD avec son ID ou libellé) --> US 0.1
    public Produit(int idProduit) {
        try (Connection connection = DBConnection.getConnection()) {
            String selectQuery = "SELECT * FROM produit WHERE idProduit = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
                pstmt.setInt(1, idProduit);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        this.idProduit = rs.getInt("idProduit");
                        this.libelleProduit = rs.getString("libelleProduit");
                        this.prixUnitaire = rs.getDouble("prixUnitaire");
                        this.prixKilo = rs.getDouble("prixKilo");
                        this.nutriscore = rs.getString("nutriscore").charAt(0);
                        this.poidsProduit = rs.getDouble("poidsProduit");
                        this.conditionnementProduit = rs.getString("conditionnementProduit");
                        this.marqueProduit = rs.getString("marqueProduit");
                    } else {
                        System.out.println("Produit introuvable (" + idProduit + ")");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    //booléen en param pour savoir si on récupère un produit par son nom exact ou mot clé
    public Produit(String libelleProduit, boolean nomExact) {
        try (Connection connection = DBConnection.getConnection()) {
            String selectQuery;
            if(nomExact) {
                selectQuery = "SELECT * FROM produit WHERE libelleProduit = ?";
            } else {
                selectQuery = "SELECT * FROM produit WHERE libelleProduit LIKE ?";
                libelleProduit = "%" + libelleProduit + "%";
            }
            
            try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
                pstmt.setString(1, libelleProduit);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        this.idProduit = rs.getInt("idProduit");
                        this.libelleProduit = rs.getString("libelleProduit");
                        this.prixUnitaire = rs.getDouble("prixUnitaire");
                        this.prixKilo = rs.getDouble("prixKilo");
                        this.nutriscore = rs.getString("nutriscore").charAt(0);
                        this.poidsProduit = rs.getDouble("poidsProduit");
                        this.conditionnementProduit = rs.getString("conditionnementProduit");
                        this.marqueProduit = rs.getString("marqueProduit");
                    } else {
                        if(nomExact) {
                            System.out.println("Produit introuvable (" + libelleProduit + ")");
                        } else {
                            System.out.println("Aucun produit trouvé avec le mot clé " + "'"  + libelleProduit + "'.");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
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

    /* Méthodes sur l'ID de la BD */
    public int getIdProduit() {return idProduit;}
    public void setIdProduit(int idProduit) {this.idProduit = idProduit;}

    // Savoir si le produit recherché existe bien en BD
    public boolean exists() {return this.idProduit != 0;}

    @Override
    public String toString() {
        return "Produit [conditionnement=" + conditionnementProduit + ", libellé=" + libelleProduit
                + ", marque=" + marqueProduit + ", nutriscore=" + nutriscore + ", poids=" + poidsProduit
                + ", prixKilo=" + prixKilo + ", prixUnitaire=" + prixUnitaire + ", id=" + idProduit + "]";
    }

    /*
     * Permet de sauvegarder en BD le produit créé
     */
    public void save() {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            String insertQuery = "INSERT INTO produit (libelleProduit, prixUnitaire, prixKilo, nutriscore, poidsProduit, conditionnementProduit, marqueProduit) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
                pstmt.setString(1, this.libelleProduit); 
                pstmt.setDouble(2, this.prixUnitaire);
                pstmt.setDouble(3, this.prixKilo);
                pstmt.setString(4, String.valueOf(this.nutriscore));
                pstmt.setDouble(5, this.poidsProduit);
                pstmt.setString(6, this.conditionnementProduit);
                pstmt.setString(7, this.marqueProduit);

                //exécution de la requête
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Produit ajouté avec succès (" + this.libelleProduit + ").");
                } else {
                    System.out.println("Aucun produit ajouté.");
                }

                connection.commit();

                //récupérer l'id du produit inséré
                String selectQuery = "SELECT * FROM produit WHERE libelleProduit = ?";
                try (PreparedStatement pstmt2 = connection.prepareStatement(selectQuery)) {
                    pstmt2.setString(1, libelleProduit);
                    try (ResultSet rs = pstmt2.executeQuery()) {
                        if (rs.next()) {
                            this.idProduit = rs.getInt("idProduit");
                            
                        } else {
                            System.out.println("Produit introuvable (" + libelleProduit + ").");
                        }
                    }
                }

            } catch (SQLException e) {
                //rollback si erreur
                connection.rollback();
                System.out.println("Erreur lors de l'ajout : " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    /*
     * Permet de valider la modification d'un produit en BD
     */
    public void update() {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            String updateQuery = "UPDATE produit SET libelleProduit = ?, prixUnitaire = ?, prixKilo = ?, nutriscore = ?, poidsProduit = ?, conditionnementProduit = ?, marqueProduit = ? WHERE idProduit = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
                pstmt.setString(1, this.libelleProduit); 
                pstmt.setDouble(2, this.prixUnitaire);
                pstmt.setDouble(3, this.prixKilo);
                pstmt.setString(4, String.valueOf(this.nutriscore));
                pstmt.setDouble(5, this.poidsProduit);
                pstmt.setString(6, this.conditionnementProduit);
                pstmt.setString(7, this.marqueProduit);
                pstmt.setInt(8, this.idProduit);
                
                //exécution de la requête
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Produit modifié avec succès (" + this.idProduit + ").");
                } else {
                    System.out.println("Aucun produit modifié.");
                }

                connection.commit();

            } catch (SQLException e) {
                //rollback si erreur
                connection.rollback();
                System.out.println("Erreur lors de la modification : " + e.getMessage());
            }
            
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

   /*
    * Permet de supprimer un produit en BD avec son ID
    */
    public void delete() {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            String deleteQuery = "DELETE FROM produit WHERE idProduit = ?";

            //TODO : supprimer les références de ce produit dans les tables concernées

            try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
                pstmt.setInt(1, this.idProduit);

                //exécution de la requête
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Produit supprimé avec succès (" + this.idProduit + ").");
                } else {
                    System.out.println("Aucun produit trouvé avec cet ID.");
                }

                connection.commit();
            } catch (SQLException e) {
                //rollback si erreur
                connection.rollback();
                System.out.println("Erreur lors de la suppression : " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
        }
    }
}