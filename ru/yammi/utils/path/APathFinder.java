package ru.yammi.utils.path;

import net.minecraft.entity.EntityLivingBase;
import java.util.List;
import java.util.Collections;
import java.util.Set;

import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.BlockVine;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSlime;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.BlockTripWire;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockSign;
import net.minecraft.util.EnumFacing;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.util.BlockPos;
import net.minecraft.client.Minecraft;

public class APathFinder
{
    public Minecraft mc;
    public boolean invulnerable;
    public boolean creativeFlying;
    public boolean flying;
    public boolean immuneToFallDamage;
    public boolean fallingAllowed;
    public PathPos start;
    public PathPos current;
    public BlockPos goal;
    public HashMap<PathPos, Float> costMap;
    public HashMap<PathPos, PathPos> prevPosMap;
    public PathQueue queue;
    public int thinkSpeed;
    public int thinkTime;
    public int iterations;
    public boolean done;
    public boolean failed;
    public ArrayList<PathPos> path;

    public APathFinder(final BlockPos goal) {
        this.mc = Minecraft.getMinecraft();
        this.invulnerable = this.mc.thePlayer.capabilities.isCreativeMode;
        this.creativeFlying = this.mc.thePlayer.capabilities.isFlying;
        this.flying = true;
        /*if (Yammi.getInstance().isModuleEnabled(TPAura.class) && Yammi.getInstance().getModule(TPAura.class).getOption("Only walk").isBooleanValue())
        {
            this.creativeFlying.setValue(false);
            this.flying = new WalkingBoolean(false);
        }*/

        this.immuneToFallDamage = true;
        this.fallingAllowed = true;
        this.costMap = new HashMap<PathPos, Float>();
        this.prevPosMap = new HashMap<PathPos, PathPos>();
        this.queue = new PathQueue();
        this.thinkSpeed = 1024;
        this.thinkTime = 200;
        this.path = new ArrayList<>();
        this.start = new PathPos(new BlockPos(this.mc.thePlayer));
        this.goal = goal;
        this.costMap.put(this.start, 0.0f);
        this.queue.add(this.start, this.getHeuristic(this.start));
    }

    public static IBlockState getState(final BlockPos pos) {
        return Minecraft.getMinecraft().theWorld.getBlockState(pos);
    }

    public static Block getBlock(final BlockPos pos) {
        return getState(pos).getBlock();
    }

    public static Material getMaterial(final BlockPos pos) {
        return getState(pos).getBlock().getMaterial();
    }

    public void think() {
        if (this.done) {
            throw new IllegalStateException("Path was already found!");
        }
        int i;
        for (i = 0; i < this.thinkSpeed && !this.checkFailed(); ++i) {
            this.current = this.queue.poll();
            if (this.checkDone()) {
                return;
            }
            for (final PathPos next : this.getNeighbors(this.current)) {
                final float newCost = this.costMap.get(this.current) + this.getCost(this.current, next);
                if (this.costMap.containsKey(next) && this.costMap.get(next) <= newCost) {
                    continue;
                }
                this.costMap.put(next, newCost);
                this.prevPosMap.put(next, this.current);
                this.queue.add(next, newCost + this.getHeuristic(next));
            }
        }
        int val = this.iterations;
        val += i;
        this.iterations = val;
    }

    protected boolean checkDone() {
        this.done = this.goal.equals(this.current);
        return this.done;
    }

    private boolean checkFailed() {
        this.failed = this.queue.isEmpty() || this.iterations >= this.thinkSpeed * this.thinkTime;
        return this.failed;
    }

