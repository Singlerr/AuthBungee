package io.github.singlerr;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

public class ReloadCMD extends Command {
    public ReloadCMD(String cmd){
        super(cmd);
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if(commandSender.hasPermission("ab.reload")){
            Main.getInstance().reload(commandSender);
        }else{
            commandSender.sendMessage(new ComponentBuilder("권한이 없습니다.").color(ChatColor.RED).create());
        }
    }
}
