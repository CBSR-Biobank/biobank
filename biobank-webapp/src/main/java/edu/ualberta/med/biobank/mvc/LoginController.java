package edu.ualberta.med.biobank.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {

    @RequestMapping(value="/login", method = RequestMethod.GET)
    public String handleRequest(){
        return "openidlogin.html"; //$NON-NLS-1$
    }

    /** Login form with error. */
    @RequestMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true); //$NON-NLS-1$
        return "openidlogin.html"; //$NON-NLS-1$
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logout() {
        return "openidlogin.html"; //$NON-NLS-1$

    }

}
