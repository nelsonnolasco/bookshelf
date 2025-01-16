package brcomncn.estante;


import brcomncn.estante.model.Amigo;
import brcomncn.estante.model.Emprestimo;
import brcomncn.estante.model.Livro;
import brcomncn.estante.service.AmigoService;
import brcomncn.estante.service.BooksApiService;
import brcomncn.estante.service.EmprestimoService;
import brcomncn.estante.service.LivroService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final LivroService livroService = new LivroService();
    private static final AmigoService amigoService = new AmigoService();
    private static final EmprestimoService emprestimoService = new EmprestimoService();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final BooksApiService booksApiService = new BooksApiService();

    public static void main(String[] args) {
        boolean continuar = true;

        while (continuar) {
            try {
                exibirMenu();
                int opcao = Integer.parseInt(scanner.nextLine());

                switch (opcao) {
                    case 1:
                        cadastrarLivro();
                        break;
                    case 2:
                        cadastrarAmigo();
                        break;
                    case 3:
                        realizarEmprestimo();
                        break;
                    case 4:
                        realizarDevolucao();
                        break;
                    case 5:
                        listarLivros();
                        break;
                    case 6:
                        listarAmigos();
                        break;
                    case 7:
                        listarEmprestimos();
                        break;
                    case 8:
                        System.out.println("Saindo do sistema...");
                        continuar = false;
                        break;
                    default:
                        System.out.println("Opção inválida!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, digite um número válido.");
            } catch (SQLException e) {
                System.out.println(STR."Erro ao acessar o banco de dados: \{e.getMessage()}");
            } catch (Exception e) {
                System.out.println(STR."Erro: \{e.getMessage()}");
            }
        }
        scanner.close();
    }

    private static void exibirMenu() {
        System.out.println("\n=== SISTEMA DE BIBLIOTECA PESSOAL ===");
        System.out.println("1. Cadastrar Livro");
        System.out.println("2. Cadastrar Amigo");
        System.out.println("3. Realizar Empréstimo");
        System.out.println("4. Realizar Devolução");
        System.out.println("5. Listar Livros");
        System.out.println("6. Listar Amigos");
        System.out.println("7. Listar Empréstimos");
        System.out.println("8. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static void cadastrarLivro() throws SQLException {
        System.out.println("\n=== CADASTRO DE LIVRO ===");

        System.out.print("ISBN: ");
        String isbn = scanner.nextLine().trim();

        try {
            Livro livro = booksApiService.buscarLivroPorIsbn(isbn);

            // Mostra os dados encontrados e permite edição
            System.out.println("\nDados encontrados:");
            System.out.println(STR."Título: \{livro.getTitulo()}");
            System.out.println(STR."Autor: \{livro.getAutor()}");
            System.out.println(STR."Editora: \{livro.getEditora()}");
            System.out.println(STR."Ano de Publicação: \{livro.getAnoPublicacao()}");
            System.out.println(STR."Número de Páginas: \{livro.getNumPaginas()}");

            System.out.println("\nDeseja editar algum dado? (S/N)");
            String resposta = scanner.nextLine().trim().toUpperCase();

            if (resposta.equals("S")) {
                System.out.println("\nPressione ENTER para manter o valor atual");

                System.out.print(STR."Título (\{livro.getTitulo()}): ");
                String novoTitulo = scanner.nextLine().trim();
                if (!novoTitulo.isEmpty()) {
                    livro.setTitulo(novoTitulo);
                }

                System.out.print(STR."Autor (\{livro.getAutor()}): ");
                String novoAutor = scanner.nextLine().trim();
                if (!novoAutor.isEmpty()) {
                    livro.setAutor(novoAutor);
                }

                System.out.print(STR."Editora (\{livro.getEditora()}): ");
                String novaEditora = scanner.nextLine().trim();
                if (!novaEditora.isEmpty()) {
                    livro.setEditora(novaEditora);
                }

                System.out.print(STR."Ano de Publicação (\{livro.getAnoPublicacao()}): ");
                String novoAno = scanner.nextLine().trim();
                if (!novoAno.isEmpty()) {
                    livro.setAnoPublicacao(Integer.parseInt(novoAno));
                }

                System.out.print(STR."Número de Páginas (\{livro.getNumPaginas()}): ");
                String novasPaginas = scanner.nextLine().trim();
                if (!novasPaginas.isEmpty()) {
                    livro.setNumPaginas(Integer.parseInt(novasPaginas));
                }
            }

            livroService.cadastrarLivro(livro);
            System.out.println("Livro cadastrado com sucesso!");

        } catch (IOException | InterruptedException e) {
            System.out.println(STR."Erro ao buscar dados do livro: \{e.getMessage()}");
            System.out.println("Deseja cadastrar manualmente? (S/N)");
            String resposta = scanner.nextLine().trim().toUpperCase();

            if (resposta.equals("S")) {
                cadastrarLivroManualmente(isbn);
            }
        } catch (IllegalArgumentException e) {
            System.out.println(STR."Erro ao cadastrar livro: \{e.getMessage()}");
        }
    }

    private static void cadastrarLivroManualmente(String isbn) throws SQLException {
        System.out.println("\n=== CADASTRO MANUAL DE LIVRO ===");

        System.out.print("Título: ");
        String titulo = scanner.nextLine().trim();

        System.out.print("Autor: ");
        String autor = scanner.nextLine().trim();

        System.out.print("Editora: ");
        String editora = scanner.nextLine().trim();

        System.out.print("Ano de Publicação: ");
        int anoPublicacao = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Número de Páginas: ");
        int numPaginas = Integer.parseInt(scanner.nextLine().trim());

        Livro livro = new Livro();
        livro.setIsbn(isbn);
        livro.setTitulo(titulo);
        livro.setAutor(autor);
        livro.setEditora(editora);
        livro.setAnoPublicacao(anoPublicacao);
        livro.setNumPaginas(numPaginas);
        livro.setStatusEmprestimo(Livro.StatusEmprestimo.DISPONIVEL);

        livroService.cadastrarLivro(livro);
        System.out.println("Livro cadastrado com sucesso!");
    }

    private static void cadastrarAmigo() throws SQLException {
        System.out.println("\n=== CADASTRO DE AMIGO ===");

        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Telefone: ");
        String telefone = scanner.nextLine().trim();

        try {
            Amigo amigo = new Amigo();
            amigo.setNome(nome);
            amigo.setEmail(email);
            amigo.setTelefone(telefone);
            amigo.setAtivo(true);

            amigoService.cadastrarAmigo(amigo);
            System.out.println("Amigo cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println(STR."Erro ao cadastrar amigo: \{e.getMessage()}");
        }
    }

    private static void realizarEmprestimo() throws SQLException {
        System.out.println("\n=== REALIZAR EMPRÉSTIMO ===");

        System.out.print("ISBN do livro: ");
        String isbn = scanner.nextLine().trim();

        System.out.print("Email do amigo: ");
        String email = scanner.nextLine().trim();

        System.out.print("Dias para devolução: ");
        int dias = Integer.parseInt(scanner.nextLine().trim());

        try {
            emprestimoService.realizarEmprestimo(isbn, email, dias);
            System.out.println("Empréstimo realizado com sucesso!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(STR."Erro ao realizar empréstimo: \{e.getMessage()}");
        }
    }

    private static void realizarDevolucao() throws SQLException {
        System.out.println("\n=== REALIZAR DEVOLUÇÃO ===");

        System.out.print("ISBN do livro: ");
        String isbn = scanner.nextLine().trim();

        try {
            emprestimoService.realizarDevolucao(isbn);
            System.out.println("Devolução realizada com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println(STR."Erro ao realizar devolução: \{e.getMessage()}");
        }
    }

    private static void listarLivros() throws SQLException {
        System.out.println("\n=== LISTA DE LIVROS ===");
        List<Livro> livros = livroService.listarTodos();

        if (livros.isEmpty()) {
            System.out.println("Nenhum livro cadastrado.");
            return;
        }

        for (Livro livro : livros) {
            System.out.println(STR."""

ISBN: \{livro.getIsbn()}""");
            System.out.println(STR."Título: \{livro.getTitulo()}");
            System.out.println(STR."Autor: \{livro.getAutor()}");
            System.out.println(STR."Editora: \{livro.getEditora()}");
            System.out.println(STR."Ano de Publicação: \{livro.getAnoPublicacao()}");
            System.out.println(STR."Número de Páginas: \{livro.getNumPaginas()}");
            System.out.println(STR."Status: \{livro.getStatusEmprestimo().getDescricao()}");
            System.out.println("------------------------");
        }
    }

    private static void listarAmigos() throws SQLException {
        System.out.println("\n=== LISTA DE AMIGOS ===");
        List<Amigo> amigos = amigoService.listarTodos();

        if (amigos.isEmpty()) {
            System.out.println("Nenhum amigo cadastrado.");
            return;
        }

        for (Amigo amigo : amigos) {
            System.out.println(STR."""

Nome: \{amigo.getNome()}""");
            System.out.println(STR."Email: \{amigo.getEmail()}");
            System.out.println(STR."Telefone: \{amigo.getTelefone()}");
            System.out.println(STR."Status: \{amigo.isAtivo() ? "Ativo" : "Inativo"}");
            System.out.println("------------------------");
        }
    }

    private static void listarEmprestimos() throws SQLException {
        System.out.println("\n=== LISTA DE EMPRÉSTIMOS ===");
        List<Emprestimo> emprestimos = emprestimoService.listarTodos();

        if (emprestimos.isEmpty()) {
            System.out.println("Nenhum empréstimo registrado.");
            return;
        }

        for (Emprestimo emprestimo : emprestimos) {
            System.out.println(STR."""

Livro: \{emprestimo.getLivro().getTitulo()}""");
            System.out.println(STR."Amigo: \{emprestimo.getAmigo().getNome()}");
            System.out.println(STR."Data Empréstimo: \{emprestimo.getDataEmprestimo().format(formatter)}");
            System.out.println(STR."Data Devolução Prevista: \{emprestimo.getDataDevolucaoPrevista().format(formatter)}");
            if (emprestimo.getDataDevolucaoReal() != null) {
                System.out.println(STR."Data Devolução Real: \{emprestimo.getDataDevolucaoReal().format(formatter)}");
            }
            System.out.println(STR."Status: \{emprestimo.getStatus().getDescricao()}");
            System.out.println("------------------------");
        }
    }
}