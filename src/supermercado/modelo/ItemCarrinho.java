package supermercado.modelo;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Linha do carrinho: um produto e a quantidade escolhida pelo cliente.
 *
 * <p>Classe imutável. O cálculo do subtotal foi movido para cá (antes ficava
 * em {@code CarrinhoDeCompras.calcularTotal()}, que precisava "pedir" os dados
 * do produto para fazer a conta — o code smell <i>Feature Envy</i>).</p>
 */
public final class ItemCarrinho {

    private final Produto produto;
    private final int quantidade;

    public ItemCarrinho(Produto produto, int quantidade) {
        this.produto = Objects.requireNonNull(produto, "Produto e obrigatorio.");
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }
        this.quantidade = quantidade;
    }

    public Produto getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    /** Preço unitário x quantidade, sem erros de arredondamento. */
    public BigDecimal calcularSubtotal() {
        return produto.getPrecoUnitario().multiply(BigDecimal.valueOf(quantidade));
    }

    /**
     * Devolve um novo item com a quantidade somada. Usado quando o cliente
     * adiciona de novo um produto que já está no carrinho.
     */
    public ItemCarrinho comQuantidadeSomada(int quantidadeAdicional) {
        if (quantidadeAdicional <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }
        return new ItemCarrinho(produto, quantidade + quantidadeAdicional);
    }
}
