package com.senai.primeiraaplicacaoweb.controller;

import com.senai.primeiraaplicacaoweb.model.UserModel;
import com.senai.primeiraaplicacaoweb.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping(value = "/usuarios")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/formulario")
    public String registerUsers(Model model){
        model.addAttribute("user", new UserModel());
        return "users/register";
    }

    @PostMapping(value = "/cadastrar")
    public String saveUser(@Valid @ModelAttribute("user") UserModel userModel, BindingResult result,
                           RedirectAttributes redirect){
        if (result.hasErrors()) {
            return "users/register";
        }
        userService.save(userModel);
        redirect.addFlashAttribute("mensagem", "Usuário cadastrado com sucesso");
        return "redirect:/usuarios/listar";
    }

    @GetMapping(value = "/listar")
    public String listAllUsers(Model model){
        model.addAttribute("users", userService.findAll());
        return "users/list";
    }

    @GetMapping(value = "/{id}")
    public String getUserById(@PathVariable Long id, Model model){
        Optional<UserModel> user = userService.findById(id);

        if(user.isPresent()){
            model.addAttribute("user", user.get());
            return "users/update";
        }
        return "redirect:/usuarios/listar";
    }

    @PostMapping(value = "/alterar/{id}")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute("user") UserModel userModel,
                             BindingResult result, RedirectAttributes redirect){
        userModel.setId(id);
        if (result.hasErrors()) {
            return "users/update";
        }
        if (userService.findById(id).isPresent()) {
            userService.save(userModel);
            redirect.addFlashAttribute("mensagem", "Usuário alterado com sucesso");
        }
        return "redirect:/usuarios/listar";
    }

    @PostMapping(value = "/delete/{id}")
    public String deleteUserById(@PathVariable Long id, RedirectAttributes redirect){
        userService.deleteById(id);
        redirect.addFlashAttribute("mensagem", "Usuário excluído");
        return "redirect:/usuarios/listar";
    }
}
