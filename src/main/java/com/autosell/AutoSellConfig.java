package com.autosell;

public class AutoSellConfig {

    private static String blockId = "";
    private static long delayMs = 0L;
    private static boolean active = false;

    public static String getBlockId() { return blockId; }
    public static long getDelayMs() { return delayMs; }
    public static boolean isActive() { return active; }

    public static void set(String block, long delay) {
        blockId = block.trim();
        delayMs = delay;
        active = !blockId.isEmpty();
    }

    public static void disable() {
        active = false;
    }
}
