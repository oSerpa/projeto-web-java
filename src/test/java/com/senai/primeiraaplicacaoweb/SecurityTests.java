package com.senai.primeiraaplicacaoweb;

import com.senai.primeiraaplicacaoweb.repository.ContaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContaRepository contaRepository;

    @BeforeEach
    void limparBanco() {
        contaRepository.deleteAll();
    }

    @Test
    void semLoginRedirecionaProLogin() throws Exception {
        mockMvc.perform(get("/usuarios/listar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void criarContaGuardaSenhaCriptografadaEPermiteEntrar() throws Exception {
        mockMvc.perform(post("/registrar").with(csrf())
                        .param("nome", "Ana")
                        .param("email", "ana@email.com")
                        .param("senha", "segredo123")
                        .param("confirmacaoSenha", "segredo123"))
                .andExpect(redirectedUrl("/login?registrado"));

        String hash = contaRepository.findByEmailIgnoreCase("ana@email.com").orElseThrow().getSenha();
        assertThat(hash).isNotEqualTo("segredo123").startsWith("$2");

        mockMvc.perform(formLogin("/login").userParameter("email").passwordParam("senha")
                        .user("ana@email.com").password("segredo123"))
                .andExpect(authenticated());

        mockMvc.perform(formLogin("/login").userParameter("email").passwordParam("senha")
                        .user("ana@email.com").password("errada"))
                .andExpect(unauthenticated());
    }

    @Test
    void naoCriaContaComSenhasDiferentesOuEmailRepetido() throws Exception {
        mockMvc.perform(post("/registrar").with(csrf())
                        .param("nome", "Ana")
                        .param("email", "ana@email.com")
                        .param("senha", "segredo123")
                        .param("confirmacaoSenha", "outra"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("registro", "senhasIguais"));

        mockMvc.perform(post("/registrar").with(csrf())
                .param("nome", "Ana").param("email", "ana@email.com")
                .param("senha", "segredo123").param("confirmacaoSenha", "segredo123"));

        mockMvc.perform(post("/registrar").with(csrf())
                        .param("nome", "Outra")
                        .param("email", "ANA@email.com")
                        .param("senha", "segredo123")
                        .param("confirmacaoSenha", "segredo123"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("registro", "email"));

        assertThat(contaRepository.count()).isEqualTo(1);
    }
}
