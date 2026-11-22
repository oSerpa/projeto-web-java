package com.senai.primeiraaplicacaoweb.controller;

import com.senai.primeiraaplicacaoweb.model.RegistroForm;
import com.senai.primeiraaplicacaoweb.service.ContaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    private final ContaService contaService;

    public LoginController(ContaService contaService) {
        this.contaService = contaService;
    }

    @GetMapping("/login")
    public String login(){
        return "login/login";
    }

    @GetMapping("/registrar")
    public String formularioRegistro(Model model) {
        model.addAttribute("registro", new RegistroForm());
        return "auth/registrar";
    }

    @PostMapping("/registrar")
    public String registrar(@Valid @ModelAttribute("registro") RegistroForm form, BindingResult result) {
        if (!result.hasFieldErrors("email") && contaService.emailEmUso(form.getEmail())) {
            result.rejectValue("email", "email.emUso", "Já existe uma conta com esse email");
        }
        if (result.hasErrors()) {
            return "auth/registrar";
        }
        contaService.registrar(form);
        return "redirect:/login?registrado";
    }
}
