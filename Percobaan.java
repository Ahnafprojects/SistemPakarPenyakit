/**
 * ================================================================
 *  SISTEM PAKAR DIAGNOSA PENYAKIT
 *  Politeknik Elektronika Negeri Surabaya – iKnow Research Group
 *
 *  Metode 1 : Berbasis Aturan (Rule-Based)
 *  Metode 2 : Sistem Bobot    (Weighted System)
 *
 *  File ini berisi:
 *    1. Engine murni kedua metode (tanpa GUI)
 *    2. 10 skenario percobaan untuk masing-masing metode
 * ================================================================
 */
public class Percobaan {

    // ==============================================================
    //  BASIS PENGETAHUAN – GEJALA (index 0-16)
    //  0-3  : Influenza
    //  4-7  : Demam Berdarah
    //  8-11 : Diabetes
    //  12-16: Hipertensi
    // ==============================================================
    static final String[] GEJALA = {
        /* 0 */ "Demam 37.5-39°C (menggigil ringan)",
        /* 1 */ "Batuk kering & berdahak ringan",
        /* 2 */ "Pilek (hidung tersumbat/berair)",
        /* 3 */ "Sakit tenggorokan (nyeri menelan)",
        /* 4 */ "Demam tinggi >39°C mendadak",
        /* 5 */ "Nyeri sendi/otot",
        /* 6 */ "Ruam kulit (bintik merah)",
        /* 7 */ "Mual/muntah (>2x/hari)",
        /* 8 */ "Sering haus (>3 liter/hari)",
        /* 9 */ "Sering buang air kecil (>8x/hari)",
        /*10 */ "Mudah lelah & energi cepat habis",
        /*11 */ "Luka sulit sembuh (>2 minggu)",
        /*12 */ "Sakit kepala berdenyut",
        /*13 */ "Pusing (berputar saat ganti posisi)",
        /*14 */ "Penglihatan kabur",
        /*15 */ "Tekanan darah >=140/90 mmHg",
        /*16 */ "Mimisan"
    };

    // Index gejala per penyakit
    static final int[] IDX_FLU = {0, 1, 2, 3};
    static final int[] IDX_DBD = {4, 5, 6, 7};
    static final int[] IDX_DM  = {8, 9, 10, 11};
    static final int[] IDX_HT  = {12, 13, 14, 15, 16};
    static final int[][] IDX_GRUP = {IDX_FLU, IDX_DBD, IDX_DM, IDX_HT};

    // Bobot per gejala (jumlah bobot per penyakit = 1.0)
    static final double[] BOBOT = {
        0.30, 0.25, 0.25, 0.20,       // Influenza        (total 1.00)
        0.30, 0.25, 0.25, 0.20,       // Demam Berdarah   (total 1.00)
        0.25, 0.25, 0.25, 0.25,       // Diabetes         (total 1.00)
        0.20, 0.20, 0.20, 0.25, 0.15  // Hipertensi       (total 1.00)
    };

    static final String[] NAMA_PENYAKIT = {
        "Influenza", "Demam Berdarah", "Diabetes", "Hipertensi"
    };
    static final String[] KATEGORI = {
        "Infeksi", "Infeksi", "Non-Infeksi", "Non-Infeksi"
    };

    // Parameter metode
    // Rule-Based: SEMUA gejala penyakit harus cocok untuk diagnosa positif
    static final double THRESHOLD_W  = 0.40; // min skor (Weighted)

    // ==============================================================
    //  METODE 1 – RULE-BASED
    //  Algoritma:
    //    1. Hitung jumlah gejala yang cocok untuk tiap penyakit
    //    2. Penyakit yang SEMUA gejalanya cocok (hit == total) = kandidat
    //    3. Jika tidak ada yang semua cocok → Tidak Teridentifikasi
    //       (tetap tampilkan kecocokan tertinggi sebagai info)
    // ==============================================================
    static String diagnosaRuleBased(boolean[] aktif) {
        int[] hit = new int[4];
        for (int p = 0; p < 4; p++)
            for (int idx : IDX_GRUP[p])
                if (aktif[idx]) hit[p]++;

        int maxHit = -1, idxHasil = -1;
        for (int i = 0; i < 4; i++)
            if (hit[i] == IDX_GRUP[i].length && hit[i] > maxHit) {
                maxHit   = hit[i];
                idxHasil = i;
            }
        return idxHasil >= 0 ? NAMA_PENYAKIT[idxHasil] : "Tidak Teridentifikasi";
    }

