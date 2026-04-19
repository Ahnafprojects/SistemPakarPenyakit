#!/bin/bash
# ============================================================
# Jalankan Sistem Pakar Penyakit
# Politeknik Elektronika Negeri Surabaya - iKnow
# ============================================================
cd "$(dirname "$0")"
echo "============================================"
echo "  SISTEM PAKAR DIAGNOSA PENYAKIT - iKnow"
echo "  Politeknik Elektronika Negeri Surabaya"
echo "============================================"

# Compile jika .class belum ada atau .java lebih baru
if [ ! -f "SistemPakarPenyakit.class" ] || [ "SistemPakarPenyakit.java" -nt "SistemPakarPenyakit.class" ]; then
    echo "Compiling..."
    javac SistemPakarPenyakit.java
    if [ $? -ne 0 ]; then
        echo "ERROR: Compile gagal. Pastikan Java JDK sudah terinstall."
        exit 1
    fi
    echo "Compile sukses!"
fi

echo "Menjalankan program..."
java SistemPakarPenyakit
