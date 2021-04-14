package ru.dargen.tycoon.modules.mechanic;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import ru.dargen.tycoon.Tycoon;
import ru.dargen.tycoon.modules.IModule;
import ru.dargen.tycoon.modules.Module;
import ru.dargen.tycoon.modules.item.IItemModule;
import ru.dargen.tycoon.utils.reflect.ReflectUtil;

import java.util.List;

public class MechanicModule extends Module implements IModule {

    private BukkitTask task;
    private IItemModule items;

    public void enable(Tycoon tycoon) throws Exception {
        registerListener();
        items = IItemModule.get();
        Bukkit.getServer().clearRecipes();
        World world = Bukkit.getWorlds().get(0);
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setTime(0);
        world.setGameRuleValue("announceAdvancements", "false");
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("keepInventory", "false");
        PotionEffect nv = new PotionEffect(PotionEffectType.NIGHT_VISION, 9999999 , 1, false, false );
        task = Bukkit.getScheduler().runTaskTimer(tycoon,
                () -> Bukkit.getOnlinePlayers().forEach(p -> p.addPotionEffect(nv, false)), 0, 60);
    }

    public void disable() throws Exception {
        unRegisterListener();
        if (task != null && !task.isCancelled())
            task.cancel();
    }

    @EventHandler
    public void craft(InventoryClickEvent e) {
        ItemStack c = e.getCurrentItem();
        ItemStack cl = e.getCursor();
        if (c != null && c.getType() != Material.AIR && cl != null && cl.getType() != Material.AIR) {
            String cName = items.getItemName(c);
            String clName = items.getItemName(cl);
            if (!cName.equals("") && !clName.equals("")) {
                if (clName.equalsIgnoreCase(cName) && !clName.equalsIgnoreCase("pickaxe_13")) {
                    e.setCursor(items.getItem("pickaxe_" + (Integer.parseInt(clName.split("_")[1]) + 1)));
                    e.setCurrentItem(new ItemStack(Material.AIR));
                    Player p = (Player) e.getWhoClicked();
                    Location loc = p.getLocation();
                    p.spawnParticle(Particle.VILLAGER_HAPPY, loc, 50, 0.5, 1, 0.5);
                    p.playSound(loc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                }
            }
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setLevel(0);
        player.setExp(0);
        player.setGameMode(GameMode.ADVENTURE);
    }

    @EventHandler
    public void weather(WeatherChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void drop(PlayerDropItemEvent e) {
        if (!items.getItemName(e.getItemDrop().getItemStack()).equals(""))
            e.setCancelled(true);
    }

    @EventHandler
    public void food(FoodLevelChangeEvent e) {
        e.setCancelled(true);
        e.setFoodLevel(20);
    }

    @EventHandler
    public void xp(ExpBottleEvent e) {
        e.setExperience(0);
        e.setShowEffect(false);
    }

    @EventHandler
    public void player2PlayerDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player
                && e.getEntity() instanceof Player)
            e.setCancelled(true);
    }

    @EventHandler
    public void damage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player)
            e.setCancelled(true);
    }


}