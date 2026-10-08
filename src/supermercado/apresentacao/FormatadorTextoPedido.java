package supermercado.apresentacao;

import java.time.format.DateTimeFormatter;
import supermercado.modelo.ItemCarrinho;
import supermercado.modelo.Pedido;
import supermercado.util.FormatoMoeda;

/**
 * Resumo do pedido em texto puro, para console ou cupom.
 *
 * <p>Substitui os antigos {@code Pedido.gerarResumo()}, {@code Pedido.toString()}
 * e {@code Pedido.fecharPedido()}, que misturavam regra de negócio com exibição.</p>
 */
public class FormatadorTextoPedido implements FormatadorDePedido {

    private static final int LARGURA = 60;
    private static final String LINHA_DUPLA = "=".repeat(LARGURA);
    private static final String LINHA_SIMPLES = "-".repeat(LARGURA);
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public String formatar(Pedido pedido) {
        StringBuilder texto = new StringBuilder();
        texto.append(LINHA_DUPLA).append('\n');
        texto.append("Numero do pedido: ").append(pedido.getNumero()).append('\n');
        texto.append("Data: ").append(pedido.getDataHora().format(DATA_HORA)).append('\n');
        texto.append("Cliente: ").append(pedido.getCliente().getNome()).append('\n');
        texto.append("E-mail: ").append(pedido.getCliente().getEmail()).append('\n');
        texto.append("Status: ").append(pedido.getStatus().getDescricao()).append('\n');
        texto.append(LINHA_SIMPLES).append('\n');
        for (ItemCarrinho item : pedido.getItens()) {
            texto.append(formatarItem(item)).append('\n');
        }
        texto.append(LINHA_SIMPLES).append('\n');
        texto.append(formatarTotal("Total do pedido:", FormatoMoeda.formatar(pedido.getTotal()))).append('\n');
        texto.append(LINHA_DUPLA);
        return texto.toString();
    }

    private String formatarItem(ItemCarrinho item) {
        return String.format("%3d x %-26s %13s %13s",
                item.getQuantidade(),
                item.getProduto().getNome(),
                FormatoMoeda.formatar(item.getProduto().getPrecoUnitario()),
                FormatoMoeda.formatar(item.calcularSubtotal()));
    }

    private String formatarTotal(String rotulo, String valor) {
        return String.format("%-44s %15s", rotulo, valor);
    }
}
