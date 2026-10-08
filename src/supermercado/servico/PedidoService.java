package supermercado.servico;

import java.util.List;
import java.util.Objects;
import supermercado.dao.PedidoDAO;
import supermercado.desconto.PoliticaDeDesconto;
import supermercado.excecao.RegraNegocioException;
import supermercado.modelo.CarrinhoDeCompras;
import supermercado.modelo.Cliente;
import supermercado.modelo.Pedido;

/**
 * Casos de uso de pedidos: fechar, consultar e cancelar.
 *
 * <p>É a "porta de entrada" das regras de negócio. A tela desktop, o
 * console ou um Servlet da versão web chamam estes métodos e só se
 * preocupam em mostrar o resultado.</p>
 *
 * <p>As dependências chegam pelo construtor (injeção de dependência) e são
 * interfaces, não classes concretas (princípio da inversão de dependência).</p>
 */
public class PedidoService {

    private final PedidoDAO pedidoDAO;
    private final PoliticaDeDesconto politicaDeDesconto;

    public PedidoService(PedidoDAO pedidoDAO, PoliticaDeDesconto politicaDeDesconto) {
        this.pedidoDAO = Objects.requireNonNull(pedidoDAO, "PedidoDAO e obrigatorio.");
        this.politicaDeDesconto = Objects.requireNonNull(politicaDeDesconto, "Politica de desconto e obrigatoria.");
    }

    /**
     * Fecha o pedido com os itens do carrinho, aplica o desconto vigente,
     * grava o pedido e esvazia o carrinho.
     *
     * @throws RegraNegocioException se o carrinho estiver vazio
     */
    public Pedido fecharPedido(Cliente cliente, CarrinhoDeCompras carrinho) {
        Objects.requireNonNull(cliente, "Cliente e obrigatorio.");
        Objects.requireNonNull(carrinho, "Carrinho e obrigatorio.");
        if (carrinho.estaVazio()) {
            throw new RegraNegocioException("Nao e possivel fechar um pedido com o carrinho vazio.");
        }
        Pedido pedido = new Pedido(pedidoDAO.proximoNumero(), cliente, carrinho.getItens(), politicaDeDesconto);
        pedidoDAO.salvar(pedido);
        carrinho.esvaziar();
        return pedido;
    }

    /** @throws RegraNegocioException se não existir pedido com esse número */
    public Pedido buscarPedido(int numero) {
        return pedidoDAO.buscarPorNumero(numero)
                .orElseThrow(() -> new RegraNegocioException("Pedido " + numero + " nao encontrado."));
    }

    public List<Pedido> listarPedidos() {
        return pedidoDAO.listarTodos();
    }

    /** @throws RegraNegocioException se o pedido não existir ou já estiver cancelado */
    public Pedido cancelarPedido(int numero) {
        Pedido pedido = buscarPedido(numero);
        pedido.cancelar();
        pedidoDAO.salvar(pedido);
        return pedido;
    }
}
