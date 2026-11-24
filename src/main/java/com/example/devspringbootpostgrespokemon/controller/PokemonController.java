package com.example.devspringbootpostgrespokemon.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pokemon")
public class PokemonController {

    @GetMapping("/all")
    public List<String> getPokemonList() {
        return List.of(
                "Onyx",
                "Pikacku",
                "Ratatta"
        );
    }
}
