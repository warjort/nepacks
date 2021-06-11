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
package nepacks.config;

import static nepacks.NepacksMain.log;

import java.nio.file.Files;

import nepacks.NepacksMain;

public class NepacksConfig extends Config {

    private static final String CONFIG_FILE_NAME = NepacksMain.MOD_ID + ".json";
    private static NepacksConfig CONFIG = null;

    public final String sortModsComment = "Whether to initially sort mods by dependencies";
    public boolean sortMods = false;

    public static final NepacksConfig getConfig() {
        if (CONFIG == null)
            CONFIG = loadConfig();
        return CONFIG;
    }

    public static NepacksConfig loadConfig() {
        NepacksConfig result = new NepacksConfig();
        final var file = getConfigFile(CONFIG_FILE_NAME);
        if (Files.exists(file)) {
            result = readFile(file, NepacksConfig.class);
        }
        writeFile(file, result);
        return result;
    }

    public void save() {
        final var file = getConfigFile(CONFIG_FILE_NAME);
        writeFile(file, this);
    }

    public static boolean reload() {
        final var file = getConfigFile(CONFIG_FILE_NAME);
        try {
            CONFIG = loadConfig();
            return true;
        } catch (Exception e) {
            log.error("Error reloading: " + file, e);
            return false;
        }
    }
}