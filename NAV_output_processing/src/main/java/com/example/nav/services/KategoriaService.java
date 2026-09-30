package com.example.nav.services;

import com.example.nav.models.Kategoria;
import com.example.nav.repositories.KategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class KategoriaService {

    @Autowired
    KategoriaRepository kategoriaRepository;

    public List<Kategoria> findAllCategories() {

        return kategoriaRepository.findAllByOrderByIdDesc()
                .stream()
                .sorted((a, b) -> {
                    if (a.getNev().contains("tobbi") || a.getNev().contains("egyeb")) return 1;
                    if (b.getNev().contains("tobbi") || b.getNev().contains("egyeb")) return -1;

                    return a.getNev().compareTo(b.getNev());
                })
                .collect(Collectors.toList());
    }

    public List<String> findAllCategoryNames() {
        return kategoriaRepository.findAllByOrderByNevAsc()
                .stream().map(Kategoria::getNev).filter(Objects::nonNull).distinct()
                .sorted((a, b) -> {
                    if (a.contains("tobbi") || a.contains("egyeb")) return 1;
                    if (b.contains("tobbi") || b.contains("egyeb")) return -1;
                    return a.compareTo(b);
                })
                .collect(Collectors.toList());
    }
}
