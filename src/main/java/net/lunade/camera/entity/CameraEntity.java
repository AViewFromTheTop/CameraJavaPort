package net.lunade.camera.entity;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import net.lunade.camera.CameraPortMain;
import net.lunade.camera.networking.CameraPossessPacket;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

public class CameraEntity extends Mob {
	private static final EntityDataAccessor<Float> TRACKED_HEIGHT = SynchedEntityData.defineId(CameraEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Integer> TIMER = SynchedEntityData.defineId(CameraEntity.class, EntityDataSerializers.INT);
	public ArrayList<UUID> queuedUUIDS = new ArrayList<>();
	public long lastHit;
	public float prevTimer;
	public float timer;
	private boolean goingUp = false;

	public CameraEntity(EntityType<? extends Mob> entityType, Level world) {
		super(entityType, world);
		this.setPersistenceRequired();
		this.getNavigation().setCanFloat(false);
	}

	@NotNull
	public static AttributeSupplier.Builder addAttributes() {
		return LivingEntity.createLivingAttributes()
			.add(Attributes.FOLLOW_RANGE, 80D)
			.add(Attributes.ATTACK_KNOCKBACK)
			.add(Attributes.MOVEMENT_SPEED, 0D)
			.add(Attributes.KNOCKBACK_RESISTANCE, 100D)
			.add(Attributes.STEP_HEIGHT, 0D);
	}

	@Override
	protected @NotNull EntityDimensions getDefaultDimensions(Pose pose) {
		return EntityDimensions.scalable(this.getBoundingBoxRadius() * 2F, this.getTrackedHeight()).scale(this.getAgeScale());
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(TRACKED_HEIGHT, 1.75F);
		builder.define(TIMER, 0);
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
					if (chosen != null) this.getLookControl().setLookAt(chosen);
				}
				if (this.getTimer() == 1) {
					if (this.level() instanceof ServerLevel serverLevel) {
						ArrayList<ServerPlayer> queuedPlayers = new ArrayList<>();
						for (UUID uuid : this.queuedUUIDS) {
							Player player = this.level().getPlayerByUUID(uuid);
							if (player instanceof ServerPlayer serverPlayer) {
								queuedPlayers.add(serverPlayer);
							}
						}
						ArrayList<ServerPlayer> nonQueuedPlayers = new ArrayList<>(serverLevel.getServer().getPlayerList().getPlayers());
						nonQueuedPlayers.remove(queuedPlayers);
						nonQueuedPlayers.removeIf(serverPlayer -> serverPlayer.level().dimension() != serverLevel.dimension());

						for (ServerPlayer player : nonQueuedPlayers) {
							player.connection.send(
								new ClientboundSoundPacket(
									BuiltInRegistries.SOUND_EVENT.wrapAsHolder(CameraPortMain.CAMERA_SNAP),
									this.getSoundSource(),
									this.getX(),
									this.getY(),
									this.getZ(),
									0.5F,
									1F,
									this.random.nextLong()
								)
							);
						}

						for (ServerPlayer serverPlayer : queuedPlayers) {
							CameraPossessPacket.sendTo(serverPlayer, this);
						}
						this.queuedUUIDS.removeIf(uuid -> true);
					}
				}
			}
		}
		this.timer = Math.max(0, this.prevTimer -= 1);
		this.refreshDimensions();
	}

	@Override
	protected @NotNull InteractionResult mobInteract(@NotNull Player player, InteractionHand hand) {
		if (this.level().isClientSide) return InteractionResult.SUCCESS;

		if (player.isShiftKeyDown()) {
			if (this.canBeAdjusted()) {
				float change = this.goingUp ? 0.095F : -0.095F;
				float newHeight = (this.getTrackedHeight() + change);
				if (newHeight >= getMaxHeight()) {
					this.goingUp = false;
					newHeight = this.getMaxHeight();
				} else if (newHeight <= this.getMinHeight()) {
					this.goingUp = true;
					newHeight = this.getMinHeight();
				}
				this.setTrackedHeight(newHeight);
				this.level().playSound(null, getX(), getEyeY(), getZ(), CameraPortMain.CAMERA_ADJUST, SoundSource.NEUTRAL, this.getSoundVolume(), this.getTrackedHeight());
				return InteractionResult.SUCCESS;
			}
		} else {
			if (this.getTimer() > 1) {
				if (this.addPlayerToQueue(player)) {
					this.playSound(CameraPortMain.CAMERA_PRIME, this.getSoundVolume(), this.getVoicePitch());
				}
			} else {
				if (this.addPlayerToQueue(player)) {
					this.setTimer(60);
					this.playSound(CameraPortMain.CAMERA_PRIME, this.getSoundVolume(), this.getVoicePitch());
					return InteractionResult.SUCCESS;
				}
			}
		}
		return InteractionResult.PASS;
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

	@Override
	public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float f) {
		if (this.isRemoved()) return false;
		if (!serverLevel.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && damageSource.getEntity() instanceof Mob) return false;
		if (damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			this.kill(serverLevel);
			return false;
		}
		if (this.isInvulnerableTo(serverLevel, damageSource)) return false;
		if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) {
			this.brokenByAnything(serverLevel, damageSource);
			this.kill(serverLevel);
			return false;
		}
		if (damageSource.is(DamageTypeTags.IGNITES_ARMOR_STANDS)) {
			if (this.isOnFire()) {
				this.causeDamage(serverLevel, damageSource, 0.15F);
			} else {
				this.igniteForSeconds(5F);
			}
			return false;
		}

		if (damageSource.is(DamageTypeTags.BURNS_ARMOR_STANDS) && this.getHealth() > 0.5F) {
			this.causeDamage(serverLevel, damageSource, 4F);
			return false;
		}

		boolean canBreak = damageSource.is(DamageTypeTags.CAN_BREAK_ARMOR_STAND);
		boolean alwaysKills = damageSource.is(DamageTypeTags.ALWAYS_KILLS_ARMOR_STANDS);
		if (!canBreak && !alwaysKills) return false;

		if (damageSource.getEntity() instanceof Player player && !player.getAbilities().mayBuild) return false;

		if (damageSource.isCreativePlayer()) {
			this.playBrokenSound();
			this.showBreakingParticles();
			this.kill(serverLevel);
			return true;
		}

		long gameTime = serverLevel.getGameTime();
		if (gameTime - this.lastHit > 5L && !alwaysKills) {
			serverLevel.broadcastEntityEvent(this, EntityEvent.ARMORSTAND_WOBBLE);
			this.gameEvent(GameEvent.ENTITY_DAMAGE, damageSource.getEntity());
			this.lastHit = gameTime;
		} else {
			this.brokenByPlayer(serverLevel, damageSource);
			this.showBreakingParticles();
			this.kill(serverLevel);
		}

		return true;
	}

	private void showBreakingParticles() {
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

	private void causeDamage(ServerLevel serverLevel, DamageSource damageSource, float amount) {
		float g = this.getHealth();
		g -= amount;
		if (g <= 0.5F) {
			this.brokenByAnything(serverLevel, damageSource);
			this.kill(serverLevel);
		} else {
			this.setHealth(g);
			this.gameEvent(GameEvent.ENTITY_DAMAGE, damageSource.getEntity());
		}
	}

	private void brokenByPlayer(ServerLevel serverLevel, DamageSource damageSource) {
		ItemStack itemStack = this.getPickResult();
		itemStack.set(DataComponents.CUSTOM_NAME, this.getCustomName());
		Block.popResource(this.level(), this.blockPosition(), itemStack);
		this.brokenByAnything(serverLevel, damageSource);
	}

	private void brokenByAnything(ServerLevel serverLevel, DamageSource damageSource) {
		this.playBrokenSound();
		this.dropAllDeathLoot(serverLevel, damageSource);
	}

	private void playBrokenSound() {
		this.level().playSound(null, this.getX(), this.getY(), this.getZ(), CameraPortMain.CAMERA_BREAK, this.getSoundSource(), 1F, 1F);
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
	public void handleEntityEvent(byte id) {
		if (id == EntityEvent.ARMORSTAND_WOBBLE) {
			if (this.level().isClientSide) {
				this.level().playLocalSound(
					this.getX(),
					this.getY(),
					this.getZ(),
					CameraPortMain.CAMERA_HIT,
					this.getSoundSource(),
					0.3F,
					1F,
					false
				);
				this.lastHit = this.level().getGameTime();
			}
		} else {
			super.handleEntityEvent(id);
		}
	}

	@Override
	public void kill(ServerLevel serverLevel) {
		this.remove(Entity.RemovalReason.KILLED);
		this.gameEvent(GameEvent.ENTITY_DIE);
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
		return new Fallsounds(CameraPortMain.CAMERA_FALL, CameraPortMain.CAMERA_FALL);
	}

	@Override
	public SoundEvent getHurtSound(DamageSource damageSource) {
		return CameraPortMain.CAMERA_HIT;
	}

	@Override
	public SoundEvent getDeathSound() {
		return CameraPortMain.CAMERA_BREAK;
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
	protected void customServerAiStep(ServerLevel serverLevel) {
		super.customServerAiStep(serverLevel);
	}

	@Override
	public void addAdditionalSaveData(ValueOutput valueOutput) {
		super.addAdditionalSaveData(valueOutput);
		valueOutput.putInt("ticksToPhoto", this.getTimer());
		if (!this.queuedUUIDS.isEmpty()) {
			for (UUID uuid : this.queuedUUIDS) {
				int index = this.queuedUUIDS.indexOf(uuid);
				String uuidTag = "queuedUUID" + index;
				valueOutput.store(uuidTag, UUIDUtil.CODEC, uuid);
			}
		}
		valueOutput.putFloat("currentHeight", this.getTrackedHeight());
		valueOutput.putBoolean("goingUp", this.goingUp);
	}

	@Override
	public void readAdditionalSaveData(ValueInput valueInput) {
		super.readAdditionalSaveData(valueInput);
		this.setTimer(valueInput.getIntOr("ticksToPhoto", 0));

		for (int i = 0; true; i++) {
			String uuidTag = "queuedUUID" + i;
			Optional<UUID> optionalUUID = valueInput.read(uuidTag, UUIDUtil.CODEC);
			if (optionalUUID.isPresent()) {
				this.queuedUUIDS.add(optionalUUID.get());
			} else {
				break;
			}
		}
		this.setTrackedHeight(valueInput.getFloatOr("currentHeight", 1.75F));
		this.goingUp = valueInput.getBooleanOr("goingUp", false);
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
