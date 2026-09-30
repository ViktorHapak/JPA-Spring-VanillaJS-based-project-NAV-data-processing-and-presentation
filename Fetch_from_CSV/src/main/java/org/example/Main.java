package org.example;

import org.example.dao.gazdmutDao;
import org.example.dao.szamlaDao;

public class Main {

    public static void main(String[] args) {

        gazdmutDao dao = new gazdmutDao();
        szamlaDao dao2 = new szamlaDao();

        //dao.addGazmutResource("csv/gazdmut.csv");
        //dao2.takeCategories("csv/szamla.csv");
        //dao2.addSzamlaResource("csv/szamla.csv");
        dao2.addSzamlaResourceSequential("csv/szamla.csv");

    }
}
