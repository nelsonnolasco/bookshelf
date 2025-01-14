package brcomncn.estante.test;

import brcomncn.estante.util.ConnectionFactory;

public class TesteConexao {
    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.testarConexao();
    }
}
