package com.coronavirus.tracker.controllers;

import com.coronavirus.tracker.services.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @Autowired
    private DataService dataService;

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("data", dataService.getAllStats());
        model.addAttribute("getSumOfCases", dataService.getSumOfCases());
        model.addAttribute("getSumOfPrevDayCases", dataService.getSumOfPrevDayCases());
        return "home";
    }
}
