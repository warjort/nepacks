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
package nepacks;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.server.packs.PackType;

public class NepacksMain {

    public static final Logger log = LogManager.getLogger();
    public static final String MOD_ID = "nepacks";

    public static ModContainer getModContainer(final String modId) {
        return FabricLoader.getInstance().getModContainer(modId)
                .orElseThrow(() -> new IllegalStateException("Unable to get ModContainer: " + modId));
    }

    public static void initMain() {
        // nothing
    }

    @SuppressWarnings("resource")
    public static void appendModResourcePacks(final List<ModResourcePack> packs, final PackType type) {
        var mods = sortMods();
        for (var mod : mods) {
            final var pack = mod.createPack(type);
            if (!pack.getNamespaces(type).isEmpty()) {
                packs.add(pack);
            }
        }
    }

    private static List<ModInformation> sortMods() {
        final var sorter = new ModSorter();
        final var containers = FabricLoader.getInstance().getAllMods();
        for (var container : containers) {
            sorter.propose(container);
        }
        return sorter.sort();
    }
}
