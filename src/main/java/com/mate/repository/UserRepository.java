package com.mate.repository;

import com.mate.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<UserEntity, String> {

    @Query("{ 'isActive': true }")
    List<UserEntity> findAllActiveUsers();

}
