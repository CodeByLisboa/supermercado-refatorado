package supermercado.modelo;

import supermercado.validacao.ValidadorCpf;
import supermercado.validacao.ValidadorEmail;

/**
 * Cliente do supermercado.
 *
 * <p>O objeto só é criado em estado válido: nome preenchido, CPF com dígitos
 * verificadores corretos e e-mail em formato válido. As regras de validação
 * ficam em classes próprias ({@link ValidadorCpf} e {@link ValidadorEmail}),
 * para que possam ser reaproveitadas, por exemplo, num formulário web.</p>
 */
public class Cliente {

    private final String nome;
    private final String cpf;
    private final String email;

    public Cliente(String nome, String cpf, String email) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do cliente e obrigatorio.");
        }
        if (!ValidadorCpf.isValido(cpf)) {
            throw new IllegalArgumentException("CPF invalido: " + cpf);
        }
        if (!ValidadorEmail.isValido(email)) {
            throw new IllegalArgumentException("E-mail invalido: " + email);
        }
        this.nome = nome.trim();
        this.cpf = ValidadorCpf.somenteDigitos(cpf);
        this.email = email.trim().toLowerCase();
    }

    public String getNome() {
        return nome;
    }

    /** CPF armazenado apenas com os 11 dígitos, sem pontos e traço. */
    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }
}
