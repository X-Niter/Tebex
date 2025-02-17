package com.github.xniter.tebexio.command;

import com.github.xniter.tebexio.TebexForged;
import com.github.xniter.tebexio.util.CmdUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;

public class BuyCommand implements Command<CommandSourceStack> {

    private final TebexForged plugin;

    public BuyCommand(TebexForged plugin) {
        this.plugin = plugin;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (plugin.getServerInformation() == null) {
            ForgeMessageUtil.sendMessage(context.getSource(), Component.literal(ForgeMessageUtil.format("information_no_server"))
                    .setStyle(CmdUtil.ERROR_STYLE));
            return 1;
        }

        ForgeMessageUtil.sendMessage(context.getSource(), Component.literal("                                            ").withStyle(ChatFormatting.STRIKETHROUGH));
        ForgeMessageUtil.sendMessage(context.getSource(), Component.literal(ForgeMessageUtil.format("To view the webstore, click this link: ")).withStyle(style -> {
            return style.withColor(ChatFormatting.GREEN)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, plugin.getServerInformation().getAccount().getDomain()));
        }));
        ForgeMessageUtil.sendMessage(context.getSource(), Component.literal(plugin.getServerInformation().getAccount().getDomain()).withStyle().withStyle(style -> {
            return style.withColor(ChatFormatting.BLUE).withUnderlined(true)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, plugin.getServerInformation().getAccount().getDomain()));
        }));
        ForgeMessageUtil.sendMessage(context.getSource(), Component.literal("                                            ").withStyle(ChatFormatting.STRIKETHROUGH));
        return 1;
    }
}