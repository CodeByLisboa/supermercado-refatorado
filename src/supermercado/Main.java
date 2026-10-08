package supermercado;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import supermercado.apresentacao.FormatadorHtmlPedido;
import supermercado.apresentacao.FormatadorTextoPedido;
import supermercado.dao.PedidoDAOMemoria;
import supermercado.desconto.DescontoPercentual;
import supermercado.desconto.DescontoPorValorMinimo;
import supermercado.desconto.PoliticaDeDesconto;
import supermercado.desconto.SemDesconto;
import supermercado.excecao.RegraNegocioException;
import supermercado.modelo.CarrinhoDeCompras;
import supermercado.modelo.Cliente;
import supermercado.modelo.Pedido;
import supermercado.modelo.Produto;
import supermercado.modelo.StatusPedido;
import supermercado.servico.PedidoService;
import supermercado.testes.Verificador;
import supermercado.util.FormatoMoeda;
import supermercado.validacao.ValidadorCpf;

/**
 * Ponto de entrada do sistema (antes chamado "Supermercado").
 *
 * <p>O {@code main()} faz duas coisas:</p>
 * <ol>
 *   <li>demonstra o mesmo fluxo de compra da fase anterior, agora com a nova
 *       arquitetura, mostrando o resultado em texto e em HTML;</li>
 *   <li>executa os testes que conferem as regras de negócio. Se algum teste
 *       falhar, o programa termina com código de saída 1.</li>
 * </ol>
 */
public class Main {

    /** CPF fictício com dígitos verificadores válidos, usado nos testes. */
    private static final String CPF_VALIDO = "529.982.247-25";

    public static void main(String[] args) {
        System.out.println("##### DEMONSTRACAO: cenario da atividade 3 com a nova arquitetura #####");
        demonstrarFluxoDeCompra();

        System.out.println();
        System.out.println("##### TESTES DAS REGRAS DE NEGOCIO #####");
        Verificador verificador = new Verificador();
        testarProdutoECarrinho(verificador);
        testarCliente(verificador);
        testarPedidoEServico(verificador);
        testarPoliticasDeDesconto(verificador);
        testarApresentacao(verificador);
        verificador.imprimirResumo();

        if (!verificador.todosPassaram()) {
            System.exit(1);
        }
    }

    private static void demonstrarFluxoDeCompra() {
        // Montagem das dependências: na versão web, troque PedidoDAOMemoria por um DAO JDBC
        PedidoService pedidoService = new PedidoService(new PedidoDAOMemoria(), new SemDesconto());

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        carrinho.adicionarProduto(arroz(), 1);
        carrinho.adicionarProduto(fileDeFrango(), 2);

        Cliente cliente = new Cliente("Joao Silva", CPF_VALIDO, "joao@email.com");
        Pedido pedido = pedidoService.fecharPedido(cliente, carrinho);

        System.out.println();
        System.out.println("Saida em texto (console / sistema desktop):");
        System.out.println(new FormatadorTextoPedido().formatar(pedido));
        System.out.println();
        System.out.println("Mesmo pedido em HTML (futura versao web):");
        System.out.println(new FormatadorHtmlPedido().formatar(pedido));
    }

    // ------------------------------------------------------------------
    // Testes
    // ------------------------------------------------------------------

    private static void testarProdutoECarrinho(Verificador v) {
        v.grupo("Produto e carrinho");

        CarrinhoDeCompras carrinho = carrinhoDoCenarioOriginal();
        v.verificarValor("Total do cenario original (1 arroz + 2 file de frango) = R$ 47,97",
                "47.97", carrinho.calcularTotal());

        CarrinhoDeCompras repetido = new CarrinhoDeCompras();
        repetido.adicionarProduto(arroz(), 1);
        repetido.adicionarProduto(arroz(), 2);
        v.verificarIgual("Produto repetido vira uma unica linha no carrinho", 1, repetido.getItens().size());
        v.verificarIgual("Produto repetido soma as quantidades (1 + 2 = 3)", 3,
                repetido.getItens().get(0).getQuantidade());

        v.verificar("Remover produto deixa o carrinho vazio",
                repetido.removerProduto(arroz()) && repetido.estaVazio());
        v.verificar("Remover produto que nao esta no carrinho retorna false",
                !repetido.removerProduto(arroz()));

        CarrinhoDeCompras balas = new CarrinhoDeCompras();
        balas.adicionarProduto(new Produto(3, "Bala", reais("0.10")), 3);
        v.verificarValor("BigDecimal: 3 x R$ 0,10 = R$ 0,30 exatos (com double daria " + (0.1 * 3) + ")",
                "0.30", balas.calcularTotal());

        v.verificarExcecao("Produto com preco negativo e rejeitado", IllegalArgumentException.class,
                () -> new Produto(4, "Feijao", reais("-1.00")));
        v.verificarExcecao("Quantidade zero no carrinho e rejeitada", IllegalArgumentException.class,
                () -> new CarrinhoDeCompras().adicionarProduto(arroz(), 0));
        v.verificarExcecao("Lista de itens nao pode ser alterada por fora do carrinho",
                UnsupportedOperationException.class, () -> carrinho.getItens().clear());
    }

