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

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.impl.resource.loader.ModNioResourcePack;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.server.packs.PackType;

public class ModInformation {

    private final String modId;
    private final ModMetadata metaData;
    private final ModContainer container;
    private Collection<String> dependencies;

    public ModInformation(final ModContainer container) {
        this.container = container;
        this.metaData = this.container.getMetadata();
        this.modId = this.metaData.getId();
    }

    public String getModId() {
        return this.modId;
    }

    public ModContainer getModContainer() {
        return this.container;
    }

    public boolean isBuiltIn() {
        return this.metaData.getType().equals("builtin");
    }

    public Collection<String> getDependencies(final Collection<String> excluded) {
        if (this.dependencies != null) {
            return this.dependencies;
        }
        var depends = this.metaData.getDepends();
        if (depends.isEmpty()) {
            this.dependencies = Collections.emptySet();
            return this.dependencies;
        }
        this.dependencies = depends.stream().map(dep -> dep.getModId()).filter(id -> !excluded.contains(id)).collect(Collectors.toSet());
        return this.dependencies;
    }

    public ModResourcePack createPack(final PackType type) {
        return new ModNioResourcePack(this.metaData, this.container.getRootPath(), type, null, ResourcePackActivationType.ALWAYS_ENABLED);
    }
}
