package com.example.nav.services;

import com.example.nav.dto.CategoryIntersectionDTO;
import com.example.nav.models.Gazdmut;
import com.example.nav.models.Kategoria;
import com.example.nav.models.Szamla;
import com.example.nav.repositories.GazdmutRepository;
import com.example.nav.repositories.KategoriaRepository;
import com.example.nav.repositories.SzamlaRepository;
import jdk.jfr.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SzamlaService {

  @Autowired
  SzamlaRepository szamlaRepository;

  @Autowired
  GazdmutRepository gazdmutRepository;

  @Autowired
  KategoriaRepository kategoriaRepository;

  public Szamla findSzamlaById(int id) {
    return szamlaRepository.getSzamlaByEsstId(id);
  }

  public List<Kategoria> findKategoriasOfSzamla(Szamla szamla) {
    return (List<Kategoria>) szamla.getKategoriak();
  }

  public Gazdmut findVevo(Szamla szamla) {
    return szamla.getGazdmut();
  }

  public List<Szamla> topLargestInvoices(int page, int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "xBruttoHuf"));
    Page<Szamla> topInvoices = szamlaRepository.findAll(pageable);

    return topInvoices.getContent();
  }

  public Map<Kategoria, Long> summarizedValuesByCategories(){

    List<Kategoria> categories = kategoriaRepository.findAllByOrderByIdDesc();
    Map<Kategoria, Long> summarizedValues = new LinkedHashMap<>();

    categories.forEach(k -> {
      summarizedValues.put(k, szamlaRepository.sumSzamlaValuesByKategoria(k));
    });
    return summarizedValues;
  }

  public Long summarizedValuesByCategory(Kategoria k) {
    return szamlaRepository.sumSzamlaValuesByKategoria(k);
  }

  public Map<Kategoria, Integer> countByCategories(){

    List<Kategoria> categories = kategoriaRepository.findAllByOrderByIdDesc();
    Map<Kategoria, Integer> countsByCategory = new LinkedHashMap<>();

    categories.forEach(k -> {
      countsByCategory.put(k, szamlaRepository.countSzamlasByKategoria(k));
    });
    return countsByCategory;
  }

  public int countByCategory(Kategoria k) {
    return szamlaRepository.countSzamlasByKategoria(k);
  }

  public long totalSum(){
    return szamlaRepository.findAll().stream().mapToLong(Szamla::getXBruttoHuf).sum();
  }

  public int totalCount(){
    return szamlaRepository.findAll().size();
  }

  public List<CategoryIntersectionDTO> summarizeCategoryIntersections() {
    List<Szamla> szamlak = szamlaRepository.findAll();

    Map<String, Integer> counts = new HashMap<>();
    Map<String, Long> costs = new HashMap<>();
    Map<String, List<String>> categoriesByKey = new HashMap<>();

    for (Szamla szamla : szamlak) {
      List<String> names = szamla.getKategoriak().stream()
              .map(Kategoria::getNev)
              .filter(Objects::nonNull)
              .sorted((a, b) -> {
                if (a.contains("tobbi") || a.contains("egyeb")) return 1;
                if (b.contains("tobbi") || b.contains("egyeb")) return -1;
                return a.compareTo(b);
              })
              .distinct()
              .sorted()
              .toList();

      if (names.isEmpty()) {
        continue;
      }

      String key = String.join("|", names);

      counts.put(key, counts.getOrDefault(key, 0) + 1);
      costs.put(key, costs.getOrDefault(key, 0L) + szamla.getXBruttoHuf());
      categoriesByKey.putIfAbsent(key, names);
    }

    return counts.keySet().stream()
            .map(key -> new CategoryIntersectionDTO(
                    categoriesByKey.get(key),
                    counts.get(key),
                    costs.getOrDefault(key, 0L)
            ))
            .sorted((a, b) -> Integer.compare(b.getCount(), a.getCount()))
            .toList();
  }

  public List<CategoryIntersectionDTO> summarizeTopCategoryIntersections(int limit) {
    List<CategoryIntersectionDTO> intersections = summarizeCategoryIntersections();

    if (intersections.size() <= limit) {
      return intersections;
    }

    return intersections.subList(0, limit);
  }
}
