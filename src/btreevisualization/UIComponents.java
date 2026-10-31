package btreevisualization;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * Kumpulan komponen UI kustom dan helper yang dipakai bersama oleh panel-panel
 * aplikasi ({@link LeftPanel}, {@link CenterPanel}, {@link RightPanel}).
 *
 * <p>Kelas ini menyediakan dua kelompok fungsionalitas:</p>
 * <ul>
 *   <li><b>Factory panel</b> — {@link #createPanelHeader(String, IconType)}
 *       dan {@link #createSubPanel(String, IconType)} menghasilkan header dan
 *       panel konsisten dengan ikon, judul, dan garis tepi yang seragam.</li>
 *   <li><b>Komponen kustom</b> — {@link FlatButton} untuk tombol bergaya
 *       datar dengan ikon opsional, {@link CustomIcon} untuk ikon vektor yang
 *       digambar programatik, serta {@link #createLegendItem(Color, String)}
 *       untuk item legenda warna.</li>
 * </ul>
 *
 * <p>Konstanta warna publik di kelas ini menjadi sumber tunggal palet UI,
 * sehingga perubahan tema cukup dilakukan di satu tempat.</p>
 *
 * @author Edison Siahaan_L
 */
public class UIComponents {

    // ===== Palet warna bersama =====
    /** Warna garis tepi standar untuk panel dan kotak input. */
    public static final Color COLOR_BORDER = new Color(210, 220, 235);

    /** Warna teks judul panel. */
    public static final Color COLOR_TITLE_TEXT = new Color(20, 60, 130);

    /** Warna latar header panel. */
    public static final Color COLOR_PANEL_HEADER_BG = new Color(240, 248, 255);

    /** Tombol aksi utama "tambah/sisip". */
    public static final Color COLOR_BTN_GREEN = new Color(76, 175, 80);

    /** Tombol aksi destruktif "hapus". */
    public static final Color COLOR_BTN_RED = new Color(244, 67, 54);

    /** Tombol aksi sekunder (cari, batch, undo/redo, apply). */
    public static final Color COLOR_BTN_BLUE = new Color(33, 150, 243);

    /** Tombol netral (reset, pause, step). */
    public static final Color COLOR_BTN_GRAY = new Color(220, 225, 230);

    /**
     * Jenis ikon yang dapat digambar oleh {@link CustomIcon}.
     * Setiap nilai memiliki implementasi gambar sendiri di
     * {@link CustomIcon#paintIcon(Component, Graphics, int, int)}.
     */
    public enum IconType {
        TREE, GEAR, DOC, PLUS, MINUS, RESET, INFO, SEARCH, HISTORY, PLAY, ARROW
    }

    /**
     * Membuat header panel dengan latar lembut, ikon, dan judul berwarna.
     * Header ini dipakai oleh {@link #createSubPanel(String, IconType)} dan
     * juga oleh {@link CenterPanel}.
     *
     * <p>Judul dapat dikosongkan saat pembuatan dan diisi kemudian melalui
     * {@code updateTexts()} agar mengikuti bahasa yang aktif.</p>
     *
     * @param title    teks judul, boleh kosong.
     * @param iconType jenis ikon di kiri judul.
     * @return panel header siap pakai.
     */
    public static JPanel createPanelHeader(String title, IconType iconType) {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        header.setBackground(COLOR_PANEL_HEADER_BG);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
        header.add(new JLabel(new CustomIcon(iconType, 16, 16)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        titleLabel.setForeground(COLOR_TITLE_TEXT);
        header.add(titleLabel);
        return header;
    }

    /**
     * Membuat sub-panel berbingkai dengan header di bagian atas.
     * Isi panel ditambahkan oleh pemanggil melalui {@code BorderLayout.CENTER}.
     *
     * @param title    teks judul, boleh kosong (diisi kemudian oleh updateTexts).
     * @param iconType jenis ikon header.
     * @return sub-panel kosong dengan header.
     */
    public static JPanel createSubPanel(String title, IconType iconType) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        panel.add(createPanelHeader(title, iconType), BorderLayout.NORTH);
        return panel;
    }

    /**
     * Membuat item legenda berupa kotak warna dengan keterangan teks.
     * Keterangan mendukung {@code \n} untuk baris baru (dikonversi ke
     * {@code <br>} lewat HTML).
     *
     * @param color warna kotak legenda.
     * @param text  keterangan legenda.
     * @return panel siap ditambahkan ke wadah legenda.
     */
    public static JPanel createLegendItem(Color color, String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setBackground(Color.WHITE);
        JLabel box = new JLabel();
        box.setOpaque(true);
        box.setBackground(color);
        box.setPreferredSize(new Dimension(20, 20));
        box.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.add(box);
        String htmlText = "<html>" + text.replaceAll("\n", "<br>") + "</html>";
        JLabel label = new JLabel(htmlText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 11));
        panel.add(label);
        return panel;
    }

    /**
     * Tombol datar (flat) dengan latar membulat, ikon opsional, dan teks di
     * tengah. Menggantikan {@link JButton} standar agar tampilan konsisten
     * dengan desain aplikasi.
     *
     * <p>Warna latar bereaksi terhadap state:</p>
     * <ul>
     *   <li>Nonaktif — abu-abu muda dengan teks abu-abu.</li>
     *   <li>Normal — warna latar asli.</li>
     *   <li>Hover — warna latar yang lebih terang.</li>
     *   <li>Pressed — warna latar yang lebih gelap.</li>
     * </ul>
     *
     * <p>Ikon dan teks digambar secara manual di {@link #paint(Graphics)}
     * alih-alih lewat layout default Swing, agar pemusatan horizontal keduanya
     * (ikon + teks) presisi dan tidak bergantung pada {@link FontMetrics}
     * platform.</p>
     */
    public static class FlatButton extends JButton {
        private final Color bgColor;
        private final Color fgColor;
        private final Icon btnIcon;

        /**
         * @param text teks tombol, boleh kosong jika hanya ingin ikon.
         * @param icon ikon opsional, atau {@code null}.
         * @param bg   warna latar normal.
         * @param fg   warna teks/ikon.
         */
        public FlatButton(String text, Icon icon, Color bg, Color fg) {
            super(text);
            this.btnIcon = icon;
            this.bgColor = bg;
            this.fgColor = fg;
            // Matikan rendering default Swing agar paint() di bawah memegang
            // kendali penuh atas tampilan tombol.
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(200, 30));
            setMinimumSize(new Dimension(100, 30));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Latar membulat sesuai state tombol.
            if (!isEnabled()) {
                g2.setColor(new Color(240, 240, 240));
            } else {
                g2.setColor(getModel().isPressed() ? bgColor.darker()
                        : (getModel().isRollover() ? bgColor.brighter() : bgColor));
            }
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);

            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.setColor(isEnabled() ? fgColor : new Color(150, 150, 150));

            // Gabungkan lebar ikon + gap + teks, lalu pusatkan seluruhnya.
            FontMetrics fm = g2.getFontMetrics();
            String currentText = getText() == null ? "" : getText();
            int textWidth = fm.stringWidth(currentText);
            int textHeight = fm.getAscent();
            int iconWidth = (btnIcon != null) ? btnIcon.getIconWidth() : 0;
            int gap = (btnIcon != null) ? 8 : 0;
            int totalWidth = iconWidth + gap + textWidth;
            int startX = (getWidth() - totalWidth) / 2;
            int textY = (getHeight() + textHeight) / 2 - 2;

            if (btnIcon != null) {
                btnIcon.paintIcon(this, g2, startX, (getHeight() - btnIcon.getIconHeight()) / 2);
            }
            g2.drawString(currentText, startX + iconWidth + gap, textY);
            g2.dispose();
        }
    }

    /**
     * Ikon vektor yang digambar programatik sesuai {@link IconType}.
     * Ukuran ikon dapat berbeda per pemakaian; koordinat gambar selalu
     * diskalakan dari kanvas 24×24 agar konsisten di berbagai ukuran.
     */
    public static class CustomIcon implements Icon {
        private final IconType type;
        private final int w, h;

        /**
         * @param type jenis ikon yang akan digambar.
         * @param w    lebar ikon dalam piksel.
         * @param h    tinggi ikon dalam piksel.
         */
        public CustomIcon(IconType type, int w, int h) {
            this.type = type;
            this.w = w;
            this.h = h;
        }

        @Override public int getIconWidth()  { return w; }
        @Override public int getIconHeight() { return h; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Seluruh ikon digambar pada kanvas logis 24×24, lalu diskalakan
            // ke ukuran w×h yang diminta.
            g2.translate(x, y);
            g2.scale(w / 24.0, h / 24.0);

            switch (type) {
                case TREE:
                    g2.setColor(new Color(46, 125, 50));
                    g2.fill(new Ellipse2D.Double(4, 4, 16, 16));
                    g2.fill(new Ellipse2D.Double(2, 10, 10, 10));
                    g2.fill(new Ellipse2D.Double(12, 10, 10, 10));
                    g2.setColor(new Color(78, 52, 46));
                    g2.fill(new Rectangle2D.Double(11, 16, 2, 6));
                    break;
                case GEAR:
                    g2.setColor(COLOR_BTN_BLUE);
                    g2.fill(new Ellipse2D.Double(4, 4, 16, 16));
                    g2.setColor(Color.WHITE);
                    g2.fill(new Ellipse2D.Double(9, 9, 6, 6));
                    g2.setColor(COLOR_BTN_BLUE);
                    // Delapan "gigi" di sekeliling roda.
                    for (int i = 0; i < 8; i++) {
                        double angle = Math.toRadians(i * 45);
                        g2.fillRect((int)(12 + 10 * Math.cos(angle)) - 2,
                                    (int)(12 + 10 * Math.sin(angle)) - 2, 4, 4);
                    }
                    break;
                case DOC:
                    g2.setColor(COLOR_BTN_BLUE);
                    g2.fill(new RoundRectangle2D.Double(5, 2, 14, 20, 3, 3));
                    g2.setColor(Color.WHITE);
                    g2.fillRect(8, 8, 8, 2);
                    g2.fillRect(8, 12, 8, 2);
                    g2.fillRect(8, 16, 8, 2);
                    g2.fillRect(8, 20, 6, 2);
                    break;
                case PLUS:
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(3));
                    g2.drawLine(6, 12, 18, 12);
                    g2.drawLine(12, 6, 12, 18);
                    break;
                case MINUS:
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(3));
                    g2.drawLine(6, 12, 18, 12);
                    break;
                case RESET:
                    // Panah melingkar (arc 270°) dengan kepala panah di ujungnya.
                    g2.setColor(new Color(80, 90, 100));
                    g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawArc(4, 4, 16, 16, 45, 270);
                    g2.fillPolygon(new int[]{16, 20, 20}, new int[]{2, 4, 8}, 3);
                    break;
                case INFO:
                    g2.setColor(COLOR_BTN_BLUE);
                    g2.fill(new Ellipse2D.Double(0, 0, 24, 24));
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Serif", Font.BOLD, 16));
                    g2.drawString("i", 10, 17);
                    break;
                case SEARCH:
                    g2.setColor(new Color(13, 71, 161));
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.draw(new Ellipse2D.Double(3, 3, 14, 14));
                    g2.drawLine(15, 15, 21, 21);
                    break;
                case HISTORY:
                    // Jam: lingkaran dengan jarum penunjuk waktu.
                    g2.setColor(new Color(100, 120, 150));
                    g2.setStroke(new BasicStroke(2));
                    g2.draw(new Ellipse2D.Double(4, 4, 16, 16));
                    g2.drawLine(12, 12, 12, 6);
                    g2.drawLine(12, 12, 16, 14);
                    break;
                case PLAY:
                    g2.setColor(Color.WHITE);
                    g2.fillPolygon(new int[]{6, 18, 6}, new int[]{4, 12, 20}, 3);
                    break;
                case ARROW:
                    g2.setColor(COLOR_BTN_BLUE);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawLine(2, 12, 20, 12);
                    g2.fillPolygon(new int[]{18, 24, 18}, new int[]{8, 12, 16}, 3);
                    break;
            }
            g2.dispose();
        }
    }
}