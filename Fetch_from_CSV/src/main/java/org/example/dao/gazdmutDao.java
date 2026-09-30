package org.example.dao;

import org.example.CSV_reader;
import org.example.models.Gazdmut;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class gazdmutDao {

    private EntityManagerFactory emf;
    private EntityManager em;


    public gazdmutDao() {
        this.emf = Persistence.createEntityManagerFactory("nav_pu");
        this.em = emf.createEntityManager();
    }

    public void addGazmutResource(String gazmutRoot) {
        List<Map<String,Object>> gazdmutResource =
                CSV_reader.readCSV(gazmutRoot,new String[]{"AA_AZON","VALLALATMERET","TEAOR_KATEGORIA"});
        int batchSize = 1000;

        //Size of each chunk, depending on nthreads and number of files to be renamed.
        int nThreads = (gazdmutResource.size() < Runtime.getRuntime().availableProcessors())
                ? gazdmutResource.size() : Runtime.getRuntime().availableProcessors();;

        int chunkLenght = (gazdmutResource.size() < Runtime.getRuntime().availableProcessors())
                ? 1 : (gazdmutResource.size() + nThreads - 1) / nThreads;

        //"chunks[]" should contain initial index of each chunk (inside files)
        int chunks[] = new int[nThreads];
        for (int i = 0; i < nThreads; i++) {
            chunks[i] = i * chunkLenght;
        }

        List<Thread> threads = new ArrayList<>(nThreads);
        for(int chunk: chunks) {
            int start = chunk < gazdmutResource.size() ? chunk : gazdmutResource.size();
            int end = (start+chunkLenght) < gazdmutResource.size() ? (start+chunkLenght) : gazdmutResource.size();

            Thread thread = new Thread(() -> {
                EntityManager em = this.emf.createEntityManager();
                em.getTransaction().begin();
                int counter = 0;

                for (int i = start; i < end; i++){
                    counter++;
                    Map<String, Object> row = gazdmutResource.get(i);

                    Gazdmut gazdmut = new Gazdmut();
                    gazdmut.setAaAzon(Integer.parseInt((String) row.get("AA_AZON")));
                    gazdmut.setVallalatMeret((String) row.get("VALLALATMERET"));
                    gazdmut.setTeaorKategoria((String) row.get("TEAOR_KATEGORIA"));
                    em.persist(gazdmut);

                    if (counter > 0 && counter % batchSize == 0) {
                        em.flush();
                        em.clear();
                        counter = 0;
                    }
                }
                em.getTransaction().commit();
                em.close();
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

    public Map<Integer, Gazdmut> findAllGazdmuts() {
        List<Gazdmut> gazdmutList = this.em.createQuery("SELECT g FROM Gazdmut g").getResultList();
        Map<Integer, Gazdmut> gazdmutLookup = new HashMap<>();
        for (Gazdmut g : gazdmutList) {
            gazdmutLookup.put(g.getAaAzon(), g);
        }

        return gazdmutLookup;
    }

    public void close(){
        this.em.close();
    }
}
