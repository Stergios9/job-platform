package com.example.library.language;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Locale;

@Controller
public class LanguageController {

    @GetMapping("/change-language")
    public String changeLanguage(@RequestParam String language, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Locale locale = new Locale(language);
        session.setAttribute("locale", locale);

        return "redirect:/home"; // Επιστροφή στην αρχική σελίδα ή όπου θέλετε
    }

    @GetMapping("language")
    public String home(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Locale locale = (Locale) session.getAttribute("locale");

        if (locale == null) {
            locale = Locale.ENGLISH; // Προεπιλεγμένη γλώσσα
            session.setAttribute("lang", locale);
        }

        model.addAttribute("currentLanguage", locale.getLanguage());

        return "changeLanguage"; // Επιστροφή στο view
    }
}
