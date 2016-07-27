package com.ribay.server.job;

import com.ribay.server.repository.AuthenticationRepository;
import com.ribay.server.repository.CartRepository;
import com.ribay.server.repository.MarketingRepository;
import com.ribay.server.util.RibayProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * Repeatedly cleans data that belongs to a session after that session did not send requests anymore within some time range
 * <p>
 * Created by CD on 24.05.2016.
 */
@Configuration
@EnableAsync
@EnableScheduling
public class CleanSessionJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(CleanSessionJob.class);

    private static final long JOB_INTERVAL = 60 * 60 * 1000; // one hour

    @Autowired
    private AuthenticationRepository authRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private MarketingRepository marketingRepository;

    @Autowired
    private RibayProperties properties;

    @Scheduled(fixedDelay = JOB_INTERVAL)
    public void cleanSessions() throws Exception {
        long sessionTimeoutInMs = properties.getSessionTimeout() * 1000L;
        List<String> sessionsToClean = authRepository.getSessionIdsOlderThan(sessionTimeoutInMs);

        LOGGER.info("sessions to clean: " + sessionsToClean);

        for (String sessionId : sessionsToClean) {
            authRepository.logout(sessionId); // logout session
            cartRepository.deleteCart(sessionId); // delete articles
            marketingRepository.deleteLastVisitedArticles(sessionId); // delete last visited articles
            authRepository.deleteSessionLastAccess(sessionId); // remove last access timestamp so this session will not be found again
            // TODO do more cleanup
        }
    }

}
