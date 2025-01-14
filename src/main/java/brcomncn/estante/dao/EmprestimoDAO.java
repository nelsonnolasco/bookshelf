package brcomncn.estante.dao;

import brcomncn.estante.config.DatabaseConnection;
import brcomncn.estante.model.Emprestimo;
import brcomncn.estante.model.Livro;
import brcomncn.estante.model.Amigo;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmprestimoDAO {
    private final LivroDAO livroDAO;
    private final AmigoDAO amigoDAO;

    public EmprestimoDAO() {
        this.livroDAO = new LivroDAO();
        this.amigoDAO = new AmigoDAO();
    }

    public Emprestimo salvar(Emprestimo emprestimo) throws SQLException {
        String sql = "INSERT INTO emprestimos (livro_id, amigo_id, data_emprestimo, " +
                "data_devolucao_prevista, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Inicia transação
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setLong(1, emprestimo.getLivro().getId());
                stmt.setLong(2, emprestimo.getAmigo().getId());
                stmt.setTimestamp(3, Timestamp.valueOf(emprestimo.getDataEmprestimo()));
                stmt.setTimestamp(4, Timestamp.valueOf(emprestimo.getDataDevolucaoPrevista()));
                stmt.setString(5, emprestimo.getStatus().name());

                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        emprestimo.setId(rs.getLong(1));
                    }
                }

                // Atualiza status do livro
                atualizarStatusLivro(conn, emprestimo.getLivro().getId(),
                        Livro.StatusEmprestimo.EMPRESTADO);

                conn.commit();
                return emprestimo;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public void registrarDevolucao(Long emprestimoId, LocalDateTime dataDevolucao) throws SQLException {
        String sql = "UPDATE emprestimos SET data_devolucao_real = ?, status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setTimestamp(1, Timestamp.valueOf(dataDevolucao));
                stmt.setString(2, Emprestimo.StatusEmprestimo.DEVOLVIDO.name());
                stmt.setLong(3, emprestimoId);

                stmt.executeUpdate();

                // Busca o ID do livro
                Optional<Emprestimo> emprestimo = buscarPorId(emprestimoId);
                if (emprestimo.isPresent()) {
                    // Atualiza status do livro
                    atualizarStatusLivro(conn, emprestimo.get().getLivro().getId(),
                            Livro.StatusEmprestimo.DISPONIVEL);
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public Optional<Emprestimo> buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM emprestimos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(criarEmprestimoDoResultSet(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Emprestimo> listarEmprestimosAtivos() throws SQLException {
        List<Emprestimo> emprestimos = new ArrayList<>();
        String sql = "SELECT * FROM emprestimos WHERE status = 'ATIVO' ORDER BY data_devolucao_prevista";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                emprestimos.add(criarEmprestimoDoResultSet(rs));
            }
        }
        return emprestimos;
    }

    public List<Emprestimo> listarEmprestimosPorAmigo(Long amigoId) throws SQLException {
        List<Emprestimo> emprestimos = new ArrayList<>();
        String sql = "SELECT * FROM emprestimos WHERE amigo_id = ? ORDER BY data_emprestimo DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, amigoId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    emprestimos.add(criarEmprestimoDoResultSet(rs));
                }
            }
        }
        return emprestimos;
    }

    private void atualizarStatusLivro(Connection conn, Long livroId,
                                      Livro.StatusEmprestimo status) throws SQLException {
        String sql = "UPDATE livros SET status_emprestimo = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setLong(2, livroId);
            stmt.executeUpdate();
        }
    }

    private Emprestimo criarEmprestimoDoResultSet(ResultSet rs) throws SQLException {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setId(rs.getLong("id"));

        // Carrega livro e amigo
        Long livroId = rs.getLong("livro_id");
        Long amigoId = rs.getLong("amigo_id");

        livroDAO.buscarPorId(livroId).ifPresent(emprestimo::setLivro);
        amigoDAO.buscarPorId(amigoId).ifPresent(emprestimo::setAmigo);

        emprestimo.setDataEmprestimo(rs.getTimestamp("data_emprestimo").toLocalDateTime());
        emprestimo.setDataDevolucaoPrevista(rs.getTimestamp("data_devolucao_prevista").toLocalDateTime());

        Timestamp dataDevolucaoReal = rs.getTimestamp("data_devolucao_real");
        if (dataDevolucaoReal != null) {
            emprestimo.setDataDevolucaoReal(dataDevolucaoReal.toLocalDateTime());
        }

        emprestimo.setStatus(Emprestimo.StatusEmprestimo.valueOf(rs.getString("status")));

        return emprestimo;
    }
}