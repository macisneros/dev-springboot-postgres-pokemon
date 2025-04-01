package com.example.devspringbootpostgrespokemon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DevSpringbootPostgresPokemonApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevSpringbootPostgresPokemonApplication.class, args);
    }

}
