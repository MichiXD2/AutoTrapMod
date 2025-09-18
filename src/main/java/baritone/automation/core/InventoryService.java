package baritone.automation.core;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public class InventoryService {
    private final AutomationLogger logger;

    public InventoryService() {
        this.logger = AutomationLogger.getInstance();
    }

    public boolean hasItem(String itemId) {
        PlayerInventory inventory = Minecraft.getInstance().player.inventory;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem().getRegistryName().toString().equals(itemId)) {
                return true;
            }
        }
        return false;
    }

    public int countItem(String itemId) {
        PlayerInventory inventory = Minecraft.getInstance().player.inventory;
        int count = 0;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem().getRegistryName().toString().equals(itemId)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    public boolean hasSpaceFor(String itemId) {
        PlayerInventory inventory = Minecraft.getInstance().player.inventory;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
