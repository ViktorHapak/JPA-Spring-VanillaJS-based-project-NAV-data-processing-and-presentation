package com.example.nav.draw;

import com.example.nav.dto.CategoryIntersectionDTO;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

public class UpSetPanel extends JPanel {

    private final List<CategoryIntersectionDTO> intersections;
    private final long totalCost;
    private final int totalCount;

    private final DecimalFormat moneyFormat = new DecimalFormat("#,###");
    private final DecimalFormat percentFormat = new DecimalFormat("0.00");

    public UpSetPanel(List<CategoryIntersectionDTO> intersections, long totalCost, int totalCount) {
        this.intersections = intersections;
        this.totalCost = totalCost;
        this.totalCount = totalCount;
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        int leftMargin = 180;
        int rightMargin = 320;
        int topMargin = 90;
        int barAreaHeight = 260;
        int matrixTop = topMargin + barAreaHeight + 40;
        int rowHeight = 35;
        int columnWidth = 42;
        int dotRadius = 6;

        Set<String> categorySet = new LinkedHashSet<>();
        for (CategoryIntersectionDTO dto : intersections) {
            categorySet.addAll(dto.getCategories());
        }
        List<String> allCategories = new ArrayList<>(categorySet);
        Collections.sort(allCategories);

        allCategories.sort((a, b) -> {
            if (a.toLowerCase().contains("egyeb") && !b.toLowerCase().contains("egyeb")) return 1;
            if (!a.toLowerCase().contains("egyeb") && b.toLowerCase().contains("egyeb")) return -1;

            return a.compareToIgnoreCase(b);
        });

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.BOLD, 26));
        String title = "Számlák eloszlása kategória-metszetenként";
        FontMetrics fmTitle = g2.getFontMetrics();
        g2.drawString(title, (width - fmTitle.stringWidth(title)) / 2, 35);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
        String subtitle = "Összesített költség: " + moneyFormat.format(totalCost) + " HUF"
                + "    |    Tételek száma: " + moneyFormat.format(totalCount);
        FontMetrics fmSub = g2.getFontMetrics();
        g2.drawString(subtitle, (width - fmSub.stringWidth(subtitle)) / 2, 60);

        int maxCount = intersections.stream()
                .mapToInt(CategoryIntersectionDTO::getCount)
                .max()
                .orElse(1);

        int x = leftMargin;
        for (CategoryIntersectionDTO dto : intersections) {
            int barHeight = (int) ((dto.getCount() * 1.0 / maxCount) * (barAreaHeight - 40));
            int barY = topMargin + (barAreaHeight - barHeight);

            g2.setColor(new Color(70, 130, 180));
            g2.fillRect(x, barY, columnWidth - 10, barHeight);

            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(x, barY, columnWidth - 10, barHeight);

            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            String countText = String.valueOf(dto.getCount());
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(countText, x + ((columnWidth - 10) - fm.stringWidth(countText)) / 2, barY - 5);

            x += columnWidth;
        }

        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        for (int i = 0; i < allCategories.size(); i++) {
            int y = matrixTop + i * rowHeight;

            g2.setColor(Color.BLACK);
            g2.drawString(allCategories.get(i), 20, y + 5);

            g2.setColor(new Color(230, 230, 230));
            g2.drawLine(leftMargin - 10, y, width - rightMargin, y);
        }

        x = leftMargin;
        for (CategoryIntersectionDTO dto : intersections) {
            List<String> cats = dto.getCategories();
            List<Integer> activeRows = new ArrayList<>();

            for (int i = 0; i < allCategories.size(); i++) {
                int centerX = x + (columnWidth - 10) / 2;
                int centerY = matrixTop + i * rowHeight;

                if (cats.contains(allCategories.get(i))) {
                    g2.setColor(Color.BLACK);
                    g2.fillOval(centerX - dotRadius, centerY - dotRadius, dotRadius * 2, dotRadius * 2);
                    activeRows.add(i);
                } else {
                    g2.setColor(new Color(200, 200, 200));
                    g2.fillOval(centerX - dotRadius, centerY - dotRadius, dotRadius * 2, dotRadius * 2);
                }
            }

            if (activeRows.size() >= 2) {
                int centerX = x + (columnWidth - 10) / 2;
                int y1 = matrixTop + activeRows.get(0) * rowHeight;
                int y2 = matrixTop + activeRows.get(activeRows.size() - 1) * rowHeight;

                g2.setColor(Color.BLACK);
                g2.drawLine(centerX, y1, centerX, y2);
            }

            x += columnWidth;
        }

        int legendX = width - rightMargin + 20;
        int legendY = topMargin + 10;

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2.drawString("Metszetek összköltsége", legendX, legendY);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));

        //int maxLegendItems = Math.min(intersections.size(), 12);
        for (int i = 0; i < intersections.size(); i++) {
            CategoryIntersectionDTO dto = intersections.get(i);

            String comboText = String.join(" + ", dto.getCategories());
            String rowText = (i + 1) + ". "
                    + dto.getCount() + " db"
                    + " | "
                    + moneyFormat.format(dto.getTotalValue()) + " HUF";

            int y = legendY + 25 + i * 28;

            g2.setColor(new Color(70, 130, 180));
            g2.fillRect(legendX, y - 8, 10, 10);

            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(legendX, y - 8, 10, 10);

            g2.setColor(Color.BLACK);
            g2.drawString(rowText, legendX + 18, y);

            g2.setColor(new Color(90, 90, 90));
            g2.drawString(comboText, legendX + 18, y + 12);
        }

        int infoY = matrixTop + allCategories.size() * rowHeight + 35;
        g2.setFont(new Font("SansSerif", Font.ITALIC, 13));
        g2.setColor(new Color(80, 80, 80));
        g2.drawString("Fenti oszlopok: metszetek darabszáma | Alsó pontmátrix: kategóriakombinációk | Jobb oldalt: metszetek összköltsége", 20, infoY);
    }
}
