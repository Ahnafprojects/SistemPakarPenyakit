@echo off
REM ============================================================
REM Jalankan Sistem Pakar Penyakit
REM Politeknik Elektronika Negeri Surabaya - iKnow
REM ============================================================
cd /d "%~dp0"
echo ============================================
echo   SISTEM PAKAR DIAGNOSA PENYAKIT - iKnow
echo   Politeknik Elektronika Negeri Surabaya
echo ============================================

IF NOT EXIST SistemPakarPenyakit.class (
    echo Compiling...
    javac SistemPakarPenyakit.java
    IF ERRORLEVEL 1 (
        echo ERROR: Compile gagal. Pastikan Java JDK sudah terinstall.
        pause
        exit /b 1
    )
    echo Compile sukses!
)

echo Menjalankan program...
java SistemPakarPenyakit
pause
