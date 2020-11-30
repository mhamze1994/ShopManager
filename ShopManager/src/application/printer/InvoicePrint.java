/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application.printer;

import application.JalaliCalendar;
import entity.Contact;
import entity.Unit;
import entity.invoice.Invoice;
import entity.invoice.InvoiceDetail;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.AttributedString;

/**
 *
 * @author PersianDevStudio
 */
public class InvoicePrint implements Printable {

    private final Invoice invoice;

    private Font HEADER_FONT = new Font("B Titr", Font.PLAIN, 14);
    private Font SMALL_FONT = new Font("B Yekan+", Font.PLAIN, 11);
    private Font SMALLER_FONT = new Font("B Yekan+", Font.PLAIN, 9);

    public InvoicePrint(Invoice invoice) {
        this.invoice = invoice;
    }

    @Override
    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {

        if (page > 0) {
            return NO_SUCH_PAGE;
        }

        // User (0,0) is typically outside the
        // imageable area, so we must translate
        // by the X and Y values in the PageFormat
        // to avoid clipping.
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

//        g2d.drawLine(0, 0, 0, 100);
//        g2d.drawLine(PrintSettings.paperWidth, 0, PrintSettings.paperWidth, 100);
        // Now we perform our rendering
        String title = "فاکتور";
        switch (invoice.getOperationType()) {
            case Invoice.TYPE_BUY:
                title += "خرید";
                break;
            case Invoice.TYPE_REFUND_BUY:
                title += "برگشت خرید";
                break;
            case Invoice.TYPE_SELL:
                title += "فروش";
                break;
            case Invoice.TYPE_REFUND_SELL:
                title += "برگشت فروش";
                break;
        }
        title += " - " + Settings.getString(Settings.KEY_SHOP_TITLE);

        int paperWidth = Settings.getInt(Settings.KEY_PRINT_PAPER_WIDTH);
        int horizontal_padding = Settings.getInt(Settings.KEY_PRINT_HORIZONTAL_PADDING);

        g2d.setFont(HEADER_FONT);
        FontMetrics fm = g2d.getFontMetrics();
        int widthTitle = fm.stringWidth(title);
        int heightTitle = fm.getHeight();
        g2d.drawString(title,
                (paperWidth - widthTitle) / 2,
                g2d.getFontMetrics().getHeight());

        //----------------------------------------------------------------------
        String date = "تاریخ : " + JalaliCalendar.format(invoice.getDate());
        g2d.setFont(SMALL_FONT);
        fm = g2d.getFontMetrics();
        int widthDate = fm.stringWidth(date);
        int heightDate = fm.getHeight();
        g2d.drawString(date,
                paperWidth - widthDate - horizontal_padding,
                g2d.getFontMetrics().getHeight() + heightTitle);

        //----------------------------------------------------------------------
        String invoiceInfo = "";
        invoiceInfo += "شماره فاکتور : " + invoice.getInvoiceId();
        invoiceInfo += "            ";
        invoiceInfo += Invoice.inoutBuy(invoice.getOperationType()) ? "فروشنده : " : "خریدار : ";
        invoiceInfo += Contact.find(invoice.getContact()).concatinatedInfo();

        AttributedString string = new AttributedString(invoiceInfo);
        g2d.setFont(SMALL_FONT);
        fm = g2d.getFontMetrics();
        int widthInfo = fm.stringWidth(invoiceInfo);

        string.addAttribute(TextAttribute.FONT, SMALL_FONT, 0, invoiceInfo.length());
        TextLayout tl = new TextLayout(string.getIterator(),
                g2d.getFontRenderContext());
        tl.draw(g2d, paperWidth - widthInfo, g2d.getFontMetrics().getHeight() + heightTitle + heightDate + 5);

        Stroke dashed = new BasicStroke(0.5f);
        g2d.setStroke(dashed);
        g2d.drawLine(0, 65, paperWidth, 65);
        //-----------------------------------------------------------------------------
        double ratio[] = {0.14, 0.13, 0.13, 0.09, 0.35, 0.11, 0.05};
//        String cellText[] = {"ردیف", "کد", "شرح کالا", "مقدار", "واحد", "فی", "جمع"};

        int pageLine = 70;

        g2d.setFont(SMALLER_FONT);
        fm = g2d.getFontMetrics();

        for (int j = 0; j < invoice.getDetails().size() + 1; j++) {
            String cellText[];
            if (j == 0) {
                cellText = new String[]{"جمع", "فی", "واحد", "مقدار", "شرح کالا", "کد", "ردیف"};
            } else {
                InvoiceDetail detail = invoice.getDetails().get(j - 1);
                cellText = new String[]{
                    detail.getTotalCost().abs().stripTrailingZeros().toPlainString(),
                    detail.getUnitPrice().abs().stripTrailingZeros().toPlainString(),
                    Unit.toString(detail.getUnitId()),
                    detail.getAmount().abs().stripTrailingZeros().toPlainString(),
                    detail.getItem().getDescription(),
                    detail.getItemId() + "",
                    j + ""};
            }

            int baseLine = (int) (fm.getHeight() * 1.2f);
            g2d.drawRect(0, pageLine, paperWidth, baseLine);
            int currentX = 0;
            int lastX = 0;
            for (int i = 0; i < ratio.length; i++) {
                lastX = currentX;
                currentX += (int) (paperWidth * ratio[i]);
                //dont draw the last line
                if (i < ratio.length - 1) {
                    g2d.drawLine(currentX, pageLine, currentX, pageLine + baseLine);
                }

                int cellWidth = currentX - lastX;
                int strWidth = fm.stringWidth(cellText[i]);

                AttributedString cellString = new AttributedString(cellText[i]);
                cellString.addAttribute(TextAttribute.FONT, SMALLER_FONT, 0, cellText[i].length());

                TextLayout t2 = new TextLayout(cellString.getIterator(),
                        g2d.getFontRenderContext());
                t2.draw(g2d,
                        lastX + (cellWidth - strWidth) / 2,
                        pageLine + fm.getHeight() - fm.getDescent());
//                g2d.drawString(cellText[i], lastX + (cellWidth - strWidth) / 2, pageLine + fm.getHeight() - fm.getDescent());

            }
            pageLine += baseLine;
        }

        pageLine += fm.getDescent();
        g2d.drawLine(0, pageLine, paperWidth, pageLine);
        pageLine += fm.getAscent();

        g2d.setFont(HEADER_FONT.deriveFont(10));
        fm = g2d.getFontMetrics();

        String totalCostStr = "جمع کل فاکتور : " + invoice.getTotalCost().abs().stripTrailingZeros().toPlainString();
        AttributedString totalCostString = new AttributedString(totalCostStr);
        totalCostString.addAttribute(TextAttribute.FONT, HEADER_FONT, 0, totalCostStr.length());

        TextLayout t2 = new TextLayout(totalCostString.getIterator(),
                g2d.getFontRenderContext());
        t2.draw(g2d, 2*horizontal_padding, pageLine + fm.getHeight());
        return PAGE_EXISTS;
    }

}
