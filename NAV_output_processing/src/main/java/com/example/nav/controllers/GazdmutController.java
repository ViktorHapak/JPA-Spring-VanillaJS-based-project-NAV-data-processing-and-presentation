package com.example.nav.controllers;

import com.example.nav.draw.DiagramMaker;
import com.example.nav.dto.GazdmutDTO;
import com.example.nav.dto.GazdmutExpenseDTO;
import com.example.nav.dto.TaeorAnalyzeDTO;
import com.example.nav.models.Gazdmut;
import com.example.nav.services.GazdmutService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/gazdmut")
public class GazdmutController {

    @Autowired
    GazdmutService gazdmutService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    DiagramMaker diagramMaker;

    @GetMapping("/all")
    public ResponseEntity<?> getAllGazdmuts (){
        try{
            List<GazdmutDTO> gazdmutResources = gazdmutService.findAllGazdmuts()
                    .stream().map(g -> convertToGazdmutDTO(g)).collect(Collectors.toList());

            if(gazdmutResources.isEmpty()) return new ResponseEntity<>("Üres lista", HttpStatus.NO_CONTENT);

            return new ResponseEntity<>(gazdmutResources, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Optional.ofNullable(null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGazdmut (@PathVariable int id){
        try{

            Gazdmut gazdmut = gazdmutService.findGazdmutById(id);

            if(gazdmut == null) return ResponseEntity.badRequest().body("Nincs ilyen cégazonosító: " + id);
            else {
                GazdmutDTO gazdmutResource = convertToGazdmutDTO(gazdmut);
                return new ResponseEntity<>(gazdmutResource, HttpStatus.OK);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(Optional.ofNullable(null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/taeor-analysis")
    public ResponseEntity<?> taeorAnalysis() {
        try {
            int count = gazdmutService.countGazdmuts();
            if (count == 0){
                return new ResponseEntity<>("Üres lista", HttpStatus.NO_CONTENT);
            }

            List<String> taeorCategories = gazdmutService.findAllGazdmutCategories();
            List<Map<String, TaeorAnalyzeDTO>> analyzisResources = taeorCategories.stream().map(
                    tk -> {
                        TaeorAnalyzeDTO taeorAnalysisResource = new TaeorAnalyzeDTO(
                                gazdmutService.countByCategory(tk),
                                gazdmutService.describeByTeaorCategory(tk),
                                gazdmutService.summarizeInvoiceByTeaorCategory(tk)
                        );

                        return Map.of(tk, taeorAnalysisResource);

                    }
            ).collect(Collectors.toList());

            //A JSON-válasz előállítása
            Map<String, Object> response = new HashMap<>();
            response.put("GazdmutAnalysis", analyzisResources);
            response.put("GroupNumber", taeorCategories.size());

            //Adatforrások előállítása a diagrammhoz
            Map<String, Integer> countResources = gazdmutService.countByTeaorCategories();
            Map<String, String> descriptionResources  = gazdmutService.describeByTeaorCategories();
            Map<String,Long> expenseResources = gazdmutService.summarizeInvoiceByTeaorCategories();
            Long avarageExpense = gazdmutService.avarageExpenseByTeaorCategories();

            System.out.println("Response: " + response);
            System.out.println("countRes: " + countResources);
            System.out.println("descriptionRes: " + descriptionResources);
            System.out.println("expenseRes: " + expenseResources);

            //A diagram-létrehozó meghívása
            diagramMaker.taeorComposition(countResources, descriptionResources, count);
            diagramMaker.taeorExpenses(expenseResources, count, avarageExpense);


            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(Optional.ofNullable(null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/topGazdmuts")
    public ResponseEntity<?> getTopGazdmuts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);

            if (gazdmutService.countGazdmuts() == 0){
                return new ResponseEntity<>("Üres lista", HttpStatus.NO_CONTENT);
            }

            Page<GazdmutExpenseDTO> gazdmutPage = gazdmutService.topExpenseGazdmuts(pageable);

            int totalItems = gazdmutPage.getNumberOfElements();
            int totalPages = gazdmutPage.getTotalPages();

            List<GazdmutExpenseDTO> gazdmutResources = gazdmutPage.getContent();

            Map<String, Object> response = new HashMap<>();
            response.put("gazdmuts", gazdmutResources);
            response.put("totalItems", totalItems);
            response.put("totalPages", totalPages);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Optional.ofNullable(null), HttpStatus.INTERNAL_SERVER_ERROR);
        }



    }

    @GetMapping("/expenses")
    public ResponseEntity<?> getGazdmutExpenses() {

        try {
            if (gazdmutService.countGazdmuts() == 0){
                return new ResponseEntity<>("Üres lista", HttpStatus.NO_CONTENT);
            }

            //A JSON-válasz előállítása
            List<GazdmutExpenseDTO> gazdmutResources = gazdmutService.findGazdmutExpenses();
            GazdmutExpenseDTO largestGazdmutExpense = gazdmutService.findMostExpensingGazmut();
            Long avarageExpense = gazdmutService.avarageExpensebyGazdmut();

            Map<String, Object> response = new HashMap<>();
            response.put("gazdmuts", gazdmutResources);

            //A diagramm létrehozása
            diagramMaker.expensesDistribution(gazdmutResources,largestGazdmutExpense, avarageExpense);


            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Optional.ofNullable(null), HttpStatus.INTERNAL_SERVER_ERROR);
        }



    }

    @GetMapping("/sizes")
    public ResponseEntity<List<String>> getGazdmutSizes() {
        List<String> gazdmutSizes = gazdmutService.findAllGazdmutScales();
        return new ResponseEntity<>(gazdmutSizes, HttpStatus.OK);
    }

    @GetMapping("/taeorKategories")
    public ResponseEntity<List<String>> getGazdmutTaeorCategories() {
        List<String> gazdmutCategories = gazdmutService.findAllGazdmutCategories();
        return new ResponseEntity<>(gazdmutCategories, HttpStatus.OK);
    }


    private GazdmutDTO convertToGazdmutDTO(Gazdmut gazdmut) {
        return modelMapper.map(gazdmut, GazdmutDTO.class);
    }
}
