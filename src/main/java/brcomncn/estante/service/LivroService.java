package brcomncn.estante.service;

import brcomncn.estante.dao.LivroDAO;
import brcomncn.estante.model.IsbnResponse;
import brcomncn.estante.model.Livro;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LivroService {
    private final IsbnApiService isbnApiService;
    private final LivroDAO livroDAO;

    public LivroService() {
        this.isbnApiService = new IsbnApiService();
        this.livroDAO = new LivroDAO();
    }

    public Livro cadastrarLivroPorIsbn(String isbn) throws IOException, SQLException {
        // Verifica se o livro já existe
        if (livroDAO.buscarPorIsbn(isbn).isPresent()) {
            throw new IllegalArgumentException("Livro já cadastrado com este ISBN");
        }

        // Busca informações na API
        IsbnResponse isbnResponse = isbnApiService.buscarLivroPorIsbn(isbn);

        // Converte para objeto Livro
        Livro livro = isbnApiService.converterParaLivro(isbnResponse);

        // Salva no banco de dados
        return livroDAO.salvar(livro);
    }

    public void cadastrarLivro(Livro livro) throws SQLException {
        // Verifica se o livro já existe no banco de dados
        if (livroDAO.buscarPorIsbn(livro.getIsbn()).isPresent()) {
            throw new IllegalArgumentException("Livro já cadastrado com este ISBN");
        }
    
        // Salva o livro no banco de dados
        livroDAO.salvar(livro);
    }

    public List<Livro> listarTodos() throws SQLException {
        return livroDAO.listarTodos();
    }
}
