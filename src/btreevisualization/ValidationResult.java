/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package btreevisualization;

/**
 *
 * @author Edison Siahaan_L
 */

import java.util.List;

public class ValidationResult {
    public boolean valid;
    public String message;
    public List<String> keys; // Berubah dari Integer ke String

    public ValidationResult(boolean valid, String message, List<String> keys) {
        this.valid = valid; this.message = message; this.keys = keys;
    }
}
