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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import nepacks.NepacksMain;
import nepacks.config.NepacksConfig;
import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackUtil;
import net.minecraft.server.packs.PackType;

@Mixin(ModResourcePackUtil.class)
public class ModResourcePackUtilMixin {

    @Inject(method = "appendModResourcePacks", at = @At("HEAD"), cancellable = true, remap = false)
    private static void nepacks_appendModResourcePacks(final List<ModResourcePack> packs, final PackType type, final String subPath, final CallbackInfo callback) {
        if (!NepacksConfig.getConfig().sortMods || subPath != null) {
            return;
        }

        NepacksMain.appendModResourcePacks(packs, type);
        callback.cancel();
    }
}
