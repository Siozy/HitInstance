package siozy.dev.lunaspring.API.util.service.managers;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.OfflinePlayer;
import siozy.dev.lunaspring.API.util.service.realized.VaultService;

@UtilityClass
public class VaultManager {
    @Getter private VaultService vaultService;

    public void initialize() {
        vaultService = new VaultService();
    }

    public void withdraw(OfflinePlayer player, double amount) {
        vaultService.withdraw(player, amount);
    }

    public void deposit(OfflinePlayer player, double amount) {
        vaultService.deposit(player, amount);
    }

    public double getBalance(OfflinePlayer player) {
        return vaultService.getBalance(player);
    }

    public boolean hasEnoughMoney(OfflinePlayer player, double amount) {
        return vaultService.hasEnoughMoney(player, amount);
    }

    public boolean isEnabled() {
        return vaultService.isEnabled();
    }
}
