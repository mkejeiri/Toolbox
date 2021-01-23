package com.elearning.drink.drinkfactory.web.model;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class DrinkOrderPagedList extends PageImpl<DrinkOrderDto> {
    public DrinkOrderPagedList(List<DrinkOrderDto> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public DrinkOrderPagedList(List<DrinkOrderDto> content) {
        super(content);
    }
}