    private ArrayList<PathPos> getNeighbors(final PathPos pos) {
        ArrayList<PathPos> neighbors = new ArrayList<PathPos>();
        if (Math.abs(this.start.getX() - pos.getX()) > 256 || Math.abs(this.start.getZ() - pos.getZ()) > 256) {
            return neighbors;
        }
        BlockPos north = pos.offset(EnumFacing.NORTH);
        BlockPos east = pos.offset(EnumFacing.EAST);
        BlockPos south = pos.offset(EnumFacing.SOUTH);
        BlockPos west = pos.offset(EnumFacing.WEST);
        BlockPos northEast = north.offset(EnumFacing.EAST);
        BlockPos southEast = south.offset(EnumFacing.EAST);
        BlockPos southWest = south.offset(EnumFacing.WEST);
        BlockPos northWest = north.offset(EnumFacing.WEST);
        BlockPos up = pos.offset(EnumFacing.UP);;
        BlockPos down = pos.offset(EnumFacing.DOWN);
        boolean flying = this.canFlyAt(pos);
        boolean onGround = this.canBeSolid(down);
        if (flying || onGround || pos.isJumping() || this.canMoveSidewaysInMidairAt(pos) || this.canClimbUpAt(pos.offset(EnumFacing.DOWN))) {
            if (this.checkHorizontalMovement(pos, north)) {
                neighbors.add(new PathPos(north));
            }
            if (this.checkHorizontalMovement(pos, east)) {
                neighbors.add(new PathPos(east));
            }
            if (this.checkHorizontalMovement(pos, south)) {
                neighbors.add(new PathPos(south));
            }
            if (this.checkHorizontalMovement(pos, west)) {
                neighbors.add(new PathPos(west));
            }
            if (this.checkDiagonalMovement(pos, EnumFacing.NORTH, EnumFacing.EAST)) {
                neighbors.add(new PathPos(northEast));
            }
            if (this.checkDiagonalMovement(pos, EnumFacing.SOUTH, EnumFacing.EAST)) {
                neighbors.add(new PathPos(southEast));
            }
            if (this.checkDiagonalMovement(pos, EnumFacing.SOUTH, EnumFacing.WEST)) {
                neighbors.add(new PathPos(southWest));
            }
            if (this.checkDiagonalMovement(pos, EnumFacing.NORTH, EnumFacing.WEST)) {
                neighbors.add(new PathPos(northWest));
            }
        }
        if (pos.getY() < 256 && this.canGoThrough(up.offset(EnumFacing.UP)) && (flying || onGround || this.canClimbUpAt(pos)) && (flying || this.canClimbUpAt(pos)|| this.goal.equals(up) || this.canSafelyStandOn(north)|| this.canSafelyStandOn(east)|| this.canSafelyStandOn(south) || this.canSafelyStandOn(west))) {
            neighbors.add(new PathPos(up, onGround));
        }
        if (pos.getY() > 0 && this.canGoThrough(down) && (flying || this.canFallBelow(pos))) {
            neighbors.add(new PathPos(down));
        }
        return neighbors;
    }

    private boolean checkHorizontalMovement(final BlockPos current, final BlockPos next) {
        return this.isPassable(next)&& (this.canFlyAt(current) || this.canGoThrough(next.offset(EnumFacing.DOWN)) || this.canSafelyStandOn(next.offset(EnumFacing.DOWN)));
    }

    private boolean checkDiagonalMovement(final BlockPos current, final EnumFacing direction1, final EnumFacing direction2) {
        BlockPos horizontal1 = current.offset(direction1);
        BlockPos horizontal2 = current.offset(direction2);
        BlockPos next = horizontal1.offset(direction2);
        return this.isPassable(horizontal1) && this.isPassable(horizontal2)&& this.checkHorizontalMovement(current, next);
    }

    protected boolean isPassable(final BlockPos pos) {
        return this.canGoThrough(pos) && this.canGoThrough(pos.offset(EnumFacing.UP))&& this.canGoAbove(pos.offset(EnumFacing.DOWN));
    }

    protected boolean canBeSolid(final BlockPos pos) {
        Material material = getMaterial(pos);
        Block block = getBlock(pos);
        return (material.blocksMovement() && !(block instanceof BlockSign)) || block instanceof BlockLadder || material == Material.water || material == Material.lava;
    }

    private boolean canGoThrough(final BlockPos pos) {
        if (!this.mc.theWorld.isBlockLoaded(pos, false)) {
            return false;
        }
        Material material = getMaterial(pos);
        Block block = getBlock(pos);
        return (!material.blocksMovement() || block instanceof BlockSign) && !(block instanceof BlockTripWire) && !(block instanceof BlockPressurePlate) && (this.invulnerable|| (material != Material.lava && material != Material.fire));
    }

    private boolean canGoAbove(final BlockPos pos) {
        Block block = getBlock(pos);
        return !(block instanceof BlockFence) && !(block instanceof BlockWall) && !(block instanceof BlockFenceGate);
    }

    private boolean canSafelyStandOn(final BlockPos pos) {
        Material material = getMaterial(pos);
        return this.canBeSolid(pos) && (this.invulnerable|| (material != Material.cactus && material != Material.lava));
    }

    private boolean canFallBelow(final PathPos pos) {
        BlockPos down2 = pos.offset(EnumFacing.DOWN, 2);
        if (this.canGoThrough(down2)) {
            return true;
        }
        if (!this.canSafelyStandOn(down2)) {
            return false;
        }
        if (this.immuneToFallDamage && this.fallingAllowed) {
            return true;
        }
        if (getBlock(down2) instanceof BlockSlime && this.fallingAllowed) {
            return true;
        }
        BlockPos prevPos = pos;
        for (int i = 0; i <= (this.fallingAllowed ? 3 : 1); ++i) {
            if (prevPos == null) {
                return true;
            }
            if (!pos.offset(EnumFacing.UP, i).equals(prevPos)) {
                return true;
            }
            Block prevBlock = getBlock(prevPos);
            if (prevBlock instanceof BlockLiquid || prevBlock instanceof BlockLadder || prevBlock instanceof BlockVine || prevBlock instanceof BlockWeb) {
                return true;
            }
            prevPos = this.prevPosMap.get(prevPos);
        }
        return false;
    }

