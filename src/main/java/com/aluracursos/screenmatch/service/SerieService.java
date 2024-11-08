package com.aluracursos.screenmatch.service;

import com.aluracursos.screenmatch.dto.EpisodioDTO;
import com.aluracursos.screenmatch.dto.SerieDTO;
import com.aluracursos.screenmatch.model.Serie;
import com.aluracursos.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {

    @Autowired
    private SerieRepository serieRepository;

    public List<SerieDTO> obtenerTodasLasSeries(){
        return convertidorSeries(serieRepository.findAll());
    }

    public List<SerieDTO> obtenerTop5(){
        return convertidorSeries(serieRepository.findTop5ByOrderByEvaluacionDesc());
    }

    public List<SerieDTO> obtenerLanzamientosRecientes(){
        return convertidorSeries(serieRepository.lanzamientosMasRecientes());
    }

    public List<SerieDTO> convertidorSeries(List<Serie> series){

        return series.stream().map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalDeTemporadas(),
                s.getEvaluacion(), s.getPoster(), s.getGenero(), s.getActores(), s.getSinopsis())).collect(Collectors.toList());

    }

    public SerieDTO obtenerPorId(Long id){
        Optional<Serie> serie = serieRepository.findById(id);

        if(serie.isPresent()){
            Serie s = serie.get();
            return new SerieDTO(s.getId(),s.getTitulo(), s.getTotalDeTemporadas(),
                    s.getEvaluacion(), s.getPoster(), s.getGenero(), s.getActores(), s.getSinopsis());
        }
        return null;
    }

    public List<EpisodioDTO> obtenerTodasLasTemporadas(Long id){
        Optional<Serie> serie = serieRepository.findById(id);

        if(serie.isPresent()){
            Serie s = serie.get();

            return s.getEpisodios().stream().map(e -> new EpisodioDTO(e.getTemporada(), e.getTitulo(),
                    e.getNumeroEpisodio())).collect(Collectors.toList());

        }

        return null;
    }


    public List<EpisodioDTO> obtenerEpisodiosPorTemporada(Long id, Integer numeroTemporada){
        Optional<Serie> serie = serieRepository.findById(id);

        if(serie.isPresent()){
            Serie s = serie.get();

            return s.getEpisodios().stream().filter(e -> e.getTemporada() == numeroTemporada)
                    .map(e -> new EpisodioDTO(e.getTemporada(), e.getTitulo(),
                            e.getNumeroEpisodio())).collect(Collectors.toList());
        }

        return null;
    }

}
