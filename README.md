# B-Tree Visualization Simulator (Numeric & String Keys)

Aplikasi visualisasi dinamis untuk struktur data B-Tree. Aplikasi ini mendukung dua mode tipe data (Numerik dan String) dengan animasi *step-by-step*, fitur *undo/redo*, dan antarmuka multibahasa (ID/EN).

## Fitur Utama
*   **Multi-Mode:** Normalisasi *zero-padding* untuk kunci numerik dan pembandingan leksikografis murni untuk string.
*   **Interaktif:** Kontrol animasi (Pause, Step, Slider Speed).
*   **Arsitektur:** Menerapkan *Model-View-Controller* (MVC) dan *Design Pattern* (Observer & Memento).
*   **Pengujian:** Dilengkapi dengan Unit Test (JUnit 4) dan pengujian diferensial (*Oracle TreeSet*).

## Prasyarat (Requirements)
*   Java Development Kit (JDK) 17 atau lebih baru.
*   IDE: Apache NetBeans (atau IDE Java lainnya).
*   Library: JUnit 4.13.2 & Hamcrest (terdapat di folder `lib`).

## Cara Menjalankan Aplikasi
1. *Clone* repositori ini: `git clone https://github.com/username/btree-visualization-java.git`
2. Buka proyek di NetBeans (File -> Open Project).
3. Jalankan kelas `BTreeVisualization.java` (Run File).

## Cara Menjalankan Pengujian (Unit Test)
1. Pastikan library JUnit 4 dan Hamcrest sudah ditambahkan ke *Test Libraries*.
2. Klik kanan pada proyek -> pilih **Test**.
3. Seluruh pengujian diferensial (960.000 operasi) dan invarian akan berjalan otomatis.

## Lisensi
Proyek ini dilisensikan di bawah [MIT License](https://opensource.org/licenses/MIT).
