/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package btreevisualization;

/**
 *
 * @author Edison Siahaan_L
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class BTree {
    private BTreeNode root;
    private final int order;      
    private final int maxKeys;    
    private final int minKeys;    
    private final List<BTreeListener> listeners = new ArrayList<>();
    private String currentActiveKey = null; 

    public BTree(int order) {
        if (order < 3) throw new IllegalArgumentException("Orde minimal 3");
        this.order = order;
        this.maxKeys = order - 1;
        this.minKeys = (int) Math.ceil(order / 2.0) - 1;
    }

    public void addListener(BTreeListener listener) { listeners.add(listener); }

    // Helper untuk membersihkan padding 0 di depan untuk tampilan UI
    private String fmt(String key) {
        if (key != null && key.matches("^0\\d+$")) {
            return key.replaceFirst("^0+(?!$)", "");
        }
        return key;
    }

    private void fireEvent(BTreeEventType type, HashSet<UUID> ids, String msg) {
        fireEvent(type, ids, msg, currentActiveKey);
    }

    private void fireEvent(BTreeEventType type, HashSet<UUID> ids, String msg, String activeKey) {
        // PERBAIKAN: Jika tidak ada listener (misal saat unit test performa), lewati pembuatan snapshot
        if (listeners.isEmpty()) return; 

        TreeSnapshot snap = createSnapshot();
        BTreeEvent event = new BTreeEvent(type, ids, msg, snap, activeKey);
        for (BTreeListener l : listeners) l.onEvent(event);
    }

    public void insert(String key) {
        if (contains(key)) return; 

        this.currentActiveKey = key; 
        try {
            if (root == null) {
                root = new BTreeNode(true);
                root.keys.add(key);
                fireEvent(BTreeEventType.KEY_INSERTED, new HashSet<>(Arrays.asList(root.id)), "Membuat root baru, sisip " + fmt(key));
                return;
            }
            SplitResult split = insertRec(root, key);
            if (split != null) {
                BTreeNode newRoot = new BTreeNode(false);
                newRoot.keys.add(split.median);
                newRoot.children.add(root);
                newRoot.children.add(split.rightSibling);
                root = newRoot;
                fireEvent(BTreeEventType.NODE_SPLIT, new HashSet<>(Arrays.asList(newRoot.id, root.children.get(0).id, root.children.get(1).id)), "Root dipecah, median " + fmt(split.median) + " menjadi root baru");
            }
        } finally {
            this.currentActiveKey = null; 
        }
    }

    private SplitResult insertRec(BTreeNode node, String key) {
        fireEvent(BTreeEventType.VISIT_NODE, new HashSet<>(Arrays.asList(node.id)), "Mengunjungi node");

        if (node.isLeaf) {
            int pos = 0;
            while (pos < node.keys.size() && key.compareTo(node.keys.get(pos)) > 0) pos++;
            node.keys.add(pos, key);
            fireEvent(BTreeEventType.KEY_INSERTED, new HashSet<>(Arrays.asList(node.id)), "Sisipkan " + fmt(key) + " ke leaf");
            if (node.keys.size() > maxKeys) return splitNode(node);
            return null;
        }

        int i = 0;
        while (i < node.keys.size() && key.compareTo(node.keys.get(i)) > 0) i++;
        SplitResult childSplit = insertRec(node.children.get(i), key);

        if (childSplit != null) {
            node.keys.add(i, childSplit.median);
            node.children.add(i + 1, childSplit.rightSibling);
            fireEvent(BTreeEventType.NODE_SPLIT, new HashSet<>(Arrays.asList(node.id, childSplit.rightSibling.id)), "Split anak, median " + fmt(childSplit.median) + " naik ke parent");
            if (node.keys.size() > maxKeys) return splitNode(node);
        }
        return null;
    }

    private SplitResult splitNode(BTreeNode node) {
        int medianIndex = node.keys.size() / 2;
        String median = node.keys.get(medianIndex);
        BTreeNode right = new BTreeNode(node.isLeaf);

        for (int i = medianIndex + 1; i < node.keys.size(); i++) right.keys.add(node.keys.get(i));
        while (node.keys.size() > medianIndex) node.keys.remove(node.keys.size() - 1);

        if (!node.isLeaf) {
            for (int i = medianIndex + 1; i < node.children.size(); i++) right.children.add(node.children.get(i));
            while (node.children.size() > medianIndex + 1) node.children.remove(node.children.size() - 1);
        }

        SplitResult result = new SplitResult();
        result.median = median;
        result.rightSibling = right;
        return result;
    }

    public boolean contains(String key) {
        return containsRec(root, key);
    }

    private boolean containsRec(BTreeNode node, String key) {
        if (node == null) return false;
        int i = 0;
        while (i < node.keys.size() && key.compareTo(node.keys.get(i)) > 0) i++;
        if (i < node.keys.size() && key.equals(node.keys.get(i))) return true;
        if (node.isLeaf) return false;
        return containsRec(node.children.get(i), key);
    }

    public boolean search(String key) {
        return searchRec(root, key);
    }

    private boolean searchRec(BTreeNode node, String key) {
        if (node == null) return false;
        fireEvent(BTreeEventType.VISIT_NODE, new HashSet<>(Arrays.asList(node.id)), "Mengunjungi node");

        for (int i = 0; i < node.keys.size(); i++) {
            String currentKey = node.keys.get(i);
            fireEvent(BTreeEventType.VISIT_NODE, new HashSet<>(Arrays.asList(node.id)), "Membandingkan " + fmt(key) + " dengan " + fmt(currentKey), currentKey);

            if (key.equals(currentKey)) {
                fireEvent(BTreeEventType.VISIT_NODE, new HashSet<>(Arrays.asList(node.id)), "Kunci " + fmt(key) + " DITEMUKAN", currentKey);
                return true;
            } else if (key.compareTo(currentKey) < 0) {
                if (node.isLeaf) {
                    fireEvent(BTreeEventType.VISIT_NODE, new HashSet<>(Arrays.asList(node.id)), "Kunci " + fmt(key) + " TIDAK DITEMUKAN (Leaf)", null);
                    return false;
                }
                return searchRec(node.children.get(i), key);
            }
        }

        if (node.isLeaf) {
            fireEvent(BTreeEventType.VISIT_NODE, new HashSet<>(Arrays.asList(node.id)), "Kunci " + fmt(key) + " TIDAK DITEMUKAN (Leaf)", null);
            return false;
        }
        return searchRec(node.children.get(node.keys.size()), key);
    }

    public void delete(String key) {
        if (root == null) return;
        this.currentActiveKey = key;
        try {
            deleteRec(root, key);
            if (root.keys.isEmpty() && !root.isLeaf) root = root.children.get(0);
            else if (root.keys.isEmpty() && root.isLeaf) root = null; 
        } finally {
            this.currentActiveKey = null;
        }
    }

    private void deleteRec(BTreeNode node, String key) {
        fireEvent(BTreeEventType.VISIT_NODE, new HashSet<>(Arrays.asList(node.id)), "Mencari kunci " + fmt(key));
        int idx = 0;
        while (idx < node.keys.size() && node.keys.get(idx).compareTo(key) < 0) idx++;

        if (idx < node.keys.size() && node.keys.get(idx).equals(key)) {
            if (node.isLeaf) {
                node.keys.remove(idx);
                fireEvent(BTreeEventType.DELETE_KEY, new HashSet<>(Arrays.asList(node.id)), "Menghapus kunci " + fmt(key) + " dari leaf");
            } else {
                BTreeNode succNode = getSuccessorNode(node, idx);
                String succValue = succNode.keys.get(0);
                
                succNode.keys.set(0, key);
                node.keys.set(idx, succValue);
                
                fireEvent(BTreeEventType.DELETE_KEY, new HashSet<>(Arrays.asList(node.id, succNode.id)), "Menukar kunci " + fmt(key) + " dengan penerusnya (" + fmt(succValue) + ")");
                
                BTreeNode childToVisit = node.children.get(idx + 1);
                deleteRec(childToVisit, key);
                if (childToVisit.keys.size() < minKeys) fixUnderflow(node, idx + 1);
            }
        } else {
            if (node.isLeaf) {
                fireEvent(BTreeEventType.VISIT_NODE, new HashSet<>(Arrays.asList(node.id)), "Kunci " + fmt(key) + " tidak ditemukan");
                return;
            }
            BTreeNode childToVisit = node.children.get(idx);
            deleteRec(childToVisit, key);
            if (childToVisit.keys.size() < minKeys) fixUnderflow(node, idx);
        }
    }

    private BTreeNode getSuccessorNode(BTreeNode node, int idx) {
        BTreeNode curr = node.children.get(idx + 1);
        while (!curr.isLeaf) curr = curr.children.get(0);
        return curr;
    }

    private void fixUnderflow(BTreeNode parent, int childIdx) {
        if (childIdx < 0 || childIdx >= parent.children.size()) return;
        BTreeNode child = parent.children.get(childIdx);
        if (parent == root && parent.keys.isEmpty()) return; 
        if (child.keys.size() >= minKeys) return;

        if (childIdx > 0) {
            BTreeNode leftSib = parent.children.get(childIdx - 1);
            if (leftSib.keys.size() > minKeys) {
                child.keys.add(0, parent.keys.get(childIdx - 1));
                parent.keys.set(childIdx - 1, leftSib.keys.remove(leftSib.keys.size() - 1));
                if (!leftSib.isLeaf) child.children.add(0, leftSib.children.remove(leftSib.children.size() - 1));
                fireEvent(BTreeEventType.BORROW, new HashSet<>(Arrays.asList(child.id, leftSib.id)), "Meminjam kunci dari saudara kiri");
                return;
            }
        }

        if (childIdx < parent.children.size() - 1) {
            BTreeNode rightSib = parent.children.get(childIdx + 1);
            if (rightSib.keys.size() > minKeys) {
                child.keys.add(parent.keys.get(childIdx));
                parent.keys.set(childIdx, rightSib.keys.remove(0));
                if (!rightSib.isLeaf) child.children.add(rightSib.children.remove(0));
                fireEvent(BTreeEventType.BORROW, new HashSet<>(Arrays.asList(child.id, rightSib.id)), "Meminjam kunci dari saudara kanan");
                return;
            }
        }

        if (childIdx > 0) {
            BTreeNode leftSib = parent.children.get(childIdx - 1);
            leftSib.keys.add(parent.keys.remove(childIdx - 1));
            leftSib.keys.addAll(child.keys);
            leftSib.children.addAll(child.children);
            parent.children.remove(childIdx);
            fireEvent(BTreeEventType.MERGE, new HashSet<>(Arrays.asList(leftSib.id, child.id)), "Menggabungkan node dengan saudara kiri");
        } else {
            BTreeNode rightSib = parent.children.get(childIdx + 1);
            child.keys.add(parent.keys.remove(childIdx));
            child.keys.addAll(rightSib.keys);
            child.children.addAll(rightSib.children);
            parent.children.remove(childIdx + 1);
            fireEvent(BTreeEventType.MERGE, new HashSet<>(Arrays.asList(child.id, rightSib.id)), "Menggabungkan node dengan saudara kanan");
        }
    }

    public TreeSnapshot createSnapshot() { return new TreeSnapshot(createNodeSnapshot(root)); }

    private NodeSnapshot createNodeSnapshot(BTreeNode node) {
        if (node == null) return null;
        List<NodeSnapshot> childSnaps = new ArrayList<>();
        for (BTreeNode child : node.children) childSnaps.add(createNodeSnapshot(child));
        return new NodeSnapshot(node.id, new ArrayList<>(node.keys), childSnaps, node.isLeaf);
    }

    public void restoreFromSnapshot(TreeSnapshot snapshot) { this.root = restoreNode(snapshot.root); }

    private BTreeNode restoreNode(NodeSnapshot snap) {
        if (snap == null) return null;
        BTreeNode node = new BTreeNode(snap.isLeaf);
        node.id = snap.id;
        node.keys.addAll(snap.keys);
        for (NodeSnapshot childSnap : snap.children) node.children.add(restoreNode(childSnap));
        return node;
    }

    private static class SplitResult {
        String median;
        BTreeNode rightSibling;
    }
}