package controller;

import java.io.*;

// Sistema de guardado y carga del juego, permitiendo persistir el estado del juego en un archivo y restaurarlo posteriormente.

public final class SaveSystem {
    private static final String SAVE_FILE = "savegame.dat";

    public static boolean save(GameController controller) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(controller);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static GameController load() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (GameController) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean hasSave() {
        return new File(SAVE_FILE).exists();
    }
}
