package supermercado.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Produto do catálogo do supermercado.
 *
 * <p>Na versão anterior esta classe também guardava a quantidade comprada,
 * misturando "o que é o produto" com "quanto o cliente levou". A quantidade
 * agora fica em {@link ItemCarrinho}, e o produto representa apenas o item
 * de catálogo (código, nome e preço).</p>
 */
public class Produto {

    private final int codigo;
    private final String nome;
    private final BigDecimal precoUnitario;

    public Produto(int codigo, String nome, BigDecimal precoUnitario) {
        if (codigo <= 0) {
            throw new IllegalArgumentException("Codigo do produto deve ser maior que zero.");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do produto e obrigatorio.");
        }
        if (precoUnitario == null || precoUnitario.signum() < 0) {
            throw new IllegalArgumentException("Preco unitario nao pode ser negativo.");
        }
        this.codigo = codigo;
        this.nome = nome.trim();
        this.precoUnitario = precoUnitario.setScale(2, RoundingMode.HALF_UP);
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    /** Dois produtos são o mesmo item de catálogo quando têm o mesmo código. */
    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        if (!(outro instanceof Produto)) {
            return false;
        }
        return codigo == ((Produto) outro).codigo;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(codigo);
    }

    @Override
    public String toString() {
        return nome + " (cod. " + codigo + ")";
    }
}
