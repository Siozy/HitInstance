package siozy.dev.lunaspring;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.messaging.PluginMessageRecipient;
import siozy.dev.lunaspring.API.commands.CommandInitializer;
import siozy.dev.lunaspring.API.conditions.Conditions;
import siozy.dev.lunaspring.API.events.*;
import siozy.dev.lunaspring.API.messageActions.IMessageAction;
import siozy.dev.lunaspring.API.util.modules.Modules;
import siozy.dev.lunaspring.API.util.service.managers.ColorManager;
import siozy.dev.lunaspring.API.util.service.managers.TaskManager;
import siozy.dev.lunaspring.API.util.service.managers.worldguard.GuardManager;
import siozy.dev.lunaspring.API.util.service.managers.worldguard.LunaFlags;
import siozy.dev.lunaspring.API.util.utilities.AnnounceUtils;
import siozy.dev.lunaspring.API.util.utilities.Color;
import siozy.dev.lunaspring.API.util.utilities.Utils;
import siozy.dev.lunaspring.API.util.utilities.reflection.AnnotationScanner;
import siozy.dev.lunaspring.API.util.utilities.reflection.ClassEntry;
import siozy.dev.lunaspring.API.messageActions.MessageAction;
import siozy.dev.lunaspring.self.configuration.LSConfig;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
public final class LunaSpring extends LunaPlugin {
    @Getter private static LunaSpring instance;

    private final Set<LunaPlugin> hookedPlugins = new HashSet<>();
    private Metrics metrics;

    public LunaSpring() {
        instance = this;
    }

    @Override
    public void onLoad() {
        this.registerFlags();
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        super.onEnable();

        this.loadFile("localization.yml");
        this.registerListeners(new MenuHandler(), new MarkedItemEraserHandler(), new ItemComponentsHandler(), new ModuleHandlers());
        CommandInitializer.initialize(this, "#.self.commands");

        this.registerLunaPlaceholder("lunaspring");
        this.registerLunaPlaceholder("lsp");

        this.registerDefaultMessageActions();
        Conditions.load(this, "#.self.conditions");

        if (Utils.isPluginEnabled("WorldGuard")) this.registerListeners(new WorldGuardHandler());
        Modules.initializeServices(this);

        this.setUpBungeeCordMessaging();

        if (LSConfig.getBoolean("enableMetrics")) {
            this.initializeMetrics();
        }

        if (LSConfig.getBoolean("checkUpdates")) {
            String path = "https://raw.githubusercontent.com/NovaSparkle/LunaSpring/master/VERSION";
            this.checkUpdates(path, (ver, dver, status) -> {
                if (status != UpdateCheckerStatus.YOUR_VERSION_IS_LOW) return;
                this.startMessage(Arrays.asList(
                        "",
                        "        [color] [+] &fУ вас установлена старая версия &lLunaSpring!",
                        "        [color] [+] &fТекущая версия [color]-> v[LSVersion]",
                        "        [color] [+] &fВерсия на GitHub [color]-> v" + ver,
                        "&7Установите новую версию на &nhttps://github.com/NovaSparkle/LunaSpring/",
                        ""
                ));
            });
        }
    }

    private void registerFlags() {
        if (!Utils.hasPlugin("WorldGuard") || !LSConfig.getBoolean("enableWorldGuardFlags")) return;

        LunaFlags flags = GuardManager.flags();
        if (flags != null) {
            for (LunaFlags.State value : LunaFlags.State.values()) {
                flags.register(value);
            }
        }
    }

    private void registerLunaPlaceholder(String identifier) {
        this.createPlaceholder(identifier, ((offlinePlayer, params) -> {
            if (params.equalsIgnoreCase("hooked")) {
                return String.join(", ", this.hookedPlugins.stream().map(LunaPlugin::getName).toList()); // Список луна плагинов
            }

            if (params.startsWith("register-")) { // %lunaspring_register-SateChat%
                String[] split = params.split("-");
                if (split.length == 1) return null;

                LunaPlugin plugin = this.getLunaPlugin(split[1]);
                return plugin == null ? "no" : "yes";
            }

            if (params.startsWith("color-")) {
                String[] split = params.split("-");

                if (split.length >= 2) {
                    Color color = ColorManager.getColor(split[1]);
                    if (color == null) color = ColorManager.getColorFromReplacer(split[1]);

                    return color != null ? color.getVariable() : null;
                }
                return "";
            }

            if (params.startsWith("lp-")) { // %lunaspring_lp-SHORT_TRANSLATE_WITH_POINTS-DAYS_OR_HOURS%
                String[] split = params.split("-");
                if (split.length < 2) return null;

                Utils.Luckperms.TranslateType translateType = Utils.Luckperms.TranslateType.valueOf(split[1].toUpperCase());
                Utils.Luckperms.FormatType formatType = Utils.Luckperms.FormatType.valueOf(split[2].toUpperCase());
                return Utils.Luckperms.getFormatting(Utils.Luckperms.getGroupTime(offlinePlayer), translateType, formatType);
            }

            return null;
        }));
    }

    @SneakyThrows
    private void registerDefaultMessageActions() {
        Set<ClassEntry<MessageAction>> set = AnnotationScanner.findAnnotatedClasses(this, MessageAction.class, "#.self.messageActions");
        for (ClassEntry<MessageAction> messageActionClassEntry : set) {
            AnnounceUtils.registerAction((IMessageAction<? extends CommandSender>)
                    messageActionClassEntry.getClazz().getDeclaredConstructor().newInstance());
        }
    }

    public void hookPlugin(LunaPlugin lunaPlugin) {
        if (lunaPlugin != instance) this.hookedPlugins.add(lunaPlugin);
    }

    public LunaPlugin getLunaPlugin(String name) {
        return Utils.find(this.hookedPlugins, pl -> pl.getName().equals(name)).orElse(null);
    }

    private void initializeMetrics() {
        this.metrics = new Metrics(this, 29603);
        metrics.addCustomChart(new SingleLineChart("hooked_lunaplugins", this.hookedPlugins::size));
    }

    @Override
    public void onDisable() {
        TaskManager.stopAll();
        super.onDisable();
    }

    @SuppressWarnings("all")
    public void connect(PluginMessageRecipient recipient, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (Exception e) {
            String name = recipient instanceof CommandSender sender ? sender.getName() : "NullName";
            System.out.println("There was a problem attempting to send " + name + " to server " + server + "!");
        }

        recipient.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }
}



