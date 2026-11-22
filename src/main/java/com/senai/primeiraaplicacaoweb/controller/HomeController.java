package com.senai.primeiraaplicacaoweb.controller;

import com.senai.primeiraaplicacaoweb.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/")
public class HomeController {

    private final UserRepository userRepository;

    public HomeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public String index(Model model){
        model.addAttribute("totalUsuarios", userRepository.count());
        return "home/index";
    }

    @GetMapping(value = "/sobre")
    public String sobre(){
        return "sobre/sobre";
    }
}