    private static void testarCliente(Verificador v) {
        v.grupo("Cliente");

        Cliente cliente = new Cliente("Maria Souza", CPF_VALIDO, "Maria@Email.com");
        v.verificarIgual("CPF com mascara e aceito e guardado so com digitos", "52998224725", cliente.getCpf());
        v.verificarIgual("E-mail e guardado em minusculas", "maria@email.com", cliente.getEmail());
        v.verificar("CPF sem mascara tambem e aceito", ValidadorCpf.isValido("52998224725"));

        v.verificarExcecao("CPF usado na fase anterior (123.456.789-00) e rejeitado: digitos verificadores errados",
                IllegalArgumentException.class,
                () -> new Cliente("Joao Silva", "123.456.789-00", "joao@email.com"));
        v.verificarExcecao("CPF com todos os digitos iguais e rejeitado", IllegalArgumentException.class,
                () -> new Cliente("Joao Silva", "111.111.111-11", "joao@email.com"));
        v.verificarExcecao("E-mail sem @ e rejeitado", IllegalArgumentException.class,
                () -> new Cliente("Joao Silva", CPF_VALIDO, "joao.email.com"));
        v.verificarExcecao("Nome em branco e rejeitado", IllegalArgumentException.class,
                () -> new Cliente("   ", CPF_VALIDO, "joao@email.com"));
    }

    private static void testarPedidoEServico(Verificador v) {
        v.grupo("Pedido e PedidoService");

        PedidoService servico = new PedidoService(new PedidoDAOMemoria(), new SemDesconto());
        Cliente cliente = clienteJoao();

        v.verificarExcecao("Fechar pedido com carrinho vazio e proibido", RegraNegocioException.class,
                () -> servico.fecharPedido(cliente, new CarrinhoDeCompras()));

        CarrinhoDeCompras carrinho = carrinhoDoCenarioOriginal();
        Pedido primeiro = servico.fecharPedido(cliente, carrinho);
        v.verificarIgual("Primeiro pedido recebe o numero 1", 1, primeiro.getNumero());
        v.verificarValor("Total do pedido = R$ 47,97", "47.97", primeiro.getTotal());
        v.verificar("Carrinho e esvaziado depois de fechar o pedido", carrinho.estaVazio());

        carrinho.adicionarProduto(arroz(), 10);
        v.verificarValor("Mexer no carrinho depois de fechar NAO altera o pedido (bug da versao anterior)",
                "47.97", primeiro.getTotal());

        Pedido segundo = servico.fecharPedido(cliente, carrinho);
        v.verificarIgual("Segundo pedido recebe o numero 2 (numeracao automatica)", 2, segundo.getNumero());
        v.verificar("Pedido gravado pode ser buscado pelo numero", servico.buscarPedido(1) == primeiro);
        v.verificarIgual("Listagem retorna os 2 pedidos", 2, servico.listarPedidos().size());
        v.verificarIgual("Pedido novo comeca com status FECHADO", StatusPedido.FECHADO, segundo.getStatus());

        servico.cancelarPedido(2);
        v.verificarIgual("Cancelar muda o status para CANCELADO", StatusPedido.CANCELADO,
                servico.buscarPedido(2).getStatus());
        v.verificarExcecao("Cancelar um pedido ja cancelado e proibido", RegraNegocioException.class,
                () -> servico.cancelarPedido(2));
        v.verificarExcecao("Buscar pedido inexistente avisa com RegraNegocioException", RegraNegocioException.class,
                () -> servico.buscarPedido(99));

        // A camada de negócio não pode escrever no console: isso é papel da apresentação
        String saida = capturarConsole(() -> servico.fecharPedido(cliente, carrinhoDoCenarioOriginal()));
        v.verificar("Fechar pedido nao imprime nada no console (regra separada da interface)", saida.isEmpty());
    }

