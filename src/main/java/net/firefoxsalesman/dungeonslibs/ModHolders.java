package net.firefoxsalesman.dungeonslibs;

import net.minecraft.core.Holder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

/**
 * Resolves the data-driven registry entries 1.21 made into keys (NeoForge port).
 * Holders come from the registries of the CALLING thread: the integrated server and the client keep
 * separate copies, and a holder from the wrong one fails to encode when the client sends it.
 */
public final class ModHolders {
    private ModHolders() {}

    public static Holder<Enchantment> enchantment(ResourceKey<Enchantment> key) {
        return registries().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
    }

    public static HolderLookup.Provider registries() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null && server.isSameThread()) return server.registryAccess();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            HolderLookup.Provider client = Client.registries();
            if (client != null) return client;
        }
        if (server != null) return server.registryAccess();
        throw new IllegalStateException("no registries yet: called before a world was loaded");
    }

    private static final class Client {
        static HolderLookup.Provider registries() {
            ClientLevel level = Minecraft.getInstance().level;
            return level == null ? null : level.registryAccess();
        }
    }
}
