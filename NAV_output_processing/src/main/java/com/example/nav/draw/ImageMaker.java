package com.example.nav.draw;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageMaker {


    private int width; private int height;

    public ImageMaker(int width, int height) {
        this.width = width; this.height = height;
    }

    public void saveDiagram(ChartPanel chartPanel, String fileName) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(width, height));
        panel.removeAll();
        panel.add(chartPanel, BorderLayout.CENTER);
        panel.setSize(width, height);
        panel.doLayout();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        panel.printAll(g2);
        g2.dispose();

        try {
            File file = new File("src/main/resources/img", fileName);
            System.out.println("Új diagramm létrehozva: " + file.getAbsolutePath());
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            throw new RuntimeException("Nem sikerült a kép mentése: " + fileName, e);
        }
    }

    public void savePanel(JPanel panel, String fileName) {
        panel.setSize(width, height);
        panel.doLayout();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        panel.printAll(g2);
        g2.dispose();

        try {
            File file = new File("src/main/resources/img", fileName);
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            throw new RuntimeException("Nem sikerült a panel mentése: " + fileName, e);
        }
    }

    public void display(ChartPanel chartPanel, String title) {
        JFrame frame = new JFrame();
        frame.setSize(width, height);
        frame.setTitle(title);
        frame.setContentPane(chartPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
