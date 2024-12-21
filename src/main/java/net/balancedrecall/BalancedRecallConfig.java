package net.balancedrecall;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.fabricmc.loader.api.FabricLoader;

public class BalancedRecallConfig {
    // File stuff
    static String configFilename = "balancedrecall.json";
    static Path configDir = FabricLoader.getInstance().getConfigDir();
    static Path configPath = configDir.resolve(configFilename);
    static File configFile = configPath.toFile();

    // Configs
    public static JsonObject config;

    // Constructor creates the config file if it doesn't exist
    public BalancedRecallConfig() {
        if (!Files.exists(configPath)) {
            create();
            // Set default values
            config = new JsonObject();
            config.addProperty("take_damage_interrupts_recall", true);
            config.addProperty("take_damage_puts_mirror_on_cooldown", true);
            config.addProperty("recall_impossible_when_monsters_nearby", true);
            config.addProperty("magic_mirror_use_time_ticks", 20);
            config.addProperty("magic_mirror_cooldown_time_seconds", 1);
            config.addProperty("dimensional_mirror_use_time_ticks", 20);
            config.addProperty("dimensional_mirror_cooldown_time_seconds", 1);
            config.addProperty("sleeping_mat_resets_phantom_timer", true);
            write();
        }
        read();
    }

    // Create the config file with default values.
    private void create() {
        try {
            configFile.getParentFile().mkdirs();
            Files.createFile(configPath);
        } catch (Exception e) {
            System.out.println(String.format("[Balanced Recall] Error creating config file: %s", e.toString()));
        }
    }

    // Update the current config with the values from the file.
    private void read() {
        try {
            FileReader reader = new FileReader(configFile);
            config = new Gson().fromJson(reader, JsonObject.class);
            reader.close();
        } catch (Exception e) {
            System.out.println(String.format("[Balanced Recall] Error reading config file: %s", e.toString()));
        }
    }

    // Write the current config to the file.
    public static void write() {
        try {
            FileWriter writer = new FileWriter(configFile);
            new GsonBuilder().setPrettyPrinting().create().toJson(config, writer);
            writer.close();
        } catch (IOException e) {
            System.out.println(String.format("[Balanced Recall] Error saving config file: %s", e.toString()));
        }
    }
}
