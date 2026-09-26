package com.pyro.elementalcreepersreloaded.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

/**
 * What a Creepapedia knows: the creepers its owner has killed (entity ids, in the order found), whether an enchanting
 * table exorcised the ghost page, and whether it's the creative copy with every entry.
 */
public record CreepapediaData(List<Identifier> entries, boolean exorcised, boolean creative) {
    public static final CreepapediaData EMPTY = new CreepapediaData(List.of(), false, false);

    public static final Codec<CreepapediaData> CODEC = RecordCodecBuilder.create(i -> i.group(
            Identifier.CODEC.listOf().optionalFieldOf("entries", List.of()).forGetter(CreepapediaData::entries),
            Codec.BOOL.optionalFieldOf("exorcised", false).forGetter(CreepapediaData::exorcised),
            Codec.BOOL.optionalFieldOf("creative", false).forGetter(CreepapediaData::creative)
    ).apply(i, CreepapediaData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CreepapediaData> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()), CreepapediaData::entries,
            ByteBufCodecs.BOOL, CreepapediaData::exorcised,
            ByteBufCodecs.BOOL, CreepapediaData::creative,
            CreepapediaData::new);

    public CreepapediaData {
        entries = List.copyOf(entries);
    }

    public boolean has(Identifier entry) {
        return this.entries.contains(entry);
    }

    public CreepapediaData with(Identifier entry) {
        if (this.has(entry)) return this;
        List<Identifier> list = new ArrayList<>(this.entries);
        list.add(entry);
        return new CreepapediaData(list, this.exorcised, this.creative);
    }

    public CreepapediaData exorcise() {
        return new CreepapediaData(this.entries, true, this.creative);
    }
}
