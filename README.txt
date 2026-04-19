============================================================
  SISTEM PAKAR DIAGNOSA PENYAKIT
  iKnow Research Group | Politeknik Elektronika Negeri Surabaya
  Mata Kuliah: Expert System
  Dosen: Aliridho Barakbah, Entin Martiana
============================================================

DESKRIPSI PROGRAM
─────────────────
Program ini adalah implementasi Sistem Pakar berbasis GUI (Java Swing)
untuk mendiagnosa penyakit berdasarkan gejala yang dipilih.

Penyakit yang dapat didiagnosa:
  INFEKSI     → Influenza, Demam Berdarah
  NON-INFEKSI → Diabetes, Hipertensi

PENDEKATAN YANG DIGUNAKAN
──────────────────────────
1. Rule-Based (Berbasis Aturan)
   - Setiap penyakit memiliki daftar gejala terkait
   - IF jumlah_gejala_cocok >= 2 THEN diagnosa penyakit
   - Pemenang = penyakit dengan gejala cocok terbanyak

2. Weighted System (Sistem Bobot)
   - Setiap gejala memiliki bobot kontribusi ke penyakit
   - Skor = Σ bobot_gejala_yang_aktif
   - Diagnosa = penyakit dengan skor tertinggi & >= 40%

GEJALA & BOBOT
──────────────
INFLUENZA (Infeksi):
  • Demam 37.5-39°C, menggigil ringan     bobot 0.30
  • Batuk kering, berdahak, >3x/jam       bobot 0.25
  • Pilek, hidung tersumbat/berair         bobot 0.25
  • Sakit tenggorokan, faring merah        bobot 0.20

DEMAM BERDARAH (Infeksi):
  • Demam tinggi >39°C, naik-turun 2-7hr  bobot 0.30
  • Nyeri sendi/otot & tulang              bobot 0.25
  • Ruam kulit, torniket positif           bobot 0.25
  • Mual/muntah >2x/hari, nafsu turun     bobot 0.20

DIABETES (Non-Infeksi):
  • Sering haus, minum >3 liter/hari      bobot 0.25
  • Sering BAK >8x/hari, terutama malam   bobot 0.25
  • Mudah lelah, gula darah >200 mg/dL    bobot 0.25
  • Luka sulit sembuh, infeksi berulang   bobot 0.25

HIPERTENSI (Non-Infeksi):
  • Sakit kepala berdenyut, bgn belakang  bobot 0.20
  • Pusing berputar saat ganti posisi     bobot 0.20
  • Penglihatan kabur, penyempitan retina bobot 0.20
  • Tekanan darah ≥140/90 mmHg            bobot 0.25
  • Mimisan (perdarahan dari hidung)      bobot 0.15

FITUR PROGRAM
─────────────
  [Tab 1] Diagnosa     : Form input gejala + hasil kedua pendekatan
  [Tab 2] Riwayat Uji  : Tabel rekap 10 percobaan diagnosa
  [Tab 3] Basis Pengetahuan : Tampilan lengkap aturan & bobot

CARA MENJALANKAN
────────────────
Pastikan Java JDK 8+ sudah terinstall.

Windows:
  Double-click jalankan.bat
  ATAU buka CMD → javac SistemPakarPenyakit.java → java SistemPakarPenyakit

Linux/Mac:
  chmod +x jalankan.sh
  ./jalankan.sh
  ATAU: javac SistemPakarPenyakit.java && java SistemPakarPenyakit

STRUKTUR FILE
─────────────
  SistemPakarPenyakit.java  ← Source code utama (1 file)
  SistemPakarPenyakit.class ← Hasil compile (otomatis dibuat)
  jalankan.bat              ← Runner Windows
  jalankan.sh               ← Runner Linux/Mac
  README.txt                ← File ini
