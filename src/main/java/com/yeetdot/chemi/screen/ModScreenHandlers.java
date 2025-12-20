package com.yeetdot.chemi.screen;

import com.yeetdot.chemi.Chemi;
import com.yeetdot.chemi.screen.custom.CrucibleScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public class ModScreenHandlers {
    public static final ScreenHandlerType<CrucibleScreenHandler> CRUCIBLE_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, Identifier.of(Chemi.MOD_ID, "crucible_screen_handler"), new ExtendedScreenHandlerType<>(CrucibleScreenHandler::new, BlockPos.PACKET_CODEC));

    private static <T extends ScreenHandler, D> ScreenHandlerType<T> register(String id, ExtendedScreenHandlerType.ExtendedFactory<@NotNull T, @NotNull D> factory, PacketCodec<? super RegistryByteBuf, D> packetCodec) {
        return Registry.register(Registries.SCREEN_HANDLER, Identifier.of(Chemi.MOD_ID, id), new ExtendedScreenHandlerType<>(factory, packetCodec));
    }

    public static void registerScreenHandlers() {}
}
