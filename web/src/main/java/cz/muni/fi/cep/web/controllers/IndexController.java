package cz.muni.fi.cep.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
	@RequestMapping(value = {"/index"})
    public void index(){
    }
}