    // Versi detail – mengembalikan array hit per penyakit
    static int[] hitRuleBased(boolean[] aktif) {
        int[] hit = new int[4];
        for (int p = 0; p < 4; p++)
            for (int idx : IDX_GRUP[p])
                if (aktif[idx]) hit[p]++;
        return hit;
    }

    // ==============================================================
    //  METODE 2 – WEIGHTED SYSTEM
    //  Algoritma:
    //    1. Hitung skor tiap penyakit = gejala_cocok / total_gejala
    //    2. Penyakit dengan skor tertinggi = kandidat
    //    3. Jika skor kandidat >= THRESHOLD_W (40%) → diagnosa
    // ==============================================================
    static String diagnosaWeighted(boolean[] aktif) {
        double[] skor = skorWeighted(aktif);
        double maxSkor = -1; int idxHasil = -1;
        for (int i = 0; i < 4; i++)
            if (skor[i] > maxSkor) { maxSkor = skor[i]; idxHasil = i; }

        return (maxSkor >= THRESHOLD_W)
            ? NAMA_PENYAKIT[idxHasil]
            : "Tidak Teridentifikasi";
    }

    // Versi detail – mengembalikan array skor per penyakit
    // Skor = jumlah gejala dipilih / total gejala penyakit
    static double[] skorWeighted(boolean[] aktif) {
        double[] skor = new double[4];
        for (int p = 0; p < 4; p++) {
            int hit = 0;
            for (int idx : IDX_GRUP[p]) if (aktif[idx]) hit++;
            skor[p] = (double) hit / IDX_GRUP[p].length;
        }
        return skor;
    }

    // ==============================================================
    //  UTILITAS CETAK
    // ==============================================================
    static void cetakHeader(int no, String skenario, boolean[] aktif) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.printf ("║  PERCOBAAN %-2d : %-40s║%n", no, skenario);
        System.out.println("╚══════════════════════════════════════════════════════════╝");

