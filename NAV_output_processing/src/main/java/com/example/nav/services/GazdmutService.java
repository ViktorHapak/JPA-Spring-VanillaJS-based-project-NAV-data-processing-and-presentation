package com.example.nav.services;

import com.example.nav.dto.GazdmutExpenseDTO;
import com.example.nav.models.Gazdmut;
import com.example.nav.repositories.GazdmutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class GazdmutService {

    @Autowired
    GazdmutRepository gazdmutRepository;


    public List<Gazdmut> findAllGazdmuts(){
        return gazdmutRepository.findAll();
    }

    public Gazdmut findGazdmutById(int id){
        return gazdmutRepository.getGazdmutByAaAzon(id);
    }

    public int countGazdmuts(){
        return (int)gazdmutRepository.count();
    }

    public List<String> findAllGazdmutCategories(){
        return findAllGazdmuts().stream()
                .map(g -> g.getTeaorKategoria()).distinct()
                .sorted((a, b) -> {
                    if (a.contains("tobbi") || a.contains("egyeb")) return 1;
                    if (b.contains("tobbi") || b.contains("egyeb")) return -1;
                    return a.compareTo(b);
                })
                .collect(Collectors.toList());
    }

    public List<String> findAllGazdmutScales(){
        return findAllGazdmuts().stream()
                .map(g -> g.getVallalatMeret()).distinct()
                .sorted((a, b) -> {
                    if (a.equals("kis") && b.equals("mikro")) return 1;
                    if (a.equals("mikro") && b.equals("kis")) return -1;
                    return a.compareTo(b);
                })
                .collect(Collectors.toList());
    }

    public Map<String,Integer> countByTeaorCategories(){

        List<String> teaorCategories = findAllGazdmutCategories();

        Map<String, Integer> summarizeCategories = new HashMap<>();

        teaorCategories.forEach(tk -> {
            int count = gazdmutRepository.findByTeaorKategoria(tk).size();
            summarizeCategories.put(tk, count);
        });

        return summarizeCategories;
    }

    public Integer countByCategory(String tk){
        return gazdmutRepository.findByTeaorKategoria(tk).size();
    }

    public Map<String, String> describeByTeaorCategories() {
        List<String> teaorCategories = findAllGazdmutCategories();
        List<String> vallalatMeretek = findAllGazdmutScales();

        Map<String, String> describeCategories = new HashMap<>();

        teaorCategories.forEach(tk -> {
            String legjellemzobbMeret = null;
            int maxDb = -1;

            for (String vm : vallalatMeretek) {
                int db = gazdmutRepository.countByTeaorKategoriaAndVallalatMeret(tk, vm);

                if (db > maxDb) {
                    maxDb = db;
                    legjellemzobbMeret = vm;
                }
            }

            describeCategories.put(tk, legjellemzobbMeret);
        });

        return describeCategories;
    }

    public String describeByTeaorCategory(String tk) {

        List<String> vallalatMeretek = findAllGazdmutScales();

        String legjellemzobbMeret = null;
        int maxDb = -1;

        for (String vm : vallalatMeretek) {
            int db = gazdmutRepository.countByTeaorKategoriaAndVallalatMeret(tk, vm);

            if (db > maxDb) {
                maxDb = db;
                legjellemzobbMeret = vm;
            }
        }

        return legjellemzobbMeret;


    }

    public Map<String, Long> summarizeInvoiceByTeaorCategories() {
        List<String> teaorCategories = findAllGazdmutCategories();
        Map<String, Long> summarizeCategories = new LinkedHashMap<>();

        //A párhuzamosítás nem megfelelő, mivel a JPA-keresés nem CPU-bonyolult, hanem perzisztencia-szinten bonyolult
        /*
        teaorCategories.forEach(tk -> {

            List<Gazdmut> gazdmuts = gazdmutRepository.findByTeaorKategoria(tk);
            AtomicLong sum = new AtomicLong(0);

            List<Thread> threads = new ArrayList<>();
            int nThreads = Runtime.getRuntime().availableProcessors();

            List<List<Gazdmut>> partitions = new ArrayList<>();

            for (int i = 0; i < nThreads; i++) {
                partitions.add(new ArrayList<>());
            }

            for (Gazdmut gazdmut : gazdmuts) {
                int id = gazdmut.getAaAzon();
                int bucket = Math.floorMod(id, nThreads); //Returns div-remnant, defining number of partition
                partitions.get(bucket).add(gazdmut);
            }

            for (List<Gazdmut> partition : partitions) {
                Thread thread = new Thread(() -> {

                    for (Gazdmut gazdmut : partition) {

                        long cost = gazdmut.getSzamlak().stream()
                                .mapToLong(Szamla::getXBruttoHuf).sum();
                        sum.addAndGet(cost);
                    }
                });
                threads.add(thread);
                thread.start();
            }

            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("A szál futása megszakadt.", e);
                }
            }

            summarizeCategories.put(tk, sum.get());

        });
         */

        teaorCategories.forEach(taeor -> {
            Long sum = gazdmutRepository.sumInvoiceCostByTeaorKategoria(taeor);
            sum = (sum == null) ? 0L : sum;
            summarizeCategories.put(taeor, sum);
        });

        return summarizeCategories;
    }

    public Long summarizeInvoiceByTeaorCategory(String tk) {

        Long sum = gazdmutRepository.sumInvoiceCostByTeaorKategoria(tk);
        return sum == null ? 0L : sum;
    }

    public Page<GazdmutExpenseDTO> topExpenseGazdmuts(Pageable pageable) {
        return gazdmutRepository.findTopExpenseGazdmuts(pageable);
    }

    public List<GazdmutExpenseDTO> findGazdmutExpenses(){
        return gazdmutRepository.findGazdmutExpenses();
    }

    public GazdmutExpenseDTO findMostExpensingGazmut() {
        List<GazdmutExpenseDTO> expenses = gazdmutRepository.findGazdmutExpenses();

        if (expenses.isEmpty()) {
            return null;
        }

        return expenses.get(0);
    }

    public long avarageExpensebyGazdmut() {
        List<GazdmutExpenseDTO> expenses = gazdmutRepository.findGazdmutExpenses();

        if (expenses == null || expenses.isEmpty()) {
            return 0L;
        }

        return Math.round(
                expenses.stream()
                        .map(GazdmutExpenseDTO::getTotalExpense)
                        .filter(Objects::nonNull)
                        .mapToLong(Long::longValue)
                        .average()
                        .orElse(0.0)
        );
    }

    public long avarageExpenseByTeaorCategories() {
        List<Long> expenses = findAllGazdmutCategories()
                .stream().map(tk -> gazdmutRepository.sumInvoiceCostByTeaorKategoria(tk)).
                collect(Collectors.toList());

        if (expenses == null || expenses.isEmpty()) {
            return 0L;
        }

        return Math.round(
                expenses.stream()
                        .filter(Objects::nonNull)
                        .mapToLong(Long::longValue)
                        .average()
                        .orElse(0.0)
        );
    }


    }
