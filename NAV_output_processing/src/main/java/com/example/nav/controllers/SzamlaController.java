package com.example.nav.controllers;


import com.example.nav.draw.DiagramMaker;
import com.example.nav.dto.*;
import com.example.nav.models.Gazdmut;
import com.example.nav.models.Kategoria;
import com.example.nav.models.Szamla;
import com.example.nav.services.GazdmutService;
import com.example.nav.services.KategoriaService;
import com.example.nav.services.SzamlaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/szamla")
public class SzamlaController {

    @Autowired
    GazdmutService gazdmutService;

    @Autowired
    SzamlaService szamlaService;

    @Autowired
    KategoriaService kategoriaService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    DiagramMaker diagramMaker;

    @GetMapping("/{id}")
    public ResponseEntity<?> getSzamla(@PathVariable int id){
        try{
            Szamla szamla = szamlaService.findSzamlaById(id);

            if(szamla == null) return ResponseEntity.badRequest().body("Nincs ilyen tételazonosító: " + id);
            else {
                SzamlaDTO szamlaResource = convertToSzamlaDTO(szamla);
                return new ResponseEntity<>(szamlaResource, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/topPurchases")
    public ResponseEntity<?> getTopInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        try{
            //Adaterőforrás előállítása JSON-kimenethez és diagrammhoz
            List<SzamlaDTO> largestInvoiceResources = szamlaService.topLargestInvoices(page,size)
                    .stream().map(sz -> convertToSzamlaDTO(sz)).collect(Collectors.toList());

            if(largestInvoiceResources.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            //Diagrammgenerátor hívása
            diagramMaker.topInvoiceValues(largestInvoiceResources);
            return new ResponseEntity<>(largestInvoiceResources, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/category-summarize")
    public ResponseEntity<?> getCategorySummarize(){
        try{
            //Adaterőforrás előállítása a diagrammhoz
            List<CategoryIntersectionDTO> intersections = szamlaService.summarizeTopCategoryIntersections(15);
            Map<Kategoria, Long> summarizedValues = szamlaService.summarizedValuesByCategories();
            Map<Kategoria, Integer> countByCategories = szamlaService.countByCategories();
            if(summarizedValues.isEmpty() || intersections.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            long totalSum = szamlaService.totalSum();
            int totalCount = szamlaService.totalCount();

            //Adaterőforrás előállítása a JSON-kimenethez
            List<Kategoria> categories = kategoriaService.findAllCategories();
            List<Map<String, CategoryAnalyzeDTO>> analyzisResources = categories.stream().map(
                    c -> {
                        CategoryAnalyzeDTO categoryAnalyzeResource = new CategoryAnalyzeDTO(
                                szamlaService.countByCategory(c),
                                szamlaService.summarizedValuesByCategory(c)
                        );

                        return Map.of(c.getNev(), categoryAnalyzeResource);
                    }
            ).collect(Collectors.toList());

            //Diagrammgenerátor hívása
            System.out.println("Counts: " + countByCategories);
            System.out.println("Values: " + summarizedValues);
            diagramMaker.invoiceValuesUpsetByCategory(intersections, totalSum, totalCount);

            Map<String, Object> response = new HashMap<>();
            response.put("InvoiceAnalysis", analyzisResources);
            response.put("GroupNumber", categories.size());

            return new ResponseEntity<>(analyzisResources, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories(){

        List<String> categories = kategoriaService.findAllCategoryNames();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }



    private SzamlaDTO convertToSzamlaDTO(Szamla szamla) {
        List<String> categories = szamla.getKategoriak().stream()
                .map(Kategoria::getNev)
                .toList();

        GazdmutDTO customerDto = modelMapper.map(szamla.getGazdmut(), GazdmutDTO.class);

        return new SzamlaDTO(
                szamla.getEsstId(),
                szamla.getXBruttoHuf(),
                categories,
                customerDto
        );
    }

}
