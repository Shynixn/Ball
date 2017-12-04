package com.github.shynixn.balls.bukkit.core.nms.v1_12_R1;

import com.github.shynixn.balls.api.bukkit.event.BallDeathEvent;
import com.github.shynixn.balls.api.bukkit.event.BallInteractEvent;
import com.github.shynixn.balls.api.bukkit.event.BallThrowEvent;
import com.github.shynixn.balls.api.business.entity.Ball;
import com.github.shynixn.balls.api.persistence.BallMeta;
import com.github.shynixn.balls.bukkit.core.logic.business.helper.SkinHelper;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.logging.Level;

public final class CustomDesign extends EntityArmorStand implements Ball {
    private static final Random random = new Random();
    private static final int HITBOX_DETER = 2;

    private final boolean persistent;
    private final Entity owner;

    private final BallMeta ballMeta;
    private CustomHitbox hitBox;

    private boolean grabbed;
    private Entity interactionEntity;

    private int counter = 20;

    public CustomDesign(Location location, BallMeta ballMeta, boolean persistent, Entity owner) {
        super(((CraftWorld) location.getWorld()).getHandle());
        this.ballMeta = ballMeta;
        this.owner = owner;
        this.persistent = persistent;
        this.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    private boolean isPassengerNull() {
        return this.passengers == null || this.passengers.isEmpty();
    }

    private void teleportToHitBox() {
        final Location loc = this.hitBox.getSpigotEntity().getLocation();
        if (this.isSmall()) {
            this.setPositionRotation(loc.getX(), loc.getY() - 0.7, loc.getZ(), loc.getYaw(), loc.getPitch());
        } else {
            this.setPositionRotation(loc.getX(), loc.getY() - 1.0, loc.getZ(), loc.getYaw(), loc.getPitch());
        }
    }

    private void playRotationAnimation() {
        final double length = new Vector(this.hitBox.motX, this.hitBox.motY, this.hitBox.motZ).length();
        EulerAngle angle = null;
        final EulerAngle a = this.getSpigotEntity().getHeadPose();
        if (length > 1.0) {
            angle = new EulerAngle(a.getX() + 0.5, 0, 0);
        } else if (length > 0.1) {
            angle = new EulerAngle(a.getX() + 0.25, 0, 0);
        } else if (length > 0.08) {
            angle = new EulerAngle(a.getX() + 0.025, 0, 0);
        }
        if (angle != null) {
            this.getSpigotEntity().setHeadPose(angle);
        }
    }

    private ArmorStand getSpigotEntity() {
        return (ArmorStand) this.getArmorstand();
    }

    private void cancelEntityActionsFromEnvironment() {
        this.getBukkitEntity().setFireTicks(0);
    }

    private void checkForEntityMoveInteractions() {
        if (this.counter <= 0) {
            this.counter = 2;
            final Location hitBoxLocation = this.hitBox.getSpigotEntity().getLocation();
            for (final Entity entity : this.getSpigotEntity().getLocation().getChunk().getEntities()) {
                if (!entity.equals(this.hitBox.getSpigotEntity()) &&
                        !entity.equals(this.getSpigotEntity()) &&
                        entity.getLocation().distance(hitBoxLocation) < HITBOX_DETER) {
                    final BallInteractEvent event = new BallInteractEvent(this, entity);
                    Bukkit.getPluginManager().callEvent(event);
                    if (event.isCancelled())
                        return;
                    final Vector vector = hitBoxLocation
                            .toVector()
                            .subtract(entity.getLocation().toVector())
                            .normalize();
                    vector.setY(0.1);
                    try {
                        this.hitBox.yaw = entity.getLocation().getYaw();
                        this.getSpigotEntity().setHeadPose(new EulerAngle(2, 0, 0));
                        this.move(vector.getX(), vector.getY(), vector.getZ());
                    } catch (final IllegalArgumentException ex) {
                    }
                    return;
                }
            }
        } else {
            this.counter--;
        }
    }

    @Override
    protected void doTick() {
        super.doTick();
        if (this.hitBox == null)
            return;
        if (!this.isPassengerNull() || this.getBukkitEntity().getVehicle() != null)
            return;

        try {
            this.cancelEntityActionsFromEnvironment();
            this.teleportToHitBox();
            this.checkForEntityMoveInteractions();
            if (this.ballMeta.isRotatingEnabled()) {
                this.playRotationAnimation();
                System.out.println("PLAY MOVE");
            }
        } catch (final Exception ex) {
            Bukkit.getLogger().log(Level.WARNING, "Ball moving failed.", ex);
        }

    }

    /**
     * Kicks the ball by the given entity. Returns the calculated velocity for the ball.
     * The calculated velocity can be manipulated before actually applying it to the ball
     * in next tick.
     *
     * @param entity entity
     * @return velocity
     */
    @Override
    public Object kickByEntity(Object entity) {
        final LivingEntity livingEntity = (LivingEntity) entity;
        Vector vector = this.getDirection(livingEntity);
        vector = this.hitBox.getSpigotEntity()
                .getLocation()
                .toVector()
                .subtract(((Entity) entity).getLocation().toVector())
                .normalize()
                .multiply(this.ballMeta.getModifiers().getHorizontalKickStrengthModifier());
        vector.setY(this.ballMeta.getModifiers().getVerticalKickStrengthModifier());
        this.move(vector.getX(), vector.getY(), vector.getZ());
        return vector;
    }

    /**
     * Throws the ball by the given entity. Returns the calculated velocity for the ball.
     * The calculated velocity can be manipulated before actually applying it to the ball
     * in next tick.
     *
     * @param entity entity
     * @return velocity
     */
    @Override
    public Object throwByEntity(Object entity) {
        final LivingEntity livingEntity = (LivingEntity) entity;
        if (this.isGrabbed() && this.interactionEntity != null && livingEntity.equals(this.interactionEntity)) {
            this.deGrab();
            final Vector vector = this.getDirection(livingEntity);
            final BallThrowEvent event = new BallThrowEvent(this, livingEntity);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                this.move(vector.getX(), vector.getY(), vector.getZ());
            }
            return vector;
        }
        return null;
    }

