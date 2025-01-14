package brcomncn.estante.dao;

import brcomncn.estante.model.Livro;
import brcomncn.estante.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LivroDAO {
    private final ConnectionFactory connectionFactory;

    public LivroDAO() {
        this.connectionFactory = new ConnectionFactory();
    }

    public List<Livro> listarTodos() throws SQLException {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT * FROM livros ORDER BY titulo";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                try {
                    Livro livro = construirLivro(rs);
                    livros.add(livro);
                } catch (Exception e) {
                    System.err.println("Erro ao construir livro do ResultSet: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar livros: " + e.getMessage());
            throw e;
        }
        return livros;
    }

    private Livro construirLivro(ResultSet rs) throws SQLException {
        try {
            Livro livro = new Livro();

            livro.setId(rs.getLong("id"));
            livro.setIsbn(rs.getString("isbn"));
            livro.setTitulo(rs.getString("titulo"));
            livro.setAutor(rs.getString("autor"));
            livro.setEditora(rs.getString("editora"));
            livro.setAnoPublicacao(rs.getInt("ano_publicacao"));
            livro.setNumPaginas(rs.getInt("num_paginas"));

            String status = rs.getString("status_emprestimo");
            if (status != null) {
                try {
                    livro.setStatusEmprestimo(Livro.StatusEmprestimo.valueOf(status));
                } catch (IllegalArgumentException e) {
                    livro.setStatusEmprestimo(Livro.StatusEmprestimo.DISPONIVEL);
                }
            } else {
                livro.setStatusEmprestimo(Livro.StatusEmprestimo.DISPONIVEL);
            }

            return livro;
        } catch (SQLException e) {
            System.err.println("Erro ao construir livro: " + e.getMessage());
            throw e;
        }
    }

    public Optional<Livro> buscarPorIsbn(String isbn) {
        String sql = "SELECT * FROM livros WHERE isbn = ?";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, isbn);
    
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Livro livro = construirLivro(rs);
                    return Optional.of(livro);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar livro por ISBN: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
        
    }

    public Livro salvar(Livro livro) {
        String sqlInsert = "INSERT INTO livros (isbn, titulo, autor, editora, ano_publicacao, num_paginas, status_emprestimo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlUpdate = "UPDATE livros SET isbn = ?, titulo = ?, autor = ?, editora = ?, ano_publicacao = ?, num_paginas = ?, status_emprestimo = ? WHERE id = ?";
    
        try (Connection conn = connectionFactory.getConnection()) {
            if (livro.getId() == null) {
                try (PreparedStatement stmt = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, livro.getIsbn());
                    stmt.setString(2, livro.getTitulo());
                    stmt.setString(3, livro.getAutor());
                    stmt.setString(4, livro.getEditora());
                    stmt.setInt(5, livro.getAnoPublicacao());
                    stmt.setInt(6, livro.getNumPaginas());
                    stmt.setString(7, livro.getStatusEmprestimo().name());
    
                    stmt.executeUpdate();
    
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            livro.setId(rs.getLong(1));
                        }
                    }
                }
            } else {
                try (PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
                    stmt.setString(1, livro.getIsbn());
                    stmt.setString(2, livro.getTitulo());
                    stmt.setString(3, livro.getAutor());
                    stmt.setString(4, livro.getEditora());
                    stmt.setInt(5, livro.getAnoPublicacao());
                    stmt.setInt(6, livro.getNumPaginas());
                    stmt.setString(7, livro.getStatusEmprestimo().name());
                    stmt.setLong(8, livro.getId());
    
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar livro: " + e.getMessage());
            e.printStackTrace();
        }
        return livro;
    }

    public Optional<Livro> buscarPorId(long livroId) {
        String sql = "SELECT * FROM livros WHERE id = ?";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setLong(1, livroId);
    
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Livro livro = construirLivro(rs);
                    return Optional.of(livro);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar livro por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void atualizar(Livro livro) {
        String sqlUpdate = "UPDATE livros SET isbn = ?, titulo = ?, autor = ?, editora = ?, ano_publicacao = ?, num_paginas = ?, status_emprestimo = ? WHERE id = ?";
    
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
            
            stmt.setString(1, livro.getIsbn());
            stmt.setString(2, livro.getTitulo());
            stmt.setString(3, livro.getAutor());
            stmt.setString(4, livro.getEditora());
            stmt.setInt(5, livro.getAnoPublicacao());
            stmt.setInt(6, livro.getNumPaginas());
            stmt.setString(7, livro.getStatusEmprestimo().name());
            stmt.setLong(8, livro.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar livro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
