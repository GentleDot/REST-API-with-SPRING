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

        if (beginEnrollmentDateTime.isAfter(endEventDateTime) ||
                beginEnrollmentDateTime.isAfter(beginEventDateTime) ||
                beginEnrollmentDateTime.isAfter(closeEnrollmentDateTime)) {
            errors.rejectValue("beginEnrollmentDateTime", "wrongValue", "beginEnrollmentDateTime is wrong.");
        }

        if (closeEnrollmentDateTime.isAfter(endEventDateTime) ||
                closeEnrollmentDateTime.isAfter(beginEventDateTime) ||
                closeEnrollmentDateTime.isBefore(beginEnrollmentDateTime)) {
            errors.rejectValue("closeEnrollmentDateTime", "wrongValue", "closeEnrollmentDateTime is wrong.");
        }

        if (beginEventDateTime.isAfter(endEventDateTime) ||
                beginEventDateTime.isBefore(closeEnrollmentDateTime) ||
                beginEventDateTime.isBefore(beginEnrollmentDateTime)) {
            errors.rejectValue("beginEventDateTime", "wrongValue", "BeginEventDateTime is wrong.");
        }

        if (endEventDateTime.isBefore(beginEventDateTime) ||
                endEventDateTime.isBefore(closeEnrollmentDateTime) ||
                endEventDateTime.isBefore(beginEnrollmentDateTime)) {
            errors.rejectValue("endEventDateTime", "wrongValue", "EndEventDateTime is wrong.");
        }


    }
}
