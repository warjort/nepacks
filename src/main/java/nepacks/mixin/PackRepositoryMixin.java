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

import java.util.Map;
import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.Maps;

import nepacks.config.NepacksConfig;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;

@Mixin(PackRepository.class)
public class PackRepositoryMixin {

    @Shadow
    @Final
    private Set<RepositorySource> sources;

    @Shadow
    @Final
    private Pack.PackConstructor constructor;

    @Inject(method = "discoverAvailable", at = @At("HEAD"), cancellable = true)
    private void nepacks_discoverAvailable(final CallbackInfoReturnable<Map<String, Pack>> callback) {
        if (!NepacksConfig.getConfig().sortMods) {
            return;
        }

        final Map<String, Pack> result = Maps.newLinkedHashMap();
        for (var repositorySource : this.sources) {
            repositorySource.loadPacks((pack) -> result.put(pack.getId(), pack), this.constructor);
        }
        callback.setReturnValue(result);
    }
}
