# Spring Security Core - Spring MVC Monolith

This repository contains source code examples used to support Spring Security Core.
----------

`@RestController` **annotation** tells Spring that all **handler methods** in the **controller** should have their return **value written** directly to the **body of the response**, rather than being carried in the **model** to a **view** for **rendering**.
If we set `@Controller` **annotation for Spring MVC controller**, we need also **annotate** all of the **handler methods** with `@ResponseBody` to achieve the same result. Yet **another option** would be to return a `ResponseEntity` object.

----------

#### Password Encoding


##### Password Storage and Encoding
- When logging in, the application needs to verify the entered password matches the password value stored in the system
- Legacy systems sometimes store passwords in plain text (not recommend)
- Other systems encrypt the password in the database, then decrypt to verify : not good as it can be decrypted to original value.

##### Password Hash

- A hash is a **one-way mathematical algorithm** applied to the password
	- One way meaning the **hash value** can be generated from a **password**
	- But the **password** cannot be generated from the **hash value**
- Example:
	- password: `password1`
	- hash value: `5f4dcc3b5aa765d61d8327deb882cf99`
	- In this theoretical example, the string `password1` will always hash to `5f4dcc3b5aa765d61d8327deb882cf99`


##### Password Hash Functions
- The security area of Hash functions is effectively an arms race : As computational power increases, researchers find more vulnerabilities.
- Spring Security supports plain text and older hash functions for compatibility with legacy systems.
- These encoders are marked as deprecated to warn you they are not recommended for use.

##### Delegating Password Encoder
- **Spring Security 5** introduced a **delegating password encoder**
- Allows storage of password hashes in multiple formats
- **Password hashes** stored as : `{encodername}<somepasswordhashvalue>`.
- Thus allows you to support **multiple hash algorithms** while **migrating**.


##### Password Encoder Recommendation

- The Spring Security team **recommends** using an **adaptive one way encoding function** such as:
	- BCrypt (Default)
	- Pbkdf2
	- SCrypt
- These are also considered **slow**, which are **computationally expensive** to guard against **brute force attacks**.


**Example of hashing** :attacker will try to run hashing against a dictionary and try to find out the password value, but with salt it difficult to find the correct hash that correspond to the password.

```java
void hashingExample() {
        //Hashing value is always the same (one way encryption)
        //md5 not the best option to use for password hashing
//out : 5f4dcc3b5aa765d61d8327deb882cf99		
        System.out.println("-------------HASHED ALWAYS SAME VALUE---------");
        System.out.println(DigestUtils.md5DigestAsHex(PASSWORD.getBytes(StandardCharsets.UTF_8)));
        System.out.println(DigestUtils.md5DigestAsHex(PASSWORD.getBytes(StandardCharsets.UTF_8)));

        System.out.println("-------------SALTED---------");
        String salted = PASSWORD + "mySaltedValue";
//out : 91aad1530446cc877bf1629c3dc7be46
        System.out.println(DigestUtils.md5DigestAsHex(salted.getBytes(StandardCharsets.UTF_8)));
        System.out.println(DigestUtils.md5DigestAsHex(salted.getBytes(StandardCharsets.UTF_8)));
    }

```

NoOpPasswordEncoder
----------

**Example of NoOpPasswordEncoder** 

```java
static final String PASSWORD = "password";
	void testNoOp(){
        PasswordEncoder noOp = NoOpPasswordEncoder.getInstance();
        //out: password
        System.out.println(noOp.encode(PASSWORD));
    }
```


**Overriding** the default implementation of the **Delegating Password Encoder** with **NoOpPasswordEncoder** (Plain text password) for legacy support.

```java
 @Bean
    //we are overriding the default implementation of the Delegating Password Encoder
    //with NoOpPasswordEncoder (Plain text password), so no need to specify {noop}.
    PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }
```

**AND**

```java
   @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //we need to specify {noop} encoder.
        auth.inMemoryAuthentication()
                .withUser("admin")
                //.password("{noop}password")
                .password("password")
                .roles("ADMIN")
                .and()
                .withUser("user")
                //.password("{noop}password")
                .password("password")
                .roles("USER");

        //We could use 'and' to chain or start over as follow:
        auth.inMemoryAuthentication()
                .withUser("customer")
                //.password("{noop}password")
                .password("password")
                .roles("CUSTOMER");

    }
```


LDAP
------


**LDAP** (**LdapShaPasswordEncoder** is deprecated - legacy support) uses a **random salt**.
```java
    void testLdap(){
        //this a default
        PasswordEncoder ldap = new LdapShaPasswordEncoder();
        //output changes
        System.out.println(ldap.encode(PASSWORD));
        System.out.println(ldap.encode(PASSWORD));
        System.out.println(ldap.encode(PASSWORD));
		
		//output
		//{SSHA}23Tfbq7Evk7FAU3IhfVUxqH94wD2w0IZicWueA==
		//{SSHA}dkXywwz9YGTibhzjdYtA1I0mFNPe35qrUkoOTA==
		//{SSHA}BBdY/iUs1ONkzAiCZ+SVeXQ/XpSKrhP3hCLk1A==
    }
```

**Test passes**

```java
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
```

to use a `LdapShaPasswordEncoder` **Delegating Password Encoder**: 

 ```java
  @Bean
        //we are overriding the default implementation of the Delegating Password Encoder
        //with LdapShaPasswordEncoder (password hashed with random salt).
    PasswordEncoder passwordEncoder() {
        return new LdapShaPasswordEncoder();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //we need to specify {noop} encoder.
        auth.inMemoryAuthentication()
                .withUser("admin")
                //.password("{noop}password")
                //.password("password")
                .password("{SSHA}23Tfbq7Evk7FAU3IhfVUxqH94wD2w0IZicWueA==")
                .roles("ADMIN")
                .and()
                .withUser("user")
                //.password("{noop}password")
                //.password("password")
                .password("{SSHA}dkXywwz9YGTibhzjdYtA1I0mFNPe35qrUkoOTA==")
                .roles("USER");

        //We could use 'and' to chain or start over as follow:
        auth.inMemoryAuthentication()
                .withUser("customer")
                //.password("{noop}password")
                //.password("password")
                .password("{SSHA}BBdY/iUs1ONkzAiCZ+SVeXQ/XpSKrhP3hCLk1A==")
                .roles("CUSTOMER");

    }
```





SHA-256 (NSA, deprecated)
------
**SHA-256** (**StandardPasswordEncoder** is deprecated - legacy support) uses a **random salt**.

```java
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
```

to use a `StandardPasswordEncoder` **Delegating Password Encoder**: 
```java

    @Bean
        //we are overriding the default implementation of the Delegating Password Encoder
        //with StandardPasswordEncoder SH256 (password hashed with random salt).
    PasswordEncoder passwordEncoder() {
        return new StandardPasswordEncoder();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //we need to specify {noop} encoder.
        auth.inMemoryAuthentication()
                .withUser("admin")
                //.password("{noop}password")
                //.password("password")
                //.password("{SSHA}23Tfbq7Evk7FAU3IhfVUxqH94wD2w0IZicWueA==")
                .password("c788604069f24a84b241cf3a35dd78e680b7c1be28461b36fc558d924ace8bb5a35cb5e1de753707")
                .roles("ADMIN")
                .and()
                .withUser("user")
                //.password("{noop}password")
                //.password("password")
                //.password("{SSHA}BBdY/iUs1ONkzAiCZ+SVeXQ/XpSKrhP3hCLk1A==")
                .password("c788604069f24a84b241cf3a35dd78e680b7c1be28461b36fc558d924ace8bb5a35cb5e1de753707")
                .roles("USER");

        //We could use 'and' to chain or start over as follow:
        auth.inMemoryAuthentication()
                .withUser("customer")
                //.password("{noop}password")
                //.password("password")
                .password("4b9c85b77d5939b1588fe020684352491123e22c20f36823c8e96818b72cb22e111c0f758663c4c2")
                .roles("CUSTOMER");

    }

```


