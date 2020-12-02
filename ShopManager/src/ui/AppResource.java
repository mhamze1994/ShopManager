/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author Studio
 */
public class AppResource {

    public static final String ICON_ARROW_DOWN = "/ui/res/arrow_down.png";
    public static final String ICON_ARROW_PLUS = "/ui/res/arrow_plus.png";
    public static final String ICON_CLOSE_SMALL = "/ui/res/close_s.png";
    public static final String ICON_CLOSE_MEDIUM = "/ui/res/close_m.png";
    public static final String ICON_INVOICE_NEW = "/ui/res/invoice_new.png";
    public static final String ICON_INVOICE_REFUND = "/ui/res/invoice_refund.png";
    public static final String ICON_CONTACT = "/ui/res/contact.png";
    public static final String ICON_BOX = "/ui/res/box.png";
    public static final String ICON_INVOICE_EDIT = "/ui/res/invoice_edit.png";
    public static final String ICON_PRICE_TAG = "/ui/res/price_tag.png";
    public static final String ICON_BILL_BUY = "/ui/res/bill.png";
    public static final String ICON_BILL_BUY_REFUND = "/ui/res/bill_refund.png";
    public static final String ICON_BILL_SELL = "/ui/res/sell.png";
    public static final String ICON_BILL_SELL_REFUND = "/ui/res/sell_refund.png";
    public static final String ICON_BILL_EDIT = "/ui/res/edit.png";
    public static final String ICON_STOCK = "/ui/res/stock.png";
    public static final String ICON_REPORT_1 = "/ui/res/report_1.png";
    public static final String ICON_REPORT_2 = "/ui/res/report_2.png";
    public static final String ICON_REPORT_3 = "/ui/res/money.png";
    public static final String ICON_BACKUP = "/ui/res/backup.png";
    public static final String ICON_PRINTER_SETTING = "/ui/res/printer_setting.png";
    public static final String ICON_WAREHOUSE = "/ui/res/warehouse.png";
    public static final String ICON_PROFIT_LOSS = "/ui/res/profit_loss.png";

    public static BufferedImage getImage(String path) {
        return getImage(path, null);
    }

    public static BufferedImage getImage(String path, Color overlay) {
        try {
            InputStream stream = AppResource.class.getResourceAsStream(path);
            BufferedImage read = ImageIO.read(stream);
            if (overlay != null) {
                colorOverlay(read, overlay);
            }
            return read;
        } catch (IOException ex) {
            Logger.getLogger(AppResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static ImageIcon getIcon(String path) {
        try {
            InputStream stream = AppResource.class.getResourceAsStream(path);
            return new ImageIcon(ImageIO.read(stream));
        } catch (IOException ex) {
            Logger.getLogger(AppResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static void colorOverlay(BufferedImage image, Color color) {
        Graphics2D gbi = image.createGraphics();
        gbi.setColor(color);
        gbi.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1));
        gbi.fillRect(0, 0, image.getWidth(), image.getHeight());
    }

    public static BufferedImage getScaledInstance(BufferedImage img,
            int targetWidth,
            int targetHeight,
            Object hint,
            boolean higherQuality) {
        int type = (img.getTransparency() == Transparency.OPAQUE)
                ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage) img;
        int w, h;
        if (higherQuality) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }

        do {
            if (higherQuality && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (higherQuality && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth || h != targetHeight);

        return ret;
    }
}
