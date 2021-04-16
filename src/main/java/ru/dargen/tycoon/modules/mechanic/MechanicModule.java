package ru.dargen.tycoon.modules.mechanic;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import ru.dargen.tycoon.Tycoon;
import ru.dargen.tycoon.modules.IModule;
import ru.dargen.tycoon.modules.Module;
import ru.dargen.tycoon.modules.booster.enums.Type;
import ru.dargen.tycoon.modules.item.IItemModule;
import ru.dargen.tycoon.modules.packet.event.PacketPlayInEvent;
import ru.dargen.tycoon.modules.player.IPlayerData;
import ru.dargen.tycoon.modules.player.IPlayerModule;
import ru.dargen.tycoon.utils.formatter.DoubleFormatter;
import ru.dargen.tycoon.utils.nms.PacketEntityUtil;
import ru.dargen.tycoon.utils.reflect.ReflectUtil;

import java.util.*;

public class MechanicModule extends Module implements IModule {

    private BukkitTask task;
    private IItemModule items;
    private IPlayerModule players;
    private OreRecover oreRecover;

    public void enable(Tycoon tycoon) throws Exception {
        registerListener();
        oreRecover = new OreRecover(players = IPlayerModule.get());
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
        oreRecover.stop();
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
    public void quit(PlayerQuitEvent e) {
        oreRecover.remove(e.getPlayer().getUniqueId());
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

    Map<Material, Double> prices;
    {
        prices = new HashMap<>();
        prices.put(Material.COAL_ORE, 10d);
        prices.put(Material.QUARTZ_ORE, 10d);
        prices.put(Material.IRON_ORE, 25d);
        prices.put(Material.GOLD_ORE, 75d);
        prices.put(Material.LAPIS_ORE, 50d);
        prices.put(Material.REDSTONE_ORE, 100d);
        prices.put(Material.GLOWING_REDSTONE_ORE, 100d);
        prices.put(Material.DIAMOND_ORE, 175d);
        prices.put(Material.EMERALD_ORE, 250D);
    }
    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        IPlayerData data = players.getPlayer(e.getPlayer());
        Location loc = e.getPlayer().getLocation();
        BlockPosition bp = new BlockPosition(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ());
        Material block = e.getBlock().getType();
        if (!block.toString().contains("ORE"))
            return;
        e.setCancelled(true);
        oreRecover.add(e.getPlayer().getUniqueId(), bp);
        double price = prices.getOrDefault(block, 0d);
        PacketPlayOutBlockChange bc = new PacketPlayOutBlockChange();
        bc.block = Blocks.BEDROCK.getBlockData();
        ReflectUtil.setValue(bc, "a", bp);
        new Thread(() -> {
            try {
                Thread.sleep(51); // TODO: 015 15.04.21 fix bugs
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            ((CraftPlayer) e.getPlayer()).getHandle().playerConnection.sendPacket(bc);
        }).start();
        data.addMoney(price = data.getBoost(Type.INCOME) * price);
        e.getPlayer().sendActionBar("§a§l+" + DoubleFormatter.format(price) + " $");
    }


    @EventHandler
    public void pet(PlayerJoinEvent e) {
        EntityPlayer ep = ((CraftPlayer) e.getPlayer()).getHandle();
        Player p = e.getPlayer();
        p.sendMessage("Pets enabled");
        EntityArmorStand stand = new EntityArmorStand(ep.getWorld(), ep.getX(), ep.getY(), ep.getZ());
        PacketEntityUtil.spawnEntityLivings(ep.getBukkitEntity(), stand);

        Bukkit.getScheduler().runTaskTimer(Tycoon.getInstance(), () -> {

            Location loc = edit(p);

            stand.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
            PacketEntityUtil.teleportEntity(p, stand);
        }, 0, 1);
    }

    public Location edit(Player player) {
        Location loc = player.getLocation();
        loc.setDirection(loc.toVector().subtract(loc.toVector()));
        Vector direction = loc.getDirection().normalize();
        direction.setX(direction.getX() + 1);
        return loc;
    }
}