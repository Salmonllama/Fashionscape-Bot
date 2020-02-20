/*
 * Copyright (c) 2020. Aleksei Gryczewski
 * All rights reserved.
 */

package dev.salmonllama.fsbot.commands.staff;

import com.vdurmont.emoji.EmojiParser;
import dev.salmonllama.fsbot.config.BotConfig;
import dev.salmonllama.fsbot.guthix.Command;
import dev.salmonllama.fsbot.guthix.CommandContext;
import dev.salmonllama.fsbot.guthix.CommandPermission;
import dev.salmonllama.fsbot.guthix.PermissionType;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class SetStatusCommand extends Command {
    @Override public String name() { return "Set Status"; }
    @Override public String description() { return "Updates the bot's current status"; }
    @Override public String usage()  { return "updatestatus <String status>"; }
    @Override public String category() { return "Staff"; }
    @Override public CommandPermission permission() { return new CommandPermission(PermissionType.ROLE, BotConfig.STAFF_ROLE); }
    @Override public Collection<String> aliases() { return new ArrayList<>(Arrays.asList("setstatus", "status")); }

    @Override
    public void onCommand(CommandContext ctx) {
        String[] args = ctx.getArgs();

        // argument checking
        if (args.length == 0) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(Color.YELLOW)
                    .addField("WARNING", "Not enough arguments supplied");
            ctx.getChannel().sendMessage(embed);
            return;
        }

        String status = String.join(" ", args);

        ctx.getApi().updateActivity(status);

        ctx.getMessage().addReaction(EmojiParser.parseToUnicode(":white_check_mark:"));
    }
}