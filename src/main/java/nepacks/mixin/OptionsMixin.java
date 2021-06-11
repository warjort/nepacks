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

import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Lists;

import nepacks.config.NepacksConfig;
import net.minecraft.client.Options;
import net.minecraft.server.packs.repository.PackRepository;

@Mixin(Options.class)
public class OptionsMixin {

    @Shadow
    @Final
    private List<String> resourcePacks;

    @Shadow
    @Final
    private List<String> incompatibleResourcePacks;

    @Shadow
    @Final
    private static Logger LOGGER;

    @SuppressWarnings("resource")
    @Inject(method = "loadSelectedResourcePacks", at = @At("HEAD"), cancellable = true)
    private void nepacks_loadSelectedResourcePacks(final PackRepository packRepository, final CallbackInfo callback) {
        if (!NepacksConfig.getConfig().sortMods) {
            return;
        }

        final List<String> ids = Lists.newArrayList();
        var packs = this.resourcePacks.iterator();

        while (packs.hasNext()) {
            var id = packs.next();
            var pack = packRepository.getPack(id);
            if (pack == null && !id.startsWith("file/")) {
               pack = packRepository.getPack("file/" + id);
            }

            if (pack == null) {
                LOGGER.warn("Removed resource pack {} from options because it doesn't seem to exist anymore", id);
                packs.remove();
            } else if (!pack.getCompatibility().isCompatible() && !this.incompatibleResourcePacks.contains(id)) {
                LOGGER.warn("Removed resource pack {} from options because it is no longer compatible", id);
                packs.remove();
            } else if (pack.getCompatibility().isCompatible() && this.incompatibleResourcePacks.contains(id)) {
                LOGGER.info("Removed resource pack {} from incompatibility list because it's now compatible", id);
                this.incompatibleResourcePacks.remove(id);
            } else {
                ids.add(pack.getId());
            }
         }
         packRepository.setSelected(ids);
         callback.cancel();
    }
}
