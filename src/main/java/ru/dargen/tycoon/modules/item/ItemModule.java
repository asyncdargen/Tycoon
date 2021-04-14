package ru.dargen.tycoon.modules.item;

import lombok.Getter;
import lombok.val;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.apache.logging.log4j.util.BiConsumer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import ru.dargen.tycoon.Tycoon;
import ru.dargen.tycoon.modules.Module;
import ru.dargen.tycoon.modules.booster.Booster;
import ru.dargen.tycoon.modules.booster.IBoosterModule;
import ru.dargen.tycoon.modules.booster.enums.Source;
import ru.dargen.tycoon.modules.booster.enums.Spread;
import ru.dargen.tycoon.modules.booster.enums.Type;
import ru.dargen.tycoon.modules.chat.Prefix;
import ru.dargen.tycoon.modules.menu.menus.MainMenu;
import ru.dargen.tycoon.modules.player.IPlayerModule;
import ru.dargen.tycoon.utils.ItemBuilder;

import java.util.HashMap;
import java.util.Map;

public class ItemModule extends Module implements IItemModule {

    private @Getter Map<String, ItemStack> items;
    private @Getter Map<String, BiConsumer<PlayerInteractEvent, ItemStack>> interacts;


    public void enable(Tycoon tycoon) throws Exception {
        items = new HashMap<>();
        interacts = new HashMap<>();

        registerListener();
        registerDefault();
    }

    public void disable() throws Exception {
        unRegisterListener();
        items.clear();
        interacts.clear();
    }

