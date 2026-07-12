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
    
    // defaults
    public static final Boolean DEFAULT_TAKE_DAMAGE_INTERRUPTS_RECALL = true;
    public static final Boolean DEFAULT_TAKE_DAMAGE_PUTS_MIRROR_ON_COOLDOWN = true;
    public static final Boolean DEFAULT_RECALL_IMPOSSIBLE_WHEN_MONSTERS_NEARBY = false;
    public static final Double DEFAULT_MAGIC_MIRROR_USE_TIME_SECONDS = 1D;
    public static final Double DEFAULT_MAGIC_MIRROR_COOLDOWN_TIME_SECONDS = 1D;
    public static final Double DEFAULT_DIMENSIONAL_MIRROR_USE_TIME_SECONDS = 1D;
    public static final Double DEFAULT_DIMENSIONAL_MIRROR_COOLDOWN_TIME_SECONDS = 1D;
    public static final Boolean DEFAULT_SLEEPING_MAT_RESETS_PHANTOM_TIMER = false;

    public JsonObject getDefaults() {
        JsonObject defaults = new JsonObject();
        defaults.addProperty("take_damage_interrupts_recall", DEFAULT_TAKE_DAMAGE_INTERRUPTS_RECALL);
        defaults.addProperty("take_damage_puts_mirror_on_cooldown", DEFAULT_TAKE_DAMAGE_PUTS_MIRROR_ON_COOLDOWN);
        defaults.addProperty("recall_impossible_when_monsters_nearby", DEFAULT_RECALL_IMPOSSIBLE_WHEN_MONSTERS_NEARBY);
        defaults.addProperty("magic_mirror_use_time_seconds", DEFAULT_MAGIC_MIRROR_USE_TIME_SECONDS);
        defaults.addProperty("magic_mirror_cooldown_time_seconds", DEFAULT_MAGIC_MIRROR_COOLDOWN_TIME_SECONDS);
        defaults.addProperty("dimensional_mirror_use_time_seconds", DEFAULT_DIMENSIONAL_MIRROR_USE_TIME_SECONDS);
        defaults.addProperty("dimensional_mirror_cooldown_time_seconds", DEFAULT_DIMENSIONAL_MIRROR_COOLDOWN_TIME_SECONDS);
        defaults.addProperty("sleeping_mat_resets_phantom_timer", DEFAULT_SLEEPING_MAT_RESETS_PHANTOM_TIMER);
        return defaults;
    }

    public void setToDefaults() {
        jsonObject = getDefaults();
    }

    public Boolean areDefaults() {
        return jsonObject.equals(getDefaults());
    }

    // Setters and getters
    public void set(String key, Boolean value) {
        jsonObject.addProperty(key, value);
    }
    public void set(String key, String value) {
        try {
            jsonObject.addProperty(key, Double.parseDouble(value));
        } catch (NumberFormatException e) {
            return;
        }
    }
    public Boolean getBoolean(String key) {
        return jsonObject.get(key).getAsBoolean();
    }
    public Double getDouble(String key) {
        return jsonObject.get(key).getAsDouble();
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
