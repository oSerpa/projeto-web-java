package com.senai.primeiraaplicacaoweb.service;

import com.senai.primeiraaplicacaoweb.model.ContaModel;
import com.senai.primeiraaplicacaoweb.model.RegistroForm;
import com.senai.primeiraaplicacaoweb.repository.ContaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ContaService implements UserDetailsService {

    private final ContaRepository contaRepository;
    private final PasswordEncoder passwordEncoder;

    public ContaService(ContaRepository contaRepository, PasswordEncoder passwordEncoder) {
        this.contaRepository = contaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean emailEmUso(String email) {
        return contaRepository.existsByEmailIgnoreCase(email.trim());
    }

    public ContaModel registrar(RegistroForm form) {
        ContaModel conta = new ContaModel(form.getNome().trim(), form.getEmail().trim().toLowerCase(),
                passwordEncoder.encode(form.getSenha()));
        return contaRepository.save(conta);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        ContaModel conta = contaRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new UsernameNotFoundException("Conta não encontrada"));
        return User.withUsername(conta.getEmail())
                .password(conta.getSenha())
                .roles("USER")
                .build();
    }
}
