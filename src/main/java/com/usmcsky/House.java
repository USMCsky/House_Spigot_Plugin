package com.usmcsky;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Door;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class House extends JavaPlugin {

    private static final int HOUSE_DISTANCE = 5;
    private static final int HOUSE_WIDTH = 7;
    private static final int HOUSE_DEPTH = 7;
    private static final int WALL_HEIGHT = 7;
    private static final int SECOND_FLOOR_Y = 4;
    private static final int ROOF_Y = 8;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("build")) {
            return false;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use /build.");
            return true;
        }

        buildHouse(player);
        player.sendMessage(ChatColor.GREEN + "Built a " + HOUSE_WIDTH + " X " + HOUSE_DEPTH + " two-story house.");
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
        buildRoof(origin, right, depth);
        buildWalls(origin, right, depth);
        placeWindows(origin, right, depth);
        placeDoor(origin, right, front, depth);
    }

    private void clearBuildArea(Location origin, BlockFace right, BlockFace depth) {
        int halfWidth = getHalfWidth();
        for (int y = 1; y <= ROOF_Y; y++) {
            for (int x = -halfWidth; x <= halfWidth; x++) {
                for (int z = 0; z < HOUSE_DEPTH; z++) {
                    setBlock(origin, right, depth, x, y, z, Material.AIR);
                }
            }
        }
    }

    private void buildFloors(Location origin, BlockFace right, BlockFace depth) {
        int halfWidth = getHalfWidth();
        for (int x = -halfWidth; x <= halfWidth; x++) {
            for (int z = 0; z < HOUSE_DEPTH; z++) {
                setBlock(origin, right, depth, x, 0, z, Material.COBBLESTONE);
                setBlock(origin, right, depth, x, SECOND_FLOOR_Y, z, Material.STRIPPED_OAK_LOG);
            }
        }
    }

    private void buildRoof(Location origin, BlockFace right, BlockFace depth) {
        int halfWidth = getHalfWidth();
        for (int x = -halfWidth; x <= halfWidth; x++) {
            for (int z = 0; z < HOUSE_DEPTH; z++) {
                setBlock(origin, right, depth, x, ROOF_Y, z, Material.COBBLESTONE);
            }
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
                            ? Material.STRIPPED_OAK_LOG
                            : Material.OAK_PLANKS;
                    setBlock(origin, right, depth, x, y, z, material);
                }
            }
        }
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

    private Block getBlock(Location origin, BlockFace right, BlockFace depth, int xOffset, int yOffset, int zOffset) {
        int x = origin.getBlockX() + right.getModX() * xOffset + depth.getModX() * zOffset;
        int y = origin.getBlockY() + yOffset;
        int z = origin.getBlockZ() + right.getModZ() * xOffset + depth.getModZ() * zOffset;
        return origin.getWorld().getBlockAt(x, y, z);
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
