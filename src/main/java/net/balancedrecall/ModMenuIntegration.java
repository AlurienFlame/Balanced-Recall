package net.balancedrecall;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<ConfigScreen> getModConfigScreenFactory() {
        return ConfigScreen::new;
    }
}
