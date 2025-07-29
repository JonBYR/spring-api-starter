package com.codewithmosh.store.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    @RequestMapping("/") //when accessing the root endpoint, return this method
    public String index() {
        return "index.html";
    }
}
