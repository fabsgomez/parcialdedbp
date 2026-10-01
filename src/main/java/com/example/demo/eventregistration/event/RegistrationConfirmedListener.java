package com.example.demo.eventregistration.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class RegistrationConfirmedListener {

    private static final Logger log = LoggerFactory.getLogger(RegistrationConfirmedListener.class);

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRegistrationConfirmed(RegistrationConfirmedEvent event) {
        log.info(
                "Registration confirmed after commit. registrationId={}, eventId={}, attendeeId={}",
                event.registrationId(),
                event.eventId(),
                event.attendeeId()
        );
    }
}
