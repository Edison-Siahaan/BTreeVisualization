package btreevisualization;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Kumpulan dialog kustom untuk aplikasi visualisasi B-Tree.
 *
 * <p>Kelas ini menyediakan dua dialog modal yang dipanggil dari
 * {@link BTreeVisualization}:</p>
 * <ul>
 *   <li>{@link #showGuide(Window)} — panduan penggunaan aplikasi berisi
 *       langkah-langkah singkat dan legenda warna.</li>
 *   <li>{@link #showAbout(Window)} — informasi tentang aplikasi, pengembang,
 *       institusi, dan teknologi yang digunakan.</li>
 * </ul>
 *
 * <p>Dialog dibangun sepenuhnya secara programatik (tanpa file .form) agar
 * tampilan konsisten di berbagai platform. Seluruh kelas utilitas di dalamnya
 * ({@link NumberBadge}, {@link Chip}, {@link AppIcon}) bersifat private-package
 * dan hanya dipakai oleh dialog-dialog di kelas ini.</p>
 *
 * @author Edison Siahaan_L
 */
public class CustomDialogs {

    // ===== Palet warna dialog =====
    /** Warna aksen utama untuk judul dan ikon. */
    static final Color ACCENT    = new Color(0x185FA5);
    /** Warna latar lembut untuk header dan badge. */
    static final Color ACCENT_BG = new Color(0xE6F1FB);
    /** Warna teks utama. */
    static final Color TEXT      = new Color(0x2C2C2A);
    /** Warna teks sekunder (deskripsi, label). */
    static final Color MUTED     = new Color(0x5F5E5A);
    /** Warna garis pemisah antar baris. */
    static final Color LINE      = new Color(0xD3D1C7);

    /**
     * Membuat font dengan gaya dan ukuran tertentu.
     *
     * @param style gaya font (misalnya {@link Font#BOLD} atau {@link Font#PLAIN}).
     * @param size  ukuran font dalam poin.
     * @return objek {@link Font} baru.
     */
    static Font font(int style, int size) {
        return new Font("Segoe UI", style, size);
    }

    // =====================================================================
    //  Komponen kecil
    // =====================================================================

    /**
     * Badge berbentuk lingkaran berisi angka, dipakai sebagai penanda langkah
     * pada dialog panduan.
     */
    static class NumberBadge extends JComponent {
        private final String number;

        NumberBadge(int n) {
            this.number = String.valueOf(n);
            setPreferredSize(new Dimension(24, 24));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(ACCENT_BG);
            g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
            g2.setColor(ACCENT);
            g2.setFont(font(Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(number)) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(number, x, y);
            g2.dispose();
        }
    }

    /**
     * Label berbentuk pil (chip) dengan latar dan garis tepi opsional.
     * Dipakai untuk menampilkan fitur dan label versi pada dialog About.
     */
    static class Chip extends JLabel {
        private final Color bg;
        private final Color line;

        /**
         * @param text teks chip
         * @param bg   warna latar
         * @param fg   warna teks
         * @param line warna garis tepi, atau {@code null} untuk tanpa garis
         */
        Chip(String text, Color bg, Color fg, Color line) {
            super(text);
            this.bg = bg;
            this.line = line;
            setForeground(fg);
            setFont(font(Font.PLAIN, 12));
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(3, 11, 3, 11));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
            if (line != null) {
                g2.setColor(line);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Ikon aplikasi berupa ilustrasi B-Tree sederhana (root + dua leaf)
     * yang ditampilkan pada header dialog About.
     */
    static class AppIcon implements Icon {
        private static final int SIZE = 56;

        @Override public int getIconWidth()  { return SIZE; }
        @Override public int getIconHeight() { return SIZE; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);

            // Latar putih dengan garis tepi membulat.
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, SIZE - 1, SIZE - 1, 16, 16);
            g2.setColor(ACCENT);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, SIZE - 1, SIZE - 1, 16, 16);

            // Tiga node: root di atas, dua leaf di bawah, dihubungkan garis.
            int rootX = SIZE / 2, rootY = 17;
            int leftX = 16, rightX = SIZE - 16, leafY = SIZE - 16;
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(ACCENT);
            g2.drawLine(rootX, rootY, leftX, leafY);
            g2.drawLine(rootX, rootY, rightX, leafY);
            g2.fillOval(rootX - 7, rootY - 7, 14, 14);
            g2.setColor(new Color(0x3B6D11));
            g2.fillOval(leftX - 6, leafY - 6, 12, 12);
            g2.fillOval(rightX - 6, leafY - 6, 12, 12);
            g2.dispose();
        }
    }

    // =====================================================================
    //  Helper dialog
    // =====================================================================

    /**
     * Membuat {@link JLabel} berisi HTML yang dibungkus pada lebar tertentu,
     * agar teks panjang tidak membuat dialog melebar.
     */
    static JLabel wrapped(String htmlBody, int width, Color color, Font font) {
        JLabel l = new JLabel("<html><div style='width:" + width + "px'>" + htmlBody + "</div></html>");
        l.setForeground(color);
        l.setFont(font);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    /**
     * Membuat garis pemisah horizontal yang selalu mengisi lebar parent.
     */
    static JSeparator separator() {
        JSeparator s = new JSeparator();
        s.setForeground(LINE);
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        s.setAlignmentX(Component.LEFT_ALIGNMENT);
        return s;
    }

    /**
     * Membuat baris tombol tutup di bagian bawah dialog.
     * Tombol juga dijadikan default button agar bisa dipicu dengan Enter.
     */
    static JPanel buttonBar(JDialog dlg, String label) {
        JButton close = new JButton(label);
        close.setPreferredSize(new Dimension(100, 30));
        close.addActionListener(e -> dlg.dispose());
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createEmptyBorder(4, 0, 6, 0));
        bar.add(close);
        dlg.getRootPane().setDefaultButton(close);
        return bar;
    }

    /**
     * Menampilkan dialog secara modal dengan perilaku standar:
     * tombol Escape menutup dialog, ukuran tidak dapat diubah, dan posisi
     * dialog dipusatkan relatif terhadap owner.
     */
    static void showDialog(JDialog dlg, Window owner) {
        dlg.getRootPane().registerKeyboardAction(
                e -> dlg.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        dlg.setResizable(false);
        dlg.pack();
        dlg.setLocationRelativeTo(owner);
        dlg.setVisible(true);
    }

    // =====================================================================
    //  Dialog: Panduan Penggunaan
    // =====================================================================

    /**
     * Menambahkan satu baris langkah (badge nomor + judul + deskripsi)
     * ke dalam panel panduan.
     */
    static void addStep(JPanel parent, int number, String title, String descHtml) {
        JPanel badgeWrap = new JPanel(new BorderLayout());
        badgeWrap.setOpaque(false);
        badgeWrap.add(new NumberBadge(number), BorderLayout.NORTH);

        JLabel text = new JLabel("<html><div style='width:340px'><b style='color:#2C2C2A'>" + title
                + "</b><br>" + descHtml + "</div></html>");
        text.setForeground(MUTED);
        text.setFont(font(Font.PLAIN, 13));

        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        row.add(badgeWrap, BorderLayout.WEST);
        row.add(text, BorderLayout.CENTER);
        parent.add(row);
    }

    /**
     * Menambahkan satu baris legenda (kotak warna + keterangan) ke panel.
     */
    static void addLegend(JPanel parent, Color swatch, String text) {
        JPanel box = new JPanel();
        box.setPreferredSize(new Dimension(16, 16));
        box.setBackground(swatch);
        box.setBorder(BorderFactory.createLineBorder(new Color(0x888888)));

        JLabel label = new JLabel(text);
        label.setFont(font(Font.PLAIN, 13));
        label.setForeground(TEXT);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        row.add(box);
        row.add(label);
        parent.add(row);
    }

    /**
     * Menampilkan dialog panduan penggunaan aplikasi.
     *
     * @param owner window induk untuk pemusatan posisi dialog.
     */
    public static void showGuide(Window owner) {
        JDialog dlg = new JDialog(owner, LanguageManager.get("guide.title"),
                Dialog.ModalityType.APPLICATION_MODAL);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(18, 22, 8, 22));

        // Judul dan subjudul.
        JLabel title = new JLabel(LanguageManager.get("guide.title"));
        title.setFont(font(Font.BOLD, 18));
        title.setForeground(ACCENT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel(LanguageManager.get("guide.subtitle"));
        sub.setFont(font(Font.PLAIN, 12));
        sub.setForeground(MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setBorder(BorderFactory.createEmptyBorder(2, 0, 14, 0));

        content.add(title);
        content.add(sub);

        // Empat langkah penggunaan.
        addStep(content, 1, LanguageManager.get("guide.1.title"), LanguageManager.get("guide.1.desc"));
        addStep(content, 2, LanguageManager.get("guide.2.title"), LanguageManager.get("guide.2.desc"));
        addStep(content, 3, LanguageManager.get("guide.3.title"), LanguageManager.get("guide.3.desc"));
        addStep(content, 4, LanguageManager.get("guide.4.title"), LanguageManager.get("guide.4.desc"));

        // Bagian legenda warna.
        content.add(separator());
        JLabel legendTitle = new JLabel(LanguageManager.get("guide.legend.title"));
        legendTitle.setFont(font(Font.BOLD, 14));
        legendTitle.setForeground(ACCENT);
        legendTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        legendTitle.setBorder(BorderFactory.createEmptyBorder(12, 0, 8, 0));
        content.add(legendTitle);

        addLegend(content, new Color(0xADD8E6), LanguageManager.get("guide.legend.1"));
        addLegend(content, new Color(0x90EE90), LanguageManager.get("guide.legend.2"));
        addLegend(content, new Color(0xFFE066), LanguageManager.get("guide.legend.3"));

        dlg.getContentPane().setBackground(Color.WHITE);
        dlg.add(content, BorderLayout.CENTER);
        dlg.add(buttonBar(dlg, LanguageManager.get("guide.btn")), BorderLayout.SOUTH);
        showDialog(dlg, owner);
    }

    // =====================================================================
    //  Dialog: Tentang Aplikasi
    // =====================================================================

    /**
     * Membuat satu baris info (label kiri, nilai kanan) dengan garis pemisah
     * di atasnya. Baris pertama tidak memiliki garis atas.
     */
    static JPanel infoRow(String label, String value, boolean first) {
        JLabel l = new JLabel(label);
        l.setFont(font(Font.PLAIN, 13));
        l.setForeground(MUTED);

        JLabel v = new JLabel(value);
        v.setFont(font(Font.PLAIN, 13));
        v.setForeground(TEXT);

        Border top = BorderFactory.createMatteBorder(first ? 0 : 1, 0, 0, 0, LINE);
        Border pad = BorderFactory.createEmptyBorder(7, 0, 7, 0);

        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(BorderFactory.createCompoundBorder(top, pad));
        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        return row;
    }

    /**
     * Menampilkan dialog "Tentang Aplikasi".
     *
     * @param owner window induk untuk pemusatan posisi dialog.
     */
    public static void showAbout(Window owner) {
        JDialog dlg = new JDialog(owner, LanguageManager.get("about.title"),
                Dialog.ModalityType.APPLICATION_MODAL);

        // ----- Header: ikon, nama aplikasi, tagline, dan chip versi -----
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(ACCENT_BG);
        header.setBorder(BorderFactory.createEmptyBorder(22, 24, 18, 24));

        JLabel icon = new JLabel(new AppIcon());
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel name = new JLabel(LanguageManager.get("app.title"));
        name.setFont(font(Font.BOLD, 20));
        name.setForeground(ACCENT);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel(LanguageManager.get("about.subtitle"));
        tagline.setFont(font(Font.PLAIN, 12));
        tagline.setForeground(MUTED);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        Chip version = new Chip(LanguageManager.get("about.version"), Color.WHITE, ACCENT, ACCENT);
        version.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(icon);
        header.add(Box.createVerticalStrut(10));
        header.add(name);
        header.add(Box.createVerticalStrut(2));
        header.add(tagline);
        header.add(Box.createVerticalStrut(10));
        header.add(version);

        // ----- Isi: deskripsi, chip fitur, dan tabel info -----
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(16, 24, 4, 24));

        JLabel desc = wrapped(LanguageManager.get("about.desc"), 340, MUTED, font(Font.PLAIN, 13));
        desc.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        chips.setOpaque(false);
        chips.setAlignmentX(Component.LEFT_ALIGNMENT);
        chips.add(new Chip(LanguageManager.get("about.feat1"), new Color(0xE6F1FB), new Color(0x0C447C), null));
        chips.add(new Chip(LanguageManager.get("about.feat2"), new Color(0xFCEBEB), new Color(0x791F1F), null));
        chips.add(new Chip(LanguageManager.get("about.feat3"), new Color(0xEAF3DE), new Color(0x27500A), null));
        chips.add(new Chip(LanguageManager.get("about.feat4"), new Color(0xEEEDFE), new Color(0x3C3489), null));
        chips.add(new Chip(LanguageManager.get("about.feat5"), new Color(0xFAEEDA), new Color(0x633806), null));

        JPanel chipsWrap = new JPanel(new BorderLayout());
        chipsWrap.setOpaque(false);
        chipsWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        chipsWrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        chipsWrap.add(chips, BorderLayout.CENTER);

        body.add(desc);
        body.add(chipsWrap);
        body.add(infoRow(LanguageManager.get("about.dev"),  LanguageManager.get("about.dev.val"),  true));
        body.add(infoRow(LanguageManager.get("about.inst"), LanguageManager.get("about.inst.val"), false));
        body.add(infoRow(LanguageManager.get("about.tech"), LanguageManager.get("about.tech.val"), false));
        body.add(infoRow(LanguageManager.get("about.year"), LanguageManager.get("about.year.val"), false));

        dlg.getContentPane().setBackground(Color.WHITE);
        dlg.add(header, BorderLayout.NORTH);
        dlg.add(body, BorderLayout.CENTER);
        dlg.add(buttonBar(dlg, LanguageManager.get("about.btn")), BorderLayout.SOUTH);
        showDialog(dlg, owner);
    }
}