package com.example.reactive.controller.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class MapResource implements Serializable {

    private int counter;

    private String position;

    private String name;

    private String time;
}