        System.out.println("Gejala yang Aktif:");
        int n = 0;
        for (int i = 0; i < aktif.length; i++)
            if (aktif[i]) { System.out.printf("  [%2d] %s%n", i, GEJALA[i]); n++; }
        if (n == 0) System.out.println("  (tidak ada gejala dipilih)");
        System.out.println();
    }

    static void cetakRuleBased(boolean[] aktif) {
        int[] hit    = hitRuleBased(aktif);
        String hasil = diagnosaRuleBased(aktif);

        System.out.println("┌─ METODE 1: RULE-BASED ───────────────────────────────────┐");
        System.out.println("│ IF SEMUA gejala penyakit cocok  THEN diagnosa penyakit   │");
        System.out.println("├──────────────────────────────────────────────────────────┤");
        String[] tag = {"FLU","DBD","DM ","HT "};
        for (int i = 0; i < 4; i++) {
            int total = IDX_GRUP[i].length;
            String status = (hit[i] == total) ? "COCOK SEMUA ✓" : "belum lengkap ✗";
            System.out.printf("│  [%s] %-15s : %d/%d   %s│%n",
                tag[i], NAMA_PENYAKIT[i], hit[i], total, status);
        }
        System.out.println("├──────────────────────────────────────────────────────────┤");
        if (hasil.equals("Tidak Teridentifikasi")) {
            // tampilkan kecocokan tertinggi sebagai info
            int best = -1, bestIdx = -1;
            for (int i = 0; i < 4; i++) if (hit[i] > best) { best = hit[i]; bestIdx = i; }
            System.out.printf("│  HASIL  : %-46s│%n", hasil);
            if (best > 0)
                System.out.printf("│  Info   : kecocokan tertinggi %s (%d/%d = %.0f%%)  %s│%n",
                    NAMA_PENYAKIT[bestIdx], best, IDX_GRUP[bestIdx].length,
                    (double)best/IDX_GRUP[bestIdx].length*100, " ".repeat(
                        Math.max(0, 5 - NAMA_PENYAKIT[bestIdx].length())));
        } else {
            System.out.printf("│  HASIL  : %-46s│%n", hasil);
        }
        System.out.println("└──────────────────────────────────────────────────────────┘");
        System.out.println();
    }

    static void cetakWeighted(boolean[] aktif) {
        double[] skor  = skorWeighted(aktif);
        String   hasil = diagnosaWeighted(aktif);
        int[]    hit   = hitRuleBased(aktif);

        System.out.println("┌─ METODE 2: WEIGHTED SYSTEM ──────────────────────────────┐");
        System.out.println("│ Skor = gejala_cocok / total_gejala ;  threshold = " + (int)(THRESHOLD_W*100) + "%     │");
        System.out.println("├──────────────────────────────────────────────────────────┤");
        String[] tag = {"FLU","DBD","DM ","HT "};
        for (int i = 0; i < 4; i++) {
            String status = skor[i] >= THRESHOLD_W ? "✓ LOLOS threshold   " : "✗ bawah threshold   ";
            System.out.printf("│  [%s] %-15s : %d/%d = %.2f (%.0f%%)   %s│%n",
                tag[i], NAMA_PENYAKIT[i], hit[i], IDX_GRUP[i].length, skor[i], skor[i]*100, status);
        }
        System.out.println("├──────────────────────────────────────────────────────────┤");
        System.out.printf ("│  HASIL  : %-46s│%n", hasil);
        System.out.println("└──────────────────────────────────────────────────────────┘");
        System.out.println();
    }

    static void jalankanPercobaan(int no, String skenario, boolean[] aktif) {
        cetakHeader(no, skenario, aktif);
        cetakRuleBased(aktif);
        cetakWeighted(aktif);
        System.out.println("=".repeat(60));
        System.out.println();
    }

    // ==============================================================
    //  HELPER – buat array boolean dari index yang aktif
    // ==============================================================
    static boolean[] gejalaAktif(int... indices) {
        boolean[] a = new boolean[GEJALA.length];
        for (int i : indices) a[i] = true;
        return a;
    }

    // ==============================================================
    //  MAIN – 10 SKENARIO PERCOBAAN
    // ==============================================================
    public static void main(String[] args) {

        System.out.println();
        System.out.println("  SISTEM PAKAR DIAGNOSA PENYAKIT – iKnow PENS");
        System.out.println("  Uji Coba Metode Rule-Based & Weighted System");
        System.out.println("  10 Skenario Percobaan");
        System.out.println("=".repeat(60));
        System.out.println();

        // ----------------------------------------------------------
        //  PERCOBAAN 1
        //  Skenario : Semua gejala Influenza aktif
        //  Ekspektasi: Influenza (kedua metode)
        // ----------------------------------------------------------
        jalankanPercobaan(1,
            "Semua gejala Influenza",
            gejalaAktif(0, 1, 2, 3));

        // ----------------------------------------------------------
        //  PERCOBAAN 2
        //  Skenario : Hanya 2 gejala Influenza (Demam + Batuk)
        //  Ekspektasi: Influenza (kedua metode)
        // ----------------------------------------------------------
        jalankanPercobaan(2,
            "2 gejala Influenza (Demam + Batuk)",
            gejalaAktif(0, 1));

        // ----------------------------------------------------------
        //  PERCOBAAN 3
        //  Skenario : Semua gejala Demam Berdarah aktif
        //  Ekspektasi: Demam Berdarah (kedua metode)
        // ----------------------------------------------------------
        jalankanPercobaan(3,
            "Semua gejala Demam Berdarah",
            gejalaAktif(4, 5, 6, 7));

        // ----------------------------------------------------------
        //  PERCOBAAN 4
        //  Skenario : 2 gejala DBD (Demam Tinggi + Nyeri Sendi)
        //  Ekspektasi: Demam Berdarah (kedua metode)
        // ----------------------------------------------------------
        jalankanPercobaan(4,
            "2 gejala DBD (Demam Tinggi + Nyeri Sendi)",
            gejalaAktif(4, 5));

        // ----------------------------------------------------------
        //  PERCOBAAN 5
        //  Skenario : Semua gejala Diabetes aktif
        //  Ekspektasi: Diabetes (kedua metode)
        // ----------------------------------------------------------
        jalankanPercobaan(5,
            "Semua gejala Diabetes",
            gejalaAktif(8, 9, 10, 11));

        // ----------------------------------------------------------
        //  PERCOBAAN 6
        //  Skenario : 2 gejala Diabetes (Sering Haus + Sering BAK)
        //  Ekspektasi: Diabetes (kedua metode)
        // ----------------------------------------------------------
        jalankanPercobaan(6,
            "2 gejala Diabetes (Haus + Sering BAK)",
            gejalaAktif(8, 9));

        // ----------------------------------------------------------
        //  PERCOBAAN 7
        //  Skenario : Semua gejala Hipertensi aktif
        //  Ekspektasi: Hipertensi (kedua metode)
        // ----------------------------------------------------------
        jalankanPercobaan(7,
            "Semua gejala Hipertensi",
            gejalaAktif(12, 13, 14, 15, 16));

        // ----------------------------------------------------------
        //  PERCOBAAN 8
        //  Skenario : 2 gejala Hipertensi (Sakit Kepala + Tekanan Darah)
        //  Ekspektasi: Hipertensi (kedua metode)
        // ----------------------------------------------------------
        jalankanPercobaan(8,
            "2 gejala HT (Sakit Kepala + Tek. Darah)",
            gejalaAktif(12, 15));

        // ----------------------------------------------------------
        //  PERCOBAAN 9
        //  Skenario : Gejala campuran Influenza (3) + DBD (2)
        //  Ekspektasi: Rule → Influenza (3 hit > 2 hit)
        //              Weighted → Influenza (skor lebih tinggi)
        // ----------------------------------------------------------
        jalankanPercobaan(9,
            "Gejala Campuran FLU(3) + DBD(2)",
            gejalaAktif(0, 1, 2, 4, 5));

        // ----------------------------------------------------------
        //  PERCOBAAN 10
        //  Skenario : Hanya 1 gejala (tidak cukup untuk diagnosa)
        //  Ekspektasi: Tidak Teridentifikasi (Rule-Based)
        //              Bergantung threshold (Weighted)
        // ----------------------------------------------------------
        jalankanPercobaan(10,
            "Hanya 1 gejala (Demam Tinggi DBD)",
            gejalaAktif(4));

        // ----------------------------------------------------------
        //  TABEL RINGKASAN
        // ----------------------------------------------------------
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    TABEL RINGKASAN 10 PERCOBAAN                          ║");
        System.out.println("╠═══╦══════════════════════════════════════╦══════════════════╦════════════╣");
        System.out.println("║No.║ Skenario Gejala                      ║ Rule-Based       ║ Weighted   ║");
        System.out.println("╠═══╬══════════════════════════════════════╬══════════════════╬════════════╣");

        Object[][] tb = {
            {1,  "Semua gejala Influenza (0,1,2,3)",         gejalaAktif(0,1,2,3)},
            {2,  "2 gejala Influenza (0,1)",                 gejalaAktif(0,1)},
            {3,  "Semua gejala DBD (4,5,6,7)",               gejalaAktif(4,5,6,7)},
            {4,  "2 gejala DBD (4,5)",                       gejalaAktif(4,5)},
            {5,  "Semua gejala Diabetes (8,9,10,11)",        gejalaAktif(8,9,10,11)},
            {6,  "2 gejala Diabetes (8,9)",                  gejalaAktif(8,9)},
            {7,  "Semua gejala Hipertensi (12-16)",          gejalaAktif(12,13,14,15,16)},
            {8,  "2 gejala HT (12,15)",                      gejalaAktif(12,15)},
            {9,  "Campuran FLU(0,1,2) + DBD(4,5)",          gejalaAktif(0,1,2,4,5)},
            {10, "1 gejala saja (4)",                        gejalaAktif(4)},
        };
        for (Object[] row : tb) {
            int    no      = (int)    row[0];
            String sken    = (String) row[1];
            boolean[] a    = (boolean[]) row[2];
            String rule    = diagnosaRuleBased(a);
            String weight  = diagnosaWeighted(a);
            // singkat
            String r = rule.equals("Tidak Teridentifikasi") ? "Tdk Teridentifikasi" : rule;
            String w = weight.equals("Tidak Teridentifikasi") ? "Tdk Teridentifikasi" : weight;
            System.out.printf("║%-3d║ %-36s ║ %-16s ║ %-10s ║%n", no, sken, r, w);
        }
        System.out.println("╚═══╩══════════════════════════════════════╩══════════════════╩════════════╝");
        System.out.println();
        System.out.println("Keterangan Bobot Gejala:");
        System.out.println("  Influenza       : Demam(0.30) Batuk(0.25) Pilek(0.25) SakitTenggorokan(0.20)");
        System.out.println("  Demam Berdarah  : DemamTinggi(0.30) NyeriSendi(0.25) RuamKulit(0.25) Mual(0.20)");
        System.out.println("  Diabetes        : SeringHaus(0.25) SeringBAK(0.25) MudahLelah(0.25) LukaSulitSembuh(0.25)");
        System.out.println("  Hipertensi      : SakitKepala(0.20) Pusing(0.20) PenglihatanKabur(0.20)");
        System.out.println("                    TekananDarah(0.25) Mimisan(0.15)");
        System.out.println();
        System.out.println("Threshold Weighted System  : " + (int)(THRESHOLD_W*100) + "%");
        System.out.println("Rule-Based       : SEMUA gejala penyakit harus cocok (hit == total)");
    }
}
