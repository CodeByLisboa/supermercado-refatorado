package supermercado.apresentacao;

import supermercado.modelo.Pedido;

/**
 * Transforma um pedido em uma representação para exibição.
 *
 * <p>Cada interface do sistema (console, tela desktop, página web) tem a sua
 * implementação. Para criar um formato novo basta implementar esta interface,
 * sem mexer na classe {@link Pedido}.</p>
 */
public interface FormatadorDePedido {

    String formatar(Pedido pedido);
}
