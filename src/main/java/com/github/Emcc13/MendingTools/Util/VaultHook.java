package com.github.Emcc13.MendingTools.Util;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public class VaultHook {
    private final Economy economy;
    private final Permission permission;

    public VaultHook() {
        RegisteredServiceProvider<Economy> rspEconomy = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);

        RegisteredServiceProvider<Permission> rspPermissions = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);

        this.economy = (rspEconomy == null) ? null : (Economy)rspEconomy.getProvider();
        this.permission = (rspPermissions == null) ? null : (Permission)rspPermissions.getProvider();
    }

    public boolean hooked() {
        return (this.economy != null && this.permission != null);
    }

    public boolean hasEnough(@NotNull Player player, double amount) {
        return (this.economy != null && this.economy.has((OfflinePlayer)player, amount));
    }

    public void takeMoney(@NotNull Player player, double amount) {
        if (this.economy == null)
            return;  this.economy.withdrawPlayer((OfflinePlayer)player, amount);
    }

    public void giveMoney(@NotNull Player player, double amount) {
        if (this.economy == null)
            return;  this.economy.depositPlayer((OfflinePlayer)player, amount);
    }

    public boolean hasPermission(@NotNull Player player, @NotNull String permissionNode) {
        return (this.permission != null && this.permission.has(player, permissionNode));
    }

    public void takePermission(@NotNull Player player, @NotNull String permissionNode) {
        if (this.permission == null)
            return;  this.permission.playerRemove(null, (OfflinePlayer)player, permissionNode);
    }

    public void givePermission(@NotNull Player player, @NotNull String permissionNode) {
        if (this.permission == null)
            return;  this.permission.playerAdd(null, (OfflinePlayer)player, permissionNode);
    }
}
