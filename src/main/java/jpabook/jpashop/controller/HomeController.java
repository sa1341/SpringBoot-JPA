package jpabook.jpashop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class HomeController {

    @RequestMapping("/")
    public String home() {
        log.info("home controller");
        return "home";
    }


    @GetMapping("/board")
    public String boardForm() {
        return "boards/view";
    }


    @GetMapping("/loginForm")
    public void loginForm(){
        log.info("loginForm!!");
    }


    @GetMapping("/index")
    public void index(){
        log.info("index!!");
    }

}
