/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package btreevisualization;

/**
 *
 * @author Edison Siahaan_L
 */
import java.util.Locale;
import java.util.ResourceBundle;
import java.text.MessageFormat;

public class LanguageManager {
    private static ResourceBundle bundle;
    private static String currentLocale = "id";

    static {
        setLanguage("id"); // Default bahasa Indonesia
    }

    public static void setLanguage(String localeCode) {
        currentLocale = localeCode;
        Locale locale = new Locale(localeCode);
        try {
            // Memuat file messages_id.properties atau messages_en.properties
            bundle = ResourceBundle.getBundle("messages", locale);
        } catch (Exception e) {
            System.out.println("Gagal memuat file bahasa untuk: " + localeCode);
        }
    }

    public static String get(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return "!" + key + "!"; // Tanda jika key tidak ditemukan
        }
    }

    public static String get(String key, Object... args) {
        try {
            return MessageFormat.format(bundle.getString(key), args);
        } catch (Exception e) {
            return "!" + key + "!";
        }
    }

    public static String getCurrentLocale() {
        return currentLocale;
    }
}