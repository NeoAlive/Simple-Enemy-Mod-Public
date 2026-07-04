package net.nekoyuni.SimpleEnemyMod.inventory;

public final class PmcInventorySlots {

    private PmcInventorySlots() {}

    // Fixed slots
    public static final int MAIN_HAND = 0;
    public static final int RESERVE_AMMO = 1;
    public static final int FEET = 2;
    public static final int LEGS = 3;
    public static final int CHEST = 4;
    public static final int HEAD = 5;

    // General storage
    public static final int STORAGE_START = 6;
    public static final int STORAGE_END = 17; // inclusive

    // Combined ammo pool: reserve slot + all storage slots
    public static final int[] AMMO_SLOTS = buildAmmoSlots();

    private static int[] buildAmmoSlots() {
        int[] slots = new int[1 + (STORAGE_END - STORAGE_START + 1)];
        slots[0] = RESERVE_AMMO;
        for (int i = STORAGE_START; i <= STORAGE_END; i++) {
            slots[i - STORAGE_START + 1] = i;
        }
        return slots;
    }
}