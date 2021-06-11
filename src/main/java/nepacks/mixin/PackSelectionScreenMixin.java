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

import java.nio.file.Files;
import java.nio.file.Path;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.platform.NativeImage;

import nepacks.NepacksMain;
import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.Pack;

@Mixin(PackSelectionScreen.class)
public class PackSelectionScreenMixin {

    @Shadow
    @Final
    private static ResourceLocation DEFAULT_ICON;

    @SuppressWarnings("static-method")
    @Inject(method = "loadPackIcon", at = @At("RETURN"), cancellable = true)
    private void nepacks_loadPackIcon(final TextureManager textureManager, final Pack pack, final CallbackInfoReturnable<ResourceLocation> callback) {
        var result = callback.getReturnValue();
        if (result != DEFAULT_ICON) {
            return;
        }
        try (final var packResources = pack.open()) {
            if (packResources instanceof ModResourcePack modResourcePack) {
                final var mod = modResourcePack.getFabricModMetadata();
                final var iconPath = mod.getIconPath(512).orElse("assets/" + mod.getId() + "/icon.png");
                final var path = getModFilePath(mod.getId()).resolve(iconPath);
                try (final var inputStream = Files.newInputStream(path)) {
                    if (inputStream != null) {
                        result = new ResourceLocation(NepacksMain.MOD_ID, "pack/" + mod.getId() + "/icon");
                        try (final var nativeImage = NativeImage.read(inputStream)) {
                            register(textureManager, result, nativeImage);
                            callback.setReturnValue(result);
                        }
                    }
                }
            }
        } catch (@SuppressWarnings("unused") Exception unused) {
            // Couldn't find/load an image
        }
    }

    @SuppressWarnings("resource") // The texture manager closes it
    private static void register(final TextureManager textureManager, final ResourceLocation resourceLocation, final NativeImage nativeImage) {
        textureManager.register(resourceLocation, new DynamicTexture(nativeImage));
    }

    private static Path getModFilePath(final String modId) {
        return NepacksMain.getModContainer(modId).getRootPath();
    }
}
