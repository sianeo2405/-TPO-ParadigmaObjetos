package controller;

import model.Enemy;
import model.Item;
import model.MapNode;
import model.NodeType;
import model.Party;
import model.PartyMember;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

// Controlador principal del juego, gestionando el estado del juego, incluyendo el grupo del jugador, el mapa,
// el motor de combate y la interacción con el jugador a través de diferentes pantallas y eventos.

public final class GameController implements java.io.Serializable {
    public interface Listener {
        void onStateChanged();
    }

    private final Party party;
    private final GameMap map;
    private final CombatEngine combatEngine;
    private final Random random = new Random();
    private transient List<Listener> listeners = new ArrayList<>();

    private GameScreen screen = GameScreen.MAP;
    private String statusMessage = "Elegí tu camino. ¡Alcanzá al Señor de la Torre en la cima!";

    private int lastGoldGained = 0;
    private int lastXpGained = 0;
    private final List<String> lastLevelUps = new ArrayList<>();

    private List<Item> shopStock = new ArrayList<>();

    public GameController() {
        party = PartyMember.createDefaultParty();
        map = new MapGenerator().generate();
        combatEngine = new CombatEngine(party);
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public Party getParty() {
        return party;
    }

    public GameMap getMap() {
        return map;
    }

    public CombatEngine getCombatEngine() {
        return combatEngine;
    }

    public GameScreen getScreen() {
        return screen;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public int getLastGoldGained() {
        return lastGoldGained;
    }

    public int getLastXpGained() {
        return lastXpGained;
    }

    public List<String> getLastLevelUps() {
        return lastLevelUps;
    }

    public List<Item> getShopStock() {
        return shopStock;
    }

    public void selectNode(int nodeId) {
        if (screen != GameScreen.MAP || !map.canMoveTo(nodeId)) {
            return;
        }

        MapNode node = map.moveTo(nodeId);
        statusMessage = "Llegaste al nodo de " + node.getType().getLabel();

        switch (node.getType()) {
            case START -> {
                screen = GameScreen.MAP;
                statusMessage = "Tu viaje comienza. Elige un camino hacia arriba.";
            }
            case COMBAT -> startCombat(false);
            case ELITE -> startCombat(true);
            case REST -> {
                party.restAll();
                screen = GameScreen.REST;
                statusMessage = "Descansas junto al fuego. El grupo recupera HP y MP.";
            }
            case TREASURE -> {
                applyTreasure();
                screen = GameScreen.TREASURE;
            }
            case SHOP -> {
                openShop();
            }
            case BOSS -> startCombat(true);
            default -> screen = GameScreen.MAP;
        }

        notifyListeners();
    }

    private void startCombat(boolean elite) {
        List<Enemy> enemies = new ArrayList<>();
        MapNode node = map.getCurrentNode();

        if (node.getType() == NodeType.BOSS) {
            enemies.add(Enemy.boss());
            statusMessage = "¡El Señor de la Torre se interpone en tu camino!";
        } else if (elite) {
            enemies.add(random.nextBoolean() ? Enemy.orc() : Enemy.darkMage());
            int numEnemies = 2 + random.nextInt(3); 
            for (int i = 1; i < numEnemies; i++) {
                enemies.add(Enemy.skeleton());
            }
            statusMessage = "¡Enemigos élite aparecen!";
        } else {
            int numEnemies = 1 + random.nextInt(4); 
            for (int i = 0; i < numEnemies; i++) {
                enemies.add(pickRandom(Enemy.goblin(), Enemy.wolf(), Enemy.skeleton()));
            }
            statusMessage = "¡Los enemigos atacan!";
        }

        combatEngine.startEncounter(enemies);
        screen = GameScreen.COMBAT;
        checkCombatEnd();
    }

    @SafeVarargs
    private <T> T pickRandom(T... options) {
        return options[random.nextInt(options.length)];
    }

    private void applyTreasure() {
        List<PartyMember> alive = party.getAliveMembers();
        if (alive.isEmpty()) {
            statusMessage = "El cofre está vacío...";
            return;
        }
        int goldBonus = 30 + random.nextInt(40);
        party.addGold(goldBonus);
        lastGoldGained = goldBonus;
        PartyMember lucky = alive.get(random.nextInt(alive.size()));
        if (random.nextBoolean()) {
            lucky.boostAttack(3);
            statusMessage = "¡" + lucky.getName() + " encontró una Espada Llameante (+3 ATK)!";
        } else {
            lucky.boostDefense(2);
            statusMessage = "¡" + lucky.getName() + " encontró una Capa Reforzada (+2 DEF)!";
        }
        statusMessage += "¡También encontraste " + goldBonus + " de oro!";
        notifyListeners();
    }

    private void openShop() {
        shopStock = new ArrayList<>(Arrays.asList(
            Item.healthPotion(),
            Item.manaPotion(),
            Item.strengthElixir(),
            Item.defenseElixir(),
            Item.phoenixDown(),
            Item.megaPotion()
        ));
        screen = GameScreen.SHOP;
        statusMessage = "El mercader saluda. Tienes " + party.getGold() + " de oro. ¿Qué deseas comprar?";
    }

    public String buyItem(Item item) {
        if (!shopStock.contains(item)) {
            return "El mercader no tiene ese objeto.";
        }
        if (party.spendGold(item.getCost())) {
            party.addItem(item);
            shopStock.remove(item);
            notifyListeners();
            return "Compraste " + item.getName() + "por " + item.getCost() + " de oro. Ahora tienes " + party.getGold() + " de oro.";
        } else {
            return "No tienes suficiente oro. Necesitas " + item.getCost() + " de oro para comprar " + item.getName() + " y tienes " + party.getGold() + " de oro.";
        }
    }

    public String useItem(Item item, PartyMember target) {
        String result = party.useItem(item, target);
        notifyListeners();
        return result;
    }

    public void continueFromEvent() {
        if (screen == GameScreen.REST || screen == GameScreen.TREASURE || screen == GameScreen.SHOP) {
            screen = GameScreen.MAP;
            if (map.getAvailableNodes().isEmpty() && map.getCurrentNode().getOutgoingIds().isEmpty()) {
                screen = GameScreen.VICTORY;
                statusMessage = "¡Has limpiado la mazmorra!";
            }
            notifyListeners();
        }
    }

    public void checkCombatEnd() {
        if (screen != GameScreen.COMBAT) {
            return;
        }
        if (combatEngine.isPartyWiped()) {
            screen = GameScreen.DEFEAT;
            statusMessage = "El grupo de héroes ha sido derrotado...";
            notifyListeners();
            return;
        }
        if (combatEngine.isCombatOver()) {
            lastGoldGained = 0;
            lastXpGained = 0;
            lastLevelUps.clear();

            for (Enemy enemy : combatEngine.getEnemies()) {
                lastGoldGained += enemy.getGoldReward();
                lastXpGained += enemy.getXpReward();
            }

            party.addGold(lastGoldGained);

            for(PartyMember member : party.getAliveMembers()) {
                String levelUpMsg = member.addXp(lastXpGained);
                if (levelUpMsg != null) {
                    lastLevelUps.add(levelUpMsg);
                }
            }

            MapNode node = map.getCurrentNode();
            if (node.getType() == NodeType.BOSS) {
                screen = GameScreen.VICTORY;
                statusMessage = "¡El señor del castillo cae! ¡Has conquistado la Torre!";
            } else {
                screen = GameScreen.MAP;
                StringBuilder sb = new StringBuilder("¡Combate ganado! Ganaste " + lastGoldGained + " de oro y " + lastXpGained + " XP.");
                for (String levelUp : lastLevelUps) {
                    sb.append(" ").append(levelUp);
                }
                sb.append("Elige tu próximo destino.");
                statusMessage = sb.toString().trim();
                if (map.getAvailableNodes().isEmpty() && node.getOutgoingIds().isEmpty()) {
                    screen = GameScreen.VICTORY;
                    statusMessage = "¡Has limpiado la mazmorra!";
                }
            }
            notifyListeners();
        }
    }

    public void notifyListeners() {
        for (Listener listener : listeners) {
            listener.onStateChanged();
        }
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        listeners = new ArrayList<>();
    }
}
