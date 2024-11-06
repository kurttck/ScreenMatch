package com.aluracursos.screenmatch.repository;

import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SerieRepository extends JpaRepository<Serie, Long> {

   Optional<Serie> findByTituloContainsIgnoreCase(String titulo);

   List<Serie> findTop5ByOrderByEvaluacionDesc();

   List<Serie> findByGenero(Categoria genero);

   //List<Serie> findByTotalTemporadasLessThanEqualAndEvaluacionGreaterThanEqual(int totalTemporadas, double evaluacion);

   /*@Query( value ="select * from series where series.total_de_temporadas <= 6 and series.evaluacion >=7.5", nativeQuery = true)
   List<Serie> seriesPorTemporadaYEvaluacion();*/

   @Query("select s from Serie s where s.totalDeTemporadas <= :totalTemporadas and s.evaluacion >= :evaluacion")
   List<Serie> seriesPorTemporadaYEvaluacion(int totalTemporadas, double evaluacion);

   @Query("select e from Serie s join s.episodios e where e.titulo ilike %:nombreEpisodio%")
   List<Episodio> episodiosPorNombre(String nombreEpisodio);

   @Query("select e from Serie s join s.episodios e where s = :serie order by e.evaluacion desc limit 5")
   List<Episodio> top5Episodios(Serie serie);


}
