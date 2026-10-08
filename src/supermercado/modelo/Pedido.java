package supermercado.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import supermercado.excecao.RegraNegocioException;

/**
 * Pedido fechado por um cliente.
 *
 * <p>O pedido guarda uma cópia imutável dos itens no momento do fechamento.
 * Antes ele guardava uma referência ao carrinho, então qualquer item
 * adicionado ao carrinho depois do fechamento alterava o total do pedido.</p>
 *
 * <p>Esta classe contém apenas dados e regras do pedido. Ela não monta textos
 * nem imprime nada: isso é papel da camada de apresentação
 * ({@code supermercado.apresentacao}).</p>
 */
public class Pedido {

    private final int numero;
    private final Cliente cliente;
    private final List<ItemCarrinho> itens;
    private final LocalDateTime dataHora;
    private final BigDecimal subtotal;
    private StatusPedido status;

    public Pedido(int numero, Cliente cliente, List<ItemCarrinho> itens) {
        if (numero <= 0) {
            throw new IllegalArgumentException("Numero do pedido deve ser maior que zero.");
        }
        if (itens == null || itens.isEmpty()) {
            throw new RegraNegocioException("Um pedido precisa ter pelo menos um item.");
        }
        this.numero = numero;
        this.cliente = Objects.requireNonNull(cliente, "Cliente e obrigatorio.");
        this.itens = List.copyOf(itens);
        this.dataHora = LocalDateTime.now();
        this.subtotal = somarItens(this.itens);
        this.status = StatusPedido.FECHADO;
    }

    private static BigDecimal somarItens(List<ItemCarrinho> itens) {
        BigDecimal soma = BigDecimal.ZERO;
        for (ItemCarrinho item : itens) {
            soma = soma.add(item.calcularSubtotal());
        }
        return soma;
    }

    /** Regra de negócio: um pedido cancelado não pode ser cancelado de novo. */
    public void cancelar() {
        if (status == StatusPedido.CANCELADO) {
            throw new RegraNegocioException("O pedido " + numero + " ja esta cancelado.");
        }
        status = StatusPedido.CANCELADO;
    }

    public int getNumero() {
        return numero;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public List<ItemCarrinho> getItens() {
        return itens;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public BigDecimal getTotal() {
        return subtotal;
    }
}
