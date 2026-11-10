package com.project.gl.config;

import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.gl.model.response.PencadanganApiResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PencadanganApiClient {

    private final RestClient gadaiRestClient;
    private final ObjectMapper objectMapper;

    public PencadanganApiResponse getPencadangan(
            String periode,
            String kodeCabang) {

        try {
            PencadanganApiResponse response = gadaiRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/pencadangan.php")
                            .queryParam("periode", periode)
                            .queryParam("kode_cabang", kodeCabang)
                            .build())
                    .retrieve()
                    .body(PencadanganApiResponse.class);

            if (response == null || !response.isSuccess()
                    || response.getData() == null) {
                throw new IllegalStateException(
                        response != null
                                ? response.getMessage()
                                : "Response API pencadangan kosong"
                );
            }

            return response;

        } catch (HttpStatusCodeException e) {

            try {
                JsonNode json = objectMapper.readTree(
                        e.getResponseBodyAsString()
                );

                String message = json.path("message")
                        .asText("Data pencadangan tidak tersedia.");

                throw new IllegalStateException(message);

            } catch (IllegalStateException ex) {
                throw ex;

            } catch (Exception ex) {
                throw new IllegalStateException(
                        "Gagal mengambil data pencadangan."
                );
            }
        }
    }
}