package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.DatosEpisodio;
import com.aluracursos.screenmatch.model.DatosSerie;
import com.aluracursos.screenmatch.model.DatosTemporada;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.service.ConsumoApi;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner scan = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=86078005";
    private ConvierteDatos conversor = new ConvierteDatos();

    public void mostrarMenu() {

        System.out.println("Escribe el nombre de la serie que deseas buscar");
        String nombreSerie = scan.nextLine().trim();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        var datosSerie = conversor.obtenerDatos(json, DatosSerie.class);

        System.out.println(datosSerie);

        //Busca los datos de todas las temporadas
        List<DatosTemporada> datosTemporadas = new ArrayList<>();

        for (int i=1; i<= datosSerie.totalDeTemporadas(); i++){
            json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+")+"&Season="+i+API_KEY);
            var temporada = conversor.obtenerDatos(json, DatosTemporada.class);
            datosTemporadas.add(temporada);
        }

        //datosTemporadas.forEach(System.out::println);

        //Mostrar solo el titulo de los episodios de las temporadas
        /*for (int i = 0; i < datosSerie.totalDeTemporadas(); i++) {
            List<DatosEpisodio> episodios = datosTemporadas.get(i).episodios();
            System.out.println("Temporada "+(i+1));
            for(int j=0; j<episodios.size();j++){
                System.out.println(j+1+". "+episodios.get(j).titulo());
            }
        }*/

        //mejor forma
        //datosTemporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        //Convertir todas las informaciones a una lista del tipo DatosEpisodio

        List<DatosEpisodio> datosEpisodios = datosTemporadas.stream()
                .flatMap(t -> t.episodios().stream()).collect(Collectors.toList());

        //Top 5 episodios

        /*System.out.println("Top 5 episodios");
        datosEpisodios.stream().filter(e-> !e.evaluacion().equalsIgnoreCase("N/A"))
                .peek(e-> System.out.println("Primer filtro (N/A)"+e))
                .sorted(Comparator.comparing(DatosEpisodio::evaluacion).reversed())
                .peek(e-> System.out.println("Segundo Filtr Ordenacion (de mayor a menor)"+e))
                .map(e-> e.titulo().toUpperCase())
                .peek(e-> System.out.println("Tercer Filtro (Mayusculas)"+e))
                .limit(5)
                .forEach(System.out::println);*/


        //Conviritiendo los datos a una lista de episodios
        List<Episodio> episodios = datosTemporadas.stream()
                .flatMap(t -> t.episodios().stream().map(d -> new Episodio(t.numero(), d))).collect(Collectors.toList());

        //episodios.forEach(System.out::println);

        //Busqueda de episodios a partir de x año

        /*System.out.println("Por favor ingresa el año de lanzamiento");
        int anio = scan.nextInt();
        scan.nextLine();


        LocalDate fechaBusqueda = LocalDate.of(anio, 1,1);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        episodios.stream().filter(e -> e.getFechaDeLanzamiento() != null && e.getFechaDeLanzamiento().isAfter(fechaBusqueda))
                .forEach(e -> System.out.println(
                        "Temporada"+e.getTemporada()+
                                "Episodio"+e.getTitulo()+
                                "Fecha de lanzamiento: "+e.getFechaDeLanzamiento().format(dtf)
                ));*/


        //Busqueda de episodio x pedazo de caracteres

        /*System.out.println("Por favor ingresa el pedazo del titulo que deseas buscar");
        String pedazo = scan.nextLine();


        Optional<Episodio> episodioBuscar = episodios.stream()
                .filter(e-> e.getTitulo().toUpperCase().contains(pedazo.toUpperCase())).findFirst();

        List<Episodio> listaEpisodiosSearch = episodios.stream()
                .filter(e-> e.getTitulo().toUpperCase().contains(pedazo.toUpperCase()))
                .collect(Collectors.toList());

        if (listaEpisodiosSearch.isEmpty()) {
            System.out.println("No se encontraron coincidencias.");

        } else {
            System.out.println("Se encontraron episodios coincidentes.");
            listaEpisodiosSearch.forEach(System.out::println);
        }*/

        /*if (episodioBuscar.isPresent()){
            System.out.println("Episodio encontrado"+episodioBuscar.toString());
        }else {
            System.out.println("No se encontro el episodio");
        }*/


        Map<Integer, Double> evaluacionesPorTemporada = episodios.stream()
                .filter(e -> e.getEvaluacion()>0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getEvaluacion)));

        System.out.println(evaluacionesPorTemporada);

        DoubleSummaryStatistics stats = episodios.stream()
                .filter(e-> e.getEvaluacion()>0.0)
                .collect(Collectors.summarizingDouble(Episodio::getEvaluacion));

        System.out.println(stats.getAverage());

    }


}
