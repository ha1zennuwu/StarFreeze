package dev.ha1zen;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class StarFreeze extends JavaPlugin implements Listener {

    private Set<UUID> freezePlayers = new HashSet<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("freeze").setExecutor(new FreezeCommand());
        getCommand("unfreeze").setExecutor(new UnfreezeCommand());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (isFreeze(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (isFreeze(event.getPlayer())) {
            event.setTo(event.getFrom());
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        if (isFreeze(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        if (isFreeze(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (isFreeze(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (isFreeze(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (isFreeze(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (isFreeze(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && isFreeze((Player) event.getDamager())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player) {
            Player player = (Player) event.getEntered();
            if (isFreeze(player)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Нельзя садиться в транспорт пока вы заморожены");
            }
        }
    }

    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
        if (event.getExited() instanceof Player && isFreeze((Player) event.getExited())) {
            event.setCancelled(true);
        }
    }

    private boolean isFreeze(Player player) {
        return freezePlayers.contains(player.getUniqueId()) && !player.hasPermission("starfreeze.ignore");
    }

    private class FreezeCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!sender.hasPermission("starfreeze.admin")) {
                sender.sendMessage(ChatColor.RED + "У вас нет прав на использование этой команды.");
                return true;
            }
            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "Использование: /freeze <игрок>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Игрок не найден.");
                return true;
            }
            if (target.hasPermission("starfreeze.ignore")) {
                sender.sendMessage(ChatColor.RED + "Вы не можете заморозить этого игрока.");
                return true;
            }
            if (isFreeze(target)) {
                sender.sendMessage(ChatColor.RED + "Игрок уже заморожен.");
                return true;
            }
            freezePlayers.add(target.getUniqueId());
            target.sendMessage(ChatColor.RED + "Вы были заморожены.");
            sender.sendMessage(ChatColor.GREEN + "Игрок " + target.getName() + " был заморожен.");
            return true;
        }
    }

    private class UnfreezeCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!sender.hasPermission("starfreeze.admin")) {
                sender.sendMessage(ChatColor.RED + "У вас нет прав на использование этой команды.");
                return true;
            }
            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "Использование: /unfreeze <игрок>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Игрок не найден.");
                return true;
            }
            if (!isFreeze(target)) {
                sender.sendMessage(ChatColor.RED + "Игрок не заморожен.");
                return true;
            }
            freezePlayers.remove(target.getUniqueId());
            target.sendMessage(ChatColor.GREEN + "Вы были разморожены.");
            sender.sendMessage(ChatColor.GREEN + "Игрок " + target.getName() + " был разморожен.");
            return true;
        }
    }
}