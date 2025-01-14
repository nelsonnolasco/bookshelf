package brcomncn.estante.test;

import brcomncn.estante.model.IsbnResponse;
import brcomncn.estante.model.Livro;
import brcomncn.estante.service.IsbnApiService;

public class IsbnApiServiceTest {
    public static void main(String[] args) {
        IsbnApiService service = new IsbnApiService();

        // ISBN para teste
        String isbn = "9788535914849"; // Metamorfose - Franz Kafka

        try {
            System.out.println("Testando ISBN: " + isbn);
            System.out.println("Buscando informações...");

            IsbnResponse response = service.buscarLivroPorIsbn(isbn);
            Livro livro = service.converterParaLivro(response);

            System.out.println("\nInformações encontradas:");
            System.out.println("Título: " + livro.getTitulo());
            System.out.println("Autor: " + livro.getAutor());
            System.out.println("Editora: " + livro.getEditora());
            System.out.println("Ano: " + livro.getAnoPublicacao());
            System.out.println("Páginas: " + livro.getNumPaginas());

        } catch (Exception e) {
            System.out.println("Erro ao buscar ISBN " + isbn + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}