    private static void testarPoliticasDeDesconto(Verificador v) {
        v.grupo("Politicas de desconto (Strategy)");
        Cliente cliente = clienteJoao();

        Pedido semDesconto = new Pedido(1, cliente, carrinhoDoCenarioOriginal().getItens(), new SemDesconto());
        v.verificarValor("SemDesconto: total igual ao subtotal (R$ 47,97)", "47.97", semDesconto.getTotal());

        Pedido dezPorCento = new Pedido(2, cliente, carrinhoDoCenarioOriginal().getItens(),
                new DescontoPercentual(reais("10")));
        v.verificarValor("10% de R$ 47,97 = R$ 4,80 de desconto (arredondado)", "4.80", dezPorCento.getDesconto());
        v.verificarValor("Total com 10% de desconto = R$ 43,17", "43.17", dezPorCento.getTotal());

        PoliticaDeDesconto dezReaisAcimaDeCem = new DescontoPorValorMinimo(reais("100.00"), reais("10.00"));
        Pedido abaixoDoMinimo = new Pedido(3, cliente, carrinhoDoCenarioOriginal().getItens(), dezReaisAcimaDeCem);
        v.verificarValor("Compra abaixo de R$ 100,00 nao ganha desconto", "0", abaixoDoMinimo.getDesconto());

        CarrinhoDeCompras compraGrande = new CarrinhoDeCompras();
        compraGrande.adicionarProduto(fileDeFrango(), 5); // 5 x 21,99 = 109,95
        Pedido acimaDoMinimo = new Pedido(4, cliente, compraGrande.getItens(), dezReaisAcimaDeCem);
        v.verificarValor("Compra de R$ 109,95 ganha R$ 10,00 e paga R$ 99,95", "99.95", acimaDoMinimo.getTotal());

        v.verificarExcecao("Percentual acima de 100% e rejeitado", IllegalArgumentException.class,
                () -> new DescontoPercentual(reais("150")));

        PoliticaDeDesconto politicaQuebrada = new PoliticaDeDesconto() {
            @Override
            public BigDecimal calcularDesconto(BigDecimal subtotal) {
                return subtotal.add(BigDecimal.ONE); // viola o contrato de propósito
            }

            @Override
            public String getDescricao() {
                return "politica quebrada";
            }
        };
        v.verificarExcecao("Pedido recusa politica que desconta mais que o subtotal", RegraNegocioException.class,
                () -> new Pedido(5, cliente, carrinhoDoCenarioOriginal().getItens(), politicaQuebrada));

        // Liskov: qualquer política pode substituir outra sem quebrar o contrato da interface
        PoliticaDeDesconto[] politicas = {
            new SemDesconto(),
            new DescontoPercentual(reais("10")),
            new DescontoPercentual(reais("100")),
            dezReaisAcimaDeCem,
            new DescontoPorValorMinimo(reais("1.00"), reais("50.00"))
        };
        String[] subtotais = {"0.01", "4.99", "47.97", "100.00", "5000.00"};
        boolean contratoRespeitado = true;
        for (PoliticaDeDesconto politica : politicas) {
            for (String subtotal : subtotais) {
                BigDecimal valor = reais(subtotal);
                BigDecimal desconto = politica.calcularDesconto(valor);
                if (desconto.signum() < 0 || desconto.compareTo(valor) > 0) {
                    contratoRespeitado = false;
                }
            }
        }
        v.verificar("Todas as politicas respeitam 0 <= desconto <= subtotal (Liskov)", contratoRespeitado);
    }

    private static void testarApresentacao(Verificador v) {
        v.grupo("Apresentacao (texto e HTML)");
        Pedido pedido = new Pedido(7, clienteJoao(), carrinhoDoCenarioOriginal().getItens(), new SemDesconto());

        String texto = new FormatadorTextoPedido().formatar(pedido);
        v.verificar("Texto mostra numero, cliente e total",
                texto.contains("Numero do pedido: 7") && texto.contains("Joao Silva") && texto.contains("R$ 47,97"));

        String html = new FormatadorHtmlPedido().formatar(pedido);
        v.verificar("HTML gera tabela com os itens e o total",
                html.contains("<table>") && html.contains("File de frango sassami") && html.contains("R$ 47,97"));

        Cliente nomeComScript = new Cliente("Ana <script>alert(1)</script>", CPF_VALIDO, "ana@email.com");
        Pedido pedidoComScript = new Pedido(8, nomeComScript, carrinhoDoCenarioOriginal().getItens(), new SemDesconto());
        String htmlSeguro = new FormatadorHtmlPedido().formatar(pedidoComScript);
        v.verificar("HTML escapa caracteres especiais (protecao contra XSS)",
                htmlSeguro.contains("Ana &lt;script&gt;") && !htmlSeguro.contains("<script>"));

        v.verificarIgual("Moeda sempre no padrao brasileiro, independente do idioma do PC",
                "R$ 1.234,50", FormatoMoeda.formatar(reais("1234.5")));
    }

    // ------------------------------------------------------------------
    // Dados de apoio dos testes
    // ------------------------------------------------------------------

    private static BigDecimal reais(String valor) {
        return new BigDecimal(valor);
    }

    private static Produto arroz() {
        return new Produto(1, "Arroz", reais("3.99"));
    }

    private static Produto fileDeFrango() {
        return new Produto(2, "File de frango sassami", reais("21.99"));
    }

    private static Cliente clienteJoao() {
        return new Cliente("Joao Silva", CPF_VALIDO, "joao@email.com");
    }

    /** Mesmo carrinho da atividade 3: 1 arroz + 2 file de frango. */
    private static CarrinhoDeCompras carrinhoDoCenarioOriginal() {
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        carrinho.adicionarProduto(arroz(), 1);
        carrinho.adicionarProduto(fileDeFrango(), 2);
        return carrinho;
    }

    /** Executa a ação e devolve tudo o que ela escreveu em System.out. */
    private static String capturarConsole(Runnable acao) {
        PrintStream consoleOriginal = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true));
        try {
            acao.run();
        } finally {
            System.setOut(consoleOriginal);
        }
        return buffer.toString();
    }
}
