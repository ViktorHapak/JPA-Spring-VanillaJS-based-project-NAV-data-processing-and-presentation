package org.example.dao;

import org.example.CSV_reader;
import org.example.models.Gazdmut;
import org.example.models.Kategoria;
import org.example.models.Szamla;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import java.util.*;
import java.util.stream.Collectors;

public class szamlaDao {

    private EntityManagerFactory emf;
    private EntityManager em;


    public szamlaDao() {
        this.emf = Persistence.createEntityManagerFactory("nav_pu");
        this.em = emf.createEntityManager();
    }

    public void takeCategories(String szamlaRoot) {
        List<Map<String,Object>> szamlaResource =
                CSV_reader.readCSV(szamlaRoot,new String[]{"ESST_ID","V_ADOSZAM_TORZSSZAM_DEP","X_BRUTTO_HUF","KATEGORIA"});

        List<String> kategoriak = szamlaResource.stream()
                .map(e -> (String)e.get("KATEGORIA"))
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        kategoriak.forEach(k -> {
            Kategoria kategoria = new Kategoria();
            kategoria.setNev(k);
            em.persist(kategoria);
        });
        em.getTransaction().commit();
        em.close();
    }

    public Map<String, Kategoria> findAllCategories() {
        List<Kategoria> kategoriaList = this.em.createQuery("SELECT k FROM Kategoria k").getResultList();
        Map<String, Kategoria> kategoriaLookup = new HashMap<>();
        for (Kategoria k : kategoriaList) {
            kategoriaLookup.put(k.getNev(), k);
        }

        return kategoriaLookup;
    }

