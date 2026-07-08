package model;

import controller.CombatEngine;
import java.util.ArrayList;
import java.util.List;

// Ahora es una clase Abstracta. Sirve como "molde" para los héroes específicos.
public abstract class PartyMember extends Combatant {
    
    // Eliminamos la dependencia de CharacterClass y las variables repetidas de Combatant
    protected int maxMp;
    protected int currentMp;
    protected int attackBuff;
    protected int defenseBuff;
    protected int level = 1;
    protected int xp = 0;
    
    private static final int[] XP_THRESHOLDS = {0, 100, 250, 450, 700, 1000};

    public PartyMember(String name, int maxHp, int maxMp, int attack, int defense, int speed) {
        // Llamamos al constructor de la clase padre (Combatant)
        super(name, maxHp, attack, defense, speed);
        this.maxMp = maxMp;
        this.currentMp = maxMp;
    }

    // ==========================================
    // CONTRATOS ABSTRACTOS (Polimorfismo)
    // ==========================================
    public abstract String getRoleName();
    public abstract String getSkillName();
    public abstract String getSkillDescription();
    public abstract int getSkillMpCost();
    public abstract void executeSkill(CombatEngine engine, PartyMember allyTarget, Enemy enemyTarget);

    // Métodos abstractos para calcular el crecimiento de estadísticas al subir de nivel (Reemplaza al Switch)
    protected abstract int getHpGrowth();
    protected abstract int getMpGrowth();
    protected abstract int getAttackGrowth();
    protected abstract int getDefenseGrowth();
    protected abstract int getSpeedGrowth();


    // ==========================================
    // MÉTODOS SOBRESCRITOS Y ESTADO
    // ==========================================
    
    @Override
    public boolean isPlayerControlled() {
        return true;
    }

    @Override
    public int getAttack() {
        return super.getAttack() + attackBuff;
    }

    @Override
    public int getDefense() {
        return super.getDefense() + defenseBuff;
    }

    public int getBaseAttack() {
        return super.getAttack();
    }

    public int getBaseDefense() {
        return super.getDefense();
    }

    public int getMaxMp() { return maxMp; }
    public int getCurrentMp() { return currentMp; }
    public int getLevel() { return level; }
    public int getXp() { return xp; }

    public int getXpToNextLevel() {
        if (level >= XP_THRESHOLDS.length) {
            return Integer.MAX_VALUE;
        }
        return XP_THRESHOLDS[level] - xp;
    }

    public String addXp(int amount) {
        if (level >= XP_THRESHOLDS.length) {
            return name + " ya ha alcanzado el nivel máximo.";
        }
        xp += amount;
        
        if (xp >= getXpToNextLevel()) {
            level++;
            
            // Usamos el polimorfismo para obtener el crecimiento según la subclase
            int hpGain = getHpGrowth();
            int mpGain = getMpGrowth();
            int attackGain = getAttackGrowth();
            int defenseGain = getDefenseGrowth();
            int speedGain = getSpeedGrowth();

            this.maxHp += hpGain;
            this.currentHp = Math.min(this.currentHp + hpGain, this.maxHp);
            this.maxMp += mpGain;
            this.currentMp = Math.min(this.currentMp + mpGain, this.maxMp);
            this.attack += attackGain;
            this.defense += defenseGain;
            this.speed += speedGain;

            return "¡" + name + " ha subido al nivel " + level + "!";
        }
        return null;
    }

    public void heal(int amount) {
        currentHp = Math.min(maxHp, currentHp + amount);
    }

    public boolean spendMp(int amount) {
        if (currentMp < amount) {
            return false;
        }
        currentMp -= amount;
        return true;
    }

    public int restoreMp(int amount) {
        int restoredMp = Math.min(maxMp - currentMp, amount);
        currentMp = Math.min(maxMp, currentMp + amount);
        return restoredMp;
    }

    public void fullRestore() {
        currentHp = maxHp;
        currentMp = maxMp;
        attackBuff = 0;
        defenseBuff = 0;
    }

    public void boostAttack(int amount) {
        this.attack += amount;
    }

    public void boostDefense(int amount) {
        this.defense += amount;
    }

    public void applyAttackBuff(int amount) {
        attackBuff += amount;
    }

    public void applyDefenseBuff(int amount) {
        defenseBuff += amount;
    }

    public int revive(int hp) {
        int restoredHp = maxHp / 2 + hp;
        currentHp = restoredHp;
        return restoredHp;
    }

    public void clearBuffs() {
        attackBuff = 0;
        defenseBuff = 0;
    }

    @Override
    public String toString() {
        return name + " (" + currentHp + "/" + maxHp + " HP)";
    }

    // ==========================================
    // CREADOR DE GRUPO (Factory)
    // ==========================================
    public static Party createDefaultParty() {
        List<PartyMember> members = new ArrayList<>();
        members.add(new Warrior("Altria", 120, 30, 18, 8, 10));
        members.add(new Healer("Jean", 85, 50, 12, 3, 12));
        members.add(new Archer("Emil", 70, 35, 20, 4, 15));
        members.add(new Mage("Mesh", 60, 70, 10, 2, 14));
        return new Party(members);
    }
}