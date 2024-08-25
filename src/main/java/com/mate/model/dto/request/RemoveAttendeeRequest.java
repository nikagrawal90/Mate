package com.mate.model.dto.request;

import lombok.Data;

@Data
public class RemoveAttendeeRequest {
    private String attendeeId;
    private String eventId;
}
