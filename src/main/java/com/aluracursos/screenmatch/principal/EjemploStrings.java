package com.aluracursos.screenmatch.principal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EjemploStrings {
    public void muestraEjemplo(){
        List<String> nombres = Arrays.asList("juan", "pedro", "maria", "kurt");

        nombres.stream().sorted().limit(2).filter(n -> n.startsWith("j")).map(String::toUpperCase).forEach(System.out::println);



    }
}
