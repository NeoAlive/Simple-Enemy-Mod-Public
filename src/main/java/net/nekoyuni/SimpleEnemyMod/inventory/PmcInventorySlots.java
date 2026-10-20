package net.nekoyuni.SimpleEnemyMod.inventory;

// PMC inventory layout (UnitInventoryHandler, exposed as Capabilities.ItemHandler.ENTITY).
// Slots 0-5 mirror equipment like upstream; addons such as tacz_sewv rely on this map.
public final class PmcInventorySlots {

    private PmcInventorySlots() {}

    // Equipment-mirrored slots
    public static final int MAIN_HAND = 0;
    public static final int OFF_HAND = 1;
    public static final int FEET = 2;
    public static final int LEGS = 3;
    public static final int CHEST = 4;
    public static final int HEAD = 5;

    // General storage
    public static final int STORAGE_START = 6;
    public static final int STORAGE_END = 17; // inclusive

    // Reserve ammo pool: upstream puts the reserve in the first free slot from 1, so the offhand comes first
    public static final int[] AMMO_SLOTS = buildAmmoSlots();

    private static int[] buildAmmoSlots() {
        int[] slots = new int[1 + (STORAGE_END - STORAGE_START + 1)];
        slots[0] = OFF_HAND;
        for (int i = STORAGE_START; i <= STORAGE_END; i++) {
            slots[i - STORAGE_START + 1] = i;
        }
        return slots;
    }
}
