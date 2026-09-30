package com.example.nav.draw;

import com.example.nav.dto.CategoryIntersectionDTO;
import com.example.nav.dto.GazdmutExpenseDTO;
import com.example.nav.dto.SzamlaDTO;
import com.example.nav.models.Kategoria;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.*;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

@Component
public class DiagramMaker {

    private Scanner read;
    private final DecimalFormat df = new DecimalFormat("#.##");

    public DiagramMaker() {

    }

    public void taeorComposition(Map<String, Integer> countResources,
                                 Map<String, String> descriptionResources,
                                 int count){

        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        for (Entry<String, Integer> entry : countResources.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Cégek eloszlása ágazatokként",
                dataset,
                true,   // legend
                false,  // tooltips
                false   // urls
        );

        chart.setBackgroundPaint(Color.WHITE);

        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
        plot.setBackgroundPaint(new Color(245,245,245));
        plot.setOutlineVisible(false);

        // Szeletcímke: kategória + százalék
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0} ({2})",
                new DecimalFormat("0"),
                new DecimalFormat("0.00%")
        ));

        // Legendacímke: kategória + jellemző méret
        plot.setLegendLabelGenerator(new PieSectionLabelGenerator() {
            @Override
            public String generateSectionLabel(PieDataset dataset, Comparable key) {
                String teaor = key.toString();
                String description = descriptionResources.getOrDefault(teaor, "ismeretlen");
                return teaor + " (jellemző méret: " + description + ")";
            }

            @Override
            public AttributedString generateAttributedSectionLabel(
                    org.jfree.data.general.PieDataset dataset,
                    Comparable key) {
                return null;
            }
        });

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(1000, 700));

        ImageMaker imageMaker = new ImageMaker(1000, 700);
        imageMaker.saveDiagram(chartPanel, "taeor_composition.png");
        // imageMaker.display(chartPanel, "TEÁOR-kategóriák összetétele");
    }

    public void taeorExpenses(Map<String, Long> invoiceResources,
                              int count, long avg){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Entry<String, Long> entry : invoiceResources.entrySet()) {
            dataset.addValue(entry.getValue(), "Összköltség", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Ágazatok összköltsége",
                "Tevékenységi ágazatok",
                "Ágazatok összköltsége (HUF)",
                dataset,
                PlotOrientation.VERTICAL,
                false,   // legend
                true,  // tooltips
                false   // urls
        );

        chart.setBackgroundPaint(Color.WHITE);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(245,245,245));
        plot.setOutlineVisible(false);
        plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        ((NumberAxis) plot.getRangeAxis()).setNumberFormatOverride(new DecimalFormat("#,###"));

        plot.getRenderer().setSeriesPaint(0, new Color(52,120,190));
        plot.getRenderer().setDefaultItemLabelPaint(new Color(60,60,60));
        plot.getRenderer().setDefaultItemLabelsVisible(true,true);
        plot.getRenderer().setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator(
                "{2}",
                new DecimalFormat("#,###")
        ));

        ValueMarker avgMarker = new ValueMarker(avg);
        avgMarker.setPaint(Color.RED);
        avgMarker.setStroke(new BasicStroke(2.0f));
        avgMarker.setLabel("Átlagos költség: " + String.format("%,d", avg) + " HUF");
        avgMarker.setLabelPaint(Color.RED);
        avgMarker.setLabelFont(new Font("SansSerif", Font.BOLD, 11));
        avgMarker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
        avgMarker.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);

        plot.addRangeMarker(avgMarker);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(1000, 700));

        ImageMaker imageMaker = new ImageMaker(1000, 700);
        imageMaker.saveDiagram(chartPanel, "taeor_expenses.png");
    }

    public void expensesDistribution(List<GazdmutExpenseDTO> gazdmutExpenseResources,
                                     GazdmutExpenseDTO largestExpenseGazdmut, long avg){
        XYSeries series = new XYSeries("Cégek összköltsége");

        for (int i = 0; i < gazdmutExpenseResources.size(); i++) {
            GazdmutExpenseDTO g = gazdmutExpenseResources.get(i);
            if (g.getTotalExpense() != null && g.getTotalExpense() > 0) {
                series.add(i + 1, g.getTotalExpense());
            }
        }

        XYSeries avgSeries = new XYSeries("Átlag");
        avgSeries.add(1, avg);
        avgSeries.add(gazdmutExpenseResources.size(), avg);

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        dataset.addSeries(avgSeries);

        String extraInfo =
                "Legnagyobb költségű cég: "
                        + largestExpenseGazdmut.getAaAzon() + " ("
                        + String.format("%,d", largestExpenseGazdmut.getTotalExpense()) + " HUF)"
                + "\nÁtlagos költség: " + String.format("%,d", avg) + " HUF";

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Cégek összköltsége csökkenő sorrendben",
                "Cégek rangsora", "Költség (HUF)", dataset
        );

        chart.setBackgroundPaint(Color.WHITE);

        TextTitle subtitle = new TextTitle(extraInfo);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitle.setPaint(new Color(70, 70, 70));
        chart.addSubtitle(subtitle);

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(new Color(245, 245, 245));
        plot.setOutlineVisible(false);
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinePaint(new Color(210, 210, 210));
        plot.setDomainGridlinePaint(new Color(225, 225, 225));

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, new Color(52, 120, 190));
        renderer.setSeriesPaint(1, Color.RED);
        renderer.setSeriesStroke(1,
                new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                        1f, new float[]{6f,6f}, 0f
                ));
        renderer.setSeriesItemLabelGenerator(1,
                (dataset1, series1, item) ->
                        "Átlag: " + String.format("%,d", avg));

        renderer.setDefaultItemLabelsVisible(false);
        plot.setRenderer(renderer);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(1000, 700));

        ImageMaker imageMaker = new ImageMaker(1000, 700);
        imageMaker.saveDiagram(chartPanel, "expenses_distribution.png");
    }

    public void topInvoiceValues(List<SzamlaDTO> szamlaResources){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        szamlaResources.forEach(sz -> {
            String label = sz.getEsstId() + " [" + sz.getCustomer().getAaAzon() + "]";
            dataset.addValue(sz.getXBruttoHuf(), "Legnagyobb tételek", label);
        });

        JFreeChart chart = ChartFactory.createBarChart(
                "Legnagyobb tételek mérete",
                "Tételek azonosítója: számla_azon [cég_azon]",
                "Tételek költsége",
                dataset,
                PlotOrientation.VERTICAL,
                false,   // legend
                true,  // tooltips
                false   // urls
        );
        chart.setBackgroundPaint(Color.WHITE);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(245,245,245));
        plot.setOutlineVisible(false);
        plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        ((NumberAxis) plot.getRangeAxis()).setNumberFormatOverride(new DecimalFormat("#,###"));

        plot.getRenderer().setDefaultItemLabelsVisible(true);
        plot.getRenderer().setSeriesPaint(0, new Color(52,120,190));
        plot.getRenderer().setDefaultItemLabelPaint(new Color(60,60,60));
        plot.getRenderer().setDefaultItemLabelsVisible(true,true);
        plot.getRenderer().setDefaultItemLabelGenerator(
                new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("#,###"))
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(1000, 700));

        ImageMaker imageMaker = new ImageMaker(1000, 700);
        imageMaker.saveDiagram(chartPanel, "topInvoice_values.png");

    }

    public void invoiceValuesUpsetByCategory(List<CategoryIntersectionDTO> intersections,
                                             long totalCost,
                                             int totalCount) {

        JPanel upsetPanel = new UpSetPanel(intersections, totalCost, totalCount);
        upsetPanel.setPreferredSize(new Dimension(1200, 800));

        ImageMaker imageMaker = new ImageMaker(1200, 800);
        imageMaker.savePanel(upsetPanel, "invoice_values_upset.png");
    }
}
