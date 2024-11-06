package com.aluracursos.screenmatch.service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

public class ConsultaChatGPT {
    private static final String API_KEY = "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";



    public static String obtenerTraduccion(String texto){
        //OpenAiService service = new OpenAiService("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");

        OpenAiService service = new OpenAiService(API_KEY);

        CompletionRequest completionsRequest = CompletionRequest.builder()
                .model("gpt-3.5-turbo-instruct")
                .prompt("Traduce a español el siguiente texto: "+texto)
                .maxTokens(100)
                .temperature(0.7)
                .build();

        var respuesta = service.createCompletion(completionsRequest);
        return respuesta.getChoices().get(0).getText();

    }
}
