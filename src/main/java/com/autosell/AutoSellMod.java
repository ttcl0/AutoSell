package com.autosell;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class AutoSellMod implements ClientModInitializer {

    private int lastCount = -1;
    private long scheduledSellAt = -1L;

    @Override
    public void onInitializeClient() {
        // Reset state on world/server join
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            lastCount = -1;
            scheduledSellAt = -1L;
        });

        // Intercept ".autosell" chat → open config screen instead of sending it
        ClientSendMessageEvents.ALLOW_CHAT.register(message -> {
            if (message.equalsIgnoreCase(".autosell")) {
                MinecraftClient mc = MinecraftClient.getInstance();
                mc.execute(() -> mc.setScreen(new AutoSellScreen()));
                return false; // cancel: don't send to server
            }
            return true;
        });

        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    private void onTick(MinecraftClient client) {
        if (client.player == null || client.getNetworkHandler() == null) {
            return;
        }

        // Fire a delayed sell when the timer expires
        if (scheduledSellAt >= 0 && System.currentTimeMillis() >= scheduledSellAt) {
            client.getNetworkHandler().sendChatCommand("sellall " + toSellArg(AutoSellConfig.getBlockId()));
            scheduledSellAt = -1L;
        }

        if (!AutoSellConfig.isActive()) {
            lastCount = -1;
            return;
        }

        int current = countItem(client.player.getInventory(), AutoSellConfig.getBlockId());

        if (lastCount < 0) {
            lastCount = current;
            return;
        }

        // Item count rose → schedule (or immediately fire) the sell command
        if (current > lastCount && scheduledSellAt < 0) {
            long delay = AutoSellConfig.getDelayMs();
            if (delay <= 0) {
                client.getNetworkHandler().sendChatCommand("sellall " + toSellArg(AutoSellConfig.getBlockId()));
            } else {
                scheduledSellAt = System.currentTimeMillis() + delay;
            }
        }

        lastCount = current;
    }

    private static String toSellArg(String blockId) {
        int colon = blockId.indexOf(':');
        return colon >= 0 ? blockId.substring(colon + 1) : blockId;
    }

    private int countItem(PlayerInventory inventory, String blockId) {
        Identifier id = Identifier.tryParse(blockId);
        if (id == null) return 0;
        Item target = Registries.ITEM.get(id);
        if (target == Items.AIR) return 0;
        int count = 0;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem() == target) {
                count += stack.getCount();
            }
        }
        return count;
    }
}
