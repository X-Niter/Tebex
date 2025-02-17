package com.github.xniter.tebexio.command;

import com.github.xniter.tebexio.TebexForged;
import com.github.xniter.tebexio.util.CmdUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.buycraft.plugin.BuyCraftAPI;
import net.buycraft.plugin.data.responses.ServerInformation;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;

import java.io.IOException;

public class SecretCmd implements Command<CommandSourceStack> {
    private final TebexForged plugin;

    public SecretCmd(final TebexForged plugin) {
        this.plugin = plugin;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (context.getSource().getEntity() != null) {
            ForgeMessageUtil.sendMessage(context.getSource(), new TextComponent(ForgeMessageUtil.format("secret_console_only"))
                    .setStyle(CmdUtil.ERROR_STYLE));
            return 0;
        }

        String secret = StringArgumentType.getString(context, "secret");
        plugin.getPlatform().executeAsync(() -> {
            String currentKey = plugin.getConfiguration().getServerKey();
            BuyCraftAPI client = BuyCraftAPI.create(secret, plugin.getHttpClient());

            try {
                plugin.updateInformation(client);
            } catch (IOException e) {
                plugin.getLogger().error("Unable to verify secret", e);
                ForgeMessageUtil.sendMessage(context.getSource(), new TextComponent(ForgeMessageUtil.format("secret_does_not_work"))
                        .setStyle(CmdUtil.ERROR_STYLE));
                return;
            }

            ServerInformation information = plugin.getServerInformation();
            plugin.setApiClient(client);
            plugin.getConfiguration().setServerKey(secret);

            try {
                plugin.saveConfiguration();
            } catch (IOException e) {
                ForgeMessageUtil.sendMessage(context.getSource(), new TextComponent(ForgeMessageUtil.format("secret_cant_be_saved"))
                        .setStyle(CmdUtil.ERROR_STYLE));
            }

            ForgeMessageUtil.sendMessage(context.getSource(), new TextComponent(ForgeMessageUtil.format("secret_success",
                    information.getServer().getName(), information.getAccount().getName())).setStyle(CmdUtil.SUCCESS_STYLE));

            boolean repeatChecks = currentKey.equals("INVALID");

            plugin.getDuePlayerFetcher().run(repeatChecks);
        });
        return 1;
    }
}