    private void registerDefault() {
        registerItem("boost_income", new ItemBuilder(Material.EXP_BOTTLE).setName("§aБустер Дохода").setItemLore(" §fМножитель§7: §ax1.5", " §fДлительность§7: §a5 мин.", "", "§7Нажмите ПКМ для активации"), (e, i) -> {
            if (e.getAction() != Action.RIGHT_CLICK_AIR || e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getHand() == EquipmentSlot.OFF_HAND) {
                e.setCancelled(true);
                return;
            }
            if (IBoosterModule.get().hasBooster(IPlayerModule.get().getPlayer(e.getPlayer()), Type.INCOME, Spread.LOCAL)) {
                e.getPlayer().sendMessage(Prefix.ERR + "Такой бустер уже активирован");
                e.setCancelled(true);
                return;
            }
            IBoosterModule.get().startBooster(new Booster(System.currentTimeMillis(), 300000, Spread.LOCAL, Source.CASE, Type.INCOME, e.getPlayer().getName(), 1.5));
            i.setAmount(i.getAmount() - 1);
            e.setCancelled(true);
        });
        registerItem("boost_case", new ItemBuilder(Material.EXP_BOTTLE).setName("§aБустер Кейсов").setItemLore(" §fМножитель§7: §ax1.5", " §fДлительность§7: §a5 мин.", "", "§7Нажмите ПКМ для активации"), (e, i) -> {
            if (e.getAction() != Action.RIGHT_CLICK_AIR || e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getHand() == EquipmentSlot.OFF_HAND) {
                e.setCancelled(true);
                return;
            }
            if (IBoosterModule.get().hasBooster(IPlayerModule.get().getPlayer(e.getPlayer()), Type.CASE, Spread.LOCAL)) {
                e.getPlayer().sendMessage(Prefix.ERR + "Такой бустер уже активирован");
                e.setCancelled(true);
                return;
            }
            IBoosterModule.get().startBooster(new Booster(System.currentTimeMillis(), 300000, Spread.LOCAL, Source.CASE, Type.CASE, e.getPlayer().getName(), 1.5));
            i.setAmount(i.getAmount() - 1);
            e.setCancelled(true);
        });
        registerItem("menu", new ItemBuilder(Material.NETHER_STAR).setName("§aМеню").addLoreLine("§7Нажмите ПКМ, чтобы открыть меню"), (e, i) -> {
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getHand() != EquipmentSlot.OFF_HAND) {
                new MainMenu(e.getPlayer());
                e.setCancelled(true);
            }
        });
        String[] lore = {"§7Используется для добычи полезных ископаемых", "", "§7Соедините две одинаковых кирки", "§7в одном слоте, для улучшения"};
        ItemBuilder wood = new ItemBuilder(Material.WOOD_PICKAXE);
        wood.addFlags(ItemFlag.values());
        wood.setUnbreakable(true);
        wood.setItemLore(lore);
        registerItem("pickaxe_1", ((ItemBuilder) wood.clone()).setName("§a§lКирка §cI"));
        registerItem("pickaxe_2", ((ItemBuilder) wood.clone()).setName("§a§lКирка §cII").addLore(" ", "§fЭффективность §2I").addItemEnchant(Enchantment.DIG_SPEED, 1));
        ItemBuilder stone = new ItemBuilder(Material.STONE_PICKAXE);
        stone.addFlags(ItemFlag.values());
        stone.setUnbreakable(true);
        stone.setItemLore(lore);
        registerItem("pickaxe_3", ((ItemBuilder) stone.clone()).setName("§a§lКирка §cIII").addLore(" ", "§fЭффективность §2I").addItemEnchant(Enchantment.DIG_SPEED, 1));
        registerItem("pickaxe_4", ((ItemBuilder) stone.clone()).setName("§a§lКирка §cIV").addLore(" ", "§fЭффективность §2II").addItemEnchant(Enchantment.DIG_SPEED, 2));
        ItemBuilder iron = new ItemBuilder(Material.IRON_PICKAXE);
        iron.addFlags(ItemFlag.values());
        iron.setUnbreakable(true);
        iron.setItemLore(lore);
        registerItem("pickaxe_5", ((ItemBuilder) iron.clone()).setName("§a§lКирка §cV").addLore(" ", "§fЭффективность §2II").addItemEnchant(Enchantment.DIG_SPEED, 2));
        registerItem("pickaxe_6", ((ItemBuilder) iron.clone()).setName("§a§lКирка §cVI").addLore(" ", "§fЭффективность §2III").addItemEnchant(Enchantment.DIG_SPEED, 3));
        ItemBuilder diam = new ItemBuilder(Material.DIAMOND_PICKAXE);
        diam.addFlags(ItemFlag.values());
        diam.setUnbreakable(true);
        diam.setItemLore(lore);
        registerItem("pickaxe_7", ((ItemBuilder) diam.clone()).setName("§a§lКирка §cVII").addLore(" ", "§fЭффективность §2III").addItemEnchant(Enchantment.DIG_SPEED, 3));
        registerItem("pickaxe_8", ((ItemBuilder) diam.clone()).setName("§a§lКирка §cVIII").addLore(" ", "§fЭффективность §2IV").addItemEnchant(Enchantment.DIG_SPEED, 4));
        registerItem("pickaxe_9", ((ItemBuilder) diam.clone()).setName("§a§lКирка §cIX").addLore(" ", "§fЭффективность §2V").addItemEnchant(Enchantment.DIG_SPEED, 5));
        registerItem("pickaxe_10", ((ItemBuilder) diam.clone()).setName("§a§lКирка §cX").addLore(" ", "§fЭффективность §2VI").addItemEnchant(Enchantment.DIG_SPEED, 6));
        registerItem("pickaxe_11", ((ItemBuilder) diam.clone()).setName("§a§lКирка §cXI").addLore(" ", "§fЭффективность §2VII").addItemEnchant(Enchantment.DIG_SPEED, 7));
        registerItem("pickaxe_12", ((ItemBuilder) diam.clone()).setName("§a§lКирка §cXII").addLore(" ", "§fЭффективность §2VIII").addItemEnchant(Enchantment.DIG_SPEED, 8));
        registerItem("pickaxe_13", ((ItemBuilder) diam.clone()).setName("§a§lКирка §cXIII").setItemLore("§7Используется для добычи полезных ископаемых", "", "§fЭффективность §2IX").addItemEnchant(Enchantment.DIG_SPEED, 9));
    }

    public ItemStack getItem(String name) {
        ItemStack item = items.get(name.toLowerCase());
        if (item == null)
            return null;
        return item.clone();
    }

    public String getItemName(ItemStack item) {
        if (item != null) {
            NBTTagString nbt = new ItemBuilder(item).<NBTTagString>getTag("tycoon_item");
            String name;
            if (nbt != null && (name = nbt.c_()) != null) {
                if (items.containsKey(name.toLowerCase()))
                    return name;
            }
        }
        return "";
    }

    public void startKit(Player player) {
        player.getInventory().clear();
        player.getInventory().setItem(8, getItem("menu"));
        player.getInventory().addItem(getItem("pickaxe_1"));
    }

    public void registerItem(String name, ItemStack item, BiConsumer<PlayerInteractEvent, ItemStack> interact) {
        item = new ItemBuilder(item).setTag("tycoon_item", new NBTTagString(name.toLowerCase()));
        items.put(name.toLowerCase(), item);
        if (interact != null)
            interacts.put(name.toLowerCase(), interact);
    }

    public void registerItem(String name, ItemStack item) {
        registerItem(name, item, null);
    }


    public void interact(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getType() != Material.AIR) {
            String name;
            NBTTagString nbt = new ItemBuilder(event.getItem()).<NBTTagString>getTag("tycoon_item");
            if (nbt != null && (name = nbt.c_()) != null) {
                if (!items.containsKey(name.toLowerCase()))
                    return;
                val interact = interacts.getOrDefault(name.toLowerCase(), null);
                if (interact == null)
                    return;
                interact.accept(event, event.getItem());
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        interact(event);
    }

}
