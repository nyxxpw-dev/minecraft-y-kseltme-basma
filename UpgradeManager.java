package nyxx.nyxxMetin2.managers;

import java.util.Random;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class UpgradeManager {
   private final Random random = new Random();
   private final NamespacedKey levelKey;
   private final NamespacedKey scrollKey;
   private final Plugin plugin;

   public UpgradeManager(Plugin plugin) {
      this.plugin = plugin;
      this.levelKey = new NamespacedKey(plugin, "item_level");
      this.scrollKey = new NamespacedKey(plugin, "is_blessing_scroll");
   }

   public boolean isValidItem(ItemStack item) {
      if (item != null && item.getType() != Material.AIR) {
         String type = item.getType().name();
         return type.contains("SWORD") || type.contains("AXE") || type.contains("BOW") || type.contains("HELMET") || type.contains("CHESTPLATE") || type.contains("LEGGINGS") || type.contains("BOOTS") || type.contains("TRIDENT") || type.contains("CROSSBOW");
      } else {
         return false;
      }
   }

   public UpgradeManager.UpgradeResult attemptUpgrade(int currentLevel, ItemStack scrollItem) {
      double roll = this.random.nextDouble() * 100.0D;
      boolean hasScroll = this.isBlessingScroll(scrollItem);
      double chance = this.plugin.getConfig().getDouble("chances.level-" + currentLevel, 10.0D);
      if (roll <= chance) {
         return UpgradeManager.UpgradeResult.SUCCESS;
      } else if (currentLevel >= 7 && !hasScroll) {
         return UpgradeManager.UpgradeResult.FAIL_DESTROY;
      } else {
         return currentLevel > 0 ? UpgradeManager.UpgradeResult.FAIL_DOWNGRADE : UpgradeManager.UpgradeResult.FAIL_KEEP;
      }
   }

   public void applyUpgradeEffects(ItemStack item, int newLevel) {
      if (item != null) {
         item.editMeta((meta) -> {
            meta.getPersistentDataContainer().set(this.levelKey, PersistentDataType.INTEGER, newLevel);
            String name = item.getType().name().replace("_", " ").toLowerCase();
            String var10001 = this.capitalize(name);
            meta.displayName(((TextComponent)Component.text(var10001 + " +" + newLevel).color(newLevel >= 7 ? NamedTextColor.GOLD : NamedTextColor.WHITE)).decoration(TextDecoration.ITALIC, false));
            meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE);
            meta.removeAttributeModifier(Attribute.GENERIC_ARMOR);
            String type = item.getType().name();
            if (!type.contains("SWORD") && !type.contains("AXE") && !type.contains("BOW")) {
               if (type.contains("CHESTPLATE") || type.contains("LEGGINGS") || type.contains("BOOTS") || type.contains("HELMET")) {
                  meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(new NamespacedKey(this.plugin, "up_armor"), (double)newLevel * 2.0D, Operation.ADD_NUMBER, EquipmentSlotGroup.ARMOR));
               }
            } else {
               meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(new NamespacedKey(this.plugin, "up_dmg"), (double)newLevel * 4.5D, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
            }

         });
      }
   }

   public ItemStack getBlessingScroll() {
      ItemStack scroll = new ItemStack(Material.PAPER);
      scroll.editMeta((m) -> {
         m.displayName(Component.text("§b§lKutsal Kağıt").decoration(TextDecoration.ITALIC, false));
         m.getPersistentDataContainer().set(this.scrollKey, PersistentDataType.BOOLEAN, true);
         m.addEnchant(Enchantment.UNBREAKING, 1, true);
         m.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
      });
      return scroll;
   }

   public Material getRequiredMaterial(int level) {
      String req = this.plugin.getConfig().getString("requirements.level-" + level);
      if (req == null) {
         return null;
      } else {
         try {
            return Material.valueOf(req.split(":")[0]);
         } catch (Exception var4) {
            return null;
         }
      }
   }

   public int getRequiredAmount(int level) {
      String req = this.plugin.getConfig().getString("requirements.level-" + level);
      if (req == null) {
         return 0;
      } else {
         try {
            return Integer.parseInt(req.split(":")[1]);
         } catch (Exception var4) {
            return 0;
         }
      }
   }

   public boolean checkAndRemoveMaterials(Player p, int level) {
      Material req = this.getRequiredMaterial(level);
      if (req == null) {
         return true;
      } else {
         int amt = this.getRequiredAmount(level);
         if (p.getInventory().containsAtLeast(new ItemStack(req), amt)) {
            p.getInventory().removeItem(new ItemStack[]{new ItemStack(req, amt)});
            return true;
         } else {
            return false;
         }
      }
   }

   public int getItemLevel(ItemStack item) {
      return item != null && item.hasItemMeta() ? (Integer)item.getItemMeta().getPersistentDataContainer().getOrDefault(this.levelKey, PersistentDataType.INTEGER, 0) : 0;
   }

   public boolean isBlessingScroll(ItemStack item) {
      return item != null && item.hasItemMeta() ? item.getItemMeta().getPersistentDataContainer().has(this.scrollKey, PersistentDataType.BOOLEAN) : false;
   }

   private String capitalize(String str) {
      if (str != null && !str.isEmpty()) {
         String var10000 = str.substring(0, 1).toUpperCase();
         return var10000 + str.substring(1).toLowerCase();
      } else {
         return "";
      }
   }

   public static enum UpgradeResult {
      SUCCESS,
      FAIL_KEEP,
      FAIL_DOWNGRADE,
      FAIL_DESTROY;

      // $FF: synthetic method
      private static UpgradeManager.UpgradeResult[] $values() {
         return new UpgradeManager.UpgradeResult[]{SUCCESS, FAIL_KEEP, FAIL_DOWNGRADE, FAIL_DESTROY};
      }
   }
}
