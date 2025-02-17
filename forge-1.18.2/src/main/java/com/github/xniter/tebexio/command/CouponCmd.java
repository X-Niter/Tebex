package com.github.xniter.tebexio.command;

import com.github.xniter.tebexio.TebexForged;
import com.github.xniter.tebexio.util.CmdUtil;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.buycraft.plugin.data.Coupon;
import net.buycraft.plugin.shared.util.CouponUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;

import java.io.IOException;

public class CouponCmd {
    private final TebexForged plugin;

    public CouponCmd(final TebexForged plugin) {
        this.plugin = plugin;
    }

    public int create(CommandContext<CommandSourceStack> context) {
        if (plugin.getApiClient() == null) {
            ForgeMessageUtil.sendMessage(context.getSource(), new TextComponent(ForgeMessageUtil.format("generic_api_operation_error"))
                    .setStyle(CmdUtil.ERROR_STYLE));
            return 0;
        }

        final Coupon coupon;
        try {
            coupon = CouponUtil.parseArguments(StringArgumentType.getString(context, "data").split(" "));
        } catch (Exception e) {
            ForgeMessageUtil.sendMessage(context.getSource(), new TextComponent(ForgeMessageUtil.format("coupon_creation_arg_parse_failure", e.getMessage()))
                    .setStyle(CmdUtil.ERROR_STYLE));
            return 0;
        }

        plugin.getPlatform().executeAsync(() -> {
            try {
                plugin.getApiClient().createCoupon(coupon).execute();
                ForgeMessageUtil.sendMessage(context.getSource(), new TextComponent(ForgeMessageUtil.format("coupon_creation_success", coupon.getCode()))
                        .setStyle(CmdUtil.SUCCESS_STYLE));
            } catch (IOException e) {
                ForgeMessageUtil.sendMessage(context.getSource(), new TextComponent(ForgeMessageUtil.format("generic_api_operation_error"))
                        .setStyle(CmdUtil.ERROR_STYLE));
            }
        });

        return 1;
    }

    public int delete(CommandContext<CommandSourceStack> context) {
        if (plugin.getApiClient() == null) {
            ForgeMessageUtil.sendMessage(context.getSource(), new TextComponent(ForgeMessageUtil.format("generic_api_operation_error"))
                    .setStyle(CmdUtil.ERROR_STYLE));
            return 0;
        }

        String code = StringArgumentType.getString(context, "code");
        plugin.getPlatform().executeAsync(() -> {
            try {
                plugin.getApiClient().deleteCoupon(code).execute();
                ForgeMessageUtil.sendMessage(context.getSource(), new TextComponent(ForgeMessageUtil.format("coupon_deleted")).setStyle(CmdUtil.SUCCESS_STYLE));
            } catch (Exception e) {
                ForgeMessageUtil.sendMessage(context.getSource(), new TextComponent(e.getMessage()).setStyle(CmdUtil.ERROR_STYLE));
            }
        });

        return 1;
    }
}