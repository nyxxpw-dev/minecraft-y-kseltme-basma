package nyxx.nyxxMetin2.gui;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import nyxx.nyxxMetin2.managers.UpgradeManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class DemirciGUI {
   public void open(Player player) {
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, 27, Component.text("Demirci Ustası"));
      ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
      glass.editMeta((m) -> {
         m.displayName(Component.text(" "));
      });

      for(int i = 0; i < 27; ++i) {
         inv.setItem(i, glass);
      }

      inv.setItem(10, (ItemStack)null);
      inv.setItem(16, (ItemStack)null);
      updateButton(inv, (ItemStack)null, player, (UpgradeManager)null);
      player.openInventory(inv);
   }

   public static void updateButton(Inventory inv, ItemStack item, Player p, UpgradeManager mgr) {
      boolean can = false;
      Material req = null;
      int amt = 0;
      boolean valid = false;
      if (item != null && mgr != null) {
         valid = mgr.isValidItem(item);
         if (valid) {
            int lvl = mgr.getItemLevel(item);
            req = mgr.getRequiredMaterial(lvl);
            amt = mgr.getRequiredAmount(lvl);
            can = req == null || p.getInventory().containsAtLeast(new ItemStack(req), amt);
         }
      }

      ItemStack btn = new ItemStack(can && valid ? Material.ANVIL : Material.BARRIER);
      btn.editMeta((m) -> {
         if (item == null) {
            m.displayName(Component.text("§7Eşya yerleştirin..."));
         } else if (!valid) {
            m.displayName(Component.text("§c§lGEÇERSİZ EŞYA"));
         } else if (!can) {
            m.displayName(Component.text("§c§lMALZEME EKSİK"));
         } else {
            m.displayName(Component.text("§a§l[ GELİŞTİR ]"));
         }

         List<Component> lore = new ArrayList();
         if (req != null && valid) {
            lore.add(Component.text("§7Gereken: " + (can ? "§f" : "§c") + amt + "x " + req.name()));
         }

         m.lore(lore);
      });
      inv.setItem(13, btn);
   }
}
