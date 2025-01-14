package brcomncn.estante;

import brcomncn.estante.model.Livro;
import brcomncn.estante.service.IsbnService;
import brcomncn.estante.dao.LivroDAO;
import java.util.Scanner;
import java.time.LocalDateTime;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final LivroDAO livroDAO = new LivroDAO();
    private static final IsbnService isbnService = new IsbnService();

    public static void main(String[] args) {
        while (true) {
            exibirMenu();
            int opcao = scanner.nextInt();
            scanner.nextLine(); // Limpa o buffer

            switch (opcao) {
                case 1:
                    cadastrarLivro();
                    break;
                case 2:
                    cadastrarAmigo();
                    break;
                case 3:
                    emprestarLivro();
                    break;
                case 4:
                    devolverLivro();
                    break;
                case 5:
                    listarLivros();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private static void exibirMenu() {
        System.out.println("\n=== ESTANTE DE LIVROS ===");
        System.out.println("1. Cadastrar novo livro");
        System.out.println("2. Cadastrar amigo");
        System.out.println("3. Emprestar livro");
        System.out.println("4. Devolver livro");
        System.out.println("5. Listar livros");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static void cadastrarLivro() {
        try {
            System.out.print("Digite o ISBN do livro: ");
            String isbn = scanner.nextLine();

            // Verifica se o livro já existe
            if (livroDAO.buscarPorIsbn(isbn).isPresent()) {
                System.out.println("Livro já cadastrado!");
                return;
            }

            // Busca informações na API
            IsbnService.LivroInfo info = isbnService.buscarIsbn(isbn);

            // Cria e salva o livro
            Livro livro = new Livro();
            livro.setIsbn(isbn);
            livro.setTitulo(info.getTitle());
            livro.setAutor(String.join(", ", info.getAuthors()));
            livro.setEditora(info.getPublisher());
            livro.setAnoPublicacao(info.getYear());
            livro.setNumPaginas(info.getPages());
            livro.setSinopse(info.getSynopsis());

            livroDAO.salvar(livro);
            System.out.println("Livro cadastrado com sucesso!");

        } catch (Exception e) {
            System.out.println("Erro ao cadastrar livro: " + e.getMessage());
        }
    }

    private static void cadastrarAmigo() {
        System.out.print("Digite o nome do amigo: ");
        String nome = scanner.nextLine();
        System.out.print("Digite o telefone do amigo: ");
        String telefone = scanner.nextLine();
        
        // Aqui podemos utilizar um DAO específico para amigos caso implementado
        System.out.println("Amigo " + nome + " cadastrado com sucesso!");
    }
    
    private static void emprestarLivro() {
        try {
            System.out.print("Digite o ISBN do livro a ser emprestado: ");
            String isbn = scanner.nextLine();
            System.out.print("Digite o nome do amigo: ");
            String nomeAmigo = scanner.nextLine();
    
            Livro livro = livroDAO.buscarPorIsbn(isbn)
                                  .orElseThrow(() -> new IllegalArgumentException("Livro não encontrado!"));
    
            if (livro.isEmprestado()) {
                System.out.println("Livro já está emprestado!");
                return;
            }
    
            livro.setEmprestado(true);
            livro.setAmigo(nomeAmigo);
            livroDAO.atualizar(livro);
    
            System.out.println("Livro emprestado com sucesso a " + nomeAmigo + "!");
        } catch (Exception e) {
            System.out.println("Erro ao emprestar livro: " + e.getMessage());
        }
    }
    
    private static void devolverLivro() {
        try {
            System.out.print("Digite o ISBN do livro a ser devolvido: ");
            String isbn = scanner.nextLine();
    
            Livro livro = livroDAO.buscarPorIsbn(isbn)
                                  .orElseThrow(() -> new IllegalArgumentException("Livro não encontrado!"));
    
            if (!livro.isEmprestado()) {
                System.out.println("Este livro não está emprestado!");
                return;
            }
    
            livro.setEmprestado(false);
            livro.setAmigo(null);
            livroDAO.atualizar(livro);
    
            System.out.println("Livro devolvido com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao devolver livro: " + e.getMessage());
        }
    }
    
    private static void listarLivros() {
        System.out.println("\n=== Lista de Livros ===");
        livroDAO.buscarTodos().forEach(livro -> {
            System.out.println("Título: " + livro.getTitulo());
            System.out.println("Autor: " + livro.getAutor());
            System.out.println("ISBN: " + livro.getIsbn());
            System.out.println("Situação: " + (livro.isEmprestado() ? "Emprestado" : "Disponível"));
            if (livro.isEmprestado()) {
                System.out.println("Emprestado para: " + livro.getAmigo());
            }
            System.out.println("------------------------");
        });
    }
    
    
}