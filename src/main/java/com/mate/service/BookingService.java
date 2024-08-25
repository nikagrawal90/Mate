package com.mate.service;

import com.mate.entity.BookingEntity;
import com.mate.entity.EventEntity;
import com.mate.entity.UserEntity;
import com.mate.entity.enums.Status;
import com.mate.exception.*;
import com.mate.model.dto.BookingDto;
import com.mate.model.dto.request.ConfirmBookingRequest;
import com.mate.repository.BookingRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.time.Instant;

public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;
    @Autowired
    private PaymentService paymentService;

    public BookingDto addBooking(BookingDto bookingDto) throws UserNotFoundException, EventNotFoundException, PaymentDetailsNotFoundException, PaymentDetailsMismatchException, PaymentExpiredException, InsufficientAmountException {
        BookingEntity bookingEntity = new BookingEntity();

        BeanUtils.copyProperties(bookingDto, bookingEntity);
        bookingEntity.setStatus(Status.PENDING);
        bookingRepository.save(bookingEntity);
        bookingDto.setBookingId(bookingEntity.getBookingId());

        return bookingDto;
    }

    public BookingEntity getBookingById(String bookingId) throws BookingNotFoundException {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(String.format("Booking with id=%s not found", bookingId)));
    }

    public void confirmBooking(ConfirmBookingRequest confirmBookingRequest) throws InvalidRequestException, PaymentDetailsNotFoundException, PaymentDetailsMismatchException, PaymentExpiredException, InsufficientAmountException, PaymentFailedException {
        try {
            BookingEntity bookingEntity = getBookingById(confirmBookingRequest.getBookingId());
            UserEntity userEntity = userService.getUserById(bookingEntity.getUserId());
            EventEntity eventEntity = eventService.getEventById(bookingEntity.getEventId());

            paymentService.verifyPaymentForEvent(confirmBookingRequest.getPaymentId(), userEntity, eventEntity, confirmBookingRequest.getBookingId());

            bookingEntity.setStatus(Status.SUCCESS);
        } catch (BookingNotFoundException | UserNotFoundException | EventNotFoundException e) {
            throw new InvalidRequestException(e.getMessage());
        } catch (PaymentDetailsNotFoundException | PaymentDetailsMismatchException | PaymentFailedException e) {
            throw e;
        } catch (InsufficientAmountException | PaymentExpiredException e) {
            paymentService.initiateRefund(confirmBookingRequest.getPaymentId());
            throw e;
        }
    }

    public void cancelBooking(String bookingId) throws InvalidRequestException {
        try {
            BookingEntity bookingEntity = getBookingById(bookingId);
            if(!bookingEntity.getStatus().equals(Status.SUCCESS)) {
                throw new InvalidRequestException(String.format("Booking with bookingId=%s not in successful state", bookingId));
            }

            if(bookingEntity.getBookingTimestamp().before(Timestamp.from(Instant.now()))) {
                throw new InvalidRequestException(String.format("Booking with bookingId=%s is of older timestamp", bookingId));
            }

            paymentService.initiatePartialRefund(BookingEntity bookingEntity);
            bookingEntity.setStatus(Status.CANCELLED);
            bookingRepository.save(bookingEntity);
        } catch (BookingNotFoundException e) {
            throw e;
        } catch (InvalidRequestException e) {
            throw e;
        }


    }
}
