# RAM Monitor (prototipe Android)

Aplikasi Android native sederhana untuk menampilkan total RAM, RAM terpakai, RAM tersedia, indikator visual, serta daftar aplikasi yang terlihat oleh PackageManager dan klasifikasi sistem/pengguna.

## Batasan Android
- Android 10+ membatasi `ActivityManager.getProcessMemoryInfo()` untuk aplikasi biasa: informasi proses milik aplikasi lain tidak tersedia/umumnya nol.
- Karena itu, aplikasi ini sengaja tidak mengarang angka RAM per aplikasi. Penyortiran RAM belum dapat bermakna sampai tersedia API resmi/akses khusus yang sesuai.
- `QUERY_ALL_PACKAGES` digunakan untuk fitur inventaris aplikasi dalam prototipe sideload. Kebijakan Google Play membatasi izin ini; publikasi Play memerlukan kelayakan dan deklarasi yang disetujui. Jangan menganggap proyek ini otomatis lolos peninjauan Play.
- Aplikasi tidak memiliki izin internet, tidak mengirim data ke server, tidak menggunakan Accessibility Service, dan tidak membutuhkan root.

## Cara membuka dan membangun
1. Instal Android Studio dari situs resmi Android Developers.
2. Ekstrak ZIP, lalu buka folder `RAMMonitorAndroid` di Android Studio.
3. Izinkan Gradle melakukan sinkronisasi dan pasang Android SDK 35 jika diminta.
4. Pilih **Build > Build APK(s)**.
5. APK debug biasanya muncul di `app/build/outputs/apk/debug/app-debug.apk`.

Proyek ini adalah source project, bukan APK yang sudah dikompilasi atau diuji pada perangkat fisik. Gunakan Android Studio versi terbaru yang kompatibel dengan Android Gradle Plugin 8.7.3.
