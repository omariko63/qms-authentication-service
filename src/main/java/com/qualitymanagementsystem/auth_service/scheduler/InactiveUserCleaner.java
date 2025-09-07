package com.qualitymanagementsystem.auth_service.scheduler;


import com.qualitymanagementsystem.auth_service.util.InMemoryTokenStore;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class InactiveUserCleaner {
    private static final long TIMEOUT = 60 * 1000; // 1 min

    @Scheduled(fixedRate = 30000)
    public void cleanInactiveTokens() {
        long now = System.currentTimeMillis();
        InMemoryTokenStore.getTokens().forEach((token, lastSeen) -> {
            if (now - lastSeen > TIMEOUT) {
                InMemoryTokenStore.invalidateToken(token);
                System.out.println("Token expired due to inactivity: " + token);
            }
        });
    }
}

