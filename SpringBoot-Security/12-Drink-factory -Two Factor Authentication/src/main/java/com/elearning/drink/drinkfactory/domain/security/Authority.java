package com.elearning.drink.drinkfactory.domain.security;

import com.elearning.drink.drinkfactory.domain.security.Role;
import lombok.*;

import javax.persistence.*;
import java.util.Set;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Entity
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String permission;

    //We can use the project Lombok @Singular annotation, and in Builder pattern,
    //we will get a property called authority, and then we can add in a Singular authority via the Builder pattern.
    //@Singular : we aren't building authorities and adding user to it but the inverse is true.
    @ManyToMany(mappedBy = "authorities")
    private Set<Role> roles;
}
