package me.HybridPlague.StopChat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	public String user;
	public String prefix;
	public Boolean required;
	public Boolean active;
	
	
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		this.loadValues();
		this.getServer().getPluginManager().registerEvents(this, this);
	}

	public void loadValues() {
		this.reloadConfig();
		this.active = true;
		this.prefix = this.getConfig().getString("Prefix");
		this.required = this.getConfig().getBoolean("Chat-Stopped.Require-Reason");
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if (this.active == false) {
			Player p = (Player) e.getPlayer();
			if (!p.hasPermission("stopchat.bypass")) {
				e.setCancelled(true);
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + this.getConfig().getString("Errors.Denied-Chat")));
				return;
			}
		}
		
	}
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("stopchat")) {
			if (!sender.hasPermission("stopchat.use")) {
				sender.sendMessage(ChatColor.RED + "Insufficient permission.");
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage("/stopchat <reload | stop | resume> [reason]");
				return true;
			}
			if (args.length == 1) {
				switch (args[0].toLowerCase()) {
				case "reload":
					this.loadValues();
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&aConfig reloaded."));
					break;
				case "resume":
					if (active == true) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + this.getConfig().getString("Errors.Not-Stopped")));
						return true;
					} else {
						this.active = true;
						for (String msg : this.getConfig().getStringList("Chat-Stopped.Resumed")) {
							Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg).replace("%p%", sender.getName()));
						}
						break;
					}
				case "stop":
					if (active == false) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + this.getConfig().getString("Errors.Already-Stopped")));
						return true;
					}
					if (required == true) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + this.getConfig().getString("Errors.No-Reason")));
						return true;
					}
					else {
						this.active = false;
						for (String msg : this.getConfig().getStringList("Chat-Stopped.No-Reason")) {
							Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg).replace("%p%", sender.getName()));
						}
						break;
					}
				}
				return true;
			}
			if (args.length > 1) {
				switch (args[0].toLowerCase()) {
				case "reload":
					this.loadValues();
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&aConfig reloaded."));
					break;
				case "resume":
					if (active == true) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + this.getConfig().getString("Errors.Not-Stopped")));
						return true;
					} else {
						this.active = true;
						for (String msg : this.getConfig().getStringList("Chat-Stopped.Resumed")) {
							Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg).replace("%p%", sender.getName()));
						}
						break;
					}
				case "stop":
					if (active == false) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + this.getConfig().getString("Errors.Already-Stopped")));
						return true;
					}
					StringBuilder message = new StringBuilder();
					for(int i = 1; i < args.length; i++){
					  message.append(args[i]);
					  if( (i + 1) != args.length){
					    message.append(" ");
					  }
					}
					String reason = message.toString();
					this.active = false;
					for (String msg : this.getConfig().getStringList("Chat-Stopped.With-Reason")) {
						Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg).replace("%p%", sender.getName())
								.replace("%reason%", reason));
					}
					break;
				}
				return true;
			}
		}
		return false;
	}
	
}
