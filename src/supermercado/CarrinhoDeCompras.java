package supermercado;

import java.util.ArrayList;
import java.util.List;

public class CarrinhoDeCompras {

    private List<Produto> produtos;

    public CarrinhoDeCompras() {
        this.produtos = new ArrayList<>();
    }

    public void adicionarProduto(String nome, int quantidade, double precoUnitario) {
        this.produtos.add(new Produto(nome, quantidade, precoUnitario));
    }

    public double calcularTotal() {
        double total = 0;
        for (Produto produto : produtos) {
            total += produto.getQuantidade() * produto.getPrecoUnitario();
        }
        return total;
    }
}