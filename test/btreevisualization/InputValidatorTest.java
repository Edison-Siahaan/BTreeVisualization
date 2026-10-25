/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package btreevisualization;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Edison Siahaan_L
 */

public class InputValidatorTest {
    private InputValidator validator;

    @Before
    public void setUp() {
        validator = new InputValidator(1, 999, 20); // min=1, max=999
    }

    @Test
    public void testNumericModeValidation() {
        validator.setNumeric(true);

        ValidationResult res1 = validator.validateKey("10");
        assertTrue(res1.valid);
        assertEquals("010", res1.keys.get(0)); 

        assertFalse(validator.validateKey("0").valid);
        assertFalse(validator.validateKey("1000").valid);
        assertFalse(validator.validateKey("").valid);
        assertFalse(validator.validateKey("AX").valid);
    }

    @Test
    public void testStringModeValidation() {
        validator.setNumeric(false);

        // PERBAIKAN TES: Karena aturan murni alfabet, "007" harus Ditolak
        assertFalse("Kunci angka harus ditolak di mode String", validator.validateKey("007").valid);

        // V-05: Valid String
        assertTrue(validator.validateKey("AX").valid);

        // V-06: Karakter campuran ditolak (sesuai aturan alfabet dasar)
        assertFalse(validator.validateKey("A1").valid);
        
        // V-07: Kosong ditolak
        assertFalse(validator.validateKey("  ").valid);
    }
}



