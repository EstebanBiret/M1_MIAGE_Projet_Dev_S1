package src;
import java.sql.*; 

public class Commande {
    
    //propritetes
    private int idCommande;
    private int idPanier;
    private String typeCommande;
    private String statutCommande;
    private Timestamp dateReception;
    private Timestamp datePreparation;
    private Timestamp dateFinalisation;
    
    //récupérer la commande d'un client
    public Commande(int idCommande, int idPanier, String typeCommande, String statutCommande, Timestamp dateReception, Timestamp datePreparation, Timestamp dateFinalisation) {
        this.idCommande = idCommande;
        this.idPanier = idPanier;
        this.statutCommande = statutCommande;
        this.typeCommande = typeCommande;
        this.dateReception = dateReception;
        this.datePreparation = datePreparation;
        this.dateFinalisation = dateFinalisation;        
    }

    //constructeur pour creer une nouvelle commande.
    public Commande(int idPanier, String typeCommande, Timestamp dateReception){
        this.idPanier = idPanier;
        this.typeCommande = typeCommande;
        this.dateReception = dateReception;
        this.statutCommande = "en attente";
        this.datePreparation = null;
        this.dateFinalisation = null;

            try (Connection connection = DBConnection.getConnection()) {

            String queryTest = "SELECT * FROM commande WHERE idPanier = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(queryTest)) {
                pstmt.setInt(1, idPanier);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("La commande existe déjà .");
                        return;
                    }
                }
            }

            //création d'une commande en BD
            String query = "INSERT INTO commande (idCommande,idPanier, statutCommande, typeCommande, dateReception,datePreparation,dateFinialisation) VALUES (?, ?, ?, ?,?,?,?)";
            try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, idCommande);
                pstmt.setInt(2, this.idPanier);
                pstmt.setString(3, this.statutCommande);
                pstmt.setString(4, this.typeCommande);
                pstmt.setTimestamp(5, this.dateReception);
                pstmt.setTimestamp(6, this.datePreparation); 
                pstmt.setTimestamp(7, this.dateFinalisation); 

                int rowsAffected = pstmt.executeUpdate();

                //on récupère l'id auto increment de la ligne tout juste générée pour l'attribuer à notre commande java
                if (rowsAffected > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            this.idCommande = rs.getInt(1);
                            System.out.println("Commande créé avec succès : " + this.toString());
                        }
                    }
                } else {
                    System.out.println("Aucune Commande créé.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la création du commande : " + e.getMessage());
        }
    }

    //getters & setters
    public int getIdCommande() {return idCommande;}
    public int getIdPanier() {return idPanier;}
    public String getStatutCommande() {return statutCommande;}
    public String getTypeCommande() {return typeCommande;}
    public Timestamp getDateReception() {return dateReception;}
    public Timestamp getDatePreparation() {return datePreparation;}
    public Timestamp getDateFinalisation() {return dateFinalisation;}
    public void setStatutCommande(String statutCommande) {this.statutCommande = statutCommande;}
    public void setTypeCommande(String typeCommande) {this.typeCommande = typeCommande;}
    public void setDateReception(Timestamp dateReception) {this.dateReception = dateReception;}
    public void setDatePreparation(Timestamp datePreparation) {this.datePreparation = datePreparation;}
    public void setDateFinalisation(Timestamp dateFinalisation) {this.dateFinalisation = dateFinalisation;}

        @Override
        public String toString() {
            return "Commande [idCommande=" + idCommande + ", idPanier=" + idPanier + ", statutCommande="
                    + statutCommande + ", typeCommande=" + typeCommande + ", dateReception=" + dateReception
                    + ", datePreparation=" + datePreparation + ", dateFinalisation=" + dateFinalisation + "]";
        }  
    
}