    private boolean canFlyAt(final BlockPos pos) {
        return this.flying;
    }

    private boolean canClimbUpAt(final BlockPos pos) {
        Block block = getBlock(pos);
        if (!(block instanceof BlockLadder) && !(block instanceof BlockVine)) {
            return false;
        }
        BlockPos up = pos.offset(EnumFacing.UP);
        return this.canBeSolid(pos.offset(EnumFacing.NORTH)) || this.canBeSolid(pos.offset(EnumFacing.EAST))|| this.canBeSolid(pos.offset(EnumFacing.SOUTH)) || this.canBeSolid(pos.offset(EnumFacing.WEST)) || this.canBeSolid(up.offset(EnumFacing.NORTH)) || this.canBeSolid(up.offset(EnumFacing.EAST)) || this.canBeSolid(up.offset(EnumFacing.SOUTH))|| this.canBeSolid(up.offset(EnumFacing.WEST));
    }

    private boolean canMoveSidewaysInMidairAt(final BlockPos pos) {
        Block blockFeet = getBlock(pos);
        if (blockFeet instanceof BlockLiquid || blockFeet instanceof BlockLadder || blockFeet instanceof BlockVine || blockFeet instanceof BlockWeb) {
            return true;
        }
        Block blockHead = getBlock(pos.offset(EnumFacing.UP));
        return blockHead instanceof BlockLiquid || blockHead instanceof BlockWeb;
    }

    private float getCost(final BlockPos current, final BlockPos next) {
        float cost = 1.0f;
        if (current.getX() != next.getX() && current.getZ() != next.getZ()) {
            cost *= 1.4142135f;
        }
        Material nextMaterial = getMaterial(next);
        if (nextMaterial == Material.lava) {
            cost *= 4.5395155f;
        }
        if (!this.canFlyAt(next) && getBlock(next.offset(EnumFacing.DOWN)) instanceof BlockSoulSand) {
            cost *= 2.5f;
        }
        return cost;
    }

    private float getHeuristic(final BlockPos pos) {
        float dx = Math.abs(pos.getX() - this.goal.getX());
        float dy = Math.abs(pos.getY() - this.goal.getY());
        float dz = Math.abs(pos.getZ() - this.goal.getZ());
        return 1.001f * (dx + dy + dz - 0.58578646f * Math.min(dx, dz));
    }

    public BlockPos getGoal() {
        return this.goal;
    }

    public boolean isDone() {
        return this.done;
    }

    public boolean isFailed() {
        return this.failed;
    }

    public ArrayList<PathPos> formatPath() {
        if (!this.done && !this.failed) {
            throw new IllegalStateException("No path found!");
        }
        if (!this.path.isEmpty()) {
            throw new IllegalStateException("Path was already formatted!");
        }
        PathPos pos;
        if (!this.failed) {
            pos = this.current;
        }
        else {
            pos = this.start;
            for (PathPos next : this.prevPosMap.keySet()) {
                if (this.getHeuristic(next) < this.getHeuristic(pos)) {
                    pos = next;
                }
            }
        }
        while (pos != null) {
            this.path.add(pos);
            pos = this.prevPosMap.get(pos);
        }
        Collections.reverse(this.path);
        return this.path;
    }

    public boolean isPathStillValid(final int index) {
        if (this.path.isEmpty()) {
            throw new IllegalStateException("Path is not formatted!");
        }
        for (int i = Math.max(1, index); i < this.path.size(); ++i) {
            if (!this.getNeighbors(this.path.get(i - 1)).contains(this.path.get(i))) {
                return false;
            }
        }
        return true;
    }

    public APathProcessor getProcessor() {
        if (this.flying) {
            return new TPPathProcessor(this.path, this.creativeFlying);
        }
        return new WalkPathProcessor(this.path);
    }

    public APathProcessor getInfiniteAuraProcessor(final EntityLivingBase e) {
        return new InfiniteauraPathProcessor(this.path, this.creativeFlying, e);
    }

    public APathProcessor getProcessor(final String type) {
        if (type.equalsIgnoreCase("walk")) {
            return new WalkPathProcessor(this.path);
        }
        if (type.equalsIgnoreCase("fly")) {
            return new FlyPathProcessor(this.path, this.creativeFlying);
        }
        if (type.equalsIgnoreCase("tpnback") || type.equalsIgnoreCase("tpback")) {
            return new TPnBACKPathProcessor(this.path, this.creativeFlying);
        }
        return new TPPathProcessor(this.path, true);
    }

}
