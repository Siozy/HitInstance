package siozy.dev.lunaspring.API.util.service.realized;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import siozy.dev.lunaspring.API.util.modules.Modules;
import siozy.dev.lunaspring.API.util.service.PluginService;
import siozy.dev.lunaspring.API.util.utilities.Utils;
import siozy.dev.lunaspring.LunaSpring;
import siozy.dev.lunaspring.self.configuration.LSConfig;

@Getter
public class VaultService extends PluginService {
    private Economy economy;

    public VaultService() {
        super("Vault");
        if (!Utils.hasPlugin("Vault") || !Utils.isPluginEnabled("Vault")) {
            LunaSpring.getInstance().warning(LSConfig.getMessage("noDependency").replace("[dependency]", "Vault"));
        }
        else {
            this.economy = Modules.provide(Economy.class, () -> {
                LunaSpring.getInstance().warning(LSConfig.getMessage("noVaultProvider"));
                return null;
            });
        }
    }

    public double getBalance(OfflinePlayer player) {
        this.checkService();
        return this.economy.getBalance(player);
    }

    public void deposit(OfflinePlayer player, double amount) {
        this.checkService();
        this.economy.depositPlayer(player, amount);
    }

    public void withdraw(OfflinePlayer player, double amount) {
        this.checkService();
        this.economy.withdrawPlayer(player, amount);
    }

    public boolean hasEnoughMoney(OfflinePlayer player, double amount) {
        this.checkService();
        return this.getBalance(player) >= amount;
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && this.economy != null && this.economy.isEnabled();
    }
}