The **StandardPasswordEncoder** **SHA-256** is still respected as far as security goes. 
We can also provide it a **secret** that you would have to **apply every time**.
The biggest critics of **SHA-256** is too fast, i.e. if there's a **brute force** strength attack whith some computational power usage, the other algorithms are much slower, Therefore, taking longer to use them. In brute force attacks will **slow attackers down** from achieving a **successful attack**.

**Spring boot team recommend**, the following : 
` If you are developing a new system, org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder is a better choice both in terms of security and interoperability with other languages.`



BCryptPasswordEncoder (Default implementation by springframework)
-------

By default **BCryptPasswordEncoder** gets **strength settings** of **10**. It takes longer as strength goes up.

```
    @Test
    void testBcrypt() {
        //strength = 10 by default -> PasswordEncoder bcrypt = new BCryptPasswordEncoder();
        int strength = 16;
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

```

to use a `BCryptPasswordEncoder` **Delegating Password Encoder**: 

```java
 @Bean
        //we are overriding the default implementation of the Delegating Password Encoder
        //with BCryptPasswordEncoder (password hashed with random salt), by default strength is 10.
    PasswordEncoder passwordEncoder() {
        //return new BCryptPasswordEncoder(10);
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //we need to specify {noop} encoder.
        auth.inMemoryAuthentication()
                .withUser("admin")
                //.password("{noop}password")
                //.password("password")
                //.password("{SSHA}23Tfbq7Evk7FAU3IhfVUxqH94wD2w0IZicWueA==")
                //.password("c788604069f24a84b241cf3a35dd78e680b7c1be28461b36fc558d924ace8bb5a35cb5e1de753707")
                .password("$2a$10$ak6SgBKbU/.GnrpkY1pqvuMbUIMq2lTYjKBmBLmR07l7BQp42ohWu")
                .roles("ADMIN")
                .and()
                .withUser("user")
                //.password("{noop}password")
                //.password("password")
                //.password("{SSHA}BBdY/iUs1ONkzAiCZ+SVeXQ/XpSKrhP3hCLk1A==")
                //.password("c788604069f24a84b241cf3a35dd78e680b7c1be28461b36fc558d924ace8bb5a35cb5e1de753707")
                .password("$2a$10$58btxzK2GugVQSLC9e0NIOYuctbFx0ZdupdMd.WbJoG0L7n.xTiqa")
                .roles("USER");

        //We could use 'and' to chain or start over as follow:
        auth.inMemoryAuthentication()
                .withUser("customer")
                //.password("{noop}password")
                //.password("password")
                //.password("4b9c85b77d5939b1588fe020684352491123e22c20f36823c8e96818b72cb22e111c0f758663c4c2")
                .password("$2a$10$ak6SgBKbU/.GnrpkY1pqvuMbUIMq2lTYjKBmBLmR07l7BQp42ohWu")
                .roles("CUSTOMER");

    }
```


PasswordEncoderFactories
--------

`PasswordEncoderFactories.createDelegatingPasswordEncoder()` is a new feature of spring 5. which includes all the type of passwordEncoder, which allow us to run with several kind of PasswordEncoder instances combined.

This is the **default implementation** as it is supplied by **Spring Security 5**, so underneath the covers with the **Spring Boot Auto Configuration** will use the **PasswordEncoderFactories** to create a new **Delegating Password Encoder**.
We get a dozen pre-configured **Password Encoder** out of the box, so if we want to create our own **Delegating Password Encoder** to override some of the properties, we can easily do so,and then support **multiple password hash algorithms** in our system.


```java
	public static PasswordEncoder createDelegatingPasswordEncoder() {
		String encodingId = "bcrypt";
		Map<String, PasswordEncoder> encoders = new HashMap<>();
		encoders.put(encodingId, new BCryptPasswordEncoder());
		encoders.put("ldap", new org.springframework.security.crypto.password.LdapShaPasswordEncoder());
		encoders.put("MD4", new org.springframework.security.crypto.password.Md4PasswordEncoder());
		encoders.put("MD5", new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("MD5"));
		encoders.put("noop", org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance());
		encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
		encoders.put("scrypt", new SCryptPasswordEncoder());
		encoders.put("SHA-1", new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-1"));
		encoders.put("SHA-256", new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-256"));
		encoders.put("sha256", new org.springframework.security.crypto.password.StandardPasswordEncoder());
		encoders.put("argon2", new Argon2PasswordEncoder());

		return new DelegatingPasswordEncoder(encodingId, encoders);
	}

```

