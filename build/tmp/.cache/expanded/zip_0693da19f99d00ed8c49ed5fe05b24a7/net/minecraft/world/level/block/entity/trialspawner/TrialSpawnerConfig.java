package net.minecraft.world.level.block.entity.trialspawner;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.SpawnData;

public record TrialSpawnerConfig(
    int requiredPlayerRange,
    int spawnRange,
    float totalMobs,
    float simultaneousMobs,
    float totalMobsAddedPerPlayer,
    float simultaneousMobsAddedPerPlayer,
    int ticksBetweenSpawn,
    int targetCooldownLength,
    SimpleWeightedRandomList<SpawnData> spawnPotentialsDefinition,
    SimpleWeightedRandomList<ResourceLocation> lootTablesToEject
) {
    public static TrialSpawnerConfig DEFAULT = new TrialSpawnerConfig(
        14, 4, 6.0F, 2.0F, 2.0F, 1.0F, 40, 36000, SimpleWeightedRandomList.empty(), SimpleWeightedRandomList.empty()
    );
    public static MapCodec<TrialSpawnerConfig> MAP_CODEC = RecordCodecBuilder.mapCodec(
        p_312022_ -> p_312022_.group(
                    Codec.intRange(1, 128)
                        .optionalFieldOf("required_player_range", DEFAULT.requiredPlayerRange)
                        .forGetter(TrialSpawnerConfig::requiredPlayerRange),
                    Codec.intRange(1, 128).optionalFieldOf("spawn_range", DEFAULT.spawnRange).forGetter(TrialSpawnerConfig::spawnRange),
                    Codec.floatRange(0.0F, Float.MAX_VALUE).optionalFieldOf("total_mobs", DEFAULT.totalMobs).forGetter(TrialSpawnerConfig::totalMobs),
                    Codec.floatRange(0.0F, Float.MAX_VALUE)
                        .optionalFieldOf("simultaneous_mobs", DEFAULT.simultaneousMobs)
                        .forGetter(TrialSpawnerConfig::simultaneousMobs),
                    Codec.floatRange(0.0F, Float.MAX_VALUE)
                        .optionalFieldOf("total_mobs_added_per_player", DEFAULT.totalMobsAddedPerPlayer)
                        .forGetter(TrialSpawnerConfig::totalMobsAddedPerPlayer),
                    Codec.floatRange(0.0F, Float.MAX_VALUE)
                        .optionalFieldOf("simultaneous_mobs_added_per_player", DEFAULT.simultaneousMobsAddedPerPlayer)
                        .forGetter(TrialSpawnerConfig::simultaneousMobsAddedPerPlayer),
                    Codec.intRange(0, Integer.MAX_VALUE)
                        .optionalFieldOf("ticks_between_spawn", DEFAULT.ticksBetweenSpawn)
                        .forGetter(TrialSpawnerConfig::ticksBetweenSpawn),
                    Codec.intRange(0, Integer.MAX_VALUE)
                        .optionalFieldOf("target_cooldown_length", DEFAULT.targetCooldownLength)
                        .forGetter(TrialSpawnerConfig::targetCooldownLength),
                    SpawnData.LIST_CODEC
                        .optionalFieldOf("spawn_potentials", SimpleWeightedRandomList.empty())
                        .forGetter(TrialSpawnerConfig::spawnPotentialsDefinition),
                    SimpleWeightedRandomList.wrappedCodecAllowingEmpty(ResourceLocation.CODEC)
                        .optionalFieldOf("loot_tables_to_eject", SimpleWeightedRandomList.empty())
                        .forGetter(TrialSpawnerConfig::lootTablesToEject)
                )
                .apply(p_312022_, TrialSpawnerConfig::new)
    );

    public int calculateTargetTotalMobs(int p_312026_) {
        return (int)Math.floor((double)(this.totalMobs + this.totalMobsAddedPerPlayer * (float)p_312026_));
    }

    public int calculateTargetSimultaneousMobs(int p_312885_) {
        return (int)Math.floor((double)(this.simultaneousMobs + this.simultaneousMobsAddedPerPlayer * (float)p_312885_));
    }
}
