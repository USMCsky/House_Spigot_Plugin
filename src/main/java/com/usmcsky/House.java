package com.usmcsky;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Axis;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class House extends JavaPlugin {

    private static final int HOUSE_DISTANCE = 5;
    private static final int HOUSE_WIDTH = 7;
    private static final int HOUSE_DEPTH = 7;
    private static final int WALL_HEIGHT = 7;
    private static final int SECOND_FLOOR_Y = 4;
    private static final int ROOF_Y = 8;
    private static final int ROOF_OVERHANG = 1;
    private static final int CLEARANCE_TOP_Y = 15;

    private static final Material FOUNDATION_MATERIAL = Material.COBBLESTONE;
    private static final Material FLOOR_MATERIAL = Material.STRIPPED_OAK_LOG;
    private static final Material WALL_MATERIAL = Material.OAK_PLANKS;
    private static final Material CORNER_MATERIAL = Material.STRIPPED_OAK_LOG;
    private static final Material MAIN_ROOF_MATERIAL = Material.DARK_OAK_STAIRS;
    private static final Material MAIN_RIDGE_MATERIAL = Material.SPRUCE_SLAB;
    private static final Material SECONDARY_ROOF_MATERIAL = Material.SPRUCE_STAIRS;
    private static final Material SECONDARY_RIDGE_MATERIAL = Material.DARK_OAK_SLAB;
    private static final Material GABLE_FILL_MATERIAL = Material.OAK_PLANKS;
    private static final Material GABLE_TRIM_MATERIAL = Material.STRIPPED_SPRUCE_LOG;
    private static final Material DORMER_FRAME_MATERIAL = Material.STRIPPED_OAK_LOG;
    private static final Material CHIMNEY_MATERIAL = Material.STONE_BRICKS;
    private static final Material CHIMNEY_CAP_MATERIAL = Material.COBBLESTONE_WALL;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("build")) {
            return false;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use /build.");
            return true;
        }

        buildHouse(player);
        player.sendMessage(ChatColor.GREEN + "Built a " + HOUSE_WIDTH + " X " + HOUSE_DEPTH
                + " two-story house with a fancy cross-gable roof.");
        return true;
    }

    private void buildHouse(Player player) {
        BlockFace depth = getCardinalFacing(player.getLocation().getYaw());
        BlockFace front = depth.getOppositeFace();
        BlockFace right = rotateCounterClockwise(front);
        Location origin = player.getLocation().getBlock().getLocation()
                .add(depth.getModX() * HOUSE_DISTANCE, -1, depth.getModZ() * HOUSE_DISTANCE);

        clearBuildArea(origin, right, depth);
        buildFloors(origin, right, depth);
        buildWalls(origin, right, depth);
        placeWindows(origin, right, depth);
        placeDoor(origin, right, front, depth);
        buildRoof(origin, right, front, depth);
    }

    private void clearBuildArea(Location origin, BlockFace right, BlockFace depth) {
        int sideClearance = getHalfWidth() + ROOF_OVERHANG + 1;
        for (int y = 1; y <= CLEARANCE_TOP_Y; y++) {
            for (int x = -sideClearance; x <= sideClearance; x++) {
                for (int z = -2; z <= HOUSE_DEPTH + ROOF_OVERHANG; z++) {
                    setBlock(origin, right, depth, x, y, z, Material.AIR);
                }
            }
        }
    }

    private void buildFloors(Location origin, BlockFace right, BlockFace depth) {
        int halfWidth = getHalfWidth();
        for (int x = -halfWidth; x <= halfWidth; x++) {
            for (int z = 0; z < HOUSE_DEPTH; z++) {
                setBlock(origin, right, depth, x, 0, z, FOUNDATION_MATERIAL);
                setBlock(origin, right, depth, x, SECOND_FLOOR_Y, z, FLOOR_MATERIAL);
            }
        }
    }

    private void buildRoof(Location origin, BlockFace right, BlockFace front, BlockFace depth) {
        buildMainGableRoof(origin, right, depth);
        buildMainGableEnds(origin, right, depth);
        buildCrossGableRoof(origin, right, front, depth);
        buildCrossGableEnds(origin, right, depth);
        buildRoofTrim(origin, right, depth);
        buildDormer(origin, right, front, depth, 1);
        buildDormer(origin, right, front, depth, HOUSE_DEPTH - 2);
        buildChimney(origin, right, depth);
        buildEntryGableDetail(origin, right, front, depth);
    }

    private void buildMainGableRoof(Location origin, BlockFace right, BlockFace depth) {
        int extendedHalfWidth = getHalfWidth() + ROOF_OVERHANG;
        int minZ = -ROOF_OVERHANG;
        int maxZ = HOUSE_DEPTH - 1 + ROOF_OVERHANG;

        for (int layer = 0; layer < extendedHalfWidth; layer++) {
            int x = extendedHalfWidth - layer;
            int y = ROOF_Y + layer;
            for (int z = minZ; z <= maxZ; z++) {
                setBottomStair(origin, right, depth, -x, y, z, MAIN_ROOF_MATERIAL, right.getOppositeFace());
                setBottomStair(origin, right, depth, x, y, z, MAIN_ROOF_MATERIAL, right);
            }
        }

        for (int z = minZ; z <= maxZ; z++) {
            setTopSlab(origin, right, depth, 0, ROOF_Y + extendedHalfWidth, z, MAIN_RIDGE_MATERIAL);
        }
    }

    private void buildMainGableEnds(Location origin, BlockFace right, BlockFace depth) {
        int halfWidth = getHalfWidth();
        for (int layer = 0; layer <= halfWidth; layer++) {
            int y = ROOF_Y + layer;
            int span = halfWidth - layer;
            for (int x = -span; x <= span; x++) {
                Material material = Math.abs(x) == span && span > 0 ? GABLE_TRIM_MATERIAL : GABLE_FILL_MATERIAL;
                setBlock(origin, right, depth, x, y, 0, material);
                setBlock(origin, right, depth, x, y, HOUSE_DEPTH - 1, material);
            }
        }
    }

    private void buildCrossGableRoof(Location origin, BlockFace right, BlockFace front, BlockFace depth) {
        int centerDepth = HOUSE_DEPTH / 2;
        int extendedHalfDepth = centerDepth + ROOF_OVERHANG;
        int halfWidth = getHalfWidth();

        for (int layer = 0; layer < extendedHalfDepth; layer++) {
            int y = ROOF_Y + 1 + layer;
            int zFront = centerDepth - (extendedHalfDepth - layer);
            int zBack = centerDepth + (extendedHalfDepth - layer);

            for (int x = -halfWidth; x <= halfWidth; x++) {
                setBottomStair(origin, right, depth, x, y, zFront, SECONDARY_ROOF_MATERIAL, front);
                setBottomStair(origin, right, depth, x, y, zBack, SECONDARY_ROOF_MATERIAL, depth);
            }
        }

        for (int x = -halfWidth; x <= halfWidth; x++) {
            setTopSlab(origin, right, depth, x, ROOF_Y + 1 + extendedHalfDepth, centerDepth, SECONDARY_RIDGE_MATERIAL);
        }
    }

    private void buildWalls(Location origin, BlockFace right, BlockFace depth) {
        int halfWidth = getHalfWidth();
        for (int y = 1; y <= WALL_HEIGHT; y++) {
            for (int x = -halfWidth; x <= halfWidth; x++) {
                for (int z = 0; z < HOUSE_DEPTH; z++) {
                    if (Math.abs(x) != halfWidth && z != 0 && z != HOUSE_DEPTH - 1) {
                        continue;
                    }

                    Material material = Math.abs(x) == halfWidth && (z == 0 || z == HOUSE_DEPTH - 1)
                            ? CORNER_MATERIAL
                            : WALL_MATERIAL;
                    setBlock(origin, right, depth, x, y, z, material);
                }
            }
        }
    }

    private void buildCrossGableEnds(Location origin, BlockFace right, BlockFace depth) {
        int halfWidth = getHalfWidth();
        int centerDepth = HOUSE_DEPTH / 2;

        for (int layer = 0; layer <= centerDepth; layer++) {
            int y = ROOF_Y + 1 + layer;
            int span = centerDepth - layer;
            for (int z = centerDepth - span; z <= centerDepth + span; z++) {
                Material material = (z == centerDepth - span || z == centerDepth + span) && span > 0
                        ? GABLE_TRIM_MATERIAL
                        : GABLE_FILL_MATERIAL;
                setBlock(origin, right, depth, -halfWidth, y, z, material);
                setBlock(origin, right, depth, halfWidth, y, z, material);
            }
        }
    }

    private void buildRoofTrim(Location origin, BlockFace right, BlockFace depth) {
        int extendedHalfWidth = getHalfWidth() + ROOF_OVERHANG;
        int minZ = -ROOF_OVERHANG;
        int maxZ = HOUSE_DEPTH - 1 + ROOF_OVERHANG;

        for (int z = minZ; z <= maxZ; z++) {
            setTopSlab(origin, right, depth, -extendedHalfWidth, ROOF_Y - 1, z, MAIN_RIDGE_MATERIAL);
            setTopSlab(origin, right, depth, extendedHalfWidth, ROOF_Y - 1, z, MAIN_RIDGE_MATERIAL);
        }

        for (int x = -extendedHalfWidth; x <= extendedHalfWidth; x++) {
            setTopSlab(origin, right, depth, x, ROOF_Y - 1, minZ, MAIN_RIDGE_MATERIAL);
            setTopSlab(origin, right, depth, x, ROOF_Y - 1, maxZ, MAIN_RIDGE_MATERIAL);
        }

        setVerticalTrimLog(origin, right, depth, 0, ROOF_Y + getHalfWidth() + 2, minZ);
        setVerticalTrimLog(origin, right, depth, 0, ROOF_Y + getHalfWidth() + 2, maxZ);
        setVerticalTrimLog(origin, right, depth, -getHalfWidth(), ROOF_Y + getHalfWidth() + 3, HOUSE_DEPTH / 2);
        setVerticalTrimLog(origin, right, depth, getHalfWidth(), ROOF_Y + getHalfWidth() + 3, HOUSE_DEPTH / 2);
    }

    private void buildDormer(Location origin, BlockFace right, BlockFace front, BlockFace depth, int zOffset) {
        int windowBaseY = ROOF_Y + 3;

        setBlock(origin, right, depth, -1, windowBaseY, zOffset, DORMER_FRAME_MATERIAL);
        setBlock(origin, right, depth, 0, windowBaseY, zOffset, Material.GLASS_PANE);
        setBlock(origin, right, depth, 1, windowBaseY, zOffset, DORMER_FRAME_MATERIAL);
        setBottomStair(origin, right, depth, -1, windowBaseY + 1, zOffset, SECONDARY_ROOF_MATERIAL, right.getOppositeFace());
        setBottomStair(origin, right, depth, 1, windowBaseY + 1, zOffset, SECONDARY_ROOF_MATERIAL, right);
        setTopSlab(origin, right, depth, 0, windowBaseY + 2, zOffset, SECONDARY_RIDGE_MATERIAL);

        BlockFace awningFace = zOffset < HOUSE_DEPTH / 2 ? front : depth;
        int awningZ = zOffset < HOUSE_DEPTH / 2 ? zOffset - 1 : zOffset + 1;
        setBottomStair(origin, right, depth, 0, windowBaseY + 1, awningZ, SECONDARY_ROOF_MATERIAL, awningFace);
    }

    private void buildChimney(Location origin, BlockFace right, BlockFace depth) {
        int chimneyX = getHalfWidth() - 1;
        int chimneyZ = HOUSE_DEPTH - 2;
        for (int y = SECOND_FLOOR_Y + 1; y <= CLEARANCE_TOP_Y - 1; y++) {
            setBlock(origin, right, depth, chimneyX, y, chimneyZ, CHIMNEY_MATERIAL);
        }
        setBlock(origin, right, depth, chimneyX, CLEARANCE_TOP_Y, chimneyZ, CHIMNEY_CAP_MATERIAL);
    }

    private void buildEntryGableDetail(Location origin, BlockFace right, BlockFace front, BlockFace depth) {
        setBlock(origin, right, depth, -1, 3, 0, GABLE_TRIM_MATERIAL);
        setBlock(origin, right, depth, 1, 3, 0, GABLE_TRIM_MATERIAL);
        setBlock(origin, right, depth, 0, 4, 0, GABLE_TRIM_MATERIAL);
        setBottomStair(origin, right, depth, -1, 5, -1, SECONDARY_ROOF_MATERIAL, right.getOppositeFace());
        setBottomStair(origin, right, depth, 1, 5, -1, SECONDARY_ROOF_MATERIAL, right);
        setBottomStair(origin, right, depth, 0, 5, -2, SECONDARY_ROOF_MATERIAL, front);
        setTopSlab(origin, right, depth, 0, 6, -1, SECONDARY_RIDGE_MATERIAL);
    }

    private void placeWindows(Location origin, BlockFace right, BlockFace depth) {
        int halfWidth = getHalfWidth();
        int frontWindowOffset = Math.max(1, halfWidth - 1);
        int centerDepth = HOUSE_DEPTH / 2;
        for (int y = 2; y <= 3; y++) {
            setBlock(origin, right, depth, -frontWindowOffset, y, 0, Material.GLASS);
            setBlock(origin, right, depth, frontWindowOffset, y, 0, Material.GLASS);
            setBlock(origin, right, depth, 0, y, HOUSE_DEPTH - 1, Material.GLASS);
            setBlock(origin, right, depth, -halfWidth, y, centerDepth, Material.GLASS);
            setBlock(origin, right, depth, halfWidth, y, centerDepth, Material.GLASS);
        }
    }

    private void placeDoor(Location origin, BlockFace right, BlockFace front, BlockFace depth) {
        setBlock(origin, right, depth, 0, 1, 0, Material.AIR);
        setBlock(origin, right, depth, 0, 2, 0, Material.AIR);

        Block bottomBlock = getBlock(origin, right, depth, 0, 1, 0);
        bottomBlock.setType(Material.OAK_DOOR, false);
        Door bottomDoor = (Door) bottomBlock.getBlockData();
        bottomDoor.setFacing(front);
        bottomDoor.setHalf(Bisected.Half.BOTTOM);
        bottomBlock.setBlockData(bottomDoor, false);

        Block topBlock = getBlock(origin, right, depth, 0, 2, 0);
        topBlock.setType(Material.OAK_DOOR, false);
        Door topDoor = (Door) topBlock.getBlockData();
        topDoor.setFacing(front);
        topDoor.setHalf(Bisected.Half.TOP);
        topBlock.setBlockData(topDoor, false);
    }

    private void setBlock(Location origin, BlockFace right, BlockFace depth, int xOffset, int yOffset, int zOffset, Material material) {
        getBlock(origin, right, depth, xOffset, yOffset, zOffset).setType(material, false);
    }

    private void setBottomStair(Location origin, BlockFace right, BlockFace depth, int xOffset, int yOffset,
                                int zOffset, Material material, BlockFace facing) {
        Block block = getBlock(origin, right, depth, xOffset, yOffset, zOffset);
        block.setType(material, false);
        Stairs stairs = (Stairs) block.getBlockData();
        stairs.setFacing(facing);
        stairs.setHalf(Bisected.Half.BOTTOM);
        block.setBlockData(stairs, false);
    }

    private void setTopSlab(Location origin, BlockFace right, BlockFace depth, int xOffset, int yOffset, int zOffset,
                            Material material) {
        Block block = getBlock(origin, right, depth, xOffset, yOffset, zOffset);
        block.setType(material, false);
        Slab slab = (Slab) block.getBlockData();
        slab.setType(Slab.Type.TOP);
        block.setBlockData(slab, false);
    }

    private void setVerticalTrimLog(Location origin, BlockFace right, BlockFace depth,
                                    int xOffset, int yOffset, int zOffset) {
        Block block = getBlock(origin, right, depth, xOffset, yOffset, zOffset);
        block.setType(GABLE_TRIM_MATERIAL, false);
        Orientable log = (Orientable) block.getBlockData();
        log.setAxis(Axis.Y);
        block.setBlockData(log, false);
    }

    private Block getBlock(Location origin, BlockFace right, BlockFace depth, int xOffset, int yOffset, int zOffset) {
        int x = origin.getBlockX() + right.getModX() * xOffset + depth.getModX() * zOffset;
        int y = origin.getBlockY() + yOffset;
        int z = origin.getBlockZ() + right.getModZ() * xOffset + depth.getModZ() * zOffset;
        return Objects.requireNonNull(origin.getWorld(), "origin world").getBlockAt(x, y, z);
    }

    private int getHalfWidth() {
        return HOUSE_WIDTH / 2;
    }

    private BlockFace getCardinalFacing(float yaw) {
        float normalizedYaw = (yaw % 360 + 360) % 360;
        if (normalizedYaw >= 45 && normalizedYaw < 135) {
            return BlockFace.WEST;
        }
        if (normalizedYaw >= 135 && normalizedYaw < 225) {
            return BlockFace.NORTH;
        }
        if (normalizedYaw >= 225 && normalizedYaw < 315) {
            return BlockFace.EAST;
        }
        return BlockFace.SOUTH;
    }

    private BlockFace rotateCounterClockwise(BlockFace face) {
        return switch (face) {
            case NORTH -> BlockFace.WEST;
            case WEST -> BlockFace.SOUTH;
            case SOUTH -> BlockFace.EAST;
            case EAST -> BlockFace.NORTH;
            default -> throw new IllegalArgumentException("Only cardinal directions are supported.");
        };
    }
}
