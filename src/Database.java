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
}
