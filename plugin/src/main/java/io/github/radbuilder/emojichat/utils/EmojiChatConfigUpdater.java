package io.github.radbuilder.emojichat.utils;

import io.github.radbuilder.emojichat.EmojiChat;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * EmojiChat config updater.
 *
 * @author RadBuilder
 * @version 1.8
 * @since 1.5
 */
public class EmojiChatConfigUpdater {
	/**
	 * The current config version number.
	 */
	private final int CONFIG_VERSION = 5;
	
	/**
	 * Creates the EmojiChat config updater with the main class instance.
	 *
	 * @param plugin The EmojiChat main class instance.
	 */
	public EmojiChatConfigUpdater(EmojiChat plugin) {
		int configVersion = plugin.getConfig().getInt("config-version");
		
		if (configVersion < CONFIG_VERSION) {
			plugin.getLogger().info("Updating your config now (old: " + configVersion + ", new: " + CONFIG_VERSION + ")...");
			updateConfig(plugin, plugin.getConfig(), configVersion);
		}
	}
	
	/**
	 * Updates the EmojiChat config.
	 *
	 * @param plugin The EmojiChat main class instance.
	 * @param config The EmojiChat config.
	 * @param configVersion The current config version number.
	 */
	private void updateConfig(EmojiChat plugin, FileConfiguration config, int configVersion) {
		// Config v1 & v2 values
		boolean fixEmojiColoring = config.getBoolean("fix-emoji-coloring");
		List<String> disabledEmojis = config.getStringList("disabled-emojis");
		LinkedHashMap<String, List<String>> shortcuts = new LinkedHashMap<>();
		for (String key : config.getConfigurationSection("shortcuts").getKeys(false)) { // Gets all of the headers/keys in the shortcuts section
			shortcuts.put(key, config.getStringList("shortcuts." + key));
		}
		
		// Config v2 values
		String metricsCollection = "FULL";
		boolean downloadResourcePack = true;
		if (configVersion > 1) {
			metricsCollection = config.getString("metrics-collection");
			downloadResourcePack = config.getBoolean("download-resourcepack");
		}
		
		// Config v4 values
		boolean emojisOnSigns = config.contains("emojis-on-signs") ? config.getBoolean("emojis-on-signs") : false;
		boolean emojisInCommands = config.contains("emojis-in-commands") ? config.getBoolean("emojis-in-commands") : false;
		boolean onlyCommandList = config.contains("only-command-list") ? config.getBoolean("only-command-list") : false;
		List<String> commandList = config.contains("command-list") ? config.getStringList("command-list") : Arrays.asList("/msg", "/tell");
		int packVariant = config.contains("pack-variant") ? config.getInt("pack-variant") : 1;
		String packUrl = config.contains("pack-url") ? config.getString("pack-url") : "";
		String packHash = config.contains("pack-hash") ? config.getString("pack-hash") : "";
		boolean disableEmojis = config.contains("disable-emojis") ? config.getBoolean("disable-emojis") : true;
		
		// Config lines
		List<String> configLines = new ArrayList<>();
		configLines.add("# Configuration file for EmojiChat by RadBuilder");
		configLines.add("");
		configLines.add("# EmojiChat collects metrics in order to get a better understanding of what configurations are used.");
		configLines.add("# Statistics include how many servers it's being used on and what version of Bukkit/Spigot they're using,");
		configLines.add("# how many players are using it, server information such as Java version, how many emojis are used,");
		configLines.add("# and how many servers are using each feature. This data is anonymous, and is submitted to understand what");
		configLines.add("# types of server configurations are being used to build a better plugin (i.e. MC/Java versions to continue supporting,");
		configLines.add("# features to keep/remove). Data collection has very little/no impact on server performance. I'd appreciate if you");
		configLines.add("# keep this set to FULL, but I completely understand if you want to send less data or opt out.");
		configLines.add("#");
		configLines.add("# 'FULL'  collects what 'SOME' collects, and data on what config options you're using.");
		configLines.add("# 'SOME'  collects what 'BASIC' collects, and what hooks you're using.");
		configLines.add("# 'BASIC' collects data on what Java version you're using, Bukkit/Spigot version you're using, other general server");
		configLines.add("#         information like player count, how many emojis you've used, and shortcuts you've used.");
		configLines.add("# 'OFF'   collects NO DATA, however, I would appreciate it if you send at least basic data.");
		configLines.add("metrics-collection: '" + metricsCollection + "'");
		configLines.add("");
		configLines.add("# If you're using chat color plugins, this will remove the coloring for emojis to be displayed correctly.");
		configLines.add("fix-emoji-coloring: " + fixEmojiColoring);
		configLines.add("");
		configLines.add("# If emojis should be displayed on signs (shortcuts and full names supported).");
		configLines.add("emojis-on-signs: " + emojisOnSigns);
		configLines.add("");
		configLines.add("# If commands should have emojis (shortcuts and full names supported).");
		configLines.add("emojis-in-commands: " + emojisInCommands);
		configLines.add("# If emojis should ONLY work with commands in 'command-list'.");
		configLines.add("only-command-list: " + onlyCommandList);
		configLines.add("# If 'only-command-list' is true, the commands here will be the only commands where emojis are allowed.");
		configLines.add("command-list:");
		for (String command : commandList) {
			configLines.add("- '" + command + "'");
		}
		configLines.add("");
		configLines.add("# If EmojiChat should auto download the ResourcePack. If you'd rather have your players manually");
		configLines.add("# download or use /emojichat resourcepack, set this to false.");
		configLines.add("download-resourcepack: " + downloadResourcePack);
		configLines.add("# The resource pack variant to use.");
		configLines.add("# 1 replaces Korean characters with emojis, and 2 replaces Chinese characters with emojis.");
		configLines.add("# WARNING: Changing this will ruin things like signs that already have the other language's characters!");
		configLines.add("# Don't change this unless your normal language is being overwritten by emojis.");
		configLines.add("# Variant 2 is now default because of https://bugs.mojang.com/browse/MC-41270 .");
		configLines.add("pack-variant: " + packVariant);
		configLines.add("# If the resource pack should be 'HD' (High Definition) or 'SD' (Standard Definition).");
		configLines.add("# HD textures aren't compatible with Minecraft (not server) versions 1.13+,");
		configLines.add("# and will result in emojis not being displayed correctly.");
		configLines.add("# If you're using a server that supports any Minecraft versions including 1.13+, use SD.");
		configLines.add("# Otherwise use HD for better quality.");
		configLines.add("# The file size difference between HD and SD packs is extremely small, and shouldn't");
		configLines.add("# be a factor when choosing which pack to use.");
		configLines.add("pack-quality: 'SD'");
		configLines.add("");
		configLines.add("pack-url: '" + packUrl + "'");
		configLines.add("pack-hash: '" + packHash + "'");
		configLines.add("");
		configLines.add("# Shortcuts will replace the items in the list with the correct emoji name.");
		configLines.add("# For example, :) will be replaced with :grinning:, which then will turn it into the emoji.");
		configLines.add("shortcuts:");
		for (String shortcutKey : shortcuts.keySet()) {
			configLines.add("  " + shortcutKey + ":");
			for (String shortcutListItem : shortcuts.get(shortcutKey)) {
				configLines.add("  - '" + shortcutListItem + "'");
			}
		}
		if (configVersion == 1) {
			configLines.add("  crazy_face:");
			configLines.add("  - ':crazy:'");
			configLines.add("  face_with_raised_eyebrow:");
			configLines.add("  - ':hmm:'");
			configLines.add("  shushing_face:");
			configLines.add("  - ':shh:'");
		}
		if (configVersion < 3) {
			configLines.add("  1st_place_medal:");
			configLines.add("  - ':first:'");
			configLines.add("  - ':1st:'");
			configLines.add("  2nd_place_medal:");
			configLines.add("  - ':second:'");
			configLines.add("  - ':2nd:'");
			configLines.add("  3rd_place_medal:");
			configLines.add("  - ':third:'");
			configLines.add("  - ':3rd:'");
		}
		if (configVersion < 4) {
			configLines.add("  microphone:");
			configLines.add("  - ':mic:'");
			configLines.add("  musical_keyboard:");
			configLines.add("  - ':piano:'");
			configLines.add("  video_game:");
			configLines.add("  - ':controller:'");
			configLines.add("  dart:");
			configLines.add("  - ':target:'");
			configLines.add("  game_die:");
			configLines.add("  - ':dice:'");
			configLines.add("  - ':die:'");
			configLines.add("  heart:");
			configLines.add("  - '<3'");
			configLines.add("  broken_heart:");
			configLines.add("  - '</3'");
			configLines.add("  zero:");
			configLines.add("  - ':0:'");
			configLines.add("  one:");
			configLines.add("  - ':1:'");
			configLines.add("  two:");
			configLines.add("  - ':2:'");
			configLines.add("  three:");
			configLines.add("  - ':3:'");
			configLines.add("  four:");
			configLines.add("  - ':4:'");
			configLines.add("  five:");
			configLines.add("  - ':5:'");
			configLines.add("  six:");
			configLines.add("  - ':6:'");
			configLines.add("  seven:");
			configLines.add("  - ':7:'");
			configLines.add("  eight:");
			configLines.add("  - ':8:'");
			configLines.add("  nine:");
			configLines.add("  - ':9:'");
			configLines.add("  keycap_ten:");
			configLines.add("  - ':ten:'");
			configLines.add("  - ':10:'");
			configLines.add("  asterisk:");
			configLines.add("  - ':*:'");
		}
		configLines.add("");
		configLines.add("# If certain emojis should be disabled or not.");
		configLines.add("# If true, it will disable all of the emojis specified in 'disabled-emojis'");
		configLines.add("# If false, emojis specified in 'disabled-emojis' will be ignored.");
		configLines.add("disable-emojis: " + disableEmojis);
		configLines.add("# Emojis to disable. Remove them from the list to enable them.");
		configLines.add("# By default, profane and potentially offensive emojis are disabled.");
		configLines.add("disabled-emojis:");
		for (String disabledEmoji : disabledEmojis) {
			configLines.add("- '" + disabledEmoji + "'");
		}
		if (configVersion == 1) {
			configLines.add("- ':sweat_drops:'");
			configLines.add("- ':banana:'");
			configLines.add("- ':cherries:'");
			configLines.add("- ':peach:'");
			configLines.add("- ':tomato:'");
			configLines.add("- ':eggplant:'");
			configLines.add("- ':cucumber:'");
			configLines.add("- ':beer:'");
			configLines.add("- ':beers:'");
			configLines.add("- ':clinking_glasses:'");
			configLines.add("- ':wine_glass:'");
			configLines.add("- ':tumbler_glass:'");
			configLines.add("- ':cocktail:'");
			configLines.add("- ':face_with_symbols_over_mouth:'");
			configLines.add("- ':face_vomiting:'");
		}
		configLines.add("");
		configLines.add("# The config version, used to be able to update your config when future versions come out.");
		configLines.add("# Don't change this, or you'll experience issues with EmojiChat.");
		configLines.add("config-version: " + CONFIG_VERSION);
		
		// Update the config
		setConfig(plugin, configLines);
		// Clear non-used lists
		disabledEmojis.clear();
		shortcuts.clear();
	}
	
	/**
	 * Sets the config to be the specified set of lines.
	 *
	 * @param plugin The EmojiChat main class instance.
	 * @param configLines The list of lines to set the config to.
	 */
	private void setConfig(EmojiChat plugin, List<String> configLines) {
		try {
			File configFile = new File(plugin.getDataFolder() + "/config.yml");
			if (!configFile.delete()) { // Delete the old config
				plugin.getLogger().warning("Failed to delete the old config. If this continues: back up your config, manually delete it, then restart.");
			}
			
			// Create the new config
			configFile = new File(plugin.getDataFolder() + "/config.yml");
			FileOutputStream fileOutputStream = new FileOutputStream(configFile);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
			Writer writer = new BufferedWriter(outputStreamWriter);
			for (String configLine : configLines) {
				writer.write(configLine + "\n");
			}
			
			// Cleanup
			writer.close();
			outputStreamWriter.close();
			fileOutputStream.close();
			configLines.clear();
			plugin.getLogger().info("Config successfully updated.");
		} catch (Exception e) {
			plugin.getLogger().severe("An error occured while updating your config. More details below.");
			e.printStackTrace();
		}
	}
}
