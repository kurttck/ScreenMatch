package com.aluracursos.screenmatch.service;

import com.aluracursos.screenmatch.model.DatosTranslate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ConsumoApi {
    public String obtenerDatos(String url){

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();
            HttpResponse<String> response = null;
            try {
                response = client
                        .send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            return response.body();
    }


    public static String traduccionDeepTranslate(String text) throws IOException, InterruptedException {

        ConvierteDatos conversor = new ConvierteDatos();
        String escapedText = text.replace("\"", "\\\"");
        String jsonBody = String.format("{\"q\":\"%s\",\"source\":\"en\",\"target\":\"es\"}", escapedText);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://deep-translate1.p.rapidapi.com/language/translate/v2"))
                .header("x-rapidapi-key", "de1d84bd43msha4ffcee32a81131p14ff80jsne4738fd14f2d")
                .header("x-rapidapi-host", "deep-translate1.p.rapidapi.com")
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        DatosTranslate datos = conversor.obtenerDatos(response.body(), DatosTranslate.class);
        return datos.translations().translatedText().text();
    }
}