In our configuration, we have three **different users and three different password encoding algorithms** and the capability of this comes from the use of the **DelegatingPasswordEncoder**.

```java

@Bean
        //we are overriding the default implementation of the Delegating Password Encoder
        //new feature of spring 5
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //we need to specify {noop} encoder.
        auth.inMemoryAuthentication()
                .withUser("admin")
                //.password("{noop}password")
                //.password("password")
                //.password("{SSHA}23Tfbq7Evk7FAU3IhfVUxqH94wD2w0IZicWueA==")
                //.password("c788604069f24a84b241cf3a35dd78e680b7c1be28461b36fc558d924ace8bb5a35cb5e1de753707")
                //.password("$2a$10$ak6SgBKbU/.GnrpkY1pqvuMbUIMq2lTYjKBmBLmR07l7BQp42ohWu")
				
				
                //we use bcrypt
                .password("{bcrypt}$2a$10$ak6SgBKbU/.GnrpkY1pqvuMbUIMq2lTYjKBmBLmR07l7BQp42ohWu")
                .roles("ADMIN")
                .and()
                .withUser("user")
                //.password("{noop}password")
                //.password("password")
                //.password("{SSHA}BBdY/iUs1ONkzAiCZ+SVeXQ/XpSKrhP3hCLk1A==")
                //.password("c788604069f24a84b241cf3a35dd78e680b7c1be28461b36fc558d924ace8bb5a35cb5e1de753707")
                //.password("$2a$10$58btxzK2GugVQSLC9e0NIOYuctbFx0ZdupdMd.WbJoG0L7n.xTiqa")


                //we use sha256
                .password("{sha256}c788604069f24a84b241cf3a35dd78e680b7c1be28461b36fc558d924ace8bb5a35cb5e1de753707")
                .roles("USER");

        //We could use 'and' to chain or start over as follow:
        auth.inMemoryAuthentication()
                .withUser("customer")
                //.password("{noop}password")
                //.password("password")
                //.password("{SSHA}BBdY/iUs1ONkzAiCZ+SVeXQ/XpSKrhP3hCLk1A==")
                //.password("4b9c85b77d5939b1588fe020684352491123e22c20f36823c8e96818b72cb22e111c0f758663c4c2")
                //.password("$2a$10$ak6SgBKbU/.GnrpkY1pqvuMbUIMq2lTYjKBmBLmR07l7BQp42ohWu")
                
				
                //we use LDAP
                .password("{ldap}{SSHA}BBdY/iUs1ONkzAiCZ+SVeXQ/XpSKrhP3hCLk1A==")
                .roles("CUSTOMER");

    }
```

We could add our customer made **DelegatingPasswordEncoder** as follows and use it.

```java
package com.elearning.drink.drinkfactory.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

public class CustomPasswordEncoderFactories {

    public static PasswordEncoder createDelegatingPasswordEncoder() {
        String encodingId = "bcrypt15";
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(encodingId, new BCryptPasswordEncoder(15));
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        encoders.put("ldap", new org.springframework.security.crypto.password.LdapShaPasswordEncoder());
        encoders.put("noop", org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance());
        encoders.put("sha256", new org.springframework.security.crypto.password.StandardPasswordEncoder());
        return new DelegatingPasswordEncoder(encodingId, encoders);
    }

    //don't instantiate
    private CustomPasswordEncoderFactories() {}
}
```

and then use it as **PasswordEncoder bean**

```java
@Bean
        //we are overriding the default implementation of the Delegating Password Encoder
        //new feature of spring 5
    PasswordEncoder passwordEncoder() {
        return CustomPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
```





