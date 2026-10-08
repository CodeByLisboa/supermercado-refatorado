package supermercado.testes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Mini-verificador usado pelos testes do método {@code main()}.
 *
 * <p>Registra cada verificação como [OK] ou [FALHOU] e, no final, mostra
 * quantos testes passaram. Funciona sem bibliotecas externas, então roda em
 * qualquer instalação do NetBeans.</p>
 */
public class Verificador {

    private int aprovados = 0;
    private final List<String> falhas = new ArrayList<>();

    /** Imprime o título de um grupo de testes. */
    public void grupo(String titulo) {
        System.out.println();
        System.out.println("--- " + titulo + " ---");
    }

    public void verificar(String descricao, boolean condicao) {
        registrar(descricao, condicao, "condicao falsa");
    }

    public void verificarIgual(String descricao, Object esperado, Object obtido) {
        registrar(descricao, Objects.equals(esperado, obtido),
                "esperado <" + esperado + "> mas foi <" + obtido + ">");
    }

    /** Compara valores monetários pelo valor (47.97 é igual a 47.970). */
    public void verificarValor(String descricao, String esperado, BigDecimal obtido) {
        boolean iguais = obtido != null && new BigDecimal(esperado).compareTo(obtido) == 0;
        registrar(descricao, iguais, "esperado <" + esperado + "> mas foi <" + obtido + ">");
    }

    /** Passa se a ação lançar uma exceção do tipo esperado. */
    public void verificarExcecao(String descricao, Class<? extends Throwable> tipoEsperado, Runnable acao) {
        try {
            acao.run();
            registrar(descricao, false, "nenhuma excecao foi lancada");
        } catch (Throwable excecao) {
            registrar(descricao, tipoEsperado.isInstance(excecao),
                    "esperado " + tipoEsperado.getSimpleName() + " mas foi " + excecao.getClass().getSimpleName());
        }
    }

    private void registrar(String descricao, boolean passou, String motivoDaFalha) {
        if (passou) {
            aprovados++;
            System.out.println("[OK]     " + descricao);
        } else {
            falhas.add(descricao);
            System.out.println("[FALHOU] " + descricao + " -> " + motivoDaFalha);
        }
    }

    public boolean todosPassaram() {
        return falhas.isEmpty();
    }

    public void imprimirResumo() {
        int total = aprovados + falhas.size();
        System.out.println();
        System.out.println("============================================================");
        System.out.println("RESULTADO: " + aprovados + " de " + total + " testes passaram.");
        for (String falha : falhas) {
            System.out.println("  Falhou: " + falha);
        }
        System.out.println("============================================================");
    }
}
