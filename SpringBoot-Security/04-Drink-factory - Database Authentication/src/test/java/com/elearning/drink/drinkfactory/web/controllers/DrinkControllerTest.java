package com.elearning.drink.drinkfactory.web.controllers;

import com.elearning.drink.drinkfactory.domain.Drink;
import com.elearning.drink.drinkfactory.repositories.DrinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DrinkControllerTest {
    @Mock
    DrinkRepository drinkRepository;

    @InjectMocks
    DrinkController controller;
    List<Drink> drinkList;
    UUID uuid;
    Drink drink;

    MockMvc mockMvc;
    Page<Drink> drinks;
    Page<Drink> pagedResponse;

    @BeforeEach
    void setUp() {
        drinkList = new ArrayList<Drink>();
        drinkList.add(Drink.builder().build());
        drinkList.add(Drink.builder().build());
        pagedResponse = new PageImpl(drinkList);

        final String id = "493410b3-dd0b-4b78-97bf-289f50f6e74f";
        uuid = UUID.fromString(id);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void findDrinks() throws Exception{
        mockMvc.perform(get("/drinks/find"))
                .andExpect(status().isOk())
                .andExpect(view().name("drinks/findDrinks"))
                .andExpect(model().attributeExists("drink"));
        verifyZeroInteractions(drinkRepository);
    }

    //ToDO: Mocking Page
     void processFindFormReturnMany() throws Exception{
        when(drinkRepository.findAllByDrinkName(anyString(), PageRequest.of(0,
              10,Sort.Direction.DESC,"drinkName"))).thenReturn(pagedResponse);
        mockMvc.perform(get("/drinks"))
                .andExpect(status().isOk())
                .andExpect(view().name("drinks/drinkList"))
                .andExpect(model().attribute("selections", hasSize(2)));
    }


    @Test
    void showDrink() throws Exception{

        when(drinkRepository.findById(uuid)).thenReturn(Optional.of(Drink.builder().id(uuid).build()));
        mockMvc.perform(get("/drinks/"+uuid))
                .andExpect(status().isOk())
                .andExpect(view().name("drinks/drinkDetails"))
                .andExpect(model().attribute("drink", hasProperty("id", is(uuid))));
    }

    @Test
    void initCreationForm() throws Exception {
        mockMvc.perform(get("/drinks/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("drinks/createDrink"))
                .andExpect(model().attributeExists("drink"));
        verifyZeroInteractions(drinkRepository);
    }

    @Test
    void processCreationForm() throws Exception {
        when(drinkRepository.save(ArgumentMatchers.any())).thenReturn(Drink.builder().id(uuid).build());
        mockMvc.perform(post("/drinks/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/drinks/"+ uuid))
                .andExpect(model().attributeExists("drink"));
        verify(drinkRepository).save(ArgumentMatchers.any());
    }

    @Test
    void initUpdateDrinkForm() throws Exception{
        when(drinkRepository.findById(uuid)).thenReturn(Optional.of(Drink.builder().id(uuid).build()));
        mockMvc.perform(get("/drinks/"+uuid+"/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("drinks/createOrUpdateDrink"))
                .andExpect(model().attributeExists("drink"));
        verifyZeroInteractions(drinkRepository);
    }

    @Test
    void processUpdationForm() throws Exception {
        when(drinkRepository.save(ArgumentMatchers.any())).thenReturn(Drink.builder().id(uuid).build());

        mockMvc.perform(post("/drinks/"+uuid+"/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/drinks/"+uuid))
                .andExpect(model().attributeExists("drink"));

        verify(drinkRepository).save(ArgumentMatchers.any());
    }
}