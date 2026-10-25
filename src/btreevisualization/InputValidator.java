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
import java.util.List;

public class InputValidator {
    private int minKey, maxKey, maxBatch;
    private boolean isNumeric = true;

    public InputValidator(int min, int max, int batch) {
        this.minKey = min; 
        this.maxKey = max; 
        this.maxBatch = batch;
    }

    public void setNumeric(boolean numeric) { this.isNumeric = numeric; }
    public boolean isNumeric() { return isNumeric; }

    public ValidationResult validateKey(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ValidationResult(false, "Input tidak boleh kosong", null);
        }

        String cleanText = text.trim();

        if (isNumeric) {
            try {
                int val = Integer.parseInt(cleanText);
                if (val < minKey || val > maxKey) 
                    return new ValidationResult(false, "Nilai harus antara " + minKey + " dan " + maxKey, null);
                return new ValidationResult(true, "OK", Arrays.asList(String.format("%03d", val)));
            } catch (NumberFormatException e) {
                return new ValidationResult(false, "Input harus berupa angka", null);
            }
        } else {
            // Mode String: Hanya alfabet (A-Z, a-z), maksimal 5 karakter
            if (!cleanText.matches("^[a-zA-Z]{1,5}$")) {
                return new ValidationResult(false, "Hanya huruf alfabet (maks. 5 karakter) yang diizinkan", null);
            }
            return new ValidationResult(true, "OK", Arrays.asList(cleanText));
        }
    }

    public ValidationResult validateBatch(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ValidationResult(false, "Input batch tidak boleh kosong", null);
        }

        String[] parts = text.split(",");
        if (parts.length > maxBatch) {
            return new ValidationResult(false, "Maksimal " + maxBatch + " data dalam satu batch", null);
        }

        List<String> keys = new ArrayList<>();
        for (String p : parts) {
            String clean = p.trim();
            if (clean.isEmpty()) continue;

            if (isNumeric) {
                try {
                    int val = Integer.parseInt(clean);
                    if (val < minKey || val > maxKey) {
                        return new ValidationResult(false, "Nilai " + val + " harus antara " + minKey + " dan " + maxKey, null);
                    }
                    keys.add(String.format("%03d", val));
                } catch (NumberFormatException e) {
                    return new ValidationResult(false, "Input '" + clean + "' bukan angka yang valid", null);
                }
            } else {
                if (!clean.matches("^[a-zA-Z]{1,5}$")) {
                    return new ValidationResult(false, "Input '" + clean + "' bukan alfabet yang valid (maks. 5 huruf)", null);
                }
                keys.add(clean);
            }
        }
        
        if (keys.isEmpty()) return new ValidationResult(false, "Tidak ada data valid", null);
        return new ValidationResult(true, "OK", keys);
    }
}