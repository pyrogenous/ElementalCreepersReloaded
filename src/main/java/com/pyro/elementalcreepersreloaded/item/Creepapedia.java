package com.pyro.elementalcreepersreloaded.item;

import com.pyro.elementalcreepersreloaded.ElementalCreepers;
import com.pyro.elementalcreepersreloaded.registry.ECEntities;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;

/** The Creepapedia's pages: the vanilla creeper, then every creeper of the mod, in the original book's order. */
public final class Creepapedia {
    private Creepapedia() {
    }

    public static List<EntityType<?>> allEntries() {
        List<EntityType<?>> list = new ArrayList<>();
        list.add(EntityTypes.CREEPER);
        for (var type : ECEntities.ELEMENTAL)
            list.add(type.get());
        list.add(list.indexOf(ECEntities.ICE_CREEPER.get()) + 1, ECEntities.FRIENDLY_CREEPER.get());
        return list;
    }

    public static boolean isEntry(EntityType<?> type) {
        return type == EntityTypes.CREEPER || ElementalCreepers.MOD_ID.equals(BuiltInRegistries.ENTITY_TYPE.getKey(type).getNamespace());
    }

    public static Identifier id(EntityType<?> type) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(type);
    }

    /** Translation key of the entry's text: book.elementalcreepersreloaded.<namespace>.<name>. */
    public static String textKey(EntityType<?> type) {
        Identifier id = id(type);
        return "book." + ElementalCreepers.MOD_ID + "." + id.getNamespace() + "." + id.getPath();
    }

    public static CreepapediaData complete() {
        return new CreepapediaData(allEntries().stream().map(Creepapedia::id).toList(), true, true);
    }
}
