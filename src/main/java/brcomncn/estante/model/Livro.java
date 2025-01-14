package brcomncn.estante.model;


public class Livro {
    private Long id;
    private String isbn;
    private String titulo;
    private String autor;
    private String editora;
    private int anoPublicacao;
    private int numPaginas;
    private StatusEmprestimo statusEmprestimo;
    private String sinopse;

    public void setSinopse(String s) {
        this.sinopse = s;
    }

    public enum StatusEmprestimo {
        DISPONIVEL("Disponível"),
        EMPRESTADO("Emprestado");

        private final String descricao;

        StatusEmprestimo(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public String getSinopse() {
        return sinopse;
    }
    
    // Construtores
    public Livro() {
        this.statusEmprestimo = StatusEmprestimo.DISPONIVEL;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getEditora() {
        return editora;
    }

    public void setEditora(String editora) {
        this.editora = editora;
    }

    public int getAnoPublicacao() {
        return anoPublicacao;
    }

    public void setAnoPublicacao(int anoPublicacao) {
        this.anoPublicacao = anoPublicacao;
    }

    public int getNumPaginas() {
        return numPaginas;
    }

    public void setNumPaginas(int numPaginas) {
        this.numPaginas = numPaginas;
    }

    public StatusEmprestimo getStatusEmprestimo() {
        return statusEmprestimo;
    }

    public void setStatusEmprestimo(StatusEmprestimo statusEmprestimo) {
        this.statusEmprestimo = statusEmprestimo;
    }

    @Override
    public String toString() {
        return "Livro{" +
                "id=" + id +
                ", isbn='" + isbn + '\'' +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", editora='" + editora + '\'' +
                ", anoPublicacao=" + anoPublicacao +
                ", numPaginas=" + numPaginas +
                ", statusEmprestimo=" + statusEmprestimo +
                '}';
    }
}