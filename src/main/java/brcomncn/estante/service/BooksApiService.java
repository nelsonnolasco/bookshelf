package brcomncn.estante.service;

import brcomncn.estante.model.Livro;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class BooksApiService {
    private static final String OPEN_LIBRARY_API_URL = "https://openlibrary.org/api/books?bibkeys=ISBN:";
    private static final String API_PARAMS = "&format=json&jscmd=data";
    private final HttpClient httpClient;

    public BooksApiService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public Livro buscarLivroPorIsbn(String isbn) throws IOException, InterruptedException {
        String url = OPEN_LIBRARY_API_URL + isbn + API_PARAMS;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return parseLivroFromJson(response.body(), isbn);
        } else {
            throw new IOException("Erro ao buscar livro: " + response.statusCode());
        }
    }

    private Livro parseLivroFromJson(String json, String isbn) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        String isbnKey = "ISBN:" + isbn;

        // Verifica se encontrou o livro
        if (!jsonObject.has(isbnKey) || jsonObject.get(isbnKey).isJsonNull()) {
            throw new RuntimeException("Livro não encontrado para o ISBN: " + isbn);
        }

        JsonObject bookInfo = jsonObject.get(isbnKey).getAsJsonObject();

        Livro livro = new Livro();
        livro.setIsbn(isbn);

        // Título
        if (bookInfo.has("title")) {
            livro.setTitulo(bookInfo.get("title").getAsString());
        }

        // Autor (pega o primeiro se houver múltiplos)
        if (bookInfo.has("authors") && bookInfo.get("authors").getAsJsonArray().size() > 0) {
            JsonObject author = bookInfo.get("authors").getAsJsonArray().get(0).getAsJsonObject();
            livro.setAutor(author.get("name").getAsString());
        }

        // Editora (publishers)
        if (bookInfo.has("publishers") && bookInfo.get("publishers").getAsJsonArray().size() > 0) {
            JsonObject publisher = bookInfo.get("publishers").getAsJsonArray().get(0).getAsJsonObject();
            livro.setEditora(publisher.get("name").getAsString());
        }

        // Ano de Publicação
        if (bookInfo.has("publish_date")) {
            String publishDate = bookInfo.get("publish_date").getAsString();
            try {
                // Tenta extrair o ano da data de publicação
                String ano = publishDate.replaceAll(".*?(\\d{4}).*", "$1");
                livro.setAnoPublicacao(Integer.parseInt(ano));
            } catch (Exception e) {
                // Se não conseguir extrair o ano, deixa como 0
                livro.setAnoPublicacao(0);
            }
        }

        // Número de Páginas
        if (bookInfo.has("number_of_pages")) {
            livro.setNumPaginas(bookInfo.get("number_of_pages").getAsInt());
        } else {
            livro.setNumPaginas(0);
        }

        livro.setStatusEmprestimo(Livro.StatusEmprestimo.DISPONIVEL);

        return livro;
    }
}