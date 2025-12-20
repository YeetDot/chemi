package com.yeetdot.chemi;

import com.yeetdot.chemi.screen.ModScreenHandlers;
import com.yeetdot.chemi.screen.custom.CrucibleScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class ChemiClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.CRUCIBLE_SCREEN_HANDLER, CrucibleScreen::new);
    }
}