    public void addSzamlaResource(String szamlaRoot) {
        List<Map<String,Object>> szamlaResource =
                CSV_reader.readCSV(szamlaRoot,new String[]{"ESST_ID","V_ADOSZAM_TORZSSZAM_DEP","X_BRUTTO_HUF","KATEGORIA"});
        int batchSize = 1000;

        gazdmutDao gazdmutDao = new gazdmutDao();
        Map<Integer,Gazdmut> gazdmutLookup = gazdmutDao.findAllGazdmuts();
        Map<String,Kategoria> kategoriaLookup = findAllCategories();

        //We need to distribute the whole szamlaResource into partitions, according to available processors:
        int nThreads = Math.min(
                Runtime.getRuntime().availableProcessors(),
                Math.max(1, szamlaResource.size())
        );

        List<List<Map<String, Object>>> partitions = new ArrayList<>();
        for (int i = 0; i < nThreads; i++) {
            partitions.add(new ArrayList<>());
        }

        for (Map<String, Object> row : szamlaResource) {
            int esstId = Integer.parseInt((String) row.get("ESST_ID"));
            int bucket = Math.floorMod(esstId, nThreads); //Returns div-remnant, defining number of partition
            partitions.get(bucket).add(row);
        }

        List<Thread> threads = new ArrayList<>(nThreads);
        for(List<Map<String, Object>> partition : partitions) {
            Thread thread = new Thread(() -> {
                try {
                    EntityManager em = this.emf.createEntityManager();
                    em.getTransaction().begin();
                    int counter = 0;

                    for (Map<String, Object> row : partition) {
                        try {
                             counter++;

                             /*Gazdmut gazdmut = em.find(Gazdmut.class, row.get("V_ADOSZAM_TORZSSZAM_DEP"));
                             Gazdmut gazdmut1 = em.createQuery(
                                    "SELECT g FROM Gazdmut g WHERE g.aaAzon = :aaAzon", Gazdmut.class)
                            .setParameter("aaAzon", Integer.parseInt((String) row.get("V_ADOSZAM_TORZSSZAM_DEP")))
                            .getSingleResult();*/

                            Integer gazdmutId = Integer.parseInt((String) row.get("V_ADOSZAM_TORZSSZAM_DEP"));

                            if (!gazdmutLookup.containsKey(gazdmutId)) {
                                throw new NoResultException("No Gazdmut found for id: " + gazdmutId);
                            }

                            Gazdmut gazdmut = em.getReference(Gazdmut.class, gazdmutId);

                            if (gazdmut == null) {
                                throw new NoResultException("No Gazdmut found for id: " + gazdmutId);
                            }

                            /*Kategoria kategoria = em.createQuery("SELECT k FROM Kategoria k WHERE k.nev = :nev",
                                            Kategoria.class)
                                    .setParameter("nev", (String) row.get("KATEGORIA")).getSingleResult();*/

                            Kategoria kategoria = em.getReference(Kategoria.class, kategoriaLookup.get((String) row.get("KATEGORIA")).getId());

                            int esst_id = Integer.parseInt((String) row.get("ESST_ID"));
                            Szamla szamla = em.find(Szamla.class, esst_id);
                            if (szamla == null) {
                                szamla = new Szamla();
                                szamla.setEsstId(Integer.parseInt((String) row.get("ESST_ID")));
                                szamla.setGazdmut(gazdmut);
                                szamla.setXBruttoHuf(Integer.parseInt((String) row.get("X_BRUTTO_HUF")));

                                szamla.addKategoria(kategoria);
                                em.persist(szamla);

                            } else {
                                if (!szamla.getKategoriak().contains(kategoria)) {
                                    szamla.addKategoria(kategoria);
                                }
                            }

                            if (counter > 0 && counter % batchSize == 0) {
                                em.flush();
                                em.clear();
                                counter = 0;
                            }
                        } catch (NoResultException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    em.getTransaction().commit();
                } catch (Exception e) {
                    if (em.getTransaction().isActive()) {em.getTransaction().rollback();}
                    throw e;
                } finally {
                    em.close();
                }
            });
            threads.add(thread);
            thread.start();
        }

        for (Thread thread: threads) {
            try{
                thread.join();
            }
            catch (InterruptedException ignored) {}
        }
    }

    public void addSzamlaResourceSequential(String szamlaRoot) {
        List<Map<String, Object>> szamlaResource =
                CSV_reader.readCSV(szamlaRoot,
                        new String[]{"ESST_ID", "V_ADOSZAM_TORZSSZAM_DEP", "X_BRUTTO_HUF", "KATEGORIA"});

        int batchSize = 1000;

        gazdmutDao gazdmutDao = new gazdmutDao();
        Map<Integer, Integer> gazdmutIdLookup =
                gazdmutDao.findAllGazdmuts()
                        .keySet()
                        .stream()
                        .collect(Collectors.toMap(
                                id -> id,
                                id -> id
                        ));

        Map<String, Integer> kategoriaIdLookup =
                findAllCategories()
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().getId()
                        ));

        EntityManager em = this.emf.createEntityManager();

        try {
            em.getTransaction().begin();
            int counter = 0;

            for (Map<String, Object> row : szamlaResource) {
                Integer esstId = Integer.parseInt((String) row.get("ESST_ID"));
                Integer gazdmutId = Integer.parseInt((String) row.get("V_ADOSZAM_TORZSSZAM_DEP"));
                Integer xBruttoHuf;
                try {
                    xBruttoHuf = Integer.parseInt((String) row.get("X_BRUTTO_HUF"));
                } catch (NumberFormatException e) {
                    xBruttoHuf = convertToNumber((String) row.get("X_BRUTTO_HUF"));
                }

                String kategoriaNev = (String) row.get("KATEGORIA");

                if (!gazdmutIdLookup.containsKey(gazdmutId)) {
                    System.out.println("No Gazdmut found for id: " + gazdmutId);
                    continue;
                }

                Integer kategoriaId = kategoriaIdLookup.get(kategoriaNev);
                if (kategoriaId == null) {
                    System.out.println("No Kategoria found for name: " + kategoriaNev);
                    continue;
                }

                Gazdmut gazdmut = em.getReference(Gazdmut.class, gazdmutId);
                Kategoria kategoria = em.getReference(Kategoria.class, kategoriaId);

                Szamla szamla = em.find(Szamla.class, esstId);

                if (szamla == null) {
                    szamla = new Szamla();
                    szamla.setEsstId(esstId);
                    szamla.setGazdmut(gazdmut);
                    szamla.setXBruttoHuf(xBruttoHuf);
                    szamla.addKategoria(kategoria);
                    em.persist(szamla);
                } else {
                    if (!szamla.getKategoriak().contains(kategoria)) {
                        szamla.addKategoria(kategoria);
                    }
                }

                counter++;
                if (counter % batchSize == 0) {
                    em.flush();
                    em.clear();
                    counter = 0;
                }
            }

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    private Integer convertToNumber(String value) {

        if (value == null)
            return null;

        value = value.trim();

        if (value.isEmpty())
            return null;

        try {
            return Integer.valueOf(value);
        }
        catch (NumberFormatException e) {

            double d = Double.parseDouble(value);

            if (d > Integer.MAX_VALUE || d < Integer.MIN_VALUE) {
                throw new RuntimeException("Number out of Integer range: " + value);
            }

            return (int) d;
        }
    }

    public void close(){
        this.em.close();
    }
}
