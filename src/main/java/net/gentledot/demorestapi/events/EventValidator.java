package net.gentledot.demorestapi.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventValidator {

    public void validate(EventDto eventDto, Errors errors) {
        int basePrice = eventDto.getBasePrice();
        int maxPrice = eventDto.getMaxPrice();
        LocalDateTime beginEventDateTime = eventDto.getBeginEventDateTime();
        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        LocalDateTime beginEnrollmentDateTime = eventDto.getBeginEnrollmentDateTime();
        LocalDateTime closeEnrollmentDateTime = eventDto.getCloseEnrollmentDateTime();

        if (maxPrice > 0 && basePrice > maxPrice) {
            errors.rejectValue("basePrice", "wrongValue", "BasePrice is wrong.");
            errors.rejectValue("maxPrice", "wrongValue", "MaxPrice is wrong.");
        }

        if (endEventDateTime.isBefore(beginEnrollmentDateTime) ||
                endEventDateTime.isBefore(beginEnrollmentDateTime) ||
                endEventDateTime.isBefore(closeEnrollmentDateTime)){
            errors.rejectValue("endEventDateTime", "wrongValue", "EndEventDateTime is wrong.");
        }

        // TODO BeginEventDateTime
        // TODO CloseEnrollmentDateTime

    }
}
