package com.aluracursos.screenmatch.dto;

import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.model.Serie;

import java.util.List;
import java.util.stream.Collectors;

public record  SerieDTO(
        Long id,
        String titulo,
        Integer totalDeTemporada,
        Double evaluacion,
        String poster,
        Categoria genero,
        String actores,
        String sinopsis
) {


}
