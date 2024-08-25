package com.mate.repository;

import com.mate.entity.EventEntity;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends MongoRepository<EventEntity, String> {
    @Query("{ 'location': { $nearSphere: { $geometry: { type: 'Point', coordinates: [?0, ?1] }, $maxDistance: ?2 } } }")
    List<EventEntity> findByLocationNear(Point location, Distance distance);
}
