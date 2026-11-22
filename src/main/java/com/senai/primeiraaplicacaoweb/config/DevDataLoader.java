package com.senai.primeiraaplicacaoweb.config;

import com.senai.primeiraaplicacaoweb.model.RegistroForm;
import com.senai.primeiraaplicacaoweb.service.ContaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Cria uma conta de teste quando o app roda no perfil dev.
 * Email: admin@admin.com / senha: admin123
 */
@Configuration
@Profile("dev")
public class DevDataLoader {

    @Bean
    CommandLineRunner criarContaDev(ContaService contaService) {
        return args -> {
            if (!contaService.emailEmUso("admin@admin.com")) {
                RegistroForm form = new RegistroForm();
                form.setNome("Admin");
                form.setEmail("admin@admin.com");
                form.setSenha("admin123");
                form.setConfirmacaoSenha("admin123");
                contaService.registrar(form);
            }
        };
    }
}
