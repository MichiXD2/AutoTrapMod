package baritone.automation.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.Hand;
import net.minecraft.block.Block;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;

public class WorldInteractionHelper {
    private final MinecraftClient mc;

    public WorldInteractionHelper() {
        this.mc = MinecraftClient.getInstance();
    }

    public boolean placeBlock(BlockPos pos, Block block) {
        if (mc.player == null || mc.world == null) {
            return false;
        }

        // Prüfe ob Block bereits vorhanden
        if (mc.world.getBlockState(pos).getBlock() == block) {
            return true;
        }

        // Finde eine freie Seite zum Platzieren
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = pos.offset(dir);
            if (mc.world.getBlockState(neighbor).getMaterial().isSolid()) {
                Vec3d hitVec = new Vec3d(
                    neighbor.getX() + 0.5 + dir.getOffsetX() * 0.5,
                    neighbor.getY() + 0.5 + dir.getOffsetY() * 0.5,
                    neighbor.getZ() + 0.5 + dir.getOffsetZ() * 0.5
                );

                // Platziere Block
                mc.interactionManager.interactBlock(
                    mc.player,
                    mc.world,
                    Hand.MAIN_HAND,
                    new BlockHitResult(hitVec, dir.getOpposite(), neighbor, false)
                );
                mc.player.swingHand(Hand.MAIN_HAND);
                return true;
            }
        }

        return false;
    }

    public boolean breakBlock(BlockPos pos) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) {
            return false;
        }

        mc.interactionManager.attackBlock(pos, Direction.UP);
        mc.player.swingHand(Hand.MAIN_HAND);
        return true;
    }

    public boolean interactWithBlock(BlockPos pos) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) {
            return false;
        }

        Vec3d hitVec = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        mc.interactionManager.interactBlock(
            mc.player,
            mc.world,
            Hand.MAIN_HAND,
            new BlockHitResult(hitVec, Direction.UP, pos, false)
        );
        return true;
    }

    public BlockPos findNearbyBlock(Block block, int radius) {
        if (mc.player == null || mc.world == null) {
            return null;
        }

        BlockPos playerPos = new BlockPos(
            mc.player.getX(),
            mc.player.getY(),
            mc.player.getZ()
        );

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = playerPos.add(x, y, z);
                    if (mc.world.getBlockState(checkPos).getBlock() == block) {
                        return checkPos;
                    }
                }
            }
        }
        return null;
    }
}
