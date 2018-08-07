package com.github.shynixn.ball.bukkit.core.nms.v1_13_R1;

import com.github.shynixn.ball.api.bukkit.business.entity.BukkitBall;
import com.github.shynixn.ball.api.bukkit.business.event.BallMoveEvent;
import com.github.shynixn.ball.api.bukkit.business.event.BallSpinEvent;
import com.github.shynixn.ball.api.bukkit.business.event.BallWallCollideEvent;
import com.github.shynixn.ball.api.persistence.BounceObject;
import com.github.shynixn.ball.bukkit.core.logic.business.helper.ReflectionUtils;
import net.minecraft.server.v1_13_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Rabbit hitbox implementation for minecraft 1.12.0-1.12.2.
 * <p>
 * Version 1.1
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public final class CustomHitbox extends EntityArmorStand {
    private static final org.bukkit.Material[] excludedRelativeItems = new org.bukkit.Material[]{
            org.bukkit.Material.FENCE,
            org.bukkit.Material.IRON_FENCE,
            org.bukkit.Material.THIN_GLASS,
            org.bukkit.Material.FENCE_GATE,
            org.bukkit.Material.NETHER_FENCE,
            org.bukkit.Material.COBBLE_WALL,
            org.bukkit.Material.STAINED_GLASS_PANE,
            org.bukkit.Material.SPRUCE_FENCE_GATE,
            org.bukkit.Material.BIRCH_FENCE_GATE,
            org.bukkit.Material.JUNGLE_FENCE_GATE,
            org.bukkit.Material.DARK_OAK_FENCE_GATE,
            org.bukkit.Material.ACACIA_FENCE_GATE,
            org.bukkit.Material.SPRUCE_FENCE,
            org.bukkit.Material.BIRCH_FENCE,
            org.bukkit.Material.JUNGLE_FENCE,
            org.bukkit.Material.DARK_OAK_FENCE,
            org.bukkit.Material.ACACIA_FENCE};

    private final BukkitBall ball;

    private int knockBackBumper;
    private Vector reduceVector;
    private Vector originVector;
    private int times;
    private float spinForce;
    private boolean clockwise;

    CustomHitbox(Location location, BukkitBall ball) {
        super(((CraftWorld) location.getWorld()).getHandle());
        this.ball = ball;
        final World mcWorld = ((CraftWorld) location.getWorld()).getHandle();
        this.setPosition(location.getX(), location.getY(), location.getZ());
        mcWorld.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
        final NBTTagCompound compound = new NBTTagCompound();
        compound.setBoolean("invulnerable", true);
        compound.setBoolean("Invisible", true);
        compound.setBoolean("PersistenceRequired", true);
        compound.setBoolean("NoBasePlate", true);
        this.a(compound);
        this.getSpigotEntity().setCustomName("ResourceBallsPlugin");
        this.getSpigotEntity().setCustomNameVisible(false);

    }

    ArmorStand getSpigotEntity() {
        return (ArmorStand) this.getBukkitEntity();
    }

    void setVelocity(Vector velocity) {
        try {
            this.times = (int) (50 * this.ball.getMeta().getModifiers().getRollingDistanceModifier());
            this.spinForce = 0F;
            this.getSpigotEntity().setVelocity(velocity);
            final Vector normalized = velocity.clone().normalize();
            this.originVector = velocity.clone();
            this.reduceVector = new Vector(normalized.getX() / this.times
                    , 0.0784 * this.ball.getMeta().getModifiers().getGravityModifier()
                    , normalized.getZ() / this.times);
        } catch (IllegalArgumentException ignored) {

        }
    }
    
    void setMagnusForce(Vector facing, Vector direction) {
        if (this.times <= 0) {
            return;
        }
        
        double angle = getAngle(direction, facing);
        final float modifier = 0.05F;
    
        if (angle > 0.3F && angle < 10F) {
            clockwise = true;
        }
        else if (angle < -0.3F && angle > -10F) {
            clockwise = false;
        } else {
            return;
        }
    
        BallSpinEvent event = new BallSpinEvent(this.ball, angle, modifier);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            this.spinForce = event.getSpinModifier();
        }
    }
    
    private void applyMagnusForce() {
        if (this.times <= 0) {
            this.spinForce = 0F;
        }
        if (spinForce == 0F) {
            return;
        }
        
        double x, z;
        final Vector originUnit = this.originVector.clone().normalize();
        if (clockwise) {
            x = -originUnit.getZ();
            z = originUnit.getX();
        } else {
            x = originUnit.getZ();
            z = -originUnit.getX();
        }
        
        Vector newVector = originVector.add(new Vector(x, 0, z).multiply(spinForce));
        originVector = newVector.multiply(this.originVector.length() / newVector.length());
    }
    
    private void applyKnockBack(Vector starter, Vector n, org.bukkit.block.Block block, BlockFace blockFace) {
        if (block == null || block.getType() == org.bukkit.Material.AIR) {
            return;
        }

        if (this.knockBackBumper <= 0) {
            final Optional<BounceObject> optBounce = this.ball.getMeta().getBounceObjectController().getBounceObjectFromBlock(block);
            if (optBounce.isPresent() || this.ball.getMeta().isAlwaysBounceBack()) {
                Vector r = starter.clone().subtract(n.multiply(2 * starter.dot(n))).multiply(0.75);
                if (optBounce.isPresent()) {
                    r = r.multiply(optBounce.get().getBounceModifier());
                } else {
                    r = r.multiply(this.ball.getMeta().getModifiers().getBounceModifier());
                }
                final BallWallCollideEvent event = new BallWallCollideEvent(this.ball, block, blockFace, r.clone(), starter.clone());
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    this.setVelocity(r);
                    ((CustomDesign) this.ball).revertAnimation = !((CustomDesign) this.ball).revertAnimation;
                    this.knockBackBumper = 5;
                }
            }
        }
    }
    
    /**
     * Calculates the angle between two vectors in two dimension (XZ Plane) <br>
     * If basis vector is clock-wise to against vector, the angle is negative.
     * @param basis The basis vector
     * @param against The vector which the angle is calculated against
     * @return The angle in the range of -180 to 180 degrees
     */
    private double getAngle(Vector basis, Vector against) {
        final Vector b = basis, a = against;
        double dot = b.getX() * a.getX() + b.getZ() * a.getZ();
        double det = b.getX() * a.getZ() - b.getZ() * a.getX();
        
        return Math.atan2(det, dot);
    }
    
    @Override
    public void move(EnumMoveType enummovetype, double d0, double d1, double d2) {
        final BallMoveEvent cevent = new BallMoveEvent(this.ball);
        Bukkit.getPluginManager().callEvent(cevent);
        if (cevent.isCancelled()) {
            return;
        }
        final Vector starter;
        if ((this.times > 0 || !this.onGround) && this.originVector != null) {
            this.originVector = this.originVector.subtract(this.reduceVector);
            if (this.times > 0) {
                this.motX = this.originVector.getX();
                this.motZ = this.originVector.getZ();
            }
            this.motY = this.originVector.getY();
            this.times--;
            starter = new Vector(this.motX, this.motY, this.motZ);
        } else {
            starter = new Vector(d0, d1, d2);
        }
        
        this.applyMagnusForce();
        this.spigotTimings(true);

        if (this.knockBackBumper > 0) {
            this.knockBackBumper--;
        }

        if (this.noclip) {
            this.a(this.getBoundingBox().d(d0, d1, d2));
            this.recalcPosition();
        } else {
            try {
                this.checkBlockCollisions();
            } catch (Throwable var49) {
                CrashReport crashreport = CrashReport.a(var49, "Checking entity block collision");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being checked for collision");
                this.appendEntityCrashDetails(crashreportsystemdetails);
                throw new ReportedException(crashreport);
            }

            if (d0 == 0.0D && d1 == 0.0D && d2 == 0.0D && this.isVehicle() && this.isPassenger()) {
                return;
            }

            this.world.methodProfiler.a("move");
            double d4 = this.locX;
            double d5 = this.locY;
            double d6 = this.locZ;
            if (this.F) {
                this.F = false;
                d0 *= 0.25D;
                d1 *= 0.05000000074505806D;
                d2 *= 0.25D;
                this.motX = 0.0D;
                this.motY = 0.0D;
                this.motZ = 0.0D;
            }

            double d7 = d0;
            double d8 = d1;
            double d9 = d2;

            AxisAlignedBB axisalignedbb = this.getBoundingBox();
            if (d0 != 0.0D || d1 != 0.0D || d2 != 0.0D) {
                VoxelShape voxelshape = this.world.a(this, this.getBoundingBox(), d0, d1, d2);
                if (d1 != 0.0D) {
                    d1 = VoxelShapes.a(EnumDirection.EnumAxis.Y, this.getBoundingBox(), voxelshape, d1);
                    this.a(this.getBoundingBox().d(0.0D, d1, 0.0D));
                }

                if (d0 != 0.0D) {
                    d0 = VoxelShapes.a(EnumDirection.EnumAxis.X, this.getBoundingBox(), voxelshape, d0);
                    if (d0 != 0.0D) {
                        this.a(this.getBoundingBox().d(d0, 0.0D, 0.0D));
                    }
                }

                if (d2 != 0.0D) {
                    d2 = VoxelShapes.a(EnumDirection.EnumAxis.Z, this.getBoundingBox(), voxelshape, d2);
                    if (d2 != 0.0D) {
                        this.a(this.getBoundingBox().d(0.0D, 0.0D, d2));
                    }
                }
            }

            boolean flag = this.onGround || d1 != d1 && d1 < 0.0D;
            double d11;
            if (this.Q > 0.0F && flag && (d7 != d0 || d9 != d2)) {
                double d12 = d0;
                double d13 = d1;
                double d14 = d2;
                AxisAlignedBB axisalignedbb1 = this.getBoundingBox();
                this.a(axisalignedbb);
                d0 = d7;
                d1 = (double) this.Q;
                d2 = d9;
                if (d7 != 0.0D || d1 != 0.0D || d9 != 0.0D) {
                    VoxelShape voxelshape1 = this.world.a(this, this.getBoundingBox(), d7, d1, d9);
                    AxisAlignedBB axisalignedbb2 = this.getBoundingBox();
                    AxisAlignedBB axisalignedbb3 = axisalignedbb2.b(d7, 0.0D, d9);
                    d11 = VoxelShapes.a(EnumDirection.EnumAxis.Y, axisalignedbb3, voxelshape1, d1);
                    if (d11 != 0.0D) {
                        axisalignedbb2 = axisalignedbb2.d(0.0D, d11, 0.0D);
                    }

                    double d15 = VoxelShapes.a(EnumDirection.EnumAxis.X, axisalignedbb2, voxelshape1, d7);
                    if (d15 != 0.0D) {
                        axisalignedbb2 = axisalignedbb2.d(d15, 0.0D, 0.0D);
                    }

                    double d16 = VoxelShapes.a(EnumDirection.EnumAxis.Z, axisalignedbb2, voxelshape1, d9);
                    if (d16 != 0.0D) {
                        axisalignedbb2 = axisalignedbb2.d(0.0D, 0.0D, d16);
                    }

                    AxisAlignedBB axisalignedbb4 = this.getBoundingBox();
                    double d17 = VoxelShapes.a(EnumDirection.EnumAxis.Y, axisalignedbb4, voxelshape1, d1);
                    if (d17 != 0.0D) {
                        axisalignedbb4 = axisalignedbb4.d(0.0D, d17, 0.0D);
                    }

                    double d18 = VoxelShapes.a(EnumDirection.EnumAxis.X, axisalignedbb4, voxelshape1, d7);
                    if (d18 != 0.0D) {
                        axisalignedbb4 = axisalignedbb4.d(d18, 0.0D, 0.0D);
                    }

                    double d19 = VoxelShapes.a(EnumDirection.EnumAxis.Z, axisalignedbb4, voxelshape1, d9);
                    if (d19 != 0.0D) {
                        axisalignedbb4 = axisalignedbb4.d(0.0D, 0.0D, d19);
                    }

                    double d20 = d15 * d15 + d16 * d16;
                    double d21 = d18 * d18 + d19 * d19;
                    if (d20 > d21) {
                        d0 = d15;
                        d2 = d16;
                        d1 = -d11;
                        this.a(axisalignedbb2);
                    } else {
                        d0 = d18;
                        d2 = d19;
                        d1 = -d17;
                        this.a(axisalignedbb4);
                    }

                    d1 = VoxelShapes.a(EnumDirection.EnumAxis.Y, this.getBoundingBox(), voxelshape1, d1);
                    if (d1 != 0.0D) {
                        this.a(this.getBoundingBox().d(0.0D, d1, 0.0D));
                    }
                }

                if (d12 * d12 + d14 * d14 >= d0 * d0 + d2 * d2) {
                    d0 = d12;
                    d1 = d13;
                    d2 = d14;
                    this.a(axisalignedbb1);
                }
            }

            this.world.methodProfiler.e();
            this.world.methodProfiler.a("rest");
            this.recalcPosition();
            this.positionChanged = d7 != d0 || d9 != d2;
            this.C = d1 != d8;
            this.onGround = this.C && d8 < 0.0D;
            this.D = this.positionChanged || this.C;
            int k = MathHelper.floor(this.locX);
            int l = MathHelper.floor(this.locY - 0.20000000298023224D);
            int i1 = MathHelper.floor(this.locZ);
            BlockPosition blockposition = new BlockPosition(k, l, i1);
            IBlockData iblockdata = this.world.getType(blockposition);
            if (iblockdata.isAir()) {
                BlockPosition blockposition1 = blockposition.down();
                IBlockData iblockdata1 = this.world.getType(blockposition1);
                net.minecraft.server.v1_13_R1.Block block = iblockdata1.getBlock();
                if (block instanceof BlockFence || block instanceof BlockCobbleWall || block instanceof BlockFenceGate) {
                    iblockdata = iblockdata1;
                    blockposition = blockposition1;
                }
            }

            this.a(d1, this.onGround, iblockdata, blockposition);
            if (d7 != d0) {
                this.motX = 0.0D;
            }

            if (d9 != d2) {
                this.motZ = 0.0D;
            }

            net.minecraft.server.v1_13_R1.Block block1 = iblockdata.getBlock();
            if (d8 != d1) {
                block1.a(this.world, this);
            }

            try {
                if (this.positionChanged) {
                    org.bukkit.block.Block var81 = this.world.getWorld().getBlockAt(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ));
                    
                    if (d7 > d0) {
                        if (this.isValidKnockBackBlock(var81)) {
                            var81 = var81.getRelative(BlockFace.EAST);
                        }

                        final Vector n = new Vector(-1, 0, 0);
                        this.applyKnockBack(starter, n, var81, BlockFace.EAST);
                    } else if (d7 < d0) {
                        if (this.isValidKnockBackBlock(var81)) {
                            var81 = var81.getRelative(BlockFace.WEST);
                        }

                        final Vector n = new Vector(1, 0, 0);
                        this.applyKnockBack(starter, n, var81, (BlockFace.WEST));
                    } else if (d9 > d2) {
                        if (this.isValidKnockBackBlock(var81)) {
                            var81 = var81.getRelative(BlockFace.SOUTH);
                        }

                        final Vector n = new Vector(0, 0, -1);
                        this.applyKnockBack(starter, n, var81, BlockFace.SOUTH);
                    } else if (d9 < d2) {
                        if (this.isValidKnockBackBlock(var81)) {
                            var81 = var81.getRelative(BlockFace.NORTH);
                        }

                        final Vector n = new Vector(0, 0, 1);
                        this.applyKnockBack(starter, n, var81, BlockFace.NORTH);
                    }
                }
            } catch (final Exception ex) {
                Bukkit.getLogger().log(Level.WARNING, "Critical exception.", ex);
            }
        }

        this.spigotTimings(false);
    }

    private boolean isValidKnockBackBlock(org.bukkit.block.Block block) {
        final org.bukkit.Material material = block.getType();
        for (final org.bukkit.Material i : excludedRelativeItems) {
            if (i == material) {
                return false;
            }
        }

        return true;
    }

    private void spigotTimings(boolean started) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName("org.bukkit.craftbukkit.v1_13_R1.SpigotTimings");
        } catch (final ClassNotFoundException ignored) {

        }
        if (clazz != null) {
            final Object moveTimer;
            try {
                moveTimer = ReflectionUtils.invokeFieldByClass(clazz, "entityMoveTimer");
                if (started) {
                    ReflectionUtils.invokeMethodByObject(moveTimer, "startTiming", new Class[]{}, new Object[]{});
                } else {
                    ReflectionUtils.invokeMethodByObject(moveTimer, "stopTiming", new Class[]{}, new Object[]{});
                }
            } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                Bukkit.getLogger().log(Level.WARNING, "Failed to invoke entityMoveTimer.", e);
            }
        }
    }

    @Override
    public void recalcPosition() {
        final AxisAlignedBB axisalignedbb = this.getBoundingBox();
        this.locX = (axisalignedbb.a + axisalignedbb.d) / 2.0D;
        this.locY = axisalignedbb.b + this.ball.getMeta().getHitBoxRelocationDistance();
        this.locZ = (axisalignedbb.c + axisalignedbb.f) / 2.0D;
    }
}
