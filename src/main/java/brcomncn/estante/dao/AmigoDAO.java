package brcomncn.estante.dao;

import brcomncn.estante.config.DatabaseConnection;
import brcomncn.estante.model.Amigo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AmigoDAO {

    public Amigo salvar(Amigo amigo) throws SQLException {
        String sql = "INSERT INTO amigos (nome, email, telefone) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, amigo.getNome());
            stmt.setString(2, amigo.getEmail());
            stmt.setString(3, amigo.getTelefone());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    amigo.setId(rs.getLong(1));
                }
            }

            return amigo;
        }
    }

    public Optional<Amigo> buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM amigos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(criarAmigoDoResultSet(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Amigo> buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM amigos WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(criarAmigoDoResultSet(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Amigo> listarTodos() throws SQLException {
        List<Amigo> amigos = new ArrayList<>();
        String sql = "SELECT * FROM amigos ORDER BY nome";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                amigos.add(criarAmigoDoResultSet(rs));
            }
        }
        return amigos;
    }

    public void atualizar(Amigo amigo) throws SQLException {
        String sql = "UPDATE amigos SET nome = ?, email = ?, telefone = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, amigo.getNome());
            stmt.setString(2, amigo.getEmail());
            stmt.setString(3, amigo.getTelefone());
            stmt.setLong(4, amigo.getId());

            stmt.executeUpdate();
        }
    }

    public void deletar(Long id) throws SQLException {
        String sql = "DELETE FROM amigos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    private Amigo criarAmigoDoResultSet(ResultSet rs) throws SQLException {
        Amigo amigo = new Amigo();
        amigo.setId(rs.getLong("id"));
        amigo.setNome(rs.getString("nome"));
        amigo.setEmail(rs.getString("email"));
        amigo.setTelefone(rs.getString("telefone"));
        amigo.setDataCadastro(rs.getTimestamp("data_cadastro").toLocalDateTime());
        return amigo;
    }
}