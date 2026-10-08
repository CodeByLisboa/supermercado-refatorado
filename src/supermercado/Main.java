package supermercado;

import java.math.BigDecimal;
import supermercado.modelo.CarrinhoDeCompras;
import supermercado.modelo.Cliente;
import supermercado.modelo.Pedido;
import supermercado.modelo.Produto;

/** Ponto de entrada do sistema (antes chamado "Supermercado"). */
public class Main {

    public static void main(String[] args) {
        Produto arroz = new Produto(1, "Arroz", new BigDecimal("3.99"));
        Produto fileDeFrango = new Produto(2, "File de frango sassami", new BigDecimal("21.99"));

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        carrinho.adicionarProduto(arroz, 1);
        carrinho.adicionarProduto(fileDeFrango, 2);

        Cliente cliente = new Cliente("Joao Silva", "529.982.247-25", "joao@email.com");

        Pedido pedido = new Pedido(1, cliente, carrinho.getItens());
        pedido.fecharPedido();
    }
}
