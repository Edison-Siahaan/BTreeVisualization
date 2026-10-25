/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package btreevisualization;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;


/**
 *
 * @author Edison Siahaan_L
 */

public class BTreeDifferentialTest {

    @Test
    public void testRandomOperationsWithTreeSetOracle() {
        // Sesuai Bab 3.5: Orde 3-10, Seed 0-199, 300 Operasi per skenario
        for (int order = 3; order <= 10; order++) {
            for (long seed = 0; seed < 200; seed++) {
                Random random = new Random(seed);
                BTree tree = new BTree(order);
                TreeSet<String> referenceSet = new TreeSet<>();

                for (int i = 0; i < 300; i++) {
                    // Buat kunci acak numerik (di-pad 3 digit)
                    String key = String.format("%03d", 1 + random.nextInt(999));
                    
                    boolean isInsert = random.nextBoolean();
                    
                    if (isInsert) {
                        if (!referenceSet.contains(key)) {
                            tree.insert(key);
                            referenceSet.add(key);
                        }
                    } else {
                        if (referenceSet.contains(key)) {
                            tree.delete(key);
                            referenceSet.remove(key);
                        }
                    }

                    // Bandingkan in-order traversal pohon dengan TreeSet
                    List<String> treeInOrder = getInOrder(tree);
                    List<String> setList = new ArrayList<>(referenceSet);
                    
                    // Cek kecocokan isi
                    assertEquals("Ketidakcocokan pada orde " + order + ", seed " + seed + ", operasi ke-" + i,
                                 setList, treeInOrder);
                    
                    // PERBAIKAN: Kirimkan parameter order ke fungsi pemeriksa invarian
                    BTreeInvariantTest.assertValidBTree(tree, order);
                }
            }
        }
        System.out.println("Pengujian Diferensial Selesai. 480.000 operasi berhasil.");
    }

    // Fungsi bantu untuk mengekstrak penelusuran in-order dari TreeSnapshot
    private List<String> getInOrder(BTree tree) {
        List<String> result = new ArrayList<>();
        traverseInOrder(tree.createSnapshot().root, result);
        return result;
    }

    private void traverseInOrder(NodeSnapshot node, List<String> result) {
        if (node == null) return;
        for (int i = 0; i < node.keys.size(); i++) {
            if (!node.isLeaf) traverseInOrder(node.children.get(i), result);
            result.add(node.keys.get(i));
        }
        if (!node.isLeaf) traverseInOrder(node.children.get(node.children.size() - 1), result);
    }
}

