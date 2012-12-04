package edu.ualberta.med.biobank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class BiobankController {

    @RequestMapping(method=RequestMethod.GET, value="/home")
    public String handleRequest(){
        return "about.html";
    }
}