package net.balancedrecall;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

public class BalancedRecallConfig {
    private static final Path configPath = FabricLoader.getInstance().getConfigDir().resolve("balancedrecall.json");
    private static final File configFile = configPath.toFile();

    // Maps camelCase fields to snake_case JSON keys, so the fields below are the
    // single source of truth for both key names and default values.
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();

    // A pristine instance whose field initializers still hold the default values
    public static final BalancedRecallConfig DEFAULTS = new BalancedRecallConfig();

    public boolean takeDamageInterruptsRecall = true;
    public boolean takeDamagePutsMirrorOnCooldown = true;
    public boolean recallImpossibleWhenMonstersNearby = false;
    public double magicMirrorUseTimeSeconds = 1;
    public double magicMirrorCooldownTimeSeconds = 1;
    public double dimensionalMirrorUseTimeSeconds = 1;
    public double dimensionalMirrorCooldownTimeSeconds = 1;
    public boolean sleepingMatResetsPhantomTimer = false;

    public static BalancedRecallConfig read() {
        if (!Files.exists(configPath)) {
            BalancedRecallConfig config = new BalancedRecallConfig();
            config.write();
            return config;
        }
        try (FileReader reader = new FileReader(configFile)) {
            BalancedRecallConfig config = GSON.fromJson(reader, BalancedRecallConfig.class);
            if (config != null) {
                return config;
            }
        } catch (Exception e) {
            System.out.println(String.format("[Balanced Recall] Error reading config file: %s", e.toString()));
        }
        return new BalancedRecallConfig();
    }

    // Write the current config to the file.
    public void write() {
        try {
            configFile.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(configFile)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            System.out.println(String.format("[Balanced Recall] Error saving config file: %s", e.toString()));
        }
    }

    public void setToDefaults() {
        takeDamageInterruptsRecall = DEFAULTS.takeDamageInterruptsRecall;
        takeDamagePutsMirrorOnCooldown = DEFAULTS.takeDamagePutsMirrorOnCooldown;
        recallImpossibleWhenMonstersNearby = DEFAULTS.recallImpossibleWhenMonstersNearby;
        magicMirrorUseTimeSeconds = DEFAULTS.magicMirrorUseTimeSeconds;
        magicMirrorCooldownTimeSeconds = DEFAULTS.magicMirrorCooldownTimeSeconds;
        dimensionalMirrorUseTimeSeconds = DEFAULTS.dimensionalMirrorUseTimeSeconds;
        dimensionalMirrorCooldownTimeSeconds = DEFAULTS.dimensionalMirrorCooldownTimeSeconds;
        sleepingMatResetsPhantomTimer = DEFAULTS.sleepingMatResetsPhantomTimer;
    }

    public boolean areDefaults() {
        return takeDamageInterruptsRecall == DEFAULTS.takeDamageInterruptsRecall &&
            takeDamagePutsMirrorOnCooldown == DEFAULTS.takeDamagePutsMirrorOnCooldown &&
            recallImpossibleWhenMonstersNearby == DEFAULTS.recallImpossibleWhenMonstersNearby &&
            magicMirrorUseTimeSeconds == DEFAULTS.magicMirrorUseTimeSeconds &&
            magicMirrorCooldownTimeSeconds == DEFAULTS.magicMirrorCooldownTimeSeconds &&
            dimensionalMirrorUseTimeSeconds == DEFAULTS.dimensionalMirrorUseTimeSeconds &&
            dimensionalMirrorCooldownTimeSeconds == DEFAULTS.dimensionalMirrorCooldownTimeSeconds &&
            sleepingMatResetsPhantomTimer == DEFAULTS.sleepingMatResetsPhantomTimer;
    }
}
