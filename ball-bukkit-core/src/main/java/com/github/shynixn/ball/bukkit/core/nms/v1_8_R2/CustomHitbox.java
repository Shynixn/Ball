package com.github.shynixn.ball.bukkit.core.nms.v1_8_R2;

import com.github.shynixn.ball.api.bukkit.business.entity.BukkitBall;
import com.github.shynixn.ball.api.bukkit.business.event.BallMoveEvent;
import com.github.shynixn.ball.api.bukkit.business.event.BallWallCollideEvent;
import com.github.shynixn.ball.api.persistence.BounceObject;
import com.github.shynixn.ball.bukkit.core.logic.business.helper.ReflectionUtils;
import net.minecraft.server.v1_8_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
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

    private final BukkitBall ball;

    private int knockBackBumper;
    private Vector reduceVector;
    private Vector originVector;
    private int times;

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
            this.getSpigotEntity().setVelocity(velocity);
            final Vector normalized = velocity.clone().normalize();
            this.originVector = velocity.clone();
            this.reduceVector = new Vector(normalized.getX() / this.times
                    , 0.0784 * this.ball.getMeta().getModifiers().getGravityModifier()
                    , normalized.getZ() / this.times);
        } catch (IllegalArgumentException ignored) {

        }
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
                }
                else {
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

    @Override
    public void move(double d0, double d1, double d2) {
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

        this.spigotTimings(true);
        if (this.knockBackBumper > 0) {
            this.knockBackBumper--;
        }

        if (this.noclip) {
            this.a(this.getBoundingBox().c(d0, d1, d2));
            this.recalcPosition();
        } else {
            try {
                this.checkBlockCollisions();
            } catch (Throwable var84) {
                CrashReport crashreport = CrashReport.a(var84, "Checking entity block collision");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being checked for collision");
                this.appendEntityCrashDetails(crashreportsystemdetails);
                throw new ReportedException(crashreport);
            }

            if (d0 == 0.0D && d1 == 0.0D && d2 == 0.0D && this.vehicle == null && this.passenger == null) {
                return;
            }

            this.world.methodProfiler.a("move");
            double d3 = this.locX;
            double d4 = this.locY;
            double d5 = this.locZ;
            if (this.H) {
                this.H = false;
                d0 *= 0.25D;
                d1 *= 0.05000000074505806D;
                d2 *= 0.25D;
                this.motX = 0.0D;
                this.motY = 0.0D;
                this.motZ = 0.0D;
            }

            double d6 = d0;
            double d7 = d1;
            double d8 = d2;
            boolean flag = this.onGround && this.isSneaking() && false;
            if (flag) {
                double d9;
                for(d9 = 0.05D; d0 != 0.0D && this.world.getCubes(this, this.getBoundingBox().c(d0, -1.0D, 0.0D)).isEmpty(); d6 = d0) {
                    if (d0 < d9 && d0 >= -d9) {
                        d0 = 0.0D;
                    } else if (d0 > 0.0D) {
                        d0 -= d9;
                    } else {
                        d0 += d9;
                    }
                }

                for(; d2 != 0.0D && this.world.getCubes(this, this.getBoundingBox().c(0.0D, -1.0D, d2)).isEmpty(); d8 = d2) {
                    if (d2 < d9 && d2 >= -d9) {
                        d2 = 0.0D;
                    } else if (d2 > 0.0D) {
                        d2 -= d9;
                    } else {
                        d2 += d9;
                    }
                }

                for(; d0 != 0.0D && d2 != 0.0D && this.world.getCubes(this, this.getBoundingBox().c(d0, -1.0D, d2)).isEmpty(); d8 = d2) {
                    if (d0 < d9 && d0 >= -d9) {
                        d0 = 0.0D;
                    } else if (d0 > 0.0D) {
                        d0 -= d9;
                    } else {
                        d0 += d9;
                    }

                    d6 = d0;
                    if (d2 < d9 && d2 >= -d9) {
                        d2 = 0.0D;
                    } else if (d2 > 0.0D) {
                        d2 -= d9;
                    } else {
                        d2 += d9;
                    }
                }
            }

            List list = this.world.getCubes(this, this.getBoundingBox().a(d0, d1, d2));
            AxisAlignedBB axisalignedbb = this.getBoundingBox();

            AxisAlignedBB axisalignedbb1;
            for(Iterator iterator = list.iterator(); iterator.hasNext(); d1 = axisalignedbb1.b(this.getBoundingBox(), d1)) {
                axisalignedbb1 = (AxisAlignedBB)iterator.next();
            }

            this.a(this.getBoundingBox().c(0.0D, d1, 0.0D));
            boolean flag1 = this.onGround || d7 != d1 && d7 < 0.0D;

            Iterator iterator1;
            AxisAlignedBB axisalignedbb2;
            for(iterator1 = list.iterator(); iterator1.hasNext(); d0 = axisalignedbb2.a(this.getBoundingBox(), d0)) {
                axisalignedbb2 = (AxisAlignedBB)iterator1.next();
            }

            this.a(this.getBoundingBox().c(d0, 0.0D, 0.0D));

            for(iterator1 = list.iterator(); iterator1.hasNext(); d2 = axisalignedbb2.c(this.getBoundingBox(), d2)) {
                axisalignedbb2 = (AxisAlignedBB)iterator1.next();
            }

            this.a(this.getBoundingBox().c(0.0D, 0.0D, d2));
            if (this.S > 0.0F && flag1 && (d6 != d0 || d8 != d2)) {
                double d10 = d0;
                double d11 = d1;
                double d12 = d2;
                AxisAlignedBB axisalignedbb3 = this.getBoundingBox();
                this.a(axisalignedbb);
                d1 = (double)this.S;
                List list1 = this.world.getCubes(this, this.getBoundingBox().a(d6, d1, d8));
                AxisAlignedBB axisalignedbb4 = this.getBoundingBox();
                AxisAlignedBB axisalignedbb5 = axisalignedbb4.a(d6, 0.0D, d8);
                double d13 = d1;

                AxisAlignedBB axisalignedbb6;
                for(Iterator iterator2 = list1.iterator(); iterator2.hasNext(); d13 = axisalignedbb6.b(axisalignedbb5, d13)) {
                    axisalignedbb6 = (AxisAlignedBB)iterator2.next();
                }

                axisalignedbb4 = axisalignedbb4.c(0.0D, d13, 0.0D);
                double d14 = d6;

                AxisAlignedBB axisalignedbb7;
                for(Iterator iterator3 = list1.iterator(); iterator3.hasNext(); d14 = axisalignedbb7.a(axisalignedbb4, d14)) {
                    axisalignedbb7 = (AxisAlignedBB)iterator3.next();
                }

                axisalignedbb4 = axisalignedbb4.c(d14, 0.0D, 0.0D);
                double d15 = d8;

                AxisAlignedBB axisalignedbb8;
                for(Iterator iterator4 = list1.iterator(); iterator4.hasNext(); d15 = axisalignedbb8.c(axisalignedbb4, d15)) {
                    axisalignedbb8 = (AxisAlignedBB)iterator4.next();
                }

                axisalignedbb4 = axisalignedbb4.c(0.0D, 0.0D, d15);
                AxisAlignedBB axisalignedbb9 = this.getBoundingBox();
                double d16 = d1;

                AxisAlignedBB axisalignedbb10;
                for(Iterator iterator5 = list1.iterator(); iterator5.hasNext(); d16 = axisalignedbb10.b(axisalignedbb9, d16)) {
                    axisalignedbb10 = (AxisAlignedBB)iterator5.next();
                }

                axisalignedbb9 = axisalignedbb9.c(0.0D, d16, 0.0D);
                double d17 = d6;

                AxisAlignedBB axisalignedbb11;
                for(Iterator iterator6 = list1.iterator(); iterator6.hasNext(); d17 = axisalignedbb11.a(axisalignedbb9, d17)) {
                    axisalignedbb11 = (AxisAlignedBB)iterator6.next();
                }

                axisalignedbb9 = axisalignedbb9.c(d17, 0.0D, 0.0D);
                double d18 = d8;

                AxisAlignedBB axisalignedbb12;
                for(Iterator iterator7 = list1.iterator(); iterator7.hasNext(); d18 = axisalignedbb12.c(axisalignedbb9, d18)) {
                    axisalignedbb12 = (AxisAlignedBB)iterator7.next();
                }

                axisalignedbb9 = axisalignedbb9.c(0.0D, 0.0D, d18);
                double d19 = d14 * d14 + d15 * d15;
                double d20 = d17 * d17 + d18 * d18;
                if (d19 > d20) {
                    d0 = d14;
                    d2 = d15;
                    d1 = -d13;
                    this.a(axisalignedbb4);
                } else {
                    d0 = d17;
                    d2 = d18;
                    d1 = -d16;
                    this.a(axisalignedbb9);
                }

                AxisAlignedBB axisalignedbb13;
                for(Iterator iterator8 = list1.iterator(); iterator8.hasNext(); d1 = axisalignedbb13.b(this.getBoundingBox(), d1)) {
                    axisalignedbb13 = (AxisAlignedBB)iterator8.next();
                }

                this.a(this.getBoundingBox().c(0.0D, d1, 0.0D));
                if (d10 * d10 + d12 * d12 >= d0 * d0 + d2 * d2) {
                    d0 = d10;
                    d1 = d11;
                    d2 = d12;
                    this.a(axisalignedbb3);
                }
            }

            this.world.methodProfiler.b();
            this.world.methodProfiler.a("rest");
            this.recalcPosition();
            this.positionChanged = d6 != d0 || d8 != d2;
            this.E = d7 != d1;
            this.onGround = this.E && d7 < 0.0D;
            this.F = this.positionChanged || this.E;
            int i = MathHelper.floor(this.locX);
            int j = MathHelper.floor(this.locY - 0.20000000298023224D);
            int k = MathHelper.floor(this.locZ);
            BlockPosition blockposition = new BlockPosition(i, j, k);
            net.minecraft.server.v1_8_R2.Block block = this.world.getType(blockposition).getBlock();
            if (block.getMaterial() == Material.AIR) {
                net.minecraft.server.v1_8_R2.Block block1 = this.world.getType(blockposition.down()).getBlock();
                if (block1 instanceof BlockFence || block1 instanceof BlockCobbleWall || block1 instanceof BlockFenceGate) {
                    block = block1;
                    blockposition = blockposition.down();
                }
            }

            this.a(d1, this.onGround, block, blockposition);
            if (d6 != d0) {
                this.motX = 0.0D;
            }

            if (d8 != d2) {
                this.motZ = 0.0D;
            }

            if (d7 != d1) {
                block.a(this.world, this);
            }

            try {
                if (this.positionChanged) {
                    org.bukkit.block.Block var81 = this.world.getWorld().getBlockAt(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ));
                    if (d6 > d0) {
                        var81 = var81.getRelative(BlockFace.EAST);
                        final Vector n = new Vector(-1, 0, 0);
                        this.applyKnockBack(starter, n, var81, BlockFace.EAST);
                    } else if (d6 < d0) {
                        var81 = var81.getRelative(BlockFace.WEST);
                        final Vector n = new Vector(1, 0, 0);
                        this.applyKnockBack(starter, n,var81, (BlockFace.WEST));
                    } else if (d8 > d2) {
                        var81 = var81.getRelative(BlockFace.SOUTH);
                        final Vector n = new Vector(0, 0, -1);
                        this.applyKnockBack(starter, n,var81, BlockFace.SOUTH);

                    } else if (d8 < d2) {
                        var81 = var81.getRelative(BlockFace.NORTH);
                        final Vector n = new Vector(0, 0, 1);
                        this.applyKnockBack(starter, n, var81,BlockFace.NORTH);
                    }
                }
            } catch (final Exception ex) {
                Bukkit.getLogger().log(Level.WARNING, "Critical exception.", ex);
            }
        }

        this.spigotTimings(false);
    }

    private void spigotTimings(boolean started) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName("org.bukkit.craftbukkit.v1_8_R2.SpigotTimings");
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

    private void recalcPosition() {
        final AxisAlignedBB axisalignedbb = this.getBoundingBox();
        this.locX = (axisalignedbb.a + axisalignedbb.d) / 2.0D;
        this.locY = axisalignedbb.b + this.ball.getMeta().getHitBoxRelocationDistance();
        this.locZ = (axisalignedbb.c + axisalignedbb.f) / 2.0D;
    }
}
