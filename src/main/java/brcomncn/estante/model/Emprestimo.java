package brcomncn.estante.model;

import java.time.LocalDateTime;

public class Emprestimo {
    private Long id;
    private Livro livro;
    private Amigo amigo;
    private LocalDateTime dataEmprestimo;
    private LocalDateTime dataDevolucaoPrevista;
    private LocalDateTime dataDevolucaoReal;
    private StatusEmprestimo status;

    public enum StatusEmprestimo {
        ATIVO("Ativo"),
        DEVOLVIDO("Devolvido"),
        ATRASADO("Atrasado");

        private final String descricao;

        StatusEmprestimo(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    // Construtores
    public Emprestimo() {
        // Construtor padrão
    }

    public Emprestimo(Livro livro, Amigo amigo, LocalDateTime dataEmprestimo,
                      LocalDateTime dataDevolucaoPrevista, StatusEmprestimo status) {
        this.livro = livro;
        this.amigo = amigo;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
        this.status = status;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }

    public Amigo getAmigo() {
        return amigo;
    }

    public void setAmigo(Amigo amigo) {
        this.amigo = amigo;
    }

    public LocalDateTime getDataEmprestimo() {
        return dataEmprestimo;
    }

    public void setDataEmprestimo(LocalDateTime dataEmprestimo) {
        this.dataEmprestimo = dataEmprestimo;
    }

    public LocalDateTime getDataDevolucaoPrevista() {
        return dataDevolucaoPrevista;
    }

    public void setDataDevolucaoPrevista(LocalDateTime dataDevolucaoPrevista) {
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
    }

    public LocalDateTime getDataDevolucaoReal() {
        return dataDevolucaoReal;
    }

    public void setDataDevolucaoReal(LocalDateTime dataDevolucaoReal) {
        this.dataDevolucaoReal = dataDevolucaoReal;
    }

    public StatusEmprestimo getStatus() {
        return status;
    }

    public void setStatus(StatusEmprestimo status) {
        this.status = status;
    }

    // Métodos auxiliares
    public boolean isAtrasado() {
        return StatusEmprestimo.ATRASADO.equals(this.status);
    }

    public boolean isAtivo() {
        return StatusEmprestimo.ATIVO.equals(this.status);
    }

    public boolean isDevolvido() {
        return StatusEmprestimo.DEVOLVIDO.equals(this.status);
    }

    public long calcularDiasAtraso() {
        if (!isAtivo() || !LocalDateTime.now().isAfter(dataDevolucaoPrevista)) {
            return 0;
        }
        return java.time.Duration.between(dataDevolucaoPrevista, LocalDateTime.now()).toDays();
    }

    // ToString
    @Override
    public String toString() {
        return "Emprestimo{" +
                "id=" + id +
                ", livro=" + (livro != null ? livro.getTitulo() : "null") +
                ", amigo=" + (amigo != null ? amigo.getNome() : "null") +
                ", dataEmprestimo=" + dataEmprestimo +
                ", dataDevolucaoPrevista=" + dataDevolucaoPrevista +
                ", dataDevolucaoReal=" + dataDevolucaoReal +
                ", status=" + (status != null ? status.getDescricao() : "null") +
                '}';
    }

    // Equals e HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Emprestimo that = (Emprestimo) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // Métodos de validação
    public void validar() {
        if (livro == null) {
            throw new IllegalArgumentException("Livro é obrigatório");
        }
        if (amigo == null) {
            throw new IllegalArgumentException("Amigo é obrigatório");
        }
        if (dataEmprestimo == null) {
            throw new IllegalArgumentException("Data de empréstimo é obrigatória");
        }
        if (dataDevolucaoPrevista == null) {
            throw new IllegalArgumentException("Data de devolução prevista é obrigatória");
        }
        if (dataDevolucaoPrevista.isBefore(dataEmprestimo)) {
            throw new IllegalArgumentException("Data de devolução prevista não pode ser anterior à data de empréstimo");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status é obrigatório");
        }
    }

    // Builder Pattern (opcional, mas útil para criação de objetos)
    public static class Builder {
        private final Emprestimo emprestimo;

        public Builder() {
            emprestimo = new Emprestimo();
        }

        public Builder livro(Livro livro) {
            emprestimo.setLivro(livro);
            return this;
        }

        public Builder amigo(Amigo amigo) {
            emprestimo.setAmigo(amigo);
            return this;
        }

        public Builder dataEmprestimo(LocalDateTime dataEmprestimo) {
            emprestimo.setDataEmprestimo(dataEmprestimo);
            return this;
        }

        public Builder dataDevolucaoPrevista(LocalDateTime dataDevolucaoPrevista) {
            emprestimo.setDataDevolucaoPrevista(dataDevolucaoPrevista);
            return this;
        }

        public Builder status(StatusEmprestimo status) {
            emprestimo.setStatus(status);
            return this;
        }

        public Emprestimo build() {
            emprestimo.validar();
            return emprestimo;
        }
    }
}