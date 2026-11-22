package com.senai.primeiraaplicacaoweb.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Dados do formulário de criar conta. Fica separado da entidade
 * pra senha em texto puro nunca chegar no banco.
 */
@Getter
@Setter
public class RegistroForm {

    @NotBlank(message = "Informe o nome")
    @Size(max = 100, message = "O nome pode ter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "Informe o email")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Informe a senha")
    @Size(min = 6, max = 72, message = "A senha precisa ter entre 6 e 72 caracteres")
    private String senha;

    private String confirmacaoSenha;

    @AssertTrue(message = "As senhas não conferem")
    public boolean isSenhasIguais() {
        return senha != null && senha.equals(confirmacaoSenha);
    }
}
