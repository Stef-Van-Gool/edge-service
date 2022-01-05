package com.example.edgeservice.controller;

import com.example.edgeservice.model.FilledGrapeWine;
import com.example.edgeservice.model.Grape;
import com.example.edgeservice.model.Wine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class FilledGrapeWineController {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${wineservice.baseurl}")
    private String wineServiceBaseUrl;

    @Value("${grapeservice.baseurl}")
    private String grapeServiceBaseUrl;

    @GetMapping("/wines/{grapeName}")
    public FilledGrapeWine getWinesByGrape(@PathVariable String grapeName){
        Grape grape = restTemplate.getForObject("http://" + grapeServiceBaseUrl + "/grapes/grapename/{grapeName}",
                Grape.class, grapeName);

        Wine wine = restTemplate.getForObject("http://" + wineServiceBaseUrl + "/wines/grape/" + grapeName,
                Wine.class);

        return new FilledGrapeWine(grape, wine);
    }

    @GetMapping("/combo/region/{region}")
    public List<FilledGrapeWine> getWinesAndGrapesByRegion(@PathVariable String region){
        List<FilledGrapeWine> returnList = new ArrayList<>();

        ResponseEntity<List<Wine>> responseEntityWines =
                restTemplate.exchange("http://" + wineServiceBaseUrl + "/wines/region/{region}",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Wine>>() {
                        }, region);
        List<Wine> wines = responseEntityWines.getBody();
        for (Wine wine: wines) {
            Grape grape =
                    restTemplate.getForObject("http://" + grapeServiceBaseUrl + "/grapes/grapename/{grapeName}",
                            Grape.class, wine.getGrapeName());
            returnList.add(new FilledGrapeWine(grape, wine));
        }

        return returnList;
    }

}
