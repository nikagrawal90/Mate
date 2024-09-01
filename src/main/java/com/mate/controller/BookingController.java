package com.mate.controller;

import com.mate.model.dto.BookingDto;
import com.mate.model.dto.request.ConfirmBookingRequest;
import com.mate.model.dto.response.RefundDetailsResponse;
import com.mate.service.BookingService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/booking")
@Log4j2
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/getAllAttendeesForEvent")
    public ResponseEntity<?> getAllAttendeesForEvent(@RequestParam String eventId) {
        List<String> allAttendees = bookingService.getAllAttendees(eventId);
        return ResponseEntity.ok(allAttendees);
    }

    @PostMapping("/addBooking")
    public ResponseEntity<?> addBooking(@RequestBody BookingDto bookingDto) {
        try {
            BookingDto savedBooking = bookingService.addBooking(bookingDto);
            return ResponseEntity.ok(savedBooking);
        } catch (Exception e) {
            String errMsg = "Error adding booking - " + e.getMessage();
            log.error(errMsg, e);
            return ResponseEntity.internalServerError().body(errMsg);
        }
    }

    @PutMapping("/confirmBooking")
    public ResponseEntity<?> confirmBooking(@RequestBody ConfirmBookingRequest confirmBookingRequest) {
        try {
            bookingService.confirmBooking(confirmBookingRequest);
            return ResponseEntity.ok("Confirmed Booking");
        } catch (Exception e) {
            String errMsg = "Error confirming booking - " + e.getMessage();
            log.error(errMsg, e);
            return ResponseEntity.internalServerError().body(errMsg);
        }
    }

    @PutMapping("/cancelEvent")
    public ResponseEntity<?> cancelEvent(@RequestBody String eventId) {
        try {
            bookingService.cancelEvent(eventId);
            return ResponseEntity.ok("Cancelled Event with eventId - " + eventId);
        } catch (Exception e) {
            String errMsg = String.format("Error cancelling event %s - %s", eventId, e.getMessage());
            log.error(errMsg, e);
            return ResponseEntity.internalServerError().body(errMsg);
        }
    }

    @GetMapping("/getRefundDetails")
    public ResponseEntity<?> getRefundDetails(@RequestBody String bookingId) {
        try {
            RefundDetailsResponse refundDetailsResponse = bookingService.getRefundDetails(bookingId);
            return ResponseEntity.ok(refundDetailsResponse);
        } catch (Exception e) {
            String errMsg = String.format("Error getting refund details for booking %s - %s", bookingId, e.getMessage());
            log.error(errMsg, e);
            return ResponseEntity.internalServerError().body(errMsg);
        }
    }
}
