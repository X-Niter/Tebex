package com.github.xniter.tebexio.util;


import com.github.xniter.tebexio.TebexForged;
import com.github.xniter.tebexio.command.ForgeMessageUtil;
import net.buycraft.plugin.data.responses.Version;
import net.buycraft.plugin.shared.util.VersionUtil;
import net.minecraft.Util;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static net.buycraft.plugin.shared.util.VersionUtil.isVersionGreater;

public class VersionCheck {
    private final TebexForged plugin;
    private final String pluginVersion;
    private final String secret;
    private Version lastKnownVersion;
    private boolean upToDate = true;

    public VersionCheck(final TebexForged plugin, final String pluginVersion, final String secret) {
        this.plugin = plugin;
        this.pluginVersion = pluginVersion;
        this.secret = secret;
    }

    public void verify() throws IOException {
        if (pluginVersion.endsWith("-SNAPSHOT") || pluginVersion.equalsIgnoreCase("NONE")) {
            return; // SNAPSHOT and IDE runServer versions ignore updates
        }

        lastKnownVersion = VersionUtil.getVersion(plugin.getHttpClient(), "forge", secret);
        if (lastKnownVersion == null) {
            return;
        }

        // Compare versions
        String latestVersionString = lastKnownVersion.getVersion();
        if (!latestVersionString.equals(pluginVersion)) {
            upToDate = !isVersionGreater(pluginVersion, latestVersionString);
            if (!upToDate) {
                plugin.getLogger().info(ForgeMessageUtil.format("update_available", lastKnownVersion.getVersion()));
            }
        }
    }

    @SubscribeEvent
    public void onPostLogin(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer().hasPermissions(2) && !upToDate) {
            plugin.getPlatform().executeAsyncLater(() ->
                            event.getPlayer().sendMessage(new TextComponent(ForgeMessageUtil.format("update_available", lastKnownVersion.getVersion()))
                                    .setStyle(CmdUtil.INFO_STYLE), Util.NIL_UUID),
                    3, TimeUnit.SECONDS);
        }
    }

    public Version getLastKnownVersion() {
        return this.lastKnownVersion;
    }

    public boolean isUpToDate() {
        return this.upToDate;
    }
}