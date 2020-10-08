/*
 * Copyright (C) 2020 Chew
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package pw.chew.chanserv.util;

import java.util.Properties;

// bot.properties manager
public class PropertiesManager {
    public static Properties properties;
    public static void loadProperties(Properties config) {
        properties = config;
    }

    /**
     * @return bot token from discord
     */
    public static String getToken() {
        return properties.getProperty("token");
    }

    /**
     * @return Client ID of the bot
     */
    public static String getClientId() {
        return properties.getProperty("client_id");
    }

    /**
     * @return Owner ID of the bot, all perms
     */
    public static String getOwnerId() {
        return properties.getProperty("owner_id");
    }

    /**
     * @return Bot prefix
     */
    public static String getPrefix() {
        return properties.getProperty("prefix");
    }

    /**
     * @return key to upload rories
     */
    public static String getRoryKey() {
        return properties.getProperty("rory_key");
    }
}