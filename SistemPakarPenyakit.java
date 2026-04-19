import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * ============================================================
 *  SISTEM PAKAR DIAGNOSA PENYAKIT
 *  Politeknik Elektronika Negeri Surabaya - iKnow Research Group
 *
 *  Pendekatan 1 : Berbasis Aturan (Rule-Based)
 *  Pendekatan 2 : Sistem Bobot (Weighted System)
 *
 *  Penyakit yang didiagnosa :
 *    Infeksi     -> Influenza, Demam Berdarah
 *    Non-Infeksi -> Diabetes, Hipertensi
 * ============================================================
 */
public class SistemPakarPenyakit extends JFrame {

    // ══════════════════════════════════════════════════════════
    //  KONSTANTA WARNA & FONT
    // ══════════════════════════════════════════════════════════
    static final Color C_PRIMARY    = new Color(25,  80, 155);
    static final Color C_ACCENT     = new Color(255, 180,  30);
    static final Color C_INFEKSI    = new Color(210,  45,  45);
    static final Color C_NONINFEKSI = new Color(28,  130,  55);
    static final Color C_BG         = new Color(243, 246, 252);
    static final Color C_CARD       = Color.WHITE;
    static final Color C_FLU        = new Color( 55, 120, 230);
    static final Color C_DBD        = new Color(210,  50,  50);
    static final Color C_DM         = new Color( 40, 155,  75);
    static final Color C_HT         = new Color(210, 140,   0);

    static final Font F_TITLE  = new Font("Segoe UI", Font.BOLD,  16);
    static final Font F_HEAD   = new Font("Segoe UI", Font.BOLD,  12);
    static final Font F_BODY   = new Font("Segoe UI", Font.PLAIN, 12);
    static final Font F_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    static final Font F_MONO   = new Font("Monospaced", Font.PLAIN, 11);

    // ══════════════════════════════════════════════════════════
    //  BASIS PENGETAHUAN – GEJALA
    // ══════════════════════════════════════════════════════════
    // Index 0-3 : Influenza
    // Index 4-7 : Demam Berdarah
    // Index 8-11: Diabetes
    // Index 12-16: Hipertensi
    static final String[] GEJALA = {
        /* 0 */ "Demam 37.5–39°C (menggigil ringan, suhu naik bertahap)",
        /* 1 */ "Batuk kering & berdahak ringan (frekuensi >3x/jam)",
        /* 2 */ "Pilek (hidung tersumbat, berair, cairan bening, bersin berulang)",
        /* 3 */ "Sakit tenggorokan (nyeri menelan, faring kemerahan)",
        /* 4 */ "Demam tinggi >39°C mendadak (pola naik-turun 2–7 hari)",
        /* 5 */ "Nyeri sendi/otot (nyeri otot & tulang)",
        /* 6 */ "Ruam kulit (bintik merah, tidak hilang ditekan, uji torniket +)",
        /* 7 */ "Mual/muntah (>2x/hari, nafsu makan turun)",
        /* 8 */ "Sering haus (minum >3 liter/hari)",
        /* 9 */ "Sering buang air kecil (>8x/hari, terutama malam)",
        /*10 */ "Mudah lelah & energi cepat habis (gula darah >200 mg/dL)",
        /*11 */ "Luka sulit sembuh (infeksi berulang, penyembuhan >2 minggu)",
        /*12 */ "Sakit kepala berdenyut (pagi hari, terutama bagian belakang)",
        /*13 */ "Pusing (berputar & terasa ringan saat ganti posisi)",
        /*14 */ "Penglihatan kabur (gangguan visual, penyempitan pembuluh retina)",
        /*15 */ "Tekanan darah ≥140/90 mmHg, detak jantung meningkat",
        /*16 */ "Mimisan (perdarahan dari hidung)"
    };

    static final int[] IDX_FLU = {0, 1, 2, 3};
    static final int[] IDX_DBD = {4, 5, 6, 7};
    static final int[] IDX_DM  = {8, 9, 10, 11};
    static final int[] IDX_HT  = {12, 13, 14, 15, 16};

    // Bobot per gejala (Σ per penyakit = 1.0)
    static final double[] BOBOT = {
        0.30, 0.25, 0.25, 0.20,   // Influenza
        0.30, 0.25, 0.25, 0.20,   // Demam Berdarah
        0.25, 0.25, 0.25, 0.25,   // Diabetes
        0.20, 0.20, 0.20, 0.25, 0.15  // Hipertensi
    };

