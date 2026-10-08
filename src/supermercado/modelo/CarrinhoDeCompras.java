package supermercado.modelo;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Carrinho de compras do cliente.
 *
 * <p>Responsabilidade única: guardar os itens escolhidos e informar o total.
 * Não cria produtos (recebe o {@link Produto} pronto) e não imprime nada.</p>
 */
public class CarrinhoDeCompras {

    /** Itens indexados pelo código do produto, mantendo a ordem de inclusão. */
    private final Map<Integer, ItemCarrinho> itens = new LinkedHashMap<>();

    /**
     * Adiciona um produto ao carrinho. Se o produto já estiver no carrinho,
     * a quantidade é somada à existente em vez de criar uma linha duplicada.
     */
    public void adicionarProduto(Produto produto, int quantidade) {
        Objects.requireNonNull(produto, "Produto e obrigatorio.");
        ItemCarrinho itemExistente = itens.get(produto.getCodigo());
        ItemCarrinho itemAtualizado = (itemExistente == null)
                ? new ItemCarrinho(produto, quantidade)
                : itemExistente.comQuantidadeSomada(quantidade);
        itens.put(produto.getCodigo(), itemAtualizado);
    }

    /** Remove o produto do carrinho. Retorna {@code false} se ele não estava lá. */
    public boolean removerProduto(Produto produto) {
        Objects.requireNonNull(produto, "Produto e obrigatorio.");
        return itens.remove(produto.getCodigo()) != null;
    }

    /** Cópia somente leitura dos itens: quem chama não consegue alterar o carrinho por fora. */
    public List<ItemCarrinho> getItens() {
        return List.copyOf(itens.values());
    }

    public BigDecimal calcularTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemCarrinho item : itens.values()) {
            total = total.add(item.calcularSubtotal());
        }
        return total;
    }

    public boolean estaVazio() {
        return itens.isEmpty();
    }

    public void esvaziar() {
        itens.clear();
    }
}
