package com.elearning.drink.drinkfactory.security.perms;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//Instruct the Java compiler that this annotation should be retained at runtime so
//that reflection can be done at runtime, so it can see this annotation.
//so that is important for it to work with Spring Security.
@Retention(RetentionPolicy.RUNTIME)
public @interface DrinkOrderReadV2Permission {
}
