package com.aluracursos.screenmatch.repository;

import com.aluracursos.screenmatch.model.Episodio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EpisodioRepository extends JpaRepository<Episodio, Long> {
}
