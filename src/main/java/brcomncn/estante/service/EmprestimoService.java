package brcomncn.estante.service;

import brcomncn.estante.dao.EmprestimoDAO;
import brcomncn.estante.dao.LivroDAO;
import brcomncn.estante.dao.AmigoDAO;
import brcomncn.estante.model.Emprestimo;
import brcomncn.estante.model.Livro;
import brcomncn.estante.model.Amigo;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class EmprestimoService {
    private final EmprestimoDAO emprestimoDAO;
    private final LivroDAO livroDAO;
    private final AmigoDAO amigoDAO;

    public EmprestimoService() {
        this.emprestimoDAO = new EmprestimoDAO();
        this.livroDAO = new LivroDAO();
        this.amigoDAO = new AmigoDAO();
    }

    public Emprestimo realizarEmprestimo(String isbn, String email, int diasEmprestimo)
            throws SQLException {
        // Buscar livro e amigo
        Optional<Livro> livroOpt = livroDAO.buscarPorIsbn(isbn);
        if (!livroOpt.isPresent()) {
            throw new IllegalArgumentException("Livro não encontrado");
        }
        Livro livro = livroOpt.get();

        Optional<Amigo> amigoOpt = amigoDAO.buscarPorEmail(email);
        if (!amigoOpt.isPresent()) {
            throw new IllegalArgumentException("Amigo não encontrado");
        }
        Amigo amigo = amigoOpt.get();

        // Verificar se o livro está disponível
        if (Livro.StatusEmprestimo.EMPRESTADO.equals(livro.getStatusEmprestimo())) {
            throw new IllegalStateException("Livro já está emprestado");
        }

        // Criar novo empréstimo
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setLivro(livro);
        emprestimo.setAmigo(amigo);
        emprestimo.setDataEmprestimo(LocalDateTime.now());
        emprestimo.setDataDevolucaoPrevista(LocalDateTime.now().plusDays(diasEmprestimo));
        emprestimo.setStatus(Emprestimo.StatusEmprestimo.ATIVO);

        // Atualizar status do livro
        livro.setStatusEmprestimo(Livro.StatusEmprestimo.EMPRESTADO);
        livroDAO.atualizar(livro);

        // Salvar empréstimo
        return emprestimoDAO.salvar(emprestimo);
    }

    public Emprestimo realizarDevolucao(String isbn) throws SQLException {
        // Buscar livro
        Optional<Livro> livroOpt = livroDAO.buscarPorIsbn(isbn);
        if (!livroOpt.isPresent()) {
            throw new IllegalArgumentException("Livro não encontrado");
        }
        Livro livro = livroOpt.get();

        // Buscar empréstimo ativo do livro
        Optional<Emprestimo> emprestimoOpt = emprestimoDAO.buscarEmprestimoAtivoPorLivro(livro);
        if (!emprestimoOpt.isPresent()) {
            throw new IllegalArgumentException("Não há empréstimo ativo para este livro");
        }
        Emprestimo emprestimo = emprestimoOpt.get();

        // Atualizar empréstimo
        emprestimo.setDataDevolucaoReal(LocalDateTime.now());
        emprestimo.setStatus(Emprestimo.StatusEmprestimo.DEVOLVIDO);

        // Atualizar status do livro
        livro.setStatusEmprestimo(Livro.StatusEmprestimo.DISPONIVEL);
        livroDAO.atualizar(livro);

        // Salvar alterações do empréstimo
        return emprestimoDAO.atualizar(emprestimo);
    }

    public List<Emprestimo> listarEmprestimosAtivos() throws SQLException {
        return emprestimoDAO.listarEmprestimosAtivos();
    }

    public List<Emprestimo> listarTodos() throws SQLException {
        return emprestimoDAO.listarTodos();
    }

    public void verificarAtrasos() throws SQLException {
        List<Emprestimo> emprestimosAtivos = emprestimoDAO.listarEmprestimosAtivos();
        LocalDateTime agora = LocalDateTime.now();

        for (Emprestimo emprestimo : emprestimosAtivos) {
            if (agora.isAfter(emprestimo.getDataDevolucaoPrevista())) {
                emprestimo.setStatus(Emprestimo.StatusEmprestimo.ATRASADO);
                emprestimoDAO.atualizar(emprestimo);
            }
        }
    }

    private void validarEmprestimo(Emprestimo emprestimo) {
        if (emprestimo == null) {
            throw new IllegalArgumentException("Empréstimo não pode ser nulo");
        }
        if (emprestimo.getLivro() == null) {
            throw new IllegalArgumentException("Livro é obrigatório");
        }
        if (emprestimo.getAmigo() == null) {
            throw new IllegalArgumentException("Amigo é obrigatório");
        }
        if (emprestimo.getDataEmprestimo() == null) {
            throw new IllegalArgumentException("Data de empréstimo é obrigatória");
        }
        if (emprestimo.getDataDevolucaoPrevista() == null) {
            throw new IllegalArgumentException("Data de devolução prevista é obrigatória");
        }
        if (emprestimo.getDataDevolucaoPrevista().isBefore(emprestimo.getDataEmprestimo())) {
            throw new IllegalArgumentException("Data de devolução prevista não pode ser anterior à data de empréstimo");
        }
    }
}