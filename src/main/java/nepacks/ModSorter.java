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
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.fabricmc.loader.api.ModContainer;

public class ModSorter {

    private final Collection<String> excluded = defaultExcluded();
    private final List<ModInformation> mods = Lists.newArrayList();

    public void propose(final ModContainer container) {
        final ModInformation mod = new ModInformation(container);
        if (mod.isBuiltIn()) {
            this.excluded.add(mod.getModId());
        } else {
            this.mods.add(mod);
        }
    }

    public List<ModInformation> sort() {
        // Sort alphabetically by mod id
        this.mods.sort(Comparator.comparing(ModInformation::getModId));

        final List<ModInformation> unresolved = Lists.newArrayList(this.mods);
        final Set<String> resolvedIds = Sets.newHashSet();
        final List<ModInformation> resolved = Lists.newArrayList();

        boolean changed = true;
        while (changed) {
            changed = false;
            for (var i = 0; i < unresolved.size(); ++i) {
                var current = unresolved.get(i);
                if (resolvedIds.containsAll(current.getDependencies(this.excluded))) {
                    resolved.add(current);
                    resolvedIds.add(current.getModId());
                    unresolved.remove(current);
                    changed = true;
                    // Start from the beginning alphabetically when we resolve something
                    break;
                }
            }
        }
        // Still unresolved stuff (must be circular dependencies?)
        for (var mod : unresolved) {
            resolved.add(mod);
        }
        return resolved;
    }

    private static Collection<String> defaultExcluded() {
        final Collection<String> result = Sets.newHashSet();
        result.add("minecraft");
        result.add("fabricloader");
        result.add("java");
        return result;
    }
}
