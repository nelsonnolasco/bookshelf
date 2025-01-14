package brcomncn.estante.dao;

import brcomncn.estante.config.DatabaseConnection;
import brcomncn.estante.model.Livro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LivroDAO {

    public Livro salvar(Livro livro) throws SQLException {
        String sql = "INSERT INTO livros (isbn, titulo, autor, editora, ano_publicacao, " +
                "num_paginas, sinopse) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, livro.getIsbn());
            stmt.setString(2, livro.getTitulo());
            stmt.setString(3, livro.getAutor());
            stmt.setString(4, livro.getEditora());
            stmt.setInt(5, livro.getAnoPublicacao());
            stmt.setInt(6, livro.getNumPaginas());
            stmt.setString(7, livro.getSinopse());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    livro.setId(rs.getLong(1));
                }
            }

            return livro;
        }
    }

    public Optional<Livro> buscarPorIsbn(String isbn) throws SQLException {
        String sql = "SELECT * FROM livros WHERE isbn = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, isbn);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(criarLivroDoResultSet(rs));
                }
            }
        }
        return Optional.empty();
    }

    private Livro criarLivroDoResultSet(ResultSet rs) throws SQLException {
        Livro livro = new Livro();
        livro.setId(rs.getLong("id"));
        livro.setIsbn(rs.getString("isbn"));
        livro.setTitulo(rs.getString("titulo"));
        livro.setAutor(rs.getString("autor"));
        livro.setEditora(rs.getString("editora"));
        livro.setAnoPublicacao(rs.getInt("ano_publicacao"));
        livro.setNumPaginas(rs.getInt("num_paginas"));
        livro.setSinopse(rs.getString("sinopse"));
        livro.setStatusEmprestimo(
                Livro.StatusEmprestimo.valueOf(rs.getString("status_emprestimo"))
        );
        return livro;
    }
    
    

    public List<Livro> listar() throws SQLException {
        String sql = "SELECT * FROM livros";
        List<Livro> livros = new ArrayList<>();
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
    
            while (rs.next()) {
                livros.add(criarLivroDoResultSet(rs));
            }
        }
        return livros;
    }
    
    public boolean atualizar(Livro livro) throws SQLException {
        String sql = "UPDATE livros SET isbn = ?, titulo = ?, autor = ?, editora = ?, ano_publicacao = ?, " +
                "num_paginas = ?, sinopse = ?, status_emprestimo = ? WHERE id = ?";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, livro.getIsbn());
            stmt.setString(2, livro.getTitulo());
            stmt.setString(3, livro.getAutor());
            stmt.setString(4, livro.getEditora());
            stmt.setInt(5, livro.getAnoPublicacao());
            stmt.setInt(6, livro.getNumPaginas());
            stmt.setString(7, livro.getSinopse());
            stmt.setString(8, livro.getStatusEmprestimo().name());
            stmt.setLong(9, livro.getId());
    
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean deletar(long id) throws SQLException {
        String sql = "DELETE FROM livros WHERE id = ?";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setLong(1, id);
    
            return stmt.executeUpdate() > 0;
        }
    }

    public Optional<Livro> buscarPorId(Long livroId) {
        return Optional.empty();
    }

    public Iterable<Object> buscarTodos() {
        return null;
    }
}

// Implementar AmigoDAO e EmprestimoDAO de forma similar
