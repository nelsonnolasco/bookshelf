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
            System.out.println(STR."Título: \{livro.getTitulo()}");
            System.out.println(STR."Autor: \{livro.getAutor()}");
            System.out.println(STR."Editora: \{livro.getEditora()}");
            System.out.println(STR."Ano: \{livro.getAnoPublicacao()}");
            System.out.println(STR."Páginas: \{livro.getNumPaginas()}");

        } catch (Exception e) {
            System.out.println(STR."Erro ao buscar ISBN \{isbn}: \{e.getMessage()}");
            e.printStackTrace();
        }
    }
}