package com.mate.model.dto.request;

import com.mate.entity.enums.EventStatus;
import lombok.Data;
import org.springframework.data.geo.Point;

@Data
public class UpdateEventRequest {
    private String eventName;
    private String eventId;
    private String requesterId;
    private EventStatus eventStatus;
    private Point location;
    private Integer capacity;
}
