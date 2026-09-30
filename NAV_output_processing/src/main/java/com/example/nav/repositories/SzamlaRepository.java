package com.example.nav.repositories;

import com.example.nav.models.Kategoria;
import com.example.nav.models.Szamla;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface SzamlaRepository extends JpaRepository<Szamla, Integer> {

    List<Szamla> getSzamlaByKategoriak(Set<Kategoria> kategoriak);

    Szamla getSzamlaByEsstId(Integer esstId);

    List<Szamla> findAll();

    Page<Szamla> findAll(Pageable pageable);

    @Query("""
    SELECT COALESCE(SUM(s.xBruttoHuf), 0)
    FROM Kategoria k
    LEFT JOIN k.szamlak s
    WHERE k = :kategoria
    """)
    long sumSzamlaValuesByKategoria(@Param("kategoria") Kategoria kategoria);

    @Query("""
    SELECT COALESCE(COUNT(s), 0)
    FROM Kategoria k
    LEFT JOIN k.szamlak s
    WHERE k = :kategoria
    """)
    int countSzamlasByKategoria(@Param("kategoria") Kategoria kategoria);


}
