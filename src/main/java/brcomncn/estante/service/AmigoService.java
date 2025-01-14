package brcomncn.estante.service;

import brcomncn.estante.dao.AmigoDAO;
import brcomncn.estante.model.Amigo;
import java.sql.SQLException;
import java.util.List;

public class AmigoService {
    private final AmigoDAO amigoDAO;

    public AmigoService() {
        this.amigoDAO = new AmigoDAO();
    }

    public Amigo cadastrarAmigo(Amigo amigo) throws SQLException {
        validarAmigo(amigo);

        // Verificar se já existe um amigo com este email
        if (amigoDAO.buscarPorEmail(amigo.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Já existe um amigo cadastrado com este email");
        }

        return amigoDAO.salvar(amigo);
    }

    public List<Amigo> listarTodos() throws SQLException {
        return amigoDAO.listarTodos();
    }

    public Amigo buscarPorId(Long id) throws SQLException {
        return amigoDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Amigo não encontrado"));
    }

    public Amigo buscarPorEmail(String email) throws SQLException {
        return amigoDAO.buscarPorEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Amigo não encontrado"));
    }

    private void validarAmigo(Amigo amigo) {
        if (amigo == null) {
            throw new IllegalArgumentException("Amigo não pode ser nulo");
        }
        if (amigo.getNome() == null || amigo.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do amigo é obrigatório");
        }
        if (amigo.getEmail() == null || amigo.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email do amigo é obrigatório");
        }
        if (!amigo.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email inválido");
        }
        if (amigo.getTelefone() == null || amigo.getTelefone().trim().isEmpty()) {
            throw new IllegalArgumentException("Telefone do amigo é obrigatório");
        }
    }
}
