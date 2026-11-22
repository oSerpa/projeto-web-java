package com.senai.primeiraaplicacaoweb;

import com.senai.primeiraaplicacaoweb.model.UserModel;
import com.senai.primeiraaplicacaoweb.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void limparBanco() {
        userRepository.deleteAll();
    }

    @Test
    void cadastroValidoSalvaERedireciona() throws Exception {
        mockMvc.perform(post("/usuarios/cadastrar").with(csrf())
                        .param("name", "Ana")
                        .param("email", "ana@email.com")
                        .param("tel", "(11) 99999-0000")
                        .param("date", "2000-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios/listar"));

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    void cadastroInvalidoVoltaProFormularioComErros() throws Exception {
        mockMvc.perform(post("/usuarios/cadastrar").with(csrf())
                        .param("name", "")
                        .param("email", "nao-e-email")
                        .param("tel", "abc")
                        .param("date", "2999-01-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"))
                .andExpect(model().attributeHasFieldErrors("user", "name", "email", "tel", "date"));

        assertThat(userRepository.count()).isZero();
    }

    private UserModel salvarAna() {
        return userRepository.save(new UserModel(null, "Ana", "ana@email.com", "11999990000", LocalDate.of(2000, 1, 1)));
    }

    @Test
    void alterarAtualizaORegistroSemCriarOutro() throws Exception {
        Long id = salvarAna().getId();

        mockMvc.perform(post("/usuarios/alterar/" + id).with(csrf())
                        .param("name", "Ana Silva")
                        .param("email", "ana.silva@email.com")
                        .param("tel", "11988887777")
                        .param("date", "1999-05-10"))
                .andExpect(redirectedUrl("/usuarios/listar"));

        assertThat(userRepository.count()).isEqualTo(1);
        UserModel atualizado = userRepository.findById(id).orElseThrow();
        assertThat(atualizado.getName()).isEqualTo("Ana Silva");
        assertThat(atualizado.getDate()).isEqualTo(LocalDate.of(1999, 5, 10));
    }

    @Test
    void alterarComDadosInvalidosNaoMudaNada() throws Exception {
        Long id = salvarAna().getId();

        mockMvc.perform(post("/usuarios/alterar/" + id).with(csrf())
                        .param("name", "Ana")
                        .param("email", "ruim")
                        .param("tel", "11999990000")
                        .param("date", "2000-01-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/update"))
                .andExpect(model().attributeHasFieldErrors("user", "email"));

        assertThat(userRepository.findById(id).orElseThrow().getEmail()).isEqualTo("ana@email.com");
    }

    @Test
    void excluirSoFuncionaPorPost() throws Exception {
        Long id = salvarAna().getId();

        mockMvc.perform(get("/usuarios/delete/" + id))
                .andExpect(status().isMethodNotAllowed());
        assertThat(userRepository.count()).isEqualTo(1);

        mockMvc.perform(post("/usuarios/delete/" + id).with(csrf()))
                .andExpect(redirectedUrl("/usuarios/listar"));
        assertThat(userRepository.count()).isZero();
    }

    @Test
    void abrirUsuarioInexistenteVoltaPraLista() throws Exception {
        mockMvc.perform(get("/usuarios/9999"))
                .andExpect(redirectedUrl("/usuarios/listar"));
    }

    @Test
    void listaEPainelMostramOsUsuarios() throws Exception {
        salvarAna();

        mockMvc.perform(get("/usuarios/listar"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("01/01/2000")));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("totalUsuarios", 1L));
    }
}
