package com.supplychain.drinkmicroservice.model;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.List;

public class DrinkPageList extends PageImpl<DrinkDto> {
    public DrinkPageList(List<DrinkDto> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public DrinkPageList(List<DrinkDto> content) {
        super(content);
    }
}
