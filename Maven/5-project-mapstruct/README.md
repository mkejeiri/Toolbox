# Mapstruct Maven Project Example

Example Maven project using [MapStruct](http://mapstruct.org/)

**key take away** in order to set **mapstruct with Maven**, is to **add** in a hook **configuration** the `annotationProcessorPaths` provided by mapstruct, this helps **maven** to **pick** it **up** and do the **annotation processing**.

```
...
 <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.0</version>
                <configuration>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.mapstruct</groupId>
                            <artifactId>mapstruct-processor</artifactId>
                            <version>${mapstruct.version}</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
        </plugins>
    </build>
...
```

>> Simply put, this is a hook into the `compile` lifecycle and `java compiler` for **mapstruct** to go in `runtime` and through `annotation processing` create the **UserMapperImpl.java** (i.e. UserMapper interface implementation), because all we did is to declare the **UserMapper interface**

**UserMapper.java**

```
package com.mkejeiri.mappers;

import com.mkejeiri.domain.User;
import com.mkejeiri.model.UserCommand;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserCommand userToUserCommand(User user);

    User userCommandToUser(UserCommand userCommand);
}
```

**UserMapperImpl.java** : auto-gen at compilation time
```
package com.mkejeiri.mappers;

import com.mkejeiri.domain.User;
import com.mkejeiri.model.UserCommand;

/*
@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-09-13T08:37:15+0200",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 11.0.8 (Oracle Corporation)"
)
*/
public class UserMapperImpl implements UserMapper {

    @Override
    public UserCommand userToUserCommand(User user) {
        if ( user == null ) {
            return null;
        }

        UserCommand userCommand = new UserCommand();

        userCommand.setFirstName( user.getFirstName() );
        userCommand.setLastName( user.getLastName() );
        userCommand.setEmail( user.getEmail() );

        return userCommand;
    }

    @Override
    public User userCommandToUser(UserCommand userCommand) {
        if ( userCommand == null ) {
            return null;
        }

        User user = new User();

        user.setFirstName( userCommand.getFirstName() );
        user.setLastName( userCommand.getLastName() );
        user.setEmail( userCommand.getEmail() );

        return user;
    }
}

``` 

