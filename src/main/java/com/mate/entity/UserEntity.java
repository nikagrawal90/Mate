package com.mate.entity;

import com.mate.entity.enums.*;
import com.mate.model.Album;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "user")
@Data
public class UserEntity {
    @Id
    private String userId;
    private String name;
    private String email;
    private Boolean isEmailVerified;
    private String contact;
    private Boolean isContactVerified;
    private Boolean isWhatsappNumber;
    private City currentCity;
    private String company;
    private String workEmail;
    private Boolean isWorkEmailVerified;
    private String workTitle;
    private LookingFor lookingFor;
    private String bio;
    private String description;
    private Date dob;
    private String aadharNo;
    private Boolean isAadhaarVerified;
    private Gender gender;
    private LikesTo likesTo;
    private List<Album> albums;
    private Religion religion;
    private RelationshipStatus relationshipStatus;


    // location will be sent from UI for user

}
