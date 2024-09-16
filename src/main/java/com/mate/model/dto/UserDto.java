package com.mate.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mate.entity.enums.*;
import com.mate.model.Album;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserDto {
    private String userId;
    private String name;
    private String email;
    private Boolean isEmailVerified;
    private String contact;
    private Boolean isContactVerified;
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
}
