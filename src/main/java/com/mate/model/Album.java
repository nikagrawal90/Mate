package com.mate.model;

import lombok.Data;

import java.util.List;

@Data
public class Album {
    private String genre;
    private List<String> artists;
}
