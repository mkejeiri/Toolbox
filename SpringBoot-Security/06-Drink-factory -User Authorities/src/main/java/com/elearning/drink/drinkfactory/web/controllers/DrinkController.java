package com.elearning.drink.drinkfactory.web.controllers;


import com.elearning.drink.drinkfactory.domain.Drink;
import com.elearning.drink.drinkfactory.repositories.DrinkInventoryRepository;
import com.elearning.drink.drinkfactory.repositories.DrinkRepository;
import com.elearning.drink.drinkfactory.security.perms.DrinkCreatePermission;
import com.elearning.drink.drinkfactory.security.perms.DrinkReadPermission;
import com.elearning.drink.drinkfactory.security.perms.DrinkUpdatePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;


@RequiredArgsConstructor
@RequestMapping("/drinks")
@Controller
public class DrinkController {

    private final DrinkRepository drinkRepository;
    private final DrinkInventoryRepository drinkInventoryRepository;

    //    @PreAuthorize("hasAuthority('drink.read')")
    @DrinkReadPermission
    @RequestMapping("/find")
    public String findDrinks(Model model) {
        model.addAttribute("drink", Drink.builder().build());
        return "drinks/findDrinks";
    }

    //    @PreAuthorize("hasAuthority('drink.read')")
    @DrinkReadPermission
    @GetMapping
    public String processFindFormReturnMany(Drink drink, BindingResult result, Model model) {
        // find drinks by name
        //ToDO: Add Service
        //ToDO: Get paging data from view
        Page<Drink> pagedResult = drinkRepository.findAllByDrinkNameIsLike("%" + drink.getDrinkName() + "%", createPageRequest(0, 10, Sort.Direction.DESC, "drinkName"));
        List<Drink> drinkList = pagedResult.getContent();
        if (drinkList.isEmpty()) {
            // no drinks found
            result.rejectValue("drinkName", "notFound", "not found");
            return "drinks/findDrinks";
        } else if (drinkList.size() == 1) {
            // 1 drink found
            drink = drinkList.get(0);
            return "redirect:/drinks/" + drink.getId();
        } else {
            // multiple drinks found
            model.addAttribute("selections", drinkList);
            return "drinks/drinkList";
        }
    }


    //    @PreAuthorize("hasAuthority('drink.read')")
    @DrinkReadPermission
    @GetMapping("/{drinkId}")
    public ModelAndView showDrink(@PathVariable UUID drinkId) {
        ModelAndView mav = new ModelAndView("drinks/drinkDetails");
        //ToDO: Add Service
        mav.addObject(drinkRepository.findById(drinkId).get());
        return mav;
    }

    //    @PreAuthorize("hasAuthority('drink.create')")
    @DrinkCreatePermission
    @GetMapping("/new")
    public String initCreationForm(Model model) {
        model.addAttribute("drink", Drink.builder().build());
        return "drinks/createDrink";
    }

    //@PreAuthorize("hasAuthority('drink.create')")
    @DrinkCreatePermission
    @PostMapping("/new")
    public String processCreationForm(Drink drink) {
        //ToDO: Add Service
        Drink newDrink = Drink.builder()
                .drinkName(drink.getDrinkName())
                .drinkStyle(drink.getDrinkStyle())
                .minOnHand(drink.getMinOnHand())
                .price(drink.getPrice())
                .quantityToBrew(drink.getQuantityToBrew())
                .upc(drink.getUpc())
                .build();

        Drink savedDrink = drinkRepository.save(newDrink);
        return "redirect:/drinks/" + savedDrink.getId();
    }

    //    @PreAuthorize("hasAuthority('drink.update')")
    @DrinkUpdatePermission
    @GetMapping("/{drinkId}/edit")
    public String initUpdateDrinkForm(@PathVariable UUID drinkId, Model model) {
        if (drinkRepository.findById(drinkId).isPresent())
            model.addAttribute("drink", drinkRepository.findById(drinkId).get());
        return "drinks/createOrUpdateDrink";
    }

    //    @PreAuthorize("hasAuthority('drink.update')")
    @DrinkUpdatePermission
    @PostMapping("/{drinkId}/edit")
    public String processUpdateForm(@Valid Drink drink, BindingResult result) {
        if (result.hasErrors()) {
            return "drinks/createOrUpdateDrink";
        } else {
            //ToDO: Add Service
            Drink savedDrink = drinkRepository.save(drink);
            return "redirect:/drinks/" + savedDrink.getId();
        }
    }

    private PageRequest createPageRequest(int page, int size, Sort.Direction sortDirection, String propertyName) {
        return PageRequest.of(page,
                size,
                Sort.by(sortDirection, propertyName));
    }
}


