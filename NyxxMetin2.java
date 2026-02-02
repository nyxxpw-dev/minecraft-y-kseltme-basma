package nyxx.nyxxMetin2;

import java.util.HashMap;
import java.util.Iterator;
import net.kyori.adventure.text.Component;
import nyxx.nyxxMetin2.gui.DemirciGUI;
import nyxx.nyxxMetin2.managers.UpgradeManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class NyxxMetin2 extends JavaPlugin implements Listener, CommandExecutor {
   private UpgradeManager upgradeManager;

   public void onEnable() {
      this.saveDefaultConfig();
      this.upgradeManager = new UpgradeManager(this);
      this.getServer().getPluginManager().registerEvents(this, this);
      this.getCommand("demirci").setExecutor(this);
      this.getCommand("nyxxadmin").setExecutor(this);
   }

   public boolean onCommand(@NotNull CommandSender s, @NotNull Command c, @NotNull String l, String[] args) {
      if (l.equalsIgnoreCase("demirci") && s instanceof Player) {
         Player p = (Player)s;
         (new DemirciGUI()).open(p);
      } else if (l.equalsIgnoreCase("nyxxadmin") && s.hasPermission("nyxxmetin2.admin")) {
         if (args.length >= 2 && args[0].equalsIgnoreCase("give")) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
               target.getInventory().addItem(new ItemStack[]{this.upgradeManager.getBlessingScroll()});
            }
         } else if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            this.reloadConfig();
            s.sendMessage("§a[NyxxMetin2] Ayarlar yenilendi.");
         }
      }

      return true;
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent e) {
      if (e.getView().title().toString().contains("Demirci Ustası")) {
         if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.BLACK_STAINED_GLASS_PANE) {
            e.setCancelled(true);
         } else {
            Player p = (Player)e.getWhoClicked();
            Inventory inv = e.getInventory();
            Bukkit.getScheduler().runTaskLater(this, () -> {
               DemirciGUI.updateButton(inv, inv.getItem(10), p, this.upgradeManager);
            }, 1L);
            if (e.getRawSlot() == 13) {
               e.setCancelled(true);
               ItemStack item = inv.getItem(10);
               if (item == null || !this.upgradeManager.isValidItem(item) || inv.getItem(13).getType() == Material.BARRIER) {
                  return;
               }

               int lvl = this.upgradeManager.getItemLevel(item);
               if (lvl >= 9 || !this.upgradeManager.checkAndRemoveMaterials(p, lvl)) {
                  return;
               }

               UpgradeManager.UpgradeResult res = this.upgradeManager.attemptUpgrade(lvl, inv.getItem(16));
               int next;
               if (res == UpgradeManager.UpgradeResult.SUCCESS) {
                  next = lvl + 1;
                  this.upgradeManager.applyUpgradeEffects(item, next);
                  p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.2F);
                  p.spawnParticle(Particle.HAPPY_VILLAGER, p.getLocation().add(0.0D, 1.0D, 0.0D), 20, 0.5D, 0.5D, 0.5D);
                  int minLevel = this.getConfig().getInt("settings.broadcast-min-level", 7);
                  if (next >= minLevel) {
                     String rawMsg = this.getConfig().getString("messages.broadcast", "§6[DUYURU] §e%player% §7eşyasını §b+%level% §7yaptı!");
                     String formattedMsg = rawMsg.replace("%player%", p.getName()).replace("%level%", String.valueOf(next));
                     Bukkit.broadcast(Component.text(formattedMsg));
                     Iterator var11 = Bukkit.getOnlinePlayers().iterator();

                     while(var11.hasNext()) {
                        Player online = (Player)var11.next();
                        online.playSound(online.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.3F, 2.0F);
                     }
                  }
               } else if (res == UpgradeManager.UpgradeResult.FAIL_DESTROY) {
                  inv.setItem(10, (ItemStack)null);
                  p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
               } else {
                  next = res == UpgradeManager.UpgradeResult.FAIL_DOWNGRADE ? Math.max(0, lvl - 1) : lvl;
                  this.upgradeManager.applyUpgradeEffects(item, next);
                  p.playSound(p.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1.0F, 1.0F);
               }

               ItemStack scroll = inv.getItem(16);
               if (scroll != null && this.upgradeManager.isBlessingScroll(scroll)) {
                  scroll.setAmount(scroll.getAmount() - 1);
               }
            }

         }
      }
   }

   @EventHandler
   public void onInventoryClose(InventoryCloseEvent e) {
      if (e.getView().title().toString().contains("Demirci Ustası")) {
         Player p = (Player)e.getPlayer();
         Inventory inv = e.getInventory();
         int[] slots = new int[]{10, 16};
         int[] var5 = slots;
         int var6 = slots.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            int slot = var5[var7];
            ItemStack item = inv.getItem(slot);
            if (item != null && item.getType() != Material.AIR) {
               HashMap<Integer, ItemStack> left = p.getInventory().addItem(new ItemStack[]{item});
               left.values().forEach((i) -> {
                  p.getWorld().dropItemNaturally(p.getLocation(), i);
               });
               inv.setItem(slot, (ItemStack)null);
            }
         }

      }
   }
}
