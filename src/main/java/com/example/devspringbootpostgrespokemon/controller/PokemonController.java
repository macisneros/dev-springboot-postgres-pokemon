package com.example.devspringbootpostgrespokemon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/pokemons")
public class PokemonController {

    @RequestMapping(method = RequestMethod.GET, path = "/all")
    @ResponseBody
    public List<String> getPokemonList() {
        return List.of(
                "Onyx",
                "Pikacku",
                "Ratatta"
        );
    }
}
