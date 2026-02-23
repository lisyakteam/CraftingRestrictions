package me.junioraww.craftingrestrictions;

import me.junioraww.craftingrestrictions.licenses.License;
import me.junioraww.craftingrestrictions.listeners.CraftListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class Main extends JavaPlugin implements CommandExecutor {
  public static NamespacedKey LICENSE_KEY;
  private static final List<License> licenses = new ArrayList<>();

  public static List<License> getLicenses() { return licenses; }

  @Override
  public void onEnable() {
    LICENSE_KEY = new NamespacedKey(this, "license_id");

    licenses.add(new License("rifle", "производство винтовки", List.of("inhouse_guns:powder_rifle", "_idle")));
    licenses.add(new License("scattergun", "производство дробовика", List.of("inhouse_guns:scattergun", "_idle")));
    licenses.add(new License("revolver", "производство револьвера", List.of("inhouse_guns:revolver", "_idle")));
    licenses.add(new License("money_green", "производство зеленых купюр", List.of("money", "green")));
    licenses.add(new License("money_yellow", "производство желтых купюр", List.of("money", "yellow")));

    getServer().getPluginManager().registerEvents(new CraftListener(), this);
    getCommand("get-license").setExecutor(this);
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player player)) return true;
    if (!player.isOp()) return true;

    if (args.length < 1) {
      player.sendMessage("Использование: /get-license <id>");
      return true;
    }

    String targetId = args[0];
    licenses.stream().filter(l -> l.getId().equalsIgnoreCase(targetId)).findFirst().ifPresentOrElse(license -> {
      ItemStack item = new ItemStack(Material.PAPER);
      var meta = item.getItemMeta();
      meta.displayName(MiniMessage.miniMessage().deserialize("<!i><gradient:gold:yellow>Лицензия"));
      meta.lore(List.of(MiniMessage.miniMessage().deserialize("<!i><white>Разрешение: <u>" + license.getName())));
      meta.getPersistentDataContainer().set(LICENSE_KEY, PersistentDataType.STRING, license.getId());
      item.setItemMeta(meta);

      player.getInventory().addItem(item);
      player.sendMessage("Вы получили лицензию: " + license.getName());
    }, () -> player.sendMessage("Лицензия не найдена!"));

    return true;
  }
}