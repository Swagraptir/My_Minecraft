package com.myminecraft.furniture;

/** What right-clicking a furniture block does. */
public enum FurnitureType {
    /** Pure decoration. */
    DECOR,
    /** Right-click to sit down. */
    SEAT,
    /** Opens a 27-slot inventory that is saved with the block. */
    STORAGE,
    /** Light that is toggled on/off with right-click. */
    LAMP,
    /** Always glowing (fireplace, aquarium). */
    GLOW
}
