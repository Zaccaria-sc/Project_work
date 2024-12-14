import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public boolean InsertProdotto(String nome, String categoria, String descrizione, Timestamp data_inserimento) {
        try {
            if (!conn.isValid(5)) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String query = "INSERT INTO prodotti (nome, categoria, descrizione, data_inserimento) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, nome);
            statement.setString(2, categoria);
            statement.setString(3, descrizione);
            statement.setTimestamp(4, data_inserimento);  // Impostazione corretta della data
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isProductExist(String productName, String categoria) {
        try {
            String query = "SELECT COUNT(*) FROM prodotti WHERE nome = ? OR categoria = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, productName);
            statement.setString(2, categoria);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0; // Se il conteggio è maggiore di 0, il prodotto esiste
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Se c'è un errore o il prodotto non esiste
    }

    public Boolean InsertSiti(String nome, String url_base, Timestamp data_aggiuntiva) {
        try {
            if (!conn.isValid(5)) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String query = "INSERT INTO siti (nome, url_base, data_aggiuntiva) VALUES (?, ?, ?)";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, nome);
            statement.setString(2, url_base);
            statement.setTimestamp(3, data_aggiuntiva);  // Impostazione corretta della data
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isSitoExist(String productName, String productUrl) {
        try {
            String query = "SELECT COUNT(*) FROM siti WHERE nome = ? OR url_base = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, productName);
            statement.setString(2, productUrl);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0; // Se il conteggio è maggiore di 0, il prodotto esiste
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Se c'è un errore o il prodotto non esiste
    }

    public List<String> getOfferteByCategoria(String categoria) {
        List<String> offerte = new ArrayList<>();

        // Query per ottenere le offerte filtrate per categoria
        String query = "SELECT p.nome, o.prezzo, p.descrizione, o.url FROM offerte o " +
                "JOIN prodotti p ON o.id_prodotto = p.id_prodotto " +
                "WHERE p.categoria = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, categoria);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String nome = resultSet.getString("nome");
                String prezzo = resultSet.getString("prezzo");
                String descrizione = resultSet.getString("descrizione");
                String url = resultSet.getString("url");

                String offerta = String.format(
                        "Nome: %s\nPrezzo: %s\nDescrizione: %s\nLink: %s",
                        nome, prezzo, descrizione, url
                );
                offerte.add(offerta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return offerte;
    }
    public boolean InsertOfferta(int idProdotto, int idSito, BigDecimal prezzo, String url, Timestamp dataRilevamento) {
        String query = "INSERT INTO offerte (id_prodotto, id_sito, prezzo, url, data_rilevamento) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            // Impostiamo i parametri per il PreparedStatement
            statement.setInt(1, idProdotto);           // id_prodotto
            statement.setInt(2, idSito);               // id_sito
            statement.setBigDecimal(3, prezzo);        // prezzo
            statement.setString(4, url);               // url
            statement.setTimestamp(5, dataRilevamento); // data_rilevamento

            // Eseguiamo l'inserimento
            int rowsAffected = statement.executeUpdate();

            // Se l'inserimento ha avuto successo (ovvero almeno una riga è stata aggiornata)
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public int getProductIdByName(String productName) {
        String query = "SELECT id_prodotto FROM prodotti WHERE nome = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, productName);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id_prodotto");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Se non trovato, restituisce un ID non valido
    }
    public int getSiteIdByUrl(String url) {
        String query = "SELECT id_sito FROM siti WHERE url_base = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, url);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id_sito");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Se non trovato, restituisce un ID non valido
    }

}
