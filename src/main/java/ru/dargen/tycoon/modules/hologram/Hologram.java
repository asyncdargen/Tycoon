package ru.dargen.tycoon.modules.hologram;

import lombok.Getter;
import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;
import ru.dargen.tycoon.modules.hologram.enums.ClickType;
import ru.dargen.tycoon.modules.hologram.text.HologramLine;
import ru.dargen.tycoon.utils.nms.PacketEntityUtil;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Hologram {

    protected static IHologramModule module;

    private @Getter List<EntityArmorStand> stands;
    private @Getter List<UUID> players;
    private @Getter EnumMap<ClickType, Consumer<Player>> clicks ;
    private @Getter List<HologramLine<?>> lines;

    private static int NEXT_ID = -1;
    private @Getter final int id = NEXT_ID++;

    private @Getter Location location;

    protected final double LINES_DISTANCE = 0.25;

    public Hologram(Location location, List<HologramLine<?>> lines){
        this.location = location.subtract(0, 2, 0);

        clicks = new EnumMap<>(ClickType.class);
        stands = new LinkedList<>();
        players = new ArrayList<>();
        this.lines = inverseList(lines);

        create();
        update(lines);
        module.register(this);
    }

    public void onUpdate(Player player) {
        for (int i = 0; i < lines.size(); i++){
            EntityArmorStand stand = stands.get(i);
            String line = lines.get(i).getLine(player);
            if (line.isEmpty())
                stand.setCustomNameVisible(false);
            else {
                stand.setCustomNameVisible(true);
                stand.setCustomName(line);
            }
            PacketEntityUtil.metadataUpdate(player, stand.getId(), stand.getDataWatcher());
        }
    }

    public void show(Player player) {
        for (int i = 0; i < lines.size(); i++){
            EntityArmorStand stand = stands.get(i);
            String line = lines.get(i).getLine(player);
            if (line.isEmpty())
                stand.setCustomNameVisible(false);
            else {
                stand.setCustomNameVisible(true);
                stand.setCustomName(line);
            }
            PacketEntityUtil.spawnEntityLivings(player, stand);
        }
    }

    public Hologram(Location location, HologramLine<?>... lines){
        this(location, Arrays.asList(lines));
    }

    public void update(List<HologramLine<?>> lines){
        if (this.lines != null && lines.size() != this.lines.size())
            throw new IllegalArgumentException("The array must have the same number of lines");
        this.lines = inverseList(lines);
        for (int i = 0; i < stands.size(); i++){
            String line = lines.get(i).getLine(null);
            EntityArmorStand stand = stands.get(i);
            if (line.isEmpty())
                stand.setCustomNameVisible(false);
            else {
                stand.setCustomNameVisible(true);
                stand.setCustomName(line);
            }
        }
        players.stream().map(Bukkit::getPlayer).forEach(this::reload);
    }

    public void update(HologramLine<?>... lines){
        update(Arrays.asList(lines));
    }

    protected void create(){
        Location location = this.location.clone();
        for (int i = 0; i < lines.size(); i++){
            EntityArmorStand stand = createStand(location);
            stand.setCustomName(" ");
            stands.add(stand);
            location.add(0, LINES_DISTANCE, 0);
        }
    }

    public void destroy(Player player){
        PacketEntityUtil.destroyEntitys(player, stands.stream().map(s -> (Entity) s).collect(Collectors.toList()));
    }

    public void destroyAll(){
        players.stream().map(Bukkit::getPlayer).forEach(this::destroy);
    }


    public void reload(Player player) {
        destroy(player);
        show(player);
    }

    public void teleport(Player player) {
        for (EntityArmorStand stand : stands) {
            PacketEntityUtil.teleportEntity(player, stand);
        }
    }

    public void moveToLocation(Location location) {
        location.subtract(0, 2, 0);
        this.location = location;
        for (EntityArmorStand stand : stands){
            stand.setLocation(
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    0, 0);
            location.add(0, LINES_DISTANCE, 0);
        }
        players.stream().map(Bukkit::getPlayer).forEach(this::teleport);
    }

    public void addClick(ClickType click, Consumer<Player> consumer) {
        clicks.put(click, consumer);
    }

    public void onClick(Player player, ClickType click) {
        if (hasClick(click))
            clicks.get(click).accept(player);
    }

    public boolean hasClick(ClickType click) {
        return clicks.containsKey(click);
    }

    public boolean isHologramStand(int id) {
        for (EntityArmorStand stand : stands) {
            if (stand.getId() == id)
                return true;
        }
        return false;
    }

    protected static EntityArmorStand createStand(Location location) {
        EntityArmorStand armorStand = new EntityArmorStand(
                ((CraftWorld) location.getWorld()).getHandle(),
                location.getX(),
                location.getY(),
                location.getZ());
        armorStand.setCustomNameVisible(true);
        armorStand.setInvisible(true);
        armorStand.setNoGravity(true);
        return armorStand;
    }

    protected static <T> List<T> inverseList(List<T> toInverse) {
        List<T> inverse = new LinkedList<>();
        for (int i = toInverse.size() - 1; i >= 0; i--){
            inverse.add(toInverse.get(i));
        }
        return inverse;
    }

}
