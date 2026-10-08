package supermercado.validacao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** Testes da regra de validação de CPF pelos dígitos verificadores (CT-U27 a CT-U32). */
public class ValidadorCpfTest {

    @Test
    public void cpfValido_comESemMascara() {
        assertTrue(ValidadorCpf.isValido("529.982.247-25"));
        assertTrue(ValidadorCpf.isValido("52998224725"));
    }

    @Test
    public void cpfDaFaseAnterior_temDigitosVerificadoresErrados() {
        assertFalse(ValidadorCpf.isValido("123.456.789-00"));
    }

    @Test
    public void mesmaSequenciaComDigitosCorretos_eValida() {
        // para 123.456.789 os dígitos verificadores corretos são 0 e 9
        assertTrue(ValidadorCpf.isValido("123.456.789-09"));
    }

    @Test
    public void todosOsDigitosIguais_eInvalido() {
        assertFalse(ValidadorCpf.isValido("111.111.111-11"));
        assertFalse(ValidadorCpf.isValido("00000000000"));
    }

    @Test
    public void tamanhoErradoVazioOuNulo_eInvalido() {
        assertFalse(ValidadorCpf.isValido("529.982.247-2"));
        assertFalse(ValidadorCpf.isValido(""));
        assertFalse(ValidadorCpf.isValido(null));
    }

    @Test
    public void somenteDigitos_removePontosETraco() {
        assertEquals("52998224725", ValidadorCpf.somenteDigitos("529.982.247-25"));
    }
}
