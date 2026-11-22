package com.senai.primeiraaplicacaoweb;

import com.senai.primeiraaplicacaoweb.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
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
        mockMvc.perform(post("/usuarios/cadastrar")
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
        mockMvc.perform(post("/usuarios/cadastrar")
                        .param("name", "")
                        .param("email", "nao-e-email")
                        .param("tel", "abc")
                        .param("date", "2999-01-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"))
                .andExpect(model().attributeHasFieldErrors("user", "name", "email", "tel", "date"));

        assertThat(userRepository.count()).isZero();
    }
}
