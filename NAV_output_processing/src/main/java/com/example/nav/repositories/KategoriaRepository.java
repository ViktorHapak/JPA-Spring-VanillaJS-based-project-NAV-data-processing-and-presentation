package com.example.nav.repositories;

import com.example.nav.models.Gazdmut;
import com.example.nav.models.Kategoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface KategoriaRepository extends JpaRepository<Kategoria, Integer> {

    List<Kategoria> findAllByOrderByIdDesc();

    List<Kategoria> findAllByOrderByNevAsc();
}
