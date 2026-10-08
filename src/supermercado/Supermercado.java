package supermercado;

// Classe principal que executa o sistema do supermercado
public class Supermercado {

    public static void main(String[] args) {

        // Criacao do carrinho e adicao dos produtos
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        carrinho.adicionarProduto("Arroz", 1, 3.99);
        carrinho.adicionarProduto("File de frango sassami", 2, 21.99);

        // Criacao do cliente
        Cliente cliente = new Cliente("Joao Silva", "123.456.789-00", "joao@email.com");

        // Criacao e fechamento do pedido
        Pedido pedido = new Pedido(1, cliente, carrinho);
        pedido.fecharPedido();
    }
}