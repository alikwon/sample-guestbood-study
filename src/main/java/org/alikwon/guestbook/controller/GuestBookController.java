package org.alikwon.guestbook.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.alikwon.guestbook.dto.PageRequestDTO;
import org.alikwon.guestbook.service.GuestbookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/guestbook")
@Log4j2
@RequiredArgsConstructor
public class GuestBookController {

    private final GuestbookService service;

    @GetMapping({"/",""})
    public String list(){
       return "redirect:/guestbook/list";
    }

    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model) {
        log.info("########## list ##########");
        model.addAttribute("result", service.getList(pageRequestDTO));
    }
}
