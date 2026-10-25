/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package btreevisualization;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Edison Siahaan_L
 */




public class BTreePerformanceTest {

    @Test
    public void testResponseTimePerformance() {
        // 1. FASE PEMANASAN (JVM Warm-up)
        // Tujuannya agar Java meng-compile kode dan memuat class terlebih dahulu
        BTree warmupTree = new BTree(3);
        for (int i = 1; i <= 1000; i++) {
            warmupTree.insert(String.format("%03d", i % 999));
        }

        // 2. FASE PENGUJIAN SESUNGGUHNYA
        BTree tree = new BTree(3);
        
        long startTime = System.nanoTime();
        for (int i = 1; i <= 100; i++) {
            // Menggunakan zero-padding 3 digit sesuai aturan aplikasi
            tree.insert(String.format("%03d", i));
        }
        long endTime = System.nanoTime();
        
        // Konversi nanodetik ke milidetik
        long durationMs = (endTime - startTime) / 1_000_000;
        
        // Cetak hasil ke jendela Output NetBeans
        System.out.println("==================================================");
        System.out.println("Waktu eksekusi algoritma (Backend) sisip 100 kunci: " + durationMs + " ms");
        System.out.println("==================================================");
        
        // Asersi NF-02: Waktu eksekusi backend harus di bawah 100 ms
        assertTrue("Waktu respons backend melebihi batas 100ms (NF-02)", durationMs < 100);
    }
}
