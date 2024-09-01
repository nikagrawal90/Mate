package com.mate.controller;

import com.mate.exception.EventNotFoundException;
import com.mate.exception.UnauthorizedException;
import com.mate.model.dto.EventDto;
import com.mate.model.dto.request.UpdateEventRequest;
import com.mate.service.EventService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/event")
@CrossOrigin("*")
@Log4j2
public class EventController {
    @Autowired
    private EventService eventService;

    @PostMapping("/createEvent")
    public ResponseEntity<?> createEvent(@RequestBody EventDto eventDto) {
        try {
            return ResponseEntity.ok(eventService.createEvent(eventDto));
        } catch (Exception e) {
            String errMsg = String.format("Error creating event=%s with error=%s", eventDto.getEventName(), e.getMessage());
            log.error(errMsg);
            return ResponseEntity.internalServerError().body(errMsg);
        }

    }

    @GetMapping("/getAllEvents")
    public ResponseEntity<?> getAllEvents() {
        try {
            return ResponseEntity.ok(eventService.getAllEvents());
        } catch (Exception e) {
            String errMsg = String.format("Error getting all events with error=%s", e.getMessage());
            return ResponseEntity.internalServerError().body(errMsg);
        }
    }

    @DeleteMapping("/deleteEvent")
    public ResponseEntity<?> deleteEvent(@RequestParam String eventId) {
        try {
            eventService.deleteEvent(eventId);
            return ResponseEntity.ok("Successfully deleted event=" + eventId);
        } catch (EventNotFoundException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/cancelEvent")
    public ResponseEntity<String> cancelEvent(@RequestParam String eventId) {
        try {
            eventService.cancelEvent(eventId);
            return ResponseEntity.ok(eventId);
        } catch (EventNotFoundException e) {
            String errMsg = "Error cancelling event - " + e.getMessage();
            log.error(errMsg);
            return ResponseEntity.internalServerError().body(errMsg);
        }
    }

    @PostMapping("/updateEvent")
    public ResponseEntity<?> updateEvent(@RequestBody UpdateEventRequest updateEventRequest) {
        try {
            EventDto eventDto = eventService.updateEvent(updateEventRequest);
            return ResponseEntity.ok(eventDto);
        } catch (UnauthorizedException | EventNotFoundException e) {
            String errMsg = "Error updating event - " + e.getMessage();
            log.error(errMsg, e);
            return ResponseEntity.internalServerError().body(errMsg);
        }
    }
}
