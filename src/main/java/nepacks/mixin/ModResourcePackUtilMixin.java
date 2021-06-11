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

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import nepacks.config.NepacksConfig;
import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.impl.resource.loader.ModNioResourcePack;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.server.packs.PackType;

@Mixin(ModResourcePackUtil.class)
public class ModResourcePackUtilMixin {

    @SuppressWarnings("resource")
    @Inject(method = "appendModResourcePacks", at = @At("HEAD"), cancellable = true, remap = false)
    private static void nepacks_appendModResourcePacks(final List<ModResourcePack> packs, final PackType type, final String subPath, final CallbackInfo callback) {
        if (!NepacksConfig.getConfig().sortMods) {
            return;
        }

        if (subPath != null) {
            return;
        }

        if (!NepacksConfig.getConfig().sortMods) {
            return;
        }

        final Collection<String> excluded = Sets.newHashSet();
        excluded.add("minecraft");
        excluded.add("fabricloader");
        excluded.add("java");

        final List<ModContainer> sortedContainers = Lists.newArrayList(FabricLoader.getInstance().getAllMods());
        final var mods = sortedContainers.iterator();
        while (mods.hasNext()) {
            var mod = mods.next();
            if (mod.getMetadata().getType().equals("builtin")) {
                mods.remove();
                excluded.add(modId(mod));
            }
        }
        sortedContainers.sort(Comparator.comparing(ModResourcePackUtilMixin::modId));

        var copy = Lists.newArrayList(sortedContainers);
        for (var mod : copy) {
            var depends = mod.getMetadata().getDepends();
            if (depends.isEmpty()) {
                continue;
            }
            var dependencies = depends.stream().map(dep -> dep.getModId()).filter(id -> !excluded.contains(id)).collect(Collectors.toSet());
            if (dependencies.isEmpty()) {
                continue;
            }
            for (var i = sortedContainers.size() - 1; i >= 0; --i) {
                if (dependencies.contains(modId(sortedContainers.get(i)))) {
                    move(sortedContainers, mod, i + 1);
                    break;
                }
            }
        }

        for (var mod : sortedContainers) {
            final var pack = new ModNioResourcePack(mod.getMetadata(), mod.getRootPath(), type, null, ResourcePackActivationType.ALWAYS_ENABLED);
            if (!pack.getNamespaces(type).isEmpty()) {
                packs.add(pack);
            }
        }
        callback.cancel();
    }

    private static <T> void move(final List<T> list, final T object, final int index) {
        var original = list.indexOf(object);
        if (original == index) {
            return;
        }
        list.add(index, object);
        if (original > index) {
            ++original;
        }
        list.remove(original);
    }

    private static String modId(final ModContainer container) {
        return container.getMetadata().getId();
    }
}