    static final int    MIN_RULE_HIT = 2;      // (tidak dipakai utk diagnosa, hanya tampilan)
    // Rule-Based: SEMUA gejala penyakit harus cocok untuk diagnosa positif
    static final double THRESHOLD_W  = 0.40;   // min skor untuk weighted

    static final String[] NAMA_PENYAKIT = {"Influenza", "Demam Berdarah", "Diabetes", "Hipertensi"};
    static final String[] KATEGORI      = {"Infeksi",   "Infeksi",        "Non-Infeksi", "Non-Infeksi"};
    static final Color[]  WARNA         = {C_FLU, C_DBD, C_DM, C_HT};
    static final int[][]  IDX_GRUP      = {IDX_FLU, IDX_DBD, IDX_DM, IDX_HT};

    // ══════════════════════════════════════════════════════════
    //  KOMPONEN GUI
    // ══════════════════════════════════════════════════════════
    JCheckBox[]      cbGejala   = new JCheckBox[GEJALA.length];
    JLabel           lbRuleHasil, lbWeightHasil;
    JTextArea        taRule, taWeight;
    DefaultTableModel mdlRiwayat;
    int percobaan = 0;

    // ══════════════════════════════════════════════════════════
    //  KONSTRUKTOR
    // ══════════════════════════════════════════════════════════
    public SistemPakarPenyakit() {
        setTitle("Sistem Pakar Diagnosa Penyakit – iKnow PENS");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 760);
        setMinimumSize(new Dimension(960, 660));
        setLocationRelativeTo(null);
        getContentPane().setBackground(C_BG);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(F_HEAD);
        tabs.addTab("  Diagnosa  ",       buildTabDiagnosa());
        tabs.addTab("  Riwayat Uji  ",    buildTabRiwayat());
        tabs.addTab("  Basis Pengetahuan  ", buildTabKnowledge());
        add(tabs, BorderLayout.CENTER);

