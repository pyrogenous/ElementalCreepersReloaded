package com.pyro.elementalcreepersreloaded.entity;

import com.pyro.elementalcreepersreloaded.ElementalCreepers;
import com.pyro.elementalcreepersreloaded.registry.ECEntities;
import com.pyro.lomlibreloaded.nitea.NiteaSupport;
import java.util.EnumSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.feline.Ocelot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

/**
 * The pink creeper. Not hostile unless provoked; tame it with gunpowder (one chance in three). A tamed one follows its
 * owner, sits when told, heals with food, breeds with flowers, and its explosions only hurt its owner's enemies.
 */
public class FriendlyCreeper extends TamableAnimal implements SwellingCreeper {
    private static final EntityDataAccessor<Integer> DATA_SWELL_DIR = SynchedEntityData.defineId(FriendlyCreeper.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_POWERED = SynchedEntityData.defineId(FriendlyCreeper.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_IGNITED = SynchedEntityData.defineId(FriendlyCreeper.class, EntityDataSerializers.BOOLEAN);
    private static final double UNTAMED_HEALTH = 8.0;
    private static final double TAMED_HEALTH = 20.0;

    private int oldSwell;
    private int swell;
    private int maxSwell = 30;
    private int explosionRadius = 3;

    public FriendlyCreeper(EntityType<? extends FriendlyCreeper> type, Level level) {
        super(type, level);
        this.setTame(false, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createAnimalAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.MAX_HEALTH, UNTAMED_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, 2.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new FriendlySwellGoal(this));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Ocelot.class, 6.0F, 1.0, 1.2));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F));
        this.goalSelector.addGoal(7, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(DATA_SWELL_DIR, -1);
        entityData.define(DATA_IS_POWERED, false);
        entityData.define(DATA_IS_IGNITED, false);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("powered", this.isPowered());
        output.putShort("Fuse", (short) this.maxSwell);
        output.putByte("ExplosionRadius", (byte) this.explosionRadius);
        output.putBoolean("ignited", this.isIgnited());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.entityData.set(DATA_IS_POWERED, input.getBooleanOr("powered", false));
        this.maxSwell = input.getShortOr("Fuse", (short) 30);
        this.explosionRadius = input.getByteOr("ExplosionRadius", (byte) 3);
        if (input.getBooleanOr("ignited", false))
            this.entityData.set(DATA_IS_IGNITED, true);
    }

    @Override
    public void tick() {
        if (this.isAlive()) {
            this.oldSwell = this.swell;
            if (this.entityData.get(DATA_IS_IGNITED))
                this.setSwellDir(1);
            int swellDir = this.getSwellDir();
            if (swellDir > 0 && this.swell == 0) {
                this.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F);
                this.gameEvent(GameEvent.PRIME_FUSE);
            }
            this.swell = Math.max(0, this.swell + swellDir);
            if (this.swell >= this.maxSwell) {
                if (this.level() instanceof ServerLevel level)
                    NiteaSupport.guard(ElementalCreepers.nitea(), "friendly_creeper explosion", () -> this.explode(level));
                this.swell = 0;
                this.oldSwell = 0;
                this.setSwellDir(-1);
            }
        }
        super.tick();
    }

    /** Wild: a normal creeper explosion, and it's gone. Tamed: a blast that spares its owner and the owner's pets. */
    private void explode(ServerLevel level) {
        float radius = this.explosionRadius * (this.isPowered() ? 2.0F : 1.0F);
        NiteaSupport.breadcrumb(ElementalCreepers.nitea(), "explosion", "friendly_creeper exploded (tame: " + this.isTame() + ")", this);
        if (!this.isTame()) {
            this.dead = true;
            level.explode(this, this.getX(), this.getY(), this.getZ(), radius, Level.ExplosionInteraction.MOB);
            this.discard();
            return;
        }
        level.explode(this, null, new SparingDamageCalculator(this), this.getX(), this.getY(), this.getZ(), radius, false,
                Level.ExplosionInteraction.NONE);
    }

    /** Leaves the owner, its tamed animals and this creeper alone. */
    private static class SparingDamageCalculator extends ExplosionDamageCalculator {
        private final FriendlyCreeper creeper;

        SparingDamageCalculator(FriendlyCreeper creeper) {
            this.creeper = creeper;
        }

        private boolean spared(Entity entity) {
            LivingEntity owner = this.creeper.getOwner();
            if (entity == this.creeper || entity == owner) return true;
            return owner != null && entity instanceof TamableAnimal pet && pet.isOwnedBy(owner)
                    && !(pet.getTarget() != null && pet.getTarget() == owner);
        }

        @Override
        public boolean shouldDamageEntity(Explosion explosion, Entity entity) {
            return !this.spared(entity) && super.shouldDamageEntity(explosion, entity);
        }

        @Override
        public float getKnockbackMultiplier(Entity entity) {
            return this.spared(entity) ? 0.0F : super.getKnockbackMultiplier(entity);
        }
    }

    @Override
    public float getSwelling(float partialTicks) {
        return Mth.lerp(partialTicks, this.oldSwell, this.swell) / (this.maxSwell - 2);
    }

    public int getSwellDir() {
        return this.entityData.get(DATA_SWELL_DIR);
    }

    public void setSwellDir(int dir) {
        this.entityData.set(DATA_SWELL_DIR, dir);
    }

    public boolean isIgnited() {
        return this.entityData.get(DATA_IS_IGNITED);
    }

    @Override
    public boolean isPowered() {
        return this.entityData.get(DATA_IS_POWERED);
    }

    @Override
    public void thunderHit(ServerLevel level, LightningBolt lightningBolt) {
        super.thunderHit(level, lightningBolt);
        this.entityData.set(DATA_IS_POWERED, true);
    }

    @Override
    protected void applyTamingSideEffects() {
        var health = this.getAttribute(Attributes.MAX_HEALTH);
        var damage = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (health != null) {
            health.setBaseValue(this.isTame() ? TAMED_HEALTH : UNTAMED_HEALTH);
            if (this.isTame())
                this.setHealth((float) TAMED_HEALTH);
        }
        if (damage != null)
            damage.setBaseValue(this.isTame() ? 4.0 : 2.0);
    }

    /** Breeds with flowers. */
    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(BlockItemTags.FLOWERS.item());
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.isTame()) {
            FoodProperties food = stack.get(DataComponents.FOOD);
            if (food != null && this.getHealth() < this.getMaxHealth()) {
                this.heal(food.nutrition());
                stack.consume(1, player);
                this.gameEvent(GameEvent.EAT);
                return InteractionResult.SUCCESS;
            }
            InteractionResult result = super.mobInteract(player, hand);
            if (!result.consumesAction() && this.isOwnedBy(player)) {
                this.setOrderedToSit(!this.isOrderedToSit());
                this.jumping = false;
                this.navigation.stop();
                this.setTarget(null);
                return InteractionResult.SUCCESS.withoutItem();
            }
            return result;
        }
        if (stack.is(ItemTags.CREEPER_IGNITERS)) {
            this.level().playSound(player, this.getX(), this.getY(), this.getZ(), SoundEvents.FLINTANDSTEEL_USE, this.getSoundSource(), 1.0F,
                    this.random.nextFloat() * 0.4F + 0.8F);
            if (!this.level().isClientSide()) {
                this.entityData.set(DATA_IS_IGNITED, true);
                if (stack.isDamageableItem())
                    stack.hurtAndBreak(1, player, hand.asEquipmentSlot());
                else
                    stack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        if (stack.is(Items.GUNPOWDER) && this.getTarget() == null) {
            stack.consume(1, player);
            if (!this.level().isClientSide())
                this.tryToTame(player);
            return InteractionResult.SUCCESS_SERVER;
        }
        return super.mobInteract(player, hand);
    }

    private void tryToTame(Player player) {
        if (this.random.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
            this.tame(player);
            this.navigation.stop();
            this.setTarget(null);
            this.setOrderedToSit(true);
            this.level().broadcastEntityEvent(this, (byte) 7);
            NiteaSupport.breadcrumb(ElementalCreepers.nitea(), "taming", "A friendly creeper was tamed", this);
        } else {
            this.level().broadcastEntityEvent(this, (byte) 6);
        }
    }

    @Override
    public boolean canMate(net.minecraft.world.entity.animal.Animal partner) {
        return partner != this && this.isTame() && partner instanceof FriendlyCreeper other && other.isTame()
                && !other.isInSittingPose() && this.isInLove() && other.isInLove();
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        FriendlyCreeper baby = ECEntities.FRIENDLY_CREEPER.get().create(level, EntitySpawnReason.BREEDING);
        if (baby != null && this.isTame()) {
            baby.setOwnerReference(this.getOwnerReference());
            baby.setTame(true, true);
        }
        return baby;
    }

    @Override
    public boolean removeWhenFarAway(double distanceSqr) {
        return !this.isTame() && this.tickCount > 2400;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.CREEPER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.CREEPER_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 8;
    }

    /** Swells when its target is close, like a creeper; gives up past 7 blocks or out of sight. */
    private static class FriendlySwellGoal extends Goal {
        private final FriendlyCreeper creeper;

        FriendlySwellGoal(FriendlyCreeper creeper) {
            this.creeper = creeper;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.creeper.getTarget();
            return this.creeper.getSwellDir() > 0 || target != null && this.creeper.distanceToSqr(target) < 9.0;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = this.creeper.getTarget();
            if (target == null || target.isDeadOrDying() || this.creeper.distanceToSqr(target) > 49.0
                    || !this.creeper.getSensing().hasLineOfSight(target))
                this.creeper.setSwellDir(-1);
            else
                this.creeper.setSwellDir(1);
        }
    }
}
