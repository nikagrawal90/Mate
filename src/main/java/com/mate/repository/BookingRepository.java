package com.mate.repository;

import com.mate.entity.BookingEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends MongoRepository<BookingEntity, String> {
    List<BookingEntity> findBookingEntitiesByEventId(String eventId);
}
