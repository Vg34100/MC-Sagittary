package net.vg.sagittary.item;

/** Storage and progression rules for Sagittary quivers. */
public enum QuiverTier {
    BASIC("quiver", 64, 5),
    HUNTER("hunter_quiver", 128, 15),
    RANGER("ranger_quiver", 256, Integer.MAX_VALUE);

    private final String id;
    private final int capacity;
    private final int typeLimit;

    QuiverTier(String id, int capacity, int typeLimit) {
        this.id = id;
        this.capacity = capacity;
        this.typeLimit = typeLimit;
    }

    public String id() { return id; }
    public int capacity() { return capacity; }
    public int typeLimit() { return typeLimit; }
    public boolean hasTypeLimit() { return typeLimit != Integer.MAX_VALUE; }
}