    /**
     * Returns the launch Direction.
     *
     * @param entity entity
     * @return launchDirection
     */
    private Vector getDirection(Entity entity) {
        final Vector vector = new Vector();
        final double rotX = entity.getLocation().getYaw();
        final double rotY = entity.getLocation().getPitch();
        vector.setY(-Math.sin(Math.toRadians(rotY)));
        final double h = Math.cos(Math.toRadians(rotY));
        vector.setX(-h * Math.sin(Math.toRadians(rotX)));
        vector.setZ(h * Math.cos(Math.toRadians(rotX)));
        vector.setY(0.5);
        vector.add(entity.getVelocity());
        return vector.multiply(3);
    }

    /**
     * Sets the ball in the hands of the entity.
     *
     * @param entity entity
     */
    @Override
    public void grab(Object entity) {
        this.interactionEntity = (Entity) entity;
        final LivingEntity livingEntity = (LivingEntity) this.interactionEntity;
        if (livingEntity.getEquipment().getItemInHand() == null || livingEntity.getEquipment().getItemInHand().getType() == Material.AIR) {
            livingEntity.getEquipment().setItemInHand(this.getSpigotEntity().getHelmet().clone());
            this.grabbed = true;
        }
    }

    /**
     * Removes the ball from the hands of an entity.
     */
    @Override
    public void deGrab() {
        if (this.isGrabbed() && this.interactionEntity != null) {
            final LivingEntity livingEntity = (LivingEntity) this.interactionEntity;
            livingEntity.getEquipment().setItemInHand(null);
            this.grabbed = false;
        }
    }

    /**
     * Respawns the ball at the current location.
     */
    @Override
    public void respawn() {
        final Location location = this.getSpigotEntity().getLocation();
        if (!this.isDead()) {
            this.remove();
        }
        final World mcWorld = ((CraftWorld) location.getWorld()).getHandle();
        this.setPosition(location.getX(), location.getY(), location.getZ());
        mcWorld.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
        final NBTTagCompound compound = new NBTTagCompound();
        compound.setBoolean("invulnerable", true);
        compound.setBoolean("Invisible", true);
        compound.setBoolean("PersistenceRequired", true);
        compound.setBoolean("NoBasePlate", true);
        this.a(compound);
        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkinHelper.setItemStackSkin(itemStack, this.ballMeta.getSkin());
        this.getSpigotEntity().setHelmet(itemStack);
        System.out.println("SET KIN");

        this.hitBox = new CustomHitbox(location, this);
    }

    /**
     * Removes the ball.
     */
    @Override
    public void remove() {
        Bukkit.getPluginManager().callEvent(new BallDeathEvent(this));
        this.getSpigotEntity().remove();
        this.hitBox.getSpigotEntity().remove();
    }

    /**
     * Lets the ball roll or fly by the given values.
     *
     * @param x x
     * @param y y
     * @param z z
     */
    @Override
    public void move(double x, double y, double z) {
        final Vector vector = new Vector(x, y, z);
        this.hitBox.setVelocity(vector);
    }

    /**
     * Teleports the ball to the given location.
     *
     * @param location location
     */
    @Override
    public void teleport(Object location) {
        this.hitBox.getSpigotEntity().teleport((Location) location);
    }

    /**
     * Returns the last entity the ball interacted with. If it is contact, kicking or grabbing.
     *
     * @return entity.
     */
    @Override
    public Object getLastInteractionEntity() {
        return this.interactionEntity;
    }

    /**
     * Returns if the ball is currently hold by any entity.
     *
     * @return isGrabbed
     */
    @Override
    public boolean isGrabbed() {
        return this.grabbed;
    }

    /**
     * Returns the meta data of the ball. Ball has to be respawned for applying changes from the ballMeta.
     *
     * @return ball
     */
    @Override
    public BallMeta getMeta() {
        return this.ballMeta;
    }

    /**
     * Returns if the ball is dead.
     *
     * @return dead
     */
    @Override
    public boolean isDead() {
        return this.getSpigotEntity().isDead() || this.hitBox == null || this.hitBox.getSpigotEntity().isDead();
    }

    /**
     * Returns the armorstand of the ball.
     *
     * @return armorstand
     */
    @Override
    public Object getArmorstand() {
        return this.getBukkitEntity();
    }

    /**
     * Returns the hitbox of the ball.
     *
     * @return armorstand
     */
    @Override
    public Object getHitBox() {
        return this.hitBox.getSpigotEntity();
    }
}