        setVisible(true);
    }

    // ══════════════════════════════════════════════════════════
    //  HEADER
    // ══════════════════════════════════════════════════════════
    JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(C_PRIMARY);
        p.setBorder(new EmptyBorder(12, 18, 12, 18));

        JPanel left = new JPanel(new GridLayout(3, 1, 0, 1));
        left.setOpaque(false);
        addLabel(left, "SISTEM PAKAR DIAGNOSA PENYAKIT",
            new Font("Segoe UI", Font.BOLD, 16), Color.WHITE);
        addLabel(left, "Berbasis Aturan (Rule-Based)  &  Sistem Bobot (Weighted System)",
            F_SMALL, new Color(195, 215, 255));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(pill("INFEKSI",     C_INFEKSI));
        right.add(pill("NON-INFEKSI", C_NONINFEKSI));

        p.add(left, BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    void addLabel(JPanel p, String txt, Font f, Color c) {
        JLabel l = new JLabel(txt); l.setFont(f); l.setForeground(c); p.add(l);
    }

    JLabel pill(String t, Color bg) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(Color.WHITE);
        l.setOpaque(true); l.setBackground(bg);
        l.setBorder(new CompoundBorder(
            new LineBorder(bg.darker(), 1, true), new EmptyBorder(3, 9, 3, 9)));
        return l;
    }

    // ══════════════════════════════════════════════════════════
    //  TAB 1 – DIAGNOSA
    // ══════════════════════════════════════════════════════════
    JPanel buildTabDiagnosa() {
        JPanel outer = new JPanel(new BorderLayout(8, 8));
        outer.setBackground(C_BG);
        outer.setBorder(new EmptyBorder(10, 12, 10, 12));

        // ─── Panel kiri: form gejala ─────────────────────────
        JPanel left = new JPanel(new BorderLayout(0, 6));
        left.setBackground(C_BG);

        JLabel hdr = new JLabel("  Pilih Gejala yang Dialami Pasien:");
        hdr.setFont(F_HEAD); hdr.setForeground(C_PRIMARY);
        left.add(hdr, BorderLayout.NORTH);

        JPanel cbWrap = new JPanel();
        cbWrap.setLayout(new BoxLayout(cbWrap, BoxLayout.Y_AXIS));
        cbWrap.setBackground(C_CARD);

        String[] grpNama = {"INFLUENZA", "DEMAM BERDARAH", "DIABETES", "HIPERTENSI"};
        String[] grpKat  = {"Penyakit Infeksi","Penyakit Infeksi","Penyakit Non-Infeksi","Penyakit Non-Infeksi"};
        Color[]  grpClr  = {C_FLU, C_DBD, C_DM, C_HT};

        for (int g = 0; g < 4; g++) {
            // Header grup
            JPanel gh = new JPanel(new BorderLayout());
            gh.setBackground(grpClr[g]);
            gh.setBorder(new EmptyBorder(5, 12, 5, 12));
            JLabel gl = new JLabel(grpNama[g] + "   ·   " + grpKat[g]);
            gl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            gl.setForeground(Color.WHITE);
            gh.add(gl, BorderLayout.WEST);
            cbWrap.add(gh);

            for (int idx : IDX_GRUP[g]) {
                cbGejala[idx] = new JCheckBox("<html>" + GEJALA[idx] + "</html>");
                cbGejala[idx].setFont(F_SMALL);
                cbGejala[idx].setBackground(C_CARD);
                cbGejala[idx].setBorder(new EmptyBorder(3, 20, 3, 6));
                cbWrap.add(cbGejala[idx]);
            }
            if (g < 3) cbWrap.add(separator());
        }

        JScrollPane sc = new JScrollPane(cbWrap);
        sc.setBorder(new LineBorder(new Color(200, 212, 230), 1));
        sc.getVerticalScrollBar().setUnitIncrement(14);
        left.add(sc, BorderLayout.CENTER);

        // Tombol aksi
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        btns.setBackground(C_BG);
        JButton bDiag  = btn("  Diagnosa  ", C_PRIMARY);
        JButton bReset = btn("  Reset  ",    new Color(110, 110, 130));
        bDiag.addActionListener(e -> jalankanDiagnosa());
        bReset.addActionListener(e -> resetForm());
        btns.add(bDiag); btns.add(bReset);
        left.add(btns, BorderLayout.SOUTH);

        // ─── Panel kanan: hasil ──────────────────────────────
        JPanel right = new JPanel(new GridLayout(2, 1, 0, 8));
        right.setBackground(C_BG);
        right.add(buildHasilCard(true));
        right.add(buildHasilCard(false));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setDividerLocation(530);
        split.setDividerSize(5);
        split.setBorder(null);

        outer.add(split, BorderLayout.CENTER);
        return outer;
    }

    JSeparator separator() {
        JSeparator s = new JSeparator();
        s.setForeground(new Color(215, 222, 235));
        return s;
    }

    JButton btn(String t, Color bg) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(7, 18, 7, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    JPanel buildHasilCard(boolean isRule) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(C_CARD);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 210, 230), 1, true),
            new EmptyBorder(10, 12, 10, 12)));

        // Judul card
        JPanel top = new JPanel(new GridLayout(2, 1));
        top.setBackground(C_CARD);
        String jdl = isRule
            ? "Pendekatan 1: Rule-Based  (semua gejala harus cocok)"
            : "Pendekatan 2: Weighted System  (threshold " + (int)(THRESHOLD_W*100) + "%)";
        JLabel lJdl = new JLabel(jdl); lJdl.setFont(F_HEAD); lJdl.setForeground(C_PRIMARY);
        JLabel lSub = new JLabel(isRule
            ? "IF semua gejala cocok (hit == total)  THEN diagnosa penyakit"
            : "Skor = gejala_cocok / total_gejala  ;  diagnosa = skor tertinggi");
        lSub.setFont(F_SMALL); lSub.setForeground(Color.GRAY);
        top.add(lJdl); top.add(lSub);
        card.add(top, BorderLayout.NORTH);

        // Label hasil (besar)
        JLabel lHasil = new JLabel("— Belum Didiagnosa —", SwingConstants.CENTER);
        lHasil.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lHasil.setForeground(Color.GRAY);
        lHasil.setOpaque(true);
        lHasil.setBackground(new Color(243, 244, 248));
        lHasil.setBorder(new EmptyBorder(7, 8, 7, 8));
        card.add(lHasil, BorderLayout.SOUTH);

        // Detail text area
        JTextArea ta = new JTextArea("Pilih gejala lalu klik Diagnosa ...");
        ta.setFont(F_MONO); ta.setEditable(false);
        ta.setLineWrap(true); ta.setWrapStyleWord(true);
        ta.setBackground(new Color(249, 251, 255));
        ta.setBorder(new EmptyBorder(6, 6, 6, 6));
        JScrollPane sc = new JScrollPane(ta);
        sc.setBorder(new LineBorder(new Color(218, 225, 238), 1));
        card.add(sc, BorderLayout.CENTER);

        if (isRule) { lbRuleHasil = lHasil; taRule   = ta; }
        else        { lbWeightHasil = lHasil; taWeight = ta; }

        return card;
    }

    // ══════════════════════════════════════════════════════════
    //  TAB 2 – RIWAYAT
    // ══════════════════════════════════════════════════════════
    JPanel buildTabRiwayat() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(C_BG);
        p.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel hdr = new JLabel("  Riwayat Percobaan Diagnosa (Target: 10 Percobaan)");
        hdr.setFont(F_HEAD); hdr.setForeground(C_PRIMARY);
        p.add(hdr, BorderLayout.NORTH);

        String[] cols = {"No.", "Gejala yang Dipilih", "Rule-Based", "Weighted", "Kategori"};
        mdlRiwayat = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(mdlRiwayat);
        tbl.setFont(F_SMALL); tbl.setRowHeight(25);
        tbl.getTableHeader().setFont(F_HEAD);
        tbl.getTableHeader().setBackground(C_PRIMARY);
        tbl.getTableHeader().setForeground(Color.WHITE);
        tbl.setGridColor(new Color(220, 226, 238));
        tbl.setSelectionBackground(new Color(205, 220, 250));

        int[] cw = {35, 370, 155, 155, 120};
        for (int i = 0; i < cw.length; i++)
            tbl.getColumnModel().getColumn(i).setPreferredWidth(cw[i]);

        tbl.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) comp.setBackground(r % 2 == 0 ? Color.WHITE : new Color(244, 247, 255));
                return comp;
            }
        });

        JScrollPane sc = new JScrollPane(tbl);
        sc.setBorder(new LineBorder(new Color(200, 210, 228), 1));
        p.add(sc, BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bot.setBackground(C_BG);
        JButton bClear = btn("  Hapus Riwayat  ", new Color(190, 50, 50));
        bClear.addActionListener(e -> { mdlRiwayat.setRowCount(0); percobaan = 0; });
        bot.add(bClear);
        p.add(bot, BorderLayout.SOUTH);
        return p;
    }

    // ══════════════════════════════════════════════════════════
    //  TAB 3 – BASIS PENGETAHUAN
    // ══════════════════════════════════════════════════════════
    JPanel buildTabKnowledge() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(C_BG);
        p.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel hdr = new JLabel("  Basis Pengetahuan & Aturan Sistem Pakar");
        hdr.setFont(F_HEAD); hdr.setForeground(C_PRIMARY);
        p.add(hdr, BorderLayout.NORTH);

        JTextArea ta = new JTextArea(buildKnowledgeTxt());
        ta.setFont(F_MONO); ta.setEditable(false);
        ta.setBackground(new Color(248, 250, 255));

        JScrollPane sc = new JScrollPane(ta);
        sc.setBorder(new LineBorder(new Color(200, 210, 228), 1));
        p.add(sc, BorderLayout.CENTER);
        return p;
    }

    String buildKnowledgeTxt() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════════════════╗\n");
        sb.append("║        BASIS PENGETAHUAN – SISTEM PAKAR DIAGNOSA PENYAKIT       ║\n");
        sb.append("║        iKnow Research Group | PENS                              ║\n");
        sb.append("╚══════════════════════════════════════════════════════════════════╝\n\n");

        sb.append("┌─ KLASIFIKASI PENYAKIT ──────────────────────────────────────────┐\n");
        sb.append("│ INFEKSI     : Influenza, Demam Berdarah (disebabkan mikroorg.)  │\n");
        sb.append("│ NON-INFEKSI : Diabetes, Hipertensi (faktor gaya hidup/genetik)  │\n");
        sb.append("└─────────────────────────────────────────────────────────────────┘\n\n");

        sb.append("┌─ PENDEKATAN 1: RULE-BASED ──────────────────────────────────────┐\n");
        sb.append("│ Aturan: SEMUA gejala penyakit harus cocok untuk diagnosa positif │\n");
        sb.append("│                                                                 │\n");
        sb.append("│ R1 [INFLUENZA]       – 4 gejala wajib semua cocok              │\n");
        sb.append("│   IF Demam AND Batuk AND Pilek AND SakitTenggorokan             │\n");
        sb.append("│   → Influenza (Infeksi)                                         │\n");
        sb.append("│                                                                 │\n");
        sb.append("│ R2 [DEMAM BERDARAH]  – 4 gejala wajib semua cocok              │\n");
        sb.append("│   IF DemamTinggi AND NyeriSendi AND RuamKulit AND Mual         │\n");
        sb.append("│   → Demam Berdarah (Infeksi)                                    │\n");
        sb.append("│                                                                 │\n");
        sb.append("│ R3 [DIABETES]        – 4 gejala wajib semua cocok              │\n");
        sb.append("│   IF SeringHaus AND SeringBAK AND MudahLelah AND LukaSulitSembuh│\n");
        sb.append("│   → Diabetes (Non-Infeksi)                                      │\n");
        sb.append("│                                                                 │\n");
        sb.append("│ R4 [HIPERTENSI]      – 5 gejala wajib semua cocok              │\n");
        sb.append("│   IF SakitKepala AND Pusing AND PenglihatanKabur               │\n");
        sb.append("│      AND TekananDarah AND Mimisan                               │\n");
        sb.append("│   → Hipertensi (Non-Infeksi)                                    │\n");
        sb.append("│                                                                 │\n");
        sb.append("│ Jika tidak ada yang semua cocok → Tidak Teridentifikasi         │\n");
        sb.append("│ (tetap tampilkan % kecocokan tertinggi sebagai informasi)       │\n");
        sb.append("└─────────────────────────────────────────────────────────────────┘\n\n");

        sb.append("┌─ PENDEKATAN 2: WEIGHTED SYSTEM ─────────────────────────────────┐\n");
        sb.append("│ Formula  : Skor = gejala_cocok / total_gejala_penyakit          │\n");
        sb.append("│ Diagnosa : penyakit dengan skor tertinggi & >= threshold        │\n");
        sb.append("│ Threshold: ").append((int)(THRESHOLD_W*100)).append("%                                              │\n");
        sb.append("│                                                                 │\n");
        sb.append("│ Contoh perhitungan:                                             │\n");
        sb.append("│   Influenza (4 gejala) : 3 cocok → Skor = 3/4 = 0.75 (75%)    │\n");
        sb.append("│   DBD       (4 gejala) : 1 cocok → Skor = 1/4 = 0.25 (25%)    │\n");
        sb.append("│   Diabetes  (4 gejala) : 0 cocok → Skor = 0/4 = 0.00  (0%)    │\n");
        sb.append("│   Hipertensi(5 gejala) : 2 cocok → Skor = 2/5 = 0.40 (40%)    │\n");
        sb.append("│                                                                 │\n");
        sb.append("│ Daftar Gejala per Penyakit:                                     │\n");

        String[][] grpGejala = {
            {"Demam 37.5-39°C","Batuk kering","Pilek","Sakit tenggorokan"},
            {"Demam tinggi >39°C","Nyeri sendi/otot","Ruam kulit","Mual/muntah"},
            {"Sering haus","Sering BAK","Mudah lelah","Luka sulit sembuh"},
            {"Sakit kepala","Pusing","Penglihatan kabur","Tekanan darah >=140/90","Mimisan"}
        };
        String[] grpTag  = {"FLU","DBD","DM ","HT "};
        String[] grpNama = {"Influenza (4)","Demam Berdarah (4)","Diabetes (4)","Hipertensi (5)"};
        for (int p = 0; p < 4; p++) {
            sb.append(String.format("│   [%s] %-18s bobot tiap gejala = 1/%d = %.2f (%.0f%%) │\n",
                grpTag[p], grpNama[p],
                grpGejala[p].length,
                1.0/grpGejala[p].length,
                100.0/grpGejala[p].length));
        }
        sb.append("└─────────────────────────────────────────────────────────────────┘\n\n");
        return sb.toString();
    }

    // ══════════════════════════════════════════════════════════
    //  LOGIKA DIAGNOSA
    // ══════════════════════════════════════════════════════════
    void jalankanDiagnosa() {
        // Kumpulkan gejala aktif
        boolean[] aktif = new boolean[GEJALA.length];
        List<String> aktifLabel = new ArrayList<>();
        for (int i = 0; i < GEJALA.length; i++) {
            aktif[i] = cbGejala[i].isSelected();
            if (aktif[i]) aktifLabel.add(GEJALA[i].split("\\(")[0].trim());
        }
        if (aktifLabel.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Silakan centang minimal satu gejala terlebih dahulu.",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ── RULE-BASED ────────────────────────────────────────
        int[] hitRule = new int[4];
        for (int p = 0; p < 4; p++)
            for (int idx : IDX_GRUP[p]) if (aktif[idx]) hitRule[p]++;

        int maxHit = -1, idxRule = -1;
        for (int i = 0; i < 4; i++)
            if (hitRule[i] == IDX_GRUP[i].length && hitRule[i] > maxHit) { maxHit = hitRule[i]; idxRule = i; }

        StringBuilder sbR = new StringBuilder();
        sbR.append("=== DIAGNOSA – RULE-BASED ===\n\n");
        sbR.append("Aturan: SEMUA gejala penyakit harus cocok\n");
        sbR.append("Gejala dipilih (").append(aktifLabel.size()).append("):\n");
        for (String g : aktifLabel) sbR.append("  • ").append(g).append("\n");
        sbR.append("\nPencocokan Aturan:\n");
        String[] tagR = {"FLU","DBD","DM ","HT "};
        for (int i = 0; i < 4; i++) {
            int total = IDX_GRUP[i].length;
            String status = (hitRule[i] == total) ? "=> COCOK SEMUA ✓" : "   " + hitRule[i] + "/" + total + " belum lengkap";
            sbR.append(String.format("  [%s] %-16s : %d/%d   %s\n",
                tagR[i], NAMA_PENYAKIT[i], hitRule[i], total, status));
        }
        sbR.append("\nHasil:\n");
        if (idxRule >= 0) {
            sbR.append("  DIAGNOSA : ").append(NAMA_PENYAKIT[idxRule]).append("\n");
            sbR.append("  KATEGORI : ").append(KATEGORI[idxRule]).append("\n");
            sbR.append("  Semua ").append(IDX_GRUP[idxRule].length).append(" gejala cocok 100%");
            setHasilLabel(lbRuleHasil, NAMA_PENYAKIT[idxRule], KATEGORI[idxRule], WARNA[idxRule]);
        } else {
            sbR.append("  Tidak Teridentifikasi\n");
            sbR.append("  (belum ada penyakit dengan semua gejala cocok)\n\n");
            sbR.append("  Kecocokan tertinggi:\n");
            int bestPartial = -1, bestIdx = -1;
            for (int i = 0; i < 4; i++) if (hitRule[i] > bestPartial) { bestPartial = hitRule[i]; bestIdx = i; }
            if (bestPartial > 0)
                sbR.append(String.format("  → %s : %d/%d gejala cocok (%.0f%%)",
                    NAMA_PENYAKIT[bestIdx], bestPartial, IDX_GRUP[bestIdx].length,
                    (double)bestPartial/IDX_GRUP[bestIdx].length*100));
            else
                sbR.append("  → Tidak ada gejala yang dipilih cocok.");
            setHasilLabelGray(lbRuleHasil);
        }
        taRule.setText(sbR.toString());
        taRule.setCaretPosition(0);

        // ── WEIGHTED SYSTEM ───────────────────────────────────
        // Formula baru: Skor = jumlah gejala dipilih / total gejala penyakit
        int[] hitW = new int[4];
        for (int p = 0; p < 4; p++)
            for (int idx : IDX_GRUP[p]) if (aktif[idx]) hitW[p]++;

        double[] skor = new double[4];
        for (int p = 0; p < 4; p++)
            skor[p] = (double) hitW[p] / IDX_GRUP[p].length;

        double maxSkor = -1; int idxW = -1;
        for (int i = 0; i < 4; i++)
            if (skor[i] > maxSkor) { maxSkor = skor[i]; idxW = i; }

        StringBuilder sbW = new StringBuilder();
        sbW.append("=== DIAGNOSA – WEIGHTED SYSTEM ===\n\n");
        sbW.append("Formula : Skor = gejala_cocok / total_gejala_penyakit\n");
        sbW.append("Gejala dipilih (").append(aktifLabel.size()).append("):\n");
        for (String g : aktifLabel) sbW.append("  • ").append(g).append("\n");
        sbW.append("\nPerhitungan Skor:\n");
        for (int i = 0; i < 4; i++) {
            sbW.append(String.format("  [%s] %-16s : %d/%d = %.2f (%.0f%%)  %s\n",
                tagR[i], NAMA_PENYAKIT[i], hitW[i], IDX_GRUP[i].length, skor[i], skor[i]*100,
                skor[i] >= THRESHOLD_W ? "=> LOLOS threshold" : "   di bawah threshold"));
        }
        sbW.append(String.format("\nThreshold minimum : %.0f%%\n", THRESHOLD_W*100));
        sbW.append("\nHasil:\n");
        if (maxSkor >= THRESHOLD_W) {
            sbW.append("  DIAGNOSA : ").append(NAMA_PENYAKIT[idxW]).append("\n");
            sbW.append("  KATEGORI : ").append(KATEGORI[idxW]).append("\n");
            sbW.append(String.format("  Skor     : %.2f (%.0f%%)", maxSkor, maxSkor*100));
            setHasilLabel(lbWeightHasil, NAMA_PENYAKIT[idxW], KATEGORI[idxW], WARNA[idxW]);
        } else {
            sbW.append(String.format("  Tidak teridentifikasi (skor tertinggi %.0f%% < threshold %.0f%%).",
                maxSkor*100, THRESHOLD_W*100));
            setHasilLabelGray(lbWeightHasil);
        }
        taWeight.setText(sbW.toString());
        taWeight.setCaretPosition(0);

        // ── SIMPAN RIWAYAT ────────────────────────────────────
        percobaan++;
        String gStr = String.join(", ", aktifLabel);
        if (gStr.length() > 65) gStr = gStr.substring(0, 62) + "...";

        // Rule-Based: tampilkan hasil + hit count
        String rRule;
        if (idxRule >= 0)
            rRule = NAMA_PENYAKIT[idxRule] + " (" + hitW[idxRule] + "/" + IDX_GRUP[idxRule].length + ")";
        else {
            // cari hit tertinggi untuk info
            int bestHit = -1, bestIdx = -1;
            for (int i = 0; i < 4; i++) if (hitW[i] > bestHit) { bestHit = hitW[i]; bestIdx = i; }
            rRule = bestHit > 0
                ? "Tdk Terdent. (" + bestHit + "/" + IDX_GRUP[bestIdx].length + ")"
                : "Tdk Terdent.";
        }

        // Weighted: tampilkan hasil + persentase skor
        String rW = maxSkor >= THRESHOLD_W
            ? String.format("%s (%.0f%%)", NAMA_PENYAKIT[idxW], maxSkor * 100)
            : String.format("Tdk Terdent. (%.0f%%)", maxSkor * 100);

        String kat = idxRule >= 0 ? KATEGORI[idxRule] : (maxSkor >= THRESHOLD_W ? KATEGORI[idxW] : "-");
        mdlRiwayat.addRow(new Object[]{percobaan, gStr, rRule, rW, kat});

        if (percobaan == 10) {
            JOptionPane.showMessageDialog(this,
                "10 percobaan telah selesai!\nLihat tab 'Riwayat Uji' untuk rekap.",
                "Target Tercapai", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    void setHasilLabel(JLabel l, String penyakit, String kat, Color c) {
        l.setText(penyakit + "  [" + kat + "]");
        l.setForeground(c.darker());
        l.setBackground(new Color(
            Math.min(c.getRed()   + 175, 255),
            Math.min(c.getGreen() + 175, 255),
            Math.min(c.getBlue()  + 175, 255)));
    }

    void setHasilLabelGray(JLabel l) {
        l.setText("Tidak Teridentifikasi");
        l.setForeground(new Color(130, 130, 130));
        l.setBackground(new Color(240, 240, 245));
    }

    void resetForm() {
        for (JCheckBox cb : cbGejala) cb.setSelected(false);
        setHasilLabelGray(lbRuleHasil);
        setHasilLabelGray(lbWeightHasil);
        lbRuleHasil.setText("— Belum Didiagnosa —");
        lbWeightHasil.setText("— Belum Didiagnosa —");
        taRule.setText("Pilih gejala lalu klik Diagnosa ...");
        taWeight.setText("Pilih gejala lalu klik Diagnosa ...");
    }

    // ══════════════════════════════════════════════════════════
    //  MAIN
    // ══════════════════════════════════════════════════════════
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(SistemPakarPenyakit::new);
    }
}
