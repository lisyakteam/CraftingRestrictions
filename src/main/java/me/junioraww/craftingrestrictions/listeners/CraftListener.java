package me.junioraww.craftingrestrictions.listeners;

import me.junioraww.craftingrestrictions.Main;
import me.junioraww.craftingrestrictions.licenses.License;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

public class CraftListener implements Listener {

  @EventHandler
  public void onPrepareCraft(PrepareItemCraftEvent event) {
    ItemStack result = event.getInventory().getResult();
    if (result == null) return;

    findLicense(result).ifPresent(license -> {
      Player player = (Player) event.getView().getPlayer();

      if (!hasLicense(player, license)) {
        player.sendRichMessage("<red>Рецепт доступен только владельцам лицензии: " + license.getName() + "!</red>");
        player.sendRichMessage("<gold>Положите предмет лицензии в ваш Эндер-сундук</gold>");
        event.getInventory().setResult(null);
      }
    });
  }

  @EventHandler
  public void onCrafterPrepare(CrafterCraftEvent event) {
    ItemStack result = event.getRecipe().getResult();

    findLicense(result).ifPresent(license -> {
      boolean authorizedPlayerNearby = event.getBlock().getLocation()
              .getNearbyPlayers(32).stream()
              .anyMatch(p -> hasLicense(p, license));

      if (!authorizedPlayerNearby) {
        event.setCancelled(true);
      }
    });
  }

  private boolean hasLicense(Player player, License license) {
    for (ItemStack item : player.getEnderChest().getContents()) {
      if (item == null || item.getType() != Material.PAPER) continue;

      var meta = item.getItemMeta();
      if (meta == null) continue;

      String licenseId = meta.getPersistentDataContainer().get(Main.LICENSE_KEY, PersistentDataType.STRING);
      if (license.getId().equals(licenseId)) {
        return true;
      }
    }
    return false;
  }

  private Optional<License> findLicense(ItemStack item) {
    if (item == null || !item.hasItemMeta()) return Optional.empty();
    var component = item.getItemMeta().getCustomModelDataComponent();

    var itemStrings = component.getStrings();
    return Main.getLicenses().stream()
            .filter(l -> l.matches(itemStrings))
            .findFirst();
  }
}