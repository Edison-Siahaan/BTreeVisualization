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

public class BTreeInvariantTest {

    @Test
    public void testInvariantsAfterOperations() {
        BTree tree = new BTree(3); // Orde 3
        tree.insert("010");
        tree.insert("020");
        tree.insert("030");
        tree.insert("040");
        tree.insert("050");
        tree.delete("020");

        // Panggil fungsi pemeriksa invarian dengan menyertakan orde 3
        assertValidBTree(tree, 3);
    }

    // PERBAIKAN: Menerima parameter order
    public static void assertValidBTree(BTree tree, int order) {
        TreeSnapshot snapshot = tree.createSnapshot();
        if (snapshot.root == null) return; // Pohon kosong valid

        // 1. Cek Kedalaman Daun
        int leafDepth = getLeafDepth(snapshot.root);
        int actualMaxDepth = getMaxDepth(snapshot.root);
        assertEquals("Semua daun harus berada di level yang sama", leafDepth, actualMaxDepth);

        // 2. Cek Kunci Terurut, Batas Kunci, dan Jumlah Anak
        validateNode(snapshot.root, order, true); // Gunakan parameter order
    }

    private static void validateNode(NodeSnapshot node, int order, boolean isRoot) {
        // Cek batas kunci berdasarkan order yang dikirim
        int maxKeys = order - 1;
        int minKeys = (int) Math.ceil(order / 2.0) - 1;

        if (!isRoot) {
            assertTrue("Underflow: Kunci kurang dari batas minimum", node.keys.size() >= minKeys);
        }
        assertTrue("Overflow: Kunci melebihi batas maksimum", node.keys.size() <= maxKeys);

        // Cek kunci terurut dan tanpa duplikat
        for (int i = 0; i < node.keys.size() - 1; i++) {
            assertTrue("Kunci tidak terurut atau duplikat", node.keys.get(i).compareTo(node.keys.get(i + 1)) < 0);
        }

        // Cek jumlah anak = jumlah kunci + 1 (jika bukan daun)
        if (!node.isLeaf) {
            assertEquals("Jumlah anak tidak sesuai", node.keys.size() + 1, node.children.size());
            
            // Rekursif ke anak-anak
            for (NodeSnapshot child : node.children) {
                validateNode(child, order, false);
            }
        }
    }

    private static int getLeafDepth(NodeSnapshot node) {
        if (node.isLeaf) return 1;
        return 1 + getLeafDepth(node.children.get(0)); // Ambil anak pertama sebagai acuan
    }

    private static int getMaxDepth(NodeSnapshot node) {
        if (node.isLeaf) return 1;
        int max = 0;
        for (NodeSnapshot child : node.children) {
            max = Math.max(max, getMaxDepth(child));
        }
        return 1 + max;
    }
}