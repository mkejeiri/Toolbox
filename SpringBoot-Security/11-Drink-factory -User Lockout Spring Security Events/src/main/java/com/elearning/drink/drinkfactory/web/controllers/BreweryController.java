package com.elearning.drink.drinkfactory.web.controllers;

import com.elearning.drink.drinkfactory.domain.Brewery;
import com.elearning.drink.drinkfactory.security.perms.BreweryReadPermission;
import com.elearning.drink.drinkfactory.services.BreweryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@RequiredArgsConstructor
@RequestMapping("/brewery")
@Controller
public class BreweryController {

    private final BreweryService breweryService;

    //    @PreAuthorize("hasAuthority('brewery.read')")
    @BreweryReadPermission
    @GetMapping({"/breweries", "/breweries/index", "/breweries/index.html", "/breweries.html"})
    public String listBreweries(Model model) {
        model.addAttribute("breweries", breweryService.getAllBreweries());
        return "breweries/index";
    }

    //    @PreAuthorize("hasAuthority('brewery.read')")
    @BreweryReadPermission
    @GetMapping("/api/v1/breweries")
    public @ResponseBody
    List<Brewery> getBreweriesJson() {
        return breweryService.getAllBreweries();
    }
}
