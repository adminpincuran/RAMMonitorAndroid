# Cara mendapatkan APK RAM Monitor

## Opsi A — GitHub Actions (tanpa Android Studio di komputer sendiri)

1. Ekstrak ZIP ini. Isi folder proyek `RAMMonitorAndroid-APK-Build` adalah isi yang perlu dimasukkan ke root repositori GitHub, termasuk folder tersembunyi `.github/workflows`.
2. Di GitHub, buat repositori baru, lalu unggah/commit seluruh isi folder proyek ke root repositori. Pastikan `.github/workflows/build-apk.yml` ikut terunggah.
3. Setelah commit ke branch `main`, buka tab **Actions** pada repositori. Workflow **Build RAM Monitor APK** akan berjalan otomatis. Jika perlu, pilih workflow tersebut lalu tekan **Run workflow**.
4. Setelah status workflow **success**, buka hasil run dan unduh artifact **RAMMonitor-debug-APK**. Di dalam ZIP artifact ada `app-debug.apk`.
5. Pindahkan `app-debug.apk` ke ponsel Android, buka file tersebut, lalu izinkan pemasangan dari sumber itu jika Android memintanya. Setelah instalasi, jalankan **RAM Monitor**.

## Opsi B — Android Studio (komputer Windows/macOS/Linux)

1. Buka folder proyek ini di Android Studio.
2. Tunggu Gradle Sync selesai dan pasang Android SDK 35 bila diminta.
3. Pilih **Build > Build APK(s)**.
4. APK debug akan berada di `app/build/outputs/apk/debug/app-debug.apk`.

## Catatan pengujian dan keamanan

- Ini adalah **APK debug** untuk uji coba, bukan rilis produksi yang ditandatangani dengan kunci rilis.
- Source aplikasi dipertahankan; workflow hanya menambahkan otomatisasi kompilasi.
- APK dibuat dari source proyek ini, tetapi tetap uji sendiri di perangkat. Pemasangan manual mungkin meminta izin *Install unknown apps*.
- Aplikasi ini menampilkan statistik RAM perangkat dan daftar aplikasi yang terlihat oleh PackageManager. Android modern umumnya tidak memberikan RAM aktual setiap aplikasi lain kepada aplikasi biasa, sehingga nilai RAM individual tetap tidak tersedia.
- Proyek meminta `QUERY_ALL_PACKAGES` untuk menampilkan inventaris aplikasi; kelayakan distribusi melalui Google Play tunduk pada kebijakan dan deklarasi Google Play.
