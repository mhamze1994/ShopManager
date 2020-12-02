/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application.printer;

import entity.AnbarGardaniEntity;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.AttributedString;
import java.util.ArrayList;

/**
 *
 * @author PersianDevStudio
 */
public class AnbarGardaniRawPrint implements Pageable, Printable {

    private final ArrayList<AnbarGardaniEntity> allItems;
    private final int rowPerPage;

    private Font SMALL_FONT = new Font("B Yekan+", Font.PLAIN, 9);

    public AnbarGardaniRawPrint(ArrayList<AnbarGardaniEntity> allItems, int rowPerPage) {
        this.allItems = allItems;
        this.rowPerPage = rowPerPage;
    }

    @Override
    public int getNumberOfPages() {
        return allItems.size() / rowPerPage + 1;
    }

    @Override
    public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException {
        return new PageFormat();
    }

    @Override
    public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException {
        return this;
    }

    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
        Graphics2D g2d = (Graphics2D) g;
        if (pageIndex > getNumberOfPages()) {
            return NO_SUCH_PAGE;
        }
        int paperWidth = Settings.getInt(Settings.KEY_PRINT_PAPER_WIDTH);
        int rowHeight = (Settings.getInt(Settings.KEY_PRINT_PAPER_HEIGHT) - (int) (3.6 * pageFormat.getImageableY())) / (rowPerPage + 1);//1 is for header

        double ratio[] = {0.15, 0.15, 0.15, 0.36, 0.11, 0.08};
        String texts[] = {"شمارش 3", "شمارش 2", "شمارش 1", "شرح کالا", "شناسه کالا", "ردیف"};

        int columnStartX = (int) ((pageFormat.getWidth() - paperWidth) / 2);
        int rowStartY = Settings.getInt(Settings.KEY_PRINT_VERTICAL_PADDING) + (int) pageFormat.getImageableY() + 1;

        g2d.setFont(SMALL_FONT);
        FontMetrics fm = g2d.getFontMetrics();

        drawRow(columnStartX, ratio, paperWidth, g2d, rowStartY, rowHeight, fm, texts);

        rowStartY += rowHeight;

        int startIndex = pageIndex * rowPerPage;
        for (int indx = startIndex; indx < startIndex + rowPerPage && indx < allItems.size(); indx++) {
            columnStartX = (int) ((pageFormat.getWidth() - paperWidth) / 2);
            texts = new String[]{" ", " ", " ", allItems.get(indx).desc, allItems.get(indx).itemId + "", (indx + 1) + ""};
            drawRow(columnStartX, ratio, paperWidth, g2d, rowStartY, rowHeight, fm, texts);
            rowStartY += rowHeight;
        }
        return PAGE_EXISTS;
    }

    private void drawRow(int columnStartX, double[] ratio, int paperWidth, Graphics2D g2d, int rowStartY, int rowHeight, FontMetrics fm, String[] texts) {
        g2d.drawRect(columnStartX, rowStartY, paperWidth, rowHeight);

        int lastX = 0;
        int currentX = columnStartX;
        for (int i = 0; i < ratio.length; i++) {
            lastX = currentX;
            currentX += (int) (paperWidth * ratio[i]);
            //dont draw the last line
            if (i < ratio.length - 1) {
                g2d.drawLine(currentX, rowStartY, currentX, rowStartY + rowHeight);
            }

            int cellWidth = currentX - lastX;
            int strWidth = fm.stringWidth(texts[i]);

            AttributedString cellString = new AttributedString(texts[i]);
            cellString.addAttribute(TextAttribute.FONT, SMALL_FONT, 0, texts[i].length());

            TextLayout t2 = new TextLayout(cellString.getIterator(),
                    g2d.getFontRenderContext());
            t2.draw(g2d,
                    lastX + (cellWidth - strWidth) / 2,
                    rowStartY + (rowHeight - fm.getHeight()) / 2 + fm.getAscent());

        }
    }

}
