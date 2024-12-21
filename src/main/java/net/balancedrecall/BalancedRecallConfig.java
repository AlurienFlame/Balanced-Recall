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
    private static String configFilename = "balancedrecall.json";
    private static Path configDir = FabricLoader.getInstance().getConfigDir();
    private static Path configPath = configDir.resolve(configFilename);
    private static File configFile = configPath.toFile();

    // Configs
    public JsonObject jsonObject;

    // Constructor creates the config file if it doesn't exist
    public BalancedRecallConfig() {
        if (!Files.exists(configPath)) {
            create();
            setToDefaults();
            write();
        }
        read();
    }

    public void setToDefaults() {
        jsonObject = new JsonObject();
        jsonObject.addProperty("take_damage_interrupts_recall", true);
        jsonObject.addProperty("take_damage_puts_mirror_on_cooldown", true);
        jsonObject.addProperty("recall_impossible_when_monsters_nearby", true);
        jsonObject.addProperty("magic_mirror_use_time_ticks", 20);
        jsonObject.addProperty("magic_mirror_cooldown_time_seconds", 1);
        jsonObject.addProperty("dimensional_mirror_use_time_ticks", 20);
        jsonObject.addProperty("dimensional_mirror_cooldown_time_seconds", 1);
        jsonObject.addProperty("sleeping_mat_resets_phantom_timer", true);
    }

    // Setters and getters
    // TODO: validate
    public void set(String key, Boolean value) {
        jsonObject.addProperty(key, value);
    }
    public void set(String key, String value) {
        jsonObject.addProperty(key, Integer.parseInt(value));
    }
    public Boolean getBoolean(String key) {
        return jsonObject.get(key).getAsBoolean();
    }
    public String getInteger(String key) {
        return jsonObject.get(key).toString();
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
    public void read() {
        try {
            FileReader reader = new FileReader(configFile);
            jsonObject = new Gson().fromJson(reader, JsonObject.class);
            reader.close();
        } catch (Exception e) {
            System.out.println(String.format("[Balanced Recall] Error reading config file: %s", e.toString()));
        }
    }

    // Write the current config to the file.
    public void write() {
        try {
            FileWriter writer = new FileWriter(configFile);
            new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject, writer);
            writer.close();
        } catch (IOException e) {
            System.out.println(String.format("[Balanced Recall] Error saving config file: %s", e.toString()));
        }
    }
}
