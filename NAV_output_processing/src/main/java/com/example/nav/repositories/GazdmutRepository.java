package com.example.nav.repositories;

import com.example.nav.models.Gazdmut;
import com.example.nav.dto.GazdmutExpenseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface GazdmutRepository extends JpaRepository<Gazdmut, Integer> {

    List<Gazdmut> findByTeaorKategoria(String teaorKategoria);

    List<Gazdmut> findByTeaorKategoriaAndVallalatMeret(String gazdmutKategoria, String vallalatMeret);

    int countByTeaorKategoriaAndVallalatMeret(String teaorKategoria, String vallalatMeret);

    Gazdmut getGazdmutByAaAzon(Integer aaAzon);

    @Query("""
    SELECT COALESCE(SUM(s.xBruttoHuf), 0)
        FROM Gazdmut g
        LEFT JOIN g.szamlak s
        WHERE g.teaorKategoria = :teaorKategoria
    """)
    Long sumInvoiceCostByTeaorKategoria(String teaorKategoria);

    @Query("""
    SELECT new com.example.nav.dto.GazdmutExpenseDTO(
        g.aaAzon,
        g.vallalatMeret,
        g.teaorKategoria,
        SUM(s.xBruttoHuf)
    )
    FROM Gazdmut g
    LEFT JOIN g.szamlak s
    GROUP BY g.aaAzon, g.vallalatMeret, g.teaorKategoria
    ORDER BY SUM(s.xBruttoHuf) DESC
    """)
    Page<GazdmutExpenseDTO> findTopExpenseGazdmuts(Pageable pageable);

    @Query("""
    SELECT new com.example.nav.dto.GazdmutExpenseDTO(
        g.aaAzon,
        g.vallalatMeret,
        g.teaorKategoria,
        SUM(s.xBruttoHuf)
    )
    FROM Gazdmut g
    LEFT JOIN g.szamlak s
    GROUP BY g.aaAzon, g.vallalatMeret, g.teaorKategoria
    ORDER BY COALESCE(SUM(s.xBruttoHuf), 0L) desc 
    """)
    List<GazdmutExpenseDTO> findGazdmutExpenses();
}
