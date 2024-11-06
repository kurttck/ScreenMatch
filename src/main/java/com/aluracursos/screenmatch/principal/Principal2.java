package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.*;
import com.aluracursos.screenmatch.repository.EpisodioRepository;
import com.aluracursos.screenmatch.repository.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoApi;
import com.aluracursos.screenmatch.service.ConvierteDatos;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Principal2 {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=86078005";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosSerie> datosSeries = new ArrayList<>();
    private SerieRepository repository;
    private List<Serie> series;
    private Optional<Serie> serieBuscada;

    public Principal2(SerieRepository repository) {
        this.repository = repository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar series 
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                    4 - Buscar Serie por Titulo
                    5 - Top 5 mejores series 
                    6 - Buscar Serie por Categoria
                    7 - Filtrar Series por temporadas y evaluacion
                    8 - Buscar episodio por titulo             
                    9 - Top 5 Mejores episodio             
                                 
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                mostrarSeriesBuscadas();
                break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarTop5Series();
                    break;
                case 6:
                    buscarSeriePorCategoria();
                    break;
                case 7:
                    filtrarPorTemporadaYEvaluacion();
                    break;
                case 8:
                    buscarEpisodiosPorTitulo();
                    break;
                case 9:
                    mostrarTop5EpisodiosPorSerie();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }




    private DatosSerie getDatosSerie() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        System.out.println(json);
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        return datos;
    }

    private void buscarSerieWeb() {
        DatosSerie datos = getDatosSerie();

        Serie serie = new Serie(datos);


        System.out.println(serie);

        try{
            repository.save(serie);
        }catch (DataIntegrityViolationException e){
            System.out.println("La serie ya existe en la base de datos");
        } catch (Exception e) {
            System.out.println("Ocurrio un problema inesperado: "+e.getMessage());
        }
    }

    private void buscarEpisodioPorSerie() {

        mostrarSeriesBuscadas();

        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        Optional<Serie> serie = series.stream().filter(s -> s.getTitulo().equals(nombreSerie)).findFirst();


        if(serie.isPresent()){

            var serieEncontrada = serie.get();
            //System.out.println(serieEncontrada);
        List<DatosTemporada> temporadas = new ArrayList<>();
        for (int i = 1; i <= serieEncontrada.getTotalDeTemporadas(); i++) {
            var json = consumoApi.obtenerDatos(URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
            DatosTemporada datosTemporada = conversor.obtenerDatos(json, DatosTemporada.class);
            temporadas.add(datosTemporada);
        }

        temporadas.forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream().flatMap(d -> d.episodios().stream().map(e -> new Episodio(d.numero(), e))).collect(Collectors.toList());

        serieEncontrada.setEpisodios(episodios);
        repository.save(serieEncontrada);

        }
    }

    private void mostrarSeriesBuscadas(){
        series = repository.findAll();
        series.stream().sorted(Comparator.comparing(Serie::getGenero)).forEach(System.out::println);
    }

    private void buscarSeriePorTitulo(){
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine().trim();

        serieBuscada = repository.findByTituloContainsIgnoreCase(nombreSerie);
        if(serieBuscada.isPresent()){
            System.out.println(serieBuscada.get());
        }else{
            System.out.println("Serie no encontrada");
        }

    }

    private void buscarTop5Series(){
        List<Serie> topSeries = repository.findTop5ByOrderByEvaluacionDesc();
        topSeries.forEach(e -> System.out.println("Serie: "+e.getTitulo()+" - "+e.getEvaluacion()));
    }


    private void buscarSeriePorCategoria(){
        System.out.println("Escribe el genero de la serie que deseas buscar");
        var genero = teclado.nextLine();

        var categoria = Categoria.fromEsp(genero);

        List<Serie> seriesPorCategoria = repository.findByGenero(categoria);
        System.out.println("Las series de la categoria: "+genero);
        seriesPorCategoria.forEach(System.out::println);


    }

    private void filtrarPorTemporadaYEvaluacion(){
        System.out.println("¿filtrar Serie con cuantas temporadas?");
        var temporadas = teclado.nextInt();
        teclado.nextLine();
        System.out.println(temporadas);
        System.out.println("¿filtrar Serie con cuantas evaluaciones?");
        var evaluaciones = teclado.nextDouble();
        System.out.println(evaluaciones);
        teclado.nextLine();

        List<Serie> seriesFiltradas = repository.seriesPorTemporadaYEvaluacion(temporadas, evaluaciones);
        System.out.println("*** Series filtradas ***");
        int[] count = {1};
        seriesFiltradas.forEach(e ->{
            System.out.println(count[0]+". "+e.getTitulo()+" ("+e.getEvaluacion()+") - "+e.getTotalDeTemporadas()+" Temporadas");
            count[0]++;
        } );
        System.out.println("\n");
    }

    private void buscarEpisodiosPorTitulo(){
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();

        List<Episodio> seriesEncontradas = repository.episodiosPorNombre(nombreSerie);

        seriesEncontradas.forEach(e-> System.out.printf("Serie: %s Temporada: %s Episodio %s Evaluacion %s \n",
                e.getSerie().getTitulo(), e.getTemporada(), e.getEvaluacion()));
    }

    private void mostrarTop5EpisodiosPorSerie(){
        buscarSeriePorTitulo();

        if(serieBuscada.isPresent()){
            Serie serie = serieBuscada.get();
            List<Episodio> topEpisodios = repository.top5Episodios(serie);

            System.out.println("*** TOP 5 EPISODIOS ***");
            topEpisodios.forEach(System.out::println);
        }
    }

}