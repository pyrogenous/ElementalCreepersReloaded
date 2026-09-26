package com.pyro.elementalcreepersreloaded.registry;

import com.pyro.elementalcreepersreloaded.ElementalCreepers;
import com.pyro.elementalcreepersreloaded.item.CreepapediaData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ECComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, ElementalCreepers.MOD_ID);

    public static final RegistryObject<DataComponentType<CreepapediaData>> CREEPAPEDIA = COMPONENTS.register("creepapedia",
            () -> DataComponentType.<CreepapediaData>builder().persistent(CreepapediaData.CODEC).networkSynchronized(CreepapediaData.STREAM_CODEC).build());

    private ECComponents() {
    }
}
