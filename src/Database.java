import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {
    private Connection conn;
    public Database(String indirizzo, String porta, String nome_tabella, String username, String password) {
        String dbConnectionString = "jdbc:mysql://" + indirizzo + ":" + porta + "/" + nome_tabella;
        try {
            conn = DriverManager.getConnection(dbConnectionString, username, password);
            if (conn != null) {
                System.out.println("Connessione avvenuta!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean InsertProdotto(String nome, String descrizione, String prezzo, String categoria, String data_inserimento) {
        try{
            if(!conn.isValid(5)){
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        String query = "INSERT INTO prodotti (nome, descrizione, prezzo, categoria, data_inserimento) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, nome);
            statement.setString(2, descrizione);
            statement.setString(3, prezzo);
            statement.setString(4, categoria);
            statement.setString(5, data_inserimento);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
