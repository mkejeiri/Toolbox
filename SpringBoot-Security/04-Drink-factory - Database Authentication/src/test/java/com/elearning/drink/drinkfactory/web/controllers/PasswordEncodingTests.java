package com.elearning.drink.drinkfactory.web.controllers;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
public class PasswordEncodingTests {
    static final String PASSWORD = "password";


    @Test
    void testBcrypt() {
        //strength = 10 by default -> PasswordEncoder bcrypt = new BCryptPasswordEncoder();
        int strength = 15;
        PasswordEncoder bcrypt = new BCryptPasswordEncoder(strength);
        //output changes
        //10 : $2a$10$ is metadata hashed
        //$2a$10$ak6SgBKbU/.GnrpkY1pqvuMbUIMq2lTYjKBmBLmR07l7BQp42ohWu
        //$2a$10$58btxzK2GugVQSLC9e0NIOYuctbFx0ZdupdMd.WbJoG0L7n.xTiqa

        //16 : $2a$16$ is metadata hashed
        //$2a$16$SO97aW8E0nlJQ6OFiR6eTu8SYCixgM9q/7/CIHI9tlGQozjUm0ZOy
        //$2a$16$FdlVT37yvZOqr3Z5LkZlL.xqASqACgHmwD1Taw4RqItdMts4dQVSy
        System.out.println(bcrypt.encode(PASSWORD));
        System.out.println(bcrypt.encode(PASSWORD));

        String encodedPassword = bcrypt.encode(PASSWORD);
        assertTrue(bcrypt.matches(PASSWORD, encodedPassword));
    }

    @Test
    void testSha256() {
        //this a default
        PasswordEncoder sha256 = new StandardPasswordEncoder();
        //output changes
        //c788604069f24a84b241cf3a35dd78e680b7c1be28461b36fc558d924ace8bb5a35cb5e1de753707
        //4b9c85b77d5939b1588fe020684352491123e22c20f36823c8e96818b72cb22e111c0f758663c4c2
        System.out.println(sha256.encode(PASSWORD));
        System.out.println(sha256.encode(PASSWORD));

        String encodedPassword = sha256.encode(PASSWORD);
        assertTrue(sha256.matches(PASSWORD, encodedPassword));
    }

    @Test
    void testLdap() {
        //this a default
        PasswordEncoder ldap = new LdapShaPasswordEncoder();
        //output changes
        System.out.println(ldap.encode(PASSWORD));
        System.out.println(ldap.encode(PASSWORD));

        String encodedPassword = ldap.encode(PASSWORD);
        assertTrue(ldap.matches(PASSWORD, encodedPassword));
    }

    @Test
    void testNoOp() {
        //this a default
        PasswordEncoder noOp = NoOpPasswordEncoder.getInstance();
        //out: password
        System.out.println(noOp.encode(PASSWORD));
    }

    @Test
    void hashingExample() {
        //Hashing value is always the same (one way encryption)
        //md5 not the best option to use for password hashing
        System.out.println("-------------HASHED ALWAYS SAME VALUE---------");
        System.out.println(DigestUtils.md5DigestAsHex(PASSWORD.getBytes(StandardCharsets.UTF_8)));
        System.out.println(DigestUtils.md5DigestAsHex(PASSWORD.getBytes(StandardCharsets.UTF_8)));

        System.out.println("-------------SALTED---------");
        String salted = PASSWORD + "mySaltedValue";
        System.out.println(DigestUtils.md5DigestAsHex(salted.getBytes(StandardCharsets.UTF_8)));
        System.out.println(DigestUtils.md5DigestAsHex(salted.getBytes(StandardCharsets.UTF_8)));
    }

}

