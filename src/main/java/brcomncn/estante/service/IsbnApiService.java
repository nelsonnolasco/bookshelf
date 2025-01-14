package brcomncn.estante.service;

import com.google.gson.Gson;
import brcomncn.estante.model.IsbnResponse;
import brcomncn.estante.model.Livro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class IsbnApiService {
    private static final String API_URL = "https://brasilapi.com.br/api/isbn/v1/";
    private final Gson gson;

    public IsbnApiService() {
        this.gson = new Gson();
    }

    public IsbnResponse buscarLivroPorIsbn(String isbn) throws IOException {
        // Remove caracteres não numéricos do ISBN
        isbn = isbn.replaceAll("[^0-9]", "");

        URL url = new URL(API_URL + isbn);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            // Configurar a conexão
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            // Verificar o código de resposta
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                throw new IOException("ISBN não encontrado: " + isbn);
            }
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("Erro na requisição: " + responseCode);
            }

            // Ler a resposta
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                if (response.length() == 0) {
                    throw new IOException("Resposta vazia da API");
                }

                return gson.fromJson(response.toString(), IsbnResponse.class);
            }

        } finally {
            conn.disconnect();
        }
    }

    public Livro converterParaLivro(IsbnResponse isbnResponse) {
        if (isbnResponse == null) {
            throw new IllegalArgumentException("Dados do ISBN não podem ser nulos");
        }

        Livro livro = new Livro();

        // Dados obrigatórios
        livro.setIsbn(isbnResponse.getIsbn());
        livro.setTitulo(isbnResponse.getTitle());

        // Dados opcionais com validação
        if (isbnResponse.getSubtitle() != null && !isbnResponse.getSubtitle().trim().isEmpty()) {
            livro.setTitulo(isbnResponse.getTitle() + ": " + isbnResponse.getSubtitle());
        }

        // Tratamento de autores
        if (isbnResponse.getAuthors() != null && isbnResponse.getAuthors().length > 0) {
            livro.setAutor(String.join(", ", isbnResponse.getAuthors()));
        } else {
            livro.setAutor("Autor desconhecido");
        }

        // Dados opcionais
        livro.setEditora(isbnResponse.getPublisher() != null ?
                isbnResponse.getPublisher() : "Editora não informada");

        livro.setAnoPublicacao(isbnResponse.getYear() != null ?
                isbnResponse.getYear() : 0);

        livro.setNumPaginas(isbnResponse.getPages() != null ?
                isbnResponse.getPages() : 0);

        livro.setSinopse(isbnResponse.getSynopsis() != null ?
                isbnResponse.getSynopsis() : "");

        // Status padrão
        livro.setStatusEmprestimo(Livro.StatusEmprestimo.DISPONIVEL);

        return livro;
    }

    // Método para validar ISBN
    private void validarIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN não pode ser vazio");
        }

        String isbnNumerico = isbn.replaceAll("[^0-9]", "");
        if (isbnNumerico.length() != 10 && isbnNumerico.length() != 13) {
            throw new IllegalArgumentException("ISBN deve ter 10 ou 13 dígitos");
        }
    }
}