package com.codewithmosh.store.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
@Controller
public class HomeController {
    @RequestMapping("/") //when accessing the root endpoint, return this method
    public String index(Model model) {
        model.addAttribute("name", "Default"); //first argument is the name of the variable
        //in the template, second is the param
        return "index"; //as it is a template, return index rather than index.html
    }
    /*
    @RequestMapping("/hello") //when accessing the root endpoint, return this method
    public String sayHello(Model model) {
        model.addAttribute("name", "Me");
        return "index";
    }
    */
}
