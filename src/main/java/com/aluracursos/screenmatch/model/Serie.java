package com.aluracursos.screenmatch.model;

import com.aluracursos.screenmatch.service.ConsumoApi;
import jakarta.persistence.*;

import java.io.IOException;
import java.util.List;
import java.util.OptionalDouble;

@Entity
@Table(name="series")
public class Serie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_serie")
    private Long id;

    @Column(unique = true, name="titulo")
    private String titulo;

    @Column(name="total_de_temporadas")
    private Integer totalDeTemporadas;

    @Column(name="evaluacion")
    private Double evaluacion;

    @Column(name="actores")
    private String actores;

    @Enumerated(EnumType.STRING)
    @Column(name="genero")
    private Categoria genero;

    private String sinopsis;

    private String poster;

    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Episodio> episodios;

    public Serie(){}

    public Serie(DatosSerie datos){
        this.titulo = datos.titulo();
        this.totalDeTemporadas = datos.totalDeTemporadas();
        this.evaluacion = OptionalDouble.of(Double.parseDouble(datos.evaluacion())).orElse(0.0);
        this.actores = datos.actores();
        this.genero = Categoria.fromString(datos.genero().split(",")[0].trim());

        try{
           this.sinopsis = ConsumoApi.traduccionDeepTranslate(datos.sinopsis());
        }catch (IOException | InterruptedException e) {
            System.out.println("No se pudo traducir la sinopsis");
            this.sinopsis = datos.sinopsis();
        }

        this.poster = datos.poster();
    }

    @Override
    public String toString() {
        return
                "titulo='" + titulo + '\n' +
                ", totalDeTemporadas=" + totalDeTemporadas +
                ", evaluacion=" + evaluacion +
                ", actores='" + actores + '\n' +
                ", genero=" + genero +
                ", sinopsis='" + sinopsis + '\n' +
                ", poster='" + poster + '\n'+ "episodios=" + episodios
                ;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getTotalDeTemporadas() {
        return totalDeTemporadas;
    }

    public void setTotalDeTemporadas(Integer totalDeTemporadas) {
        this.totalDeTemporadas = totalDeTemporadas;
    }

    public Double getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(Double evaluacion) {
        this.evaluacion = evaluacion;
    }

    public String getActores() {
        return actores;
    }

    public void setActores(String actores) {
        this.actores = actores;
    }

    public Categoria getGenero() {
        return genero;
    }

    public void setGenero(Categoria genero) {
        this.genero = genero;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public List<Episodio> getEpisodios() {
        return episodios;
    }

    public void setEpisodios(List<Episodio> episodios) {
        episodios.forEach(episodio -> episodio.setSerie(this));
        this.episodios = episodios;
    }
}
