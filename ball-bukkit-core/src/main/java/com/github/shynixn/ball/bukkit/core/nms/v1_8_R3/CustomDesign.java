package com.github.shynixn.ball.bukkit.core.nms.v1_8_R3;

import com.github.shynixn.ball.api.bukkit.business.entity.BukkitBall;
import com.github.shynixn.ball.api.bukkit.business.event.*;
import com.github.shynixn.ball.api.persistence.BallMeta;
import com.github.shynixn.ball.bukkit.core.logic.business.helper.SkinHelper;
import com.github.shynixn.ball.bukkit.core.logic.persistence.entity.BallData;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public final class CustomDesign extends EntityArmorStand implements BukkitBall {
    private boolean persistent;
    private LivingEntity owner;

    private final BallMeta ballMeta;
    private CustomHitbox hitBox;
    
    private float magnusForce = 0F;
    private boolean grabbed;
    private Entity interactionEntity;

    private int counter = 20;
    boolean revertAnimation;
    private final UUID uuid;

    public CustomDesign(Location location, BallMeta ballMeta, boolean persistent, LivingEntity owner) {
        super(((CraftWorld) location.getWorld()).getHandle());
        this.ballMeta = ballMeta;
        this.owner = owner;
        this.persistent = persistent;
        this.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        this.uuid = UUID.randomUUID();
    }

    public CustomDesign(String uuid, Map<String, Object> data) {
        super(((CraftWorld) Bukkit.getWorld((String) data.get("location.world"))).getHandle());
        this.uuid = UUID.fromString(uuid);
        this.ballMeta = new BallData(((MemorySection) data.get("meta")).getValues(true));
        final Location location = Location.deserialize(((MemorySection) data.get("location")).getValues(true));
        this.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        this.persistent = true;
    }

    /**
     * Ticking entity.
     */
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
            if (!this.isGrabbed()) {
                this.checkForEntityMoveInteractions();
                if (this.ballMeta.isRotatingEnabled()) {
                    this.playRotationAnimation();
                }
            } else {
                this.hitBox.getSpigotEntity().teleport(this.interactionEntity);
            }
        } catch (final Exception ex) {
            Bukkit.getLogger().log(Level.WARNING, "Ball moving failed.", ex);
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
            final ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            try {
                SkinHelper.setItemStackSkin(itemStack, this.ballMeta.getSkin());
            } catch (final Exception e1) {
              Bukkit.getLogger().log(Level.WARNING, "Failed to degrab entity.", e1);
            }
            this.setHelmet(itemStack);
            final Vector vector = this.getDirection(livingEntity).normalize().multiply(3);
            this.teleport(livingEntity.getLocation().add(vector));
        }
    }

    /**
     * Respawns the ball at the current location.
     */
    @Override
    public void respawn() {
        if (this.isGrabbed())
            return;

        final Location location = this.getSpigotEntity().getLocation();
        final BallSpawnEvent event = new BallSpawnEvent(location, this);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

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
        this.getSpigotEntity().setCustomName("ResourceBallsPlugin");
        this.getSpigotEntity().setCustomNameVisible(false);
        final ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        try {
            SkinHelper.setItemStackSkin(itemStack, this.ballMeta.getSkin());
        } catch (final Exception e1) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to respawn entity.", e1);
        }
        this.setHelmet(itemStack);
        this.hitBox = new CustomHitbox(location, this);
    }

    /**
     * Removes the ball.
     */
    @Override
    public void remove() {
        Bukkit.getPluginManager().callEvent(new BallDeathEvent(this));
        this.deGrab();
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
    public void moveEntity(double x, double y, double z) {
        if (this.isGrabbed())
            return;
        this.revertAnimation = false;
        this.magnusForce = 0F;
        this.setHeadPose(new EulerAngle(2, 0, 0));
        final Vector vector = new Vector(x, y, z);
        this.hitBox.setVelocity(vector);
    }

    /**
     * Sets if the ball should be stored on the fileSystem.
     *
     * @param enabled enabled
     */
    @Override
    public void setPersistent(boolean enabled) {
        this.persistent = enabled;
    }

    /**
     * Returns if the ball should be stored on the fileSystem.
     *
     * @return persistent
     */
    @Override
    public boolean isPersistent() {
        return this.persistent;
    }

    /**
     * Returns the owner of the ball.
     *
     * @return owner
     */
    @Override
    public Optional<LivingEntity> getOwner() {
        return Optional.ofNullable(this.owner);
    }

    /**
     * Teleports the ball to the given location.
     *
     * @param location location
     */
    @Override
    public void teleport(Location location) {
        if(location == null)
            throw new IllegalArgumentException("Location cannot be null!");
        if (this.isGrabbed())
            return;
        this.hitBox.getSpigotEntity().teleport(location);
    }

    /**
     * Returns the location of the ball.
     *
     * @return location
     */
    @Override
    public Location getLocation() {
        return this.getHitBox().getLocation();
    }
    
    /**
     * Returns the scale of spinning force.
     * If the ball spins clockwise, the scale should be positive.
     *
     * @return scale
     */
    @Override
    public float getMagnusForce() {
        return this.magnusForce;
    }
    
    /**
     * Sets the scale of spinning force.
     *
     * @param scale scale of force
     */
    @Override
    public void setMagnusForce(float scale) {
        this.magnusForce = scale;
    }
    
    /**
     * Kicks the ball by the given entity.
     * The calculated velocity can be manipulated by the BallKickEvent.
     *
     * @param livingEntity entity
     */
    @Override
    public void kickByEntity(LivingEntity livingEntity) {
        if(livingEntity == null)
            throw new IllegalArgumentException("Living entity cannot be null!");
        if (this.isGrabbed())
            return;
        final Vector vector = this.hitBox.getSpigotEntity()
                .getLocation()
                .toVector()
                .subtract(livingEntity.getLocation().toVector())
                .normalize()
                .multiply(this.ballMeta.getModifiers().getHorizontalKickStrengthModifier());
        this.hitBox.yaw = livingEntity.getLocation().getYaw();
        vector.setY(0.1 * this.ballMeta.getModifiers().getVerticalKickStrengthModifier());
        final BallKickEvent event = new BallKickEvent(this, livingEntity, vector);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            this.moveEntity(vector.getX(), vector.getY(), vector.getZ());
        }
    }

    /**
     * Throws the ball by the given entity.
     * The calculated velocity can be manipulated by the BallThrowEvent.
     *
     * @param livingEntity entity
     */
    @Override
    public void throwByEntity(LivingEntity livingEntity) {
        if(livingEntity == null)
            throw new IllegalArgumentException("Living entity cannot be null!");
        if (this.isGrabbed() && this.interactionEntity != null && livingEntity.equals(this.interactionEntity)) {
            this.deGrab();
            Vector vector = this.getDirection(livingEntity).normalize();
            final double y = vector.getY();
            vector = vector.multiply(this.ballMeta.getModifiers().getHorizontalThrowStrengthModifier());
            vector.setY(y * 2 * this.ballMeta.getModifiers().getVerticalThrowStrengthModifier());
            final BallThrowEvent event = new BallThrowEvent(this, livingEntity, vector);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                this.counter = 10;
                this.moveEntity(vector.getX(), vector.getY(), vector.getZ());
            }
        }
    }

    /**
     * Returns the last entity the ball interacted with. If it is contact, kicking or grabbing.
     *
     * @return entity.
     */
    @Override
    public LivingEntity getLastInteractionEntity() {
        return (LivingEntity) this.interactionEntity;
    }

    /**
     * Sets the ball in the hands of the entity.
     *
     * @param livingEntity entity
     */
    @Override
    public void grab(LivingEntity livingEntity) {
        if(livingEntity == null)
            throw new IllegalArgumentException("Living entity cannot be null!");
        if (this.isGrabbed())
            return;
        this.interactionEntity = livingEntity;
        if (livingEntity.getEquipment().getItemInHand() == null || livingEntity.getEquipment().getItemInHand().getType() == Material.AIR) {
            final BallGrabEvent event = new BallGrabEvent(this, livingEntity);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                livingEntity.getEquipment().setItemInHand(this.getSpigotEntity().getHelmet().clone());
                this.setHelmet(null);
                this.grabbed = true;
            }
        }
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
    public ArmorStand getArmorstand() {
        return (ArmorStand) this.getBukkitEntity();
    }

    /**
     * Returns the hitbox of the ball.
     *
     * @return armorstand
     */
    @Override
    public ArmorStand getHitBox() {
        return this.hitBox.getSpigotEntity();
    }

    /**
     * Returns the id of the ball.
     *
     * @return id
     */
    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    /**
     * Serializes the given ball.
     *
     * @return serializedContent
     */
    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> data = new LinkedHashMap<>();
        data.put("location", this.getLocation().serialize());
        data.put("meta", ((BallData) this.ballMeta).serialize());
        return data;
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

    private boolean isPassengerNull() {
        return this.passenger == null;
    }

    private void teleportToHitBox() {
        final Location loc = this.hitBox.getSpigotEntity().getLocation();
        if (this.isSmall()) {
            this.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        } else {
            this.setPositionRotation(loc.getX(), loc.getY() - 1.0, loc.getZ(), loc.getYaw(), loc.getPitch());
        }
    }

    private void playRotationAnimation() {
        final double length = new Vector(this.hitBox.motX, this.hitBox.motY, this.hitBox.motZ).length();
        EulerAngle angle = null;
        final EulerAngle a = this.getHeadPose();
        if (length > 1.0) {
            if (this.revertAnimation) {
                angle = new EulerAngle(a.getX() - 0.5, 0, 0);
            } else {
                angle = new EulerAngle(a.getX() + 0.5, 0, 0);
            }
        } else if (length > 0.1) {
            if (this.revertAnimation) {
                angle = new EulerAngle(a.getX() - 0.25, 0, 0);
            } else {
                angle = new EulerAngle(a.getX() + 0.25, 0, 0);
            }
        } else if (length > 0.08) {
            if (this.revertAnimation) {
                angle = new EulerAngle(a.getX() - 0.025, 0, 0);
            } else {
                angle = new EulerAngle(a.getX() + 0.025, 0, 0);
            }
        }
        if (angle != null) {
            this.setHeadPose(angle);
        }
    }

    private EulerAngle getHeadPose() {
        return this.getSpigotEntity().getHeadPose();
    }

    private void setHeadPose(EulerAngle angle) {
        this.getSpigotEntity().setHeadPose(angle);
    }

    private ArmorStand getSpigotEntity() {
        return this.getArmorstand();
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
                        entity.getLocation().distance(hitBoxLocation) < this.ballMeta.getHitBoxSize()) {
                    final BallInteractEvent event = new BallInteractEvent(this, entity);
                    Bukkit.getPluginManager().callEvent(event);
                    if (event.isCancelled())
                        return;
                    final Vector vector = hitBoxLocation
                            .toVector()
                            .subtract(entity.getLocation().toVector())
                            .normalize().multiply(this.ballMeta.getModifiers().getHorizontalTouchModifier());
                    vector.setY(0.1 * this.ballMeta.getModifiers().getVerticalTouchModifier());
                    this.hitBox.yaw = entity.getLocation().getYaw();
                    this.moveEntity(vector.getX(), vector.getY(), vector.getZ());
                    return;
                }
            }
        } else {
            this.counter--;
        }
    }

    private void setHelmet(ItemStack itemStack) {
        switch (this.ballMeta.getSize()) {
            case SMALL:
                this.getSpigotEntity().setSmall(true);
                this.getSpigotEntity().setHelmet(itemStack);
                break;
            case NORMAL:
                this.getSpigotEntity().setHelmet(itemStack);
                break;
        }
    }
}