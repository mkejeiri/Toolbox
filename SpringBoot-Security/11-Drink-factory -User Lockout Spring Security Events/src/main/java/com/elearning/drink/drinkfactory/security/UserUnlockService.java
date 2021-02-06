package com.elearning.drink.drinkfactory.security;

import com.elearning.drink.drinkfactory.domain.User;
import com.elearning.drink.drinkfactory.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserUnlockService {

    private final UserRepository userRepository;

    //Scheduled annotations instructs spring framework at fixed rate of five seconds (i.e. 5000 milliseconds).
    //This will run this every five seconds.
    @Scheduled(fixedRate = 5000)
    public void unlockAccounts() {
        log.debug("Running Unlock Accounts");

        List<User> lockedUsers = userRepository
                .findAllByAccountNonLockedAndLastModifiedDateIsBefore(false,
                        Timestamp.valueOf(LocalDateTime.now().minusSeconds(30)));

        if (lockedUsers.size() > 0) {
            log.debug("Locked Accounts Found, Unlocking");
            lockedUsers.forEach(user -> user.setAccountNonLocked(true));
            userRepository.saveAll(lockedUsers);
        }
    }

}