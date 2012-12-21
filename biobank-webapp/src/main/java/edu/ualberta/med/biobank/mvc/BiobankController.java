package edu.ualberta.med.biobank.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class BiobankController {

    @RequestMapping(method=RequestMethod.GET, value="/")
    public String handleRequest(){
        return "about";
    }
}