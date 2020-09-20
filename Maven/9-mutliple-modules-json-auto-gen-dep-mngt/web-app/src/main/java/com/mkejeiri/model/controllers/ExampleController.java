package com.mkejeiri.model.controllers;

import com.mkejeiri.model.ShippingAddress;
import com.mkejeiri.model.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ExampleController {

    @RequestMapping("/")
    public Map<String, Object> getIndex() {
        Map<String, Object> returnval = new HashMap<>();
        User user = new User();
        returnval.put("hello", "world");

        return returnval;
    }

    @RequestMapping("/shipping-address")
    public ShippingAddress getShipping() {
        var shippingAddress = new ShippingAddress();
        shippingAddress.setRegion("Brussels");
        shippingAddress.setLocality("Brussels-city");
        shippingAddress.setPostalCode("1000");
        shippingAddress.setStreetAddress("my street");
        shippingAddress.setCountryName("Belgium");
        shippingAddress.setRegion("Brussels-region");
        return shippingAddress;
    }

}
