package net.lunade.camera.entity;

import net.lunade.camera.CameraMain;
import net.lunade.camera.networking.CameraPossessPacket;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class CameraEntity extends Mob {
    private static final EntityDataAccessor<Float> TRACKED_HEIGHT = SynchedEntityData.defineId(CameraEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> TIMER = SynchedEntityData.defineId(CameraEntity.class, EntityDataSerializers.INT);
    public ArrayList<UUID> queuedUUIDS = new ArrayList<>();
    private boolean goingUp = false;
    //CLIENT VARIABLES
    public float prevTimer;
    public float timer;

    public CameraEntity(EntityType<? extends Mob> entityType, Level world) {
        super(entityType, world);
        this.setMaxUpStep(1.0F);
        this.setPersistenceRequired();
        this.getNavigation().setCanFloat(false);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return new EntityDimensions(this.getBoundingBoxRadius() * 2F, this.getTrackedHeight(), true);
    }

    @NotNull
    public static AttributeSupplier.Builder addAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D)
                .add(Attributes.ATTACK_KNOCKBACK)
                .add(Attributes.MOVEMENT_SPEED, 0.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TRACKED_HEIGHT, 1.75F);
        this.entityData.define(TIMER, 0);
    }

    @Override
    public void tick() {
        super.tick();
        this.prevTimer = this.getTimer();
        if (!this.level().isClientSide && this.level() instanceof ServerLevel) {
            if (this.getTimer() > 0) {
                this.setTimer(this.getTimer() - 1);
                if (!this.queuedUUIDS.isEmpty()) {
                    Player chosen = this.level().getPlayerByUUID(this.queuedUUIDS.get(0));
                    if (chosen != null) {
                        this.getLookControl().setLookAt(chosen);
                    }
                }
                if (this.getTimer() == 1) {
                    for (UUID uuid : this.queuedUUIDS) {
                        Player player = this.level().getPlayerByUUID(uuid);
                        if (player instanceof ServerPlayer serverPlayer) {
                            CameraPossessPacket.sendTo(serverPlayer, this);
                        }
                    }
                    this.queuedUUIDS.removeIf(uuid -> true);
                }
            }
        }
        this.timer = this.getTimer();
        this.refreshDimensions();
    }

    @Override
    protected InteractionResult mobInteract(@NotNull Player player, InteractionHand hand) {
        if (this.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (player.isShiftKeyDown()) {
            if (this.canBeAdjusted()) {
                float change = this.goingUp ? 0.095F : -0.095f;
                float newHeight = (this.getTrackedHeight() + change);
                if (newHeight >= getMaxHeight()) {
                    this.goingUp = false;
                    newHeight = this.getMaxHeight();
                } else if (newHeight <= this.getMinHeight()) {
                    this.goingUp = true;
                    newHeight = this.getMinHeight();
                }
                this.setTrackedHeight(newHeight);
                this.level().playSound(null, getX(), getEyeY(), getZ(), CameraMain.CAMERA_ADJUST, SoundSource.NEUTRAL, this.getSoundVolume(), this.getTrackedHeight());
                return InteractionResult.SUCCESS;
            }
        } else {
            if (this.getTimer() > 1) {
                if (this.addPlayerToQueue(player)) {
                    this.playSound(CameraMain.CAMERA_PRIME, this.getSoundVolume(), this.getVoicePitch());
                }
            } else {
                if (this.addPlayerToQueue(player)) {
                    this.setTimer(60);
                    this.playSound(CameraMain.CAMERA_PRIME, this.getSoundVolume(), this.getVoicePitch());
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void tickDeath() {
        if (!this.level().isClientSide() && !this.isRemoved()) {
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    public float getMaxHeight() {
        return 1.75F;
    }

    public float getMinHeight() {
        return 0.8F;
    }

    public float getBoundingBoxRadius() {
        return 0.3F;
    }

    public boolean canBeAdjusted() {
        return true;
    }

    public boolean addPlayerToQueue(@NotNull Player player) {
        UUID playerUUID = player.getUUID();
        if (!this.queuedUUIDS.contains(playerUUID)) {
            this.queuedUUIDS.add(playerUUID);
            return true;
        }
        return false;
    }

    @Override
    public void die(DamageSource damageSource) {
        this.spawnBreakParticles();
        super.die(damageSource);
    }

    @Override
    protected void dropAllDeathLoot(DamageSource damageSource) {
        super.dropAllDeathLoot(damageSource);
        if (this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            ItemStack itemStack = this.getPickResult();
            if (itemStack != null && !itemStack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemStack)) {
                this.spawnAtLocation(itemStack, this.getEyeHeight());
            }
        }
    }

    @Override
    protected void actuallyHurt(DamageSource damageSource, float f) {
        super.actuallyHurt(damageSource, f);
        this.spawnBreakParticles();
    }

    public void spawnBreakParticles() {
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.STICK)),
                    this.getX(),
                    this.getY(0.6666666666666666D),
                    this.getZ(),
                    10,
                    this.getBbWidth() / 4.0F,
                    this.getBbHeight() / 4.0F,
                    this.getBbWidth() / 4.0F, 0.05D
            );
        }
    }

    @Override
    public void doPush(Entity entity) {
    }

    @Override
    protected float getJumpPower() {
        return 0F;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance mobEffectInstance) {
        return false;
    }

    @NotNull
    @Override
    public Fallsounds getFallSounds() {
        return new Fallsounds(CameraMain.CAMERA_FALL, CameraMain.CAMERA_FALL);
    }

    @Override
    public SoundEvent getHurtSound(DamageSource damageSource) {
        return CameraMain.CAMERA_HIT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return CameraMain.CAMERA_BREAK;
    }

    @Override
    protected float getStandingEyeHeight(Pose entityPose, @NotNull EntityDimensions entityDimensions) {
        return entityDimensions.height * 0.9257142857142857F;
    }

    @Override
    public boolean canTakeItem(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return false;
    }

    @Override
    public boolean isAffectedByPotions() {
        return false;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("ticksToPhoto", this.getTimer());
        if (!this.queuedUUIDS.isEmpty()) {
            for (UUID uuid : this.queuedUUIDS) {
                int index = this.queuedUUIDS.indexOf(uuid);
                String uuidTag = "queuedUUID" + index;
                compoundTag.putUUID(uuidTag, uuid);
            }
        }
        compoundTag.putFloat("currentHeight", this.getTrackedHeight());
        compoundTag.putBoolean("goingUp", this.goingUp);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setTimer(compoundTag.getInt("ticksToPhoto"));
        for (int i = 0; true; i++) {
            String uuidTag = "queuedUUID" + i;
            if (compoundTag.contains(uuidTag)) {
                this.queuedUUIDS.add(compoundTag.getUUID(uuidTag));
            } else {
                break;
            }
        }
        this.setTrackedHeight(compoundTag.getFloat("currentHeight"));
        this.goingUp = compoundTag.getBoolean("goingUp");
    }

    public float getLerpedTimer(float tickDelta) {
        return Mth.lerp(tickDelta, this.prevTimer, this.timer);
    }

    public float getTrackedHeight() {
        return this.entityData.get(TRACKED_HEIGHT);
    }

    public void setTrackedHeight(float value) {
        this.entityData.set(TRACKED_HEIGHT, value);
    }

    public int getTimer() {
        return this.entityData.get(TIMER);
    }

    public void setTimer(int value) {
        this.entityData.set(TIMER, value);
    }
}
