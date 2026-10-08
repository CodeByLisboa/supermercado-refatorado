package supermercado;

// Classe que representa um pedido realizado por um cliente
public class Pedido {

    private int numeroPedido;
    private CarrinhoDeCompras carrinho;
    private Cliente cliente;

    public Pedido(int numeroPedido, Cliente cliente, CarrinhoDeCompras carrinho) {
        this.numeroPedido = numeroPedido;
        this.cliente = cliente;
        this.carrinho = carrinho;
    }

    public int getNumeroPedido() {
        return this.numeroPedido;
    }

    public Cliente getCliente() {
        return this.cliente;
    }

    public CarrinhoDeCompras getCarrinho() {
        return this.carrinho;
    }

    // Gera um resumo completo do pedido com dados do cliente e total
    public String gerarResumo() {
        return "Numero do pedido: " + numeroPedido + "\n"
             + "Cliente: " + cliente.getNome() + "\n"
             + "E-mail: " + cliente.getEmail() + "\n"
             + "Total do pedido: R$ " + String.format("%.2f", carrinho.calcularTotal()) + "\n"
             + "=====================================";
    }

    // Exibe o resumo do pedido no console
        @Override
    public String toString() {
        return gerarResumo();
    }

    public void fecharPedido() {
        System.out.println(this);
    }
}