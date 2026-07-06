package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

// Representa el grupo del jugador, incluyendo miembros, oro e inventario. 
// Proporciona métodos para gestionar el estado del grupo y sus recursos.

public final class Party implements java.io.Serializable {
    private final List<PartyMember> members;
    private int gold = 50;
    private final List<Item> inventory = new ArrayList<>();

    public Party(List<PartyMember> members) {
        this.members = List.copyOf(members);
    }

    public List<PartyMember> getMembers() {
        return members;
    }

    public List<PartyMember> getAliveMembers() {
        List<PartyMember> alive = new ArrayList<>();
        for (PartyMember member : members) {
            if (member.isAlive()) {
                alive.add(member);
            }
        }
        return alive;
    }

    public boolean isWiped() {
        return getAliveMembers().isEmpty();
    }

    public int getGold() {
        return gold;
    }

    public void addGold(int amount) {
        gold += amount;
    }

    public boolean spendGold(int amount) {
        if (amount > gold) {
            return false;
        }
        gold -= amount;
        return true;
    }

    public List<Item> getInventory() {
        return Collections.unmodifiableList(inventory);
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public String useItem(Item item, PartyMember target) {
        if (!inventory.contains(item)) {
            return "No tienes ese objeto.";
        }
        if (item.getEffect() == Item.Effect.REVIVE && target.isAlive()) {
            return target.getName() + " ya está vivo.";
        }
        if (item.getEffect() != Item.Effect.REVIVE && !target.isAlive()) {
            return target.getName() + " está inconsciente.";
        }
        String result = item.applyTo(target);
        inventory.remove(item);
        return result;
    }

    public void restAll() {
        for (PartyMember member : members) {
            member.heal(member.getMaxHp() / 3);
            member.restoreMp(member.getMaxMp() / 2);
            member.clearBuffs();
        }
    }
}
