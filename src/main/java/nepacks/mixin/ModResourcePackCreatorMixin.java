/*
 * This file is part of Not Enough Packs.
 * Copyright (c) 2021, warjort and others, All rights reserved.
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the software.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package nepacks.mixin;

import java.util.List;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Lists;

import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackUtil;
import net.fabricmc.fabric.impl.resource.loader.ResourceManagerHelperImpl;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;

@Mixin(ModResourcePackCreator.class)
public class ModResourcePackCreatorMixin {

    @Shadow(remap = false)
    @Final
    private PackType type;

    @Inject(method = "loadPacks", at = @At("HEAD"), cancellable = true)
    private void nepacks_loadPacks(final Consumer<Pack> consumer, final Pack.PackConstructor factory, final CallbackInfo callback) {

        final List<ModResourcePack> packs = Lists.newArrayList();
        ModResourcePackUtil.appendModResourcePacks(packs, this.type, null);
        packs.stream().map(pack -> Pack.create(pack.getName(), true, () -> pack, factory, Pack.Position.TOP, ModResourcePackCreator.RESOURCE_PACK_SOURCE)).forEach(consumer);

        ResourceManagerHelperImpl.registerBuiltinResourcePacks(this.type, consumer, factory);
        callback.cancel();
    }
}
