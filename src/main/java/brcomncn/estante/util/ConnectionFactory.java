package brcomncn.estante.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    // Corrigido o nome do banco de dados na URL
    private static final String URL = "jdbc:mysql://localhost:3306/estante_livros?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "sua_senha"; // Coloque sua senha aqui

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Erro ao carregar driver MySQL", e);
        }
    }

    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            e.printStackTrace();
            throw new SQLException("Erro ao conectar ao banco de dados: " + e.getMessage(), e);
        }
    }

    // Método para testar a conexão
    public void testarConexao() {
        try (Connection conn = getConnection()) {
            System.out.println("Conexão estabelecida com sucesso!");
            System.out.println("Database: " + conn.getCatalog());
        } catch (SQLException e) {
            System.err.println("Erro ao testar conexão: " + e.getMessage());
            e.printStackTrace();
        }
    }
}