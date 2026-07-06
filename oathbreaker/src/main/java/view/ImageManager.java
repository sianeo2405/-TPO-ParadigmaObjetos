package view;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

// Clase que gestiona la carga y almacenamiento en caché de imágenes para fondos, sprites y otros recursos gráficos.
// También define los defaults ante la ausencia de archivos y las extensiones de archivo soportadas.

public final class ImageManager {
    private static final Map<String, Image> imageCache = new HashMap<>();
    private static final String[] EXTENSIONS = {".png", ".jpg", ".jpeg"};

    private ImageManager() {}

    public static Image loadImage(String key) {
        return loadBackground(key);
    }

    public static Image loadBackground(String key) {
        return loadImage("backgrounds", key);
    }

    public static Image loadSprite(String key) {
        return loadImage("sprites", key);
    }

    public static Image loadSidebarSprite(String key) {
        return loadImage("sprites/sidebar", key);
    }

    public static Image loadTurnOrderSprite(String key) {
        return loadImage("sprites/turnorder", key);
    }

    public static Image loadImage(String subDir, String key) {
        if (key == null || subDir == null) {
            return null;
        }
        String sanitizedKey = sanitizeKey(key);
        String cacheKey = subDir + "/" + sanitizedKey;

        synchronized (imageCache) {
            if (imageCache.containsKey(cacheKey)) {
                return imageCache.get(cacheKey);
            }

            Image img = findAndLoadImage(subDir, sanitizedKey);
            if (img != null) {
                imageCache.put(cacheKey, img);
            }
            return img;
        }
    }

    private static String sanitizeKey(String key) {
        if (key == null) return null;
        String normalized = key.toLowerCase();
        normalized = normalized.replace(" ", "_");
        normalized = normalized
            .replace("ñ", "n")
            .replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u")
            .replace("ü", "u");
        return normalized.replaceAll("[^a-z0-9_/-]", "");
    }

    private static Image findAndLoadImage(String subDir, String sanitizedKey) {
        for (String ext : EXTENSIONS) {
            File localFile = new File(subDir, sanitizedKey + ext);
            if (localFile.exists()) {
                try {
                    Image img = ImageIO.read(localFile);
                    if (img != null) {
                        System.out.println("Loaded asset from file system: " + localFile.getAbsolutePath());
                        return img;
                    }
                } catch (Exception e) {
                    System.err.println("Failed to read asset file: " + localFile.getAbsolutePath() + " - " + e.getMessage());
                }
            }
        }

        for (String ext : EXTENSIONS) {
            String resourcePath = "/" + subDir + "/" + sanitizedKey + ext;
            try {
                InputStream is = ImageManager.class.getResourceAsStream(resourcePath);
                if (is != null) {
                    Image img = ImageIO.read(is);
                    if (img != null) {
                        System.out.println("Loaded asset from classpath: " + resourcePath);
                        return img;
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to read asset from classpath: " + resourcePath + " - " + e.getMessage());
            }
        }

        for (String ext : EXTENSIONS) {
            String resourcePath = "/resources/" + subDir + "/" + sanitizedKey + ext;
            try {
                InputStream is = ImageManager.class.getResourceAsStream(resourcePath);
                if (is != null) {
                    Image img = ImageIO.read(is);
                    if (img != null) {
                        System.out.println("Loaded asset from classpath: " + resourcePath);
                        return img;
                    }
                }
            } catch (Exception e) {
            }
        }

        System.out.println("No asset found for folder '" + subDir + "' and key '" + sanitizedKey + "'. Using fallback rendering.");
        return null;
    }
}
