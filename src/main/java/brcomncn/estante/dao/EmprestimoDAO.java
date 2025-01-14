package brcomncn.estante.dao;

import brcomncn.estante.model.Emprestimo;
import brcomncn.estante.model.Livro;
import brcomncn.estante.model.Amigo;
import brcomncn.estante.util.ConnectionFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmprestimoDAO {
    private final ConnectionFactory connectionFactory;
    private final LivroDAO livroDAO;
    private final AmigoDAO amigoDAO;

    public EmprestimoDAO() {
        this.connectionFactory = new ConnectionFactory();
        this.livroDAO = new LivroDAO();
        this.amigoDAO = new AmigoDAO();
    }

    public Emprestimo salvar(Emprestimo emprestimo) throws SQLException {
        emprestimo.validar();

        String sql = "INSERT INTO emprestimos (livro_id, amigo_id, data_emprestimo, data_devolucao_prevista, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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

            return emprestimo;
        }
    }

    public Emprestimo atualizar(Emprestimo emprestimo) throws SQLException {
        emprestimo.validar();

        String sql = "UPDATE emprestimos SET data_devolucao_real = ?, status = ? WHERE id = ?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (emprestimo.getDataDevolucaoReal() != null) {
                stmt.setTimestamp(1, Timestamp.valueOf(emprestimo.getDataDevolucaoReal()));
            } else {
                stmt.setNull(1, Types.TIMESTAMP);
            }
            stmt.setString(2, emprestimo.getStatus().name());
            stmt.setLong(3, emprestimo.getId());

            stmt.executeUpdate();
            return emprestimo;
        }
    }

    public Optional<Emprestimo> buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM emprestimos WHERE id = ?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(construirEmprestimo(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Emprestimo> buscarEmprestimoAtivoPorLivro(Livro livro) throws SQLException {
        String sql = "SELECT * FROM emprestimos WHERE livro_id = ? AND status = 'ATIVO'";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, livro.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(construirEmprestimo(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Emprestimo> listarEmprestimosAtivos() throws SQLException {
        String sql = "SELECT * FROM emprestimos WHERE status = 'ATIVO'";
        List<Emprestimo> emprestimos = new ArrayList<>();

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                emprestimos.add(construirEmprestimo(rs));
            }
        }
        return emprestimos;
    }

    public List<Emprestimo> listarTodos() throws SQLException {
        String sql = "SELECT * FROM emprestimos ORDER BY data_emprestimo DESC";
        List<Emprestimo> emprestimos = new ArrayList<>();

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                emprestimos.add(construirEmprestimo(rs));
            }
        }
        return emprestimos;
    }

    private Emprestimo construirEmprestimo(ResultSet rs) throws SQLException {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setId(rs.getLong("id"));

        // Buscar livro e amigo
        Livro livro = livroDAO.buscarPorId(rs.getLong("livro_id"))
                .orElseThrow(() -> new SQLException("Livro não encontrado"));
        Amigo amigo = amigoDAO.buscarPorId(rs.getLong("amigo_id"))
                .orElseThrow(() -> new SQLException("Amigo não encontrado"));

        emprestimo.setLivro(livro);
        emprestimo.setAmigo(amigo);

        emprestimo.setDataEmprestimo(rs.getTimestamp("data_emprestimo").toLocalDateTime());
        emprestimo.setDataDevolucaoPrevista(rs.getTimestamp("data_devolucao_prevista").toLocalDateTime());

        Timestamp dataDevolucaoReal = rs.getTimestamp("data_devolucao_real");
        if (dataDevolucaoReal != null) {
            emprestimo.setDataDevolucaoReal(dataDevolucaoReal.toLocalDateTime());
        }

        emprestimo.setStatus(Emprestimo.StatusEmprestimo.valueOf(rs.getString("status")));

        return emprestimo;
    }

    public void deletar(Long id) throws SQLException {
        String sql = "DELETE FROM emprestimos WHERE id = ?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
}