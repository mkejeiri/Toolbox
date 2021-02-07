package com.elearning.drink.drinkfactory.security.google;

import com.elearning.drink.drinkfactory.domain.security.User;
import com.elearning.drink.drinkfactory.repositories.security.UserRepository;
import com.warrenstrange.googleauth.ICredentialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class GoogleCredentialRepository implements ICredentialRepository {

    private final UserRepository userRepository;

    @Override
    public String getSecretKey(String userName) {
        User user = userRepository.findByUsername(userName).orElseThrow();

        return user.getGoogle2FaSecret();
    }

    @Override
    public void saveUserCredentials(String userName, String secretKey, int validationCode, List<Integer> scratchCodes) {
        User user = userRepository.findByUsername(userName).orElseThrow();
        user.setGoogle2FaSecret(secretKey);

        //user opt-in for F2A
        user.setUserGoogle2fa(true);
        
        userRepository.save(user);
    }
}