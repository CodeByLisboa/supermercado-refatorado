package supermercado.apresentacao;

import supermercado.modelo.ItemCarrinho;
import supermercado.modelo.Pedido;
import supermercado.util.FormatoMoeda;

/**
 * Resumo do pedido em HTML, pronto para ser usado na versão web
 * (por exemplo, devolvido por um Servlet ou incluído numa página JSP).
 *
 * <p>Mostra que as mesmas classes de negócio servem para outra interface:
 * só foi preciso criar um novo {@link FormatadorDePedido}, sem alterar
 * {@link Pedido}.</p>
 *
 * <p>Todo texto vindo do usuário passa por {@link #escaparHtml(String)} para
 * evitar que um nome como {@code <script>} seja executado no navegador (XSS).</p>
 */
public class FormatadorHtmlPedido implements FormatadorDePedido {

    @Override
    public String formatar(Pedido pedido) {
        StringBuilder html = new StringBuilder();
        html.append("<section class=\"pedido\">\n");
        html.append("  <h2>Pedido n&ordm; ").append(pedido.getNumero()).append("</h2>\n");
        html.append("  <p>Cliente: ").append(escaparHtml(pedido.getCliente().getNome()))
            .append(" (").append(escaparHtml(pedido.getCliente().getEmail())).append(")</p>\n");
        html.append("  <p>Status: ").append(escaparHtml(pedido.getStatus().getDescricao())).append("</p>\n");
        html.append("  <table>\n");
        html.append("    <thead><tr><th>Produto</th><th>Qtd.</th><th>Pre&ccedil;o unit.</th><th>Subtotal</th></tr></thead>\n");
        html.append("    <tbody>\n");
        for (ItemCarrinho item : pedido.getItens()) {
            html.append("      <tr><td>").append(escaparHtml(item.getProduto().getNome()))
                .append("</td><td>").append(item.getQuantidade())
                .append("</td><td>").append(FormatoMoeda.formatar(item.getProduto().getPrecoUnitario()))
                .append("</td><td>").append(FormatoMoeda.formatar(item.calcularSubtotal()))
                .append("</td></tr>\n");
        }
        html.append("    </tbody>\n");
        html.append("    <tfoot>\n");
        html.append(linhaDeTotal("Subtotal", FormatoMoeda.formatar(pedido.getSubtotal())));
        html.append(linhaDeTotal("Desconto (" + escaparHtml(pedido.getDescricaoDesconto()) + ")",
                "- " + FormatoMoeda.formatar(pedido.getDesconto())));
        html.append(linhaDeTotal("<strong>Total</strong>",
                "<strong>" + FormatoMoeda.formatar(pedido.getTotal()) + "</strong>"));
        html.append("    </tfoot>\n");
        html.append("  </table>\n");
        html.append("</section>");
        return html.toString();
    }

    private String linhaDeTotal(String rotulo, String valor) {
        return "      <tr><td colspan=\"3\">" + rotulo + "</td><td>" + valor + "</td></tr>\n";
    }

    /** Troca os caracteres especiais do HTML pelas entidades correspondentes. */
    static String escaparHtml(String texto) {
        StringBuilder seguro = new StringBuilder(texto.length());
        for (char caractere : texto.toCharArray()) {
            switch (caractere) {
                case '&':
                    seguro.append("&amp;");
                    break;
                case '<':
                    seguro.append("&lt;");
                    break;
                case '>':
                    seguro.append("&gt;");
                    break;
                case '"':
                    seguro.append("&quot;");
                    break;
                case '\'':
                    seguro.append("&#39;");
                    break;
                default:
                    seguro.append(caractere);
            }
        }
        return seguro.toString();
    }
}
