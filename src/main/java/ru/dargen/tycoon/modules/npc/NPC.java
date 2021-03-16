package ru.dargen.tycoon.modules.npc;

import com.mojang.authlib.GameProfile;
import lombok.Getter;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import ru.dargen.tycoon.modules.hologram.Hologram;
import ru.dargen.tycoon.modules.hologram.text.FunctionLine;
import ru.dargen.tycoon.modules.hologram.text.TextLine;
import ru.dargen.tycoon.modules.npc.enums.Interact;
import ru.dargen.tycoon.modules.npc.enums.Status;
import ru.dargen.tycoon.utils.nms.PacketEntityUtil;
import ru.dargen.tycoon.utils.nms.PacketPlayerUtils;
import ru.dargen.tycoon.utils.textures.skin.SkinWorker;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class NPC {

    protected static INPCModule module;

    private @Getter Location location;
    private @Getter List<UUID> players;
    private @Getter EnumMap<Interact, Consumer<Player>> clicks;
    private @Getter EnumMap<EnumItemSlot, ItemStack> items;
    private @Getter int id;
    private @Getter String name;
    private @Getter GameProfile profile;
    private @Getter EntityPlayer player;
    private @Getter UUID uuid;
    private @Getter Hologram hologram;
    private @Getter boolean glowing, lookHeadRotate;
    private @Getter ChatColor color;

    public NPC(Location location, String skin, Hologram hologram) {
        MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();

        players = new ArrayList<>();
        clicks = new EnumMap<>(Interact.class);
        items = new EnumMap<>(EnumItemSlot.class);
        this.location = location;
        uuid = UUID.randomUUID();
        name = uuid.toString().replace("-", "").substring(0, 10);
        profile = new GameProfile(uuid, name);
        player = new EntityPlayer(minecraftServer, worldServer, profile, new PlayerInteractManager(worldServer));
        player.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        applySkin(skin);
        id = player.getId();
        if (hologram != null)
            hologram.moveToLocation(location.clone().add(0, player.getFlag(1) ? 1.38 : 1.78, 0));
        this.hologram = hologram;
        color = ChatColor.WHITE;
        module.register(this);
    }

    public NPC(Location location, String skin, String... text) {
        this(location, skin, new Hologram(location.clone().add(0, 1.8d, 0),
                Arrays.stream(text).map(TextLine::of).collect(Collectors.toList())));
    }

    public NPC(Location location, String skin) {
        this(location, skin, (Hologram) null);
    }

    public NPC(Location location, String skin, FunctionLine... holo) {
        this(location, skin, new Hologram(location.clone(), holo));
    }

    public void show(Player player) {
        updateTeam(player);
        PacketPlayerUtils.playerInfo(player, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, this.player);
        PacketPlayerUtils.spawnPlayer(player, this.player);
        headRotate(player, this.player.yaw, this.player.pitch);
        PacketPlayerUtils.playerInfo(player, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, this.player);
        updateItems(player);
    }

    public void destroy(Player player) {
        PacketPlayerUtils.playerInfo(player, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, this.player);
        PacketEntityUtil.destroyEntitys(player, this.player);
        PacketPlayerUtils.playerInfo(player, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, this.player);
    }

    public void destroyAll() {
        players.stream().map(Bukkit::getPlayer).forEach(this::destroy);
    }

    public void update(Player player) {
        PacketEntityUtil.metadataUpdate(player, getId(), this.player.getDataWatcher());
    }

    public void updateAll() {
        players.stream().map(Bukkit::getPlayer).forEach(this::update);
    }

    public void reload(Player player) {
        destroy(player);
        show(player);
    }

    public void reloadAll() {
        players.stream().map(Bukkit::getPlayer).forEach(this::reload);
    }

    public void updateItems(Player player) {
        for (Map.Entry<EnumItemSlot, ItemStack> item : items.entrySet()) {
            PacketEntityUtil.equippedEntityItem(player, this.player, item.getValue(), item.getKey());
        }
    }

    public void updateItemsAll(){
        players.stream().map(Bukkit::getPlayer).forEach(this::updateItems);
    }

    public void updateTeam(Player player){
        Scoreboard board = player.getScoreboard();
        Team npc;
        if ((npc = board.getTeam("npc_" + color.toString())) == null){
            npc = board.registerNewTeam("npc_" + color.toString());
            npc.setNameTagVisibility(NameTagVisibility.NEVER);
            npc.setPrefix(color + "");
        }
        npc.addEntry(name);
    }

    public void updateTeamAll(){
        players.stream().map(Bukkit::getPlayer).forEach(this::updateTeam);
    }

    public void lookOnPlayer(Player player){
        Location npcloc = location.clone().setDirection(player.getLocation().subtract(this.location.clone()).toVector());
        float yaw = npcloc.getYaw();
        float pitch = npcloc.getPitch();
        headRotate(player, yaw, pitch);
    }

    public void updateLook(){
        players.stream().map(Bukkit::getPlayer).forEach(this::lookOnPlayer);
    }

    public void updateText(String... text){
        if (hologram != null)
            hologram.update(Arrays.stream(text).map(TextLine::of).collect(Collectors.toList()));
    }

    public NPC setItem(ItemStack item, EnumItemSlot slot){
        items.put(slot, item);
        updateItemsAll();
        return this;
    }

    public NPC moveToLocation(Location location){
        this.location = location;
        player.setLocation(location.getX(), location.getY(), location.getZ(), player.yaw, player.pitch);
        if (hologram != null)
            hologram.moveToLocation(location.clone().add(0, player.getFlag(1) ? 1.38 : 1.78, 0));
        reloadAll();
        return this;
    }

    public NPC applySkin(String skin){
        SkinWorker.applySkin(player, skin);
        reloadAll();
        updateAll();
        return this;
    }

    public NPC setGameMode(GameMode mode){
        player.getBukkitEntity().setGameMode(mode);
        updateAll();
        return this;
    }

    public NPC setGravity(boolean gravity){
        player.setNoGravity(!gravity);
        updateAll();
        return this;
    }

    public NPC setGlow(boolean glow){
        glowing = glow;
        player.setFlag(6, glow);
        updateTeamAll();
        updateAll();
        return this;
    }

    public NPC setHeadLookRotate(boolean look){
        lookHeadRotate = look;
        return this;
    }

    public NPC setColor(ChatColor color){
        this.color = color;
        updateTeamAll();
        updateAll();
        return this;
    }

    public NPC setSneaking(boolean sneaking){
        setFlag(1, sneaking);
        if (hologram != null)
            hologram.moveToLocation(location.clone().add(0, sneaking ? 1.38 : 1.78, 0));
        return this;
    }

    public NPC setBurning(boolean burn){
        setFlag(0, burn);
        return this;
    }

    public NPC setLay(boolean lay){
        setFlag(7, lay);
        return this;
    }

    public NPC setInvisible(boolean invis){
        setFlag(5, invis);
        return this;
    }

    public NPC setFlag(int flag, boolean flagValue){
        player.setFlag(flag, flagValue);
        updateAll();
        return this;
    }

    public NPC headRotate(double yaw, double pitch){
        player.yaw = (float) yaw;
        player.pitch = (float) pitch;
        players.stream().map(Bukkit::getPlayer).forEach(player -> {
            headRotate(player, yaw, pitch);
        });
        return this;
    }

    public NPC headRotate(Player player, double yaw, double pitch){
        PacketEntityUtil.headRotation(player, this.player, (byte)(yaw * 256.0F / 360.0F));
        PacketEntityUtil.entityHeadLook(player, this.player, (byte)(int)(yaw * 256.0F / 360.0F), (byte)(pitch * 256.0F / 360.0F));
        return this;
    }

    public void status(Status status){
        players.stream().map(Bukkit::getPlayer).forEach(player -> {
            PacketEntityUtil.statusEntity(player, this.player, status.getValue());
        });
    }

    public NPC addClick(Interact click, Consumer<Player> consumer) {
        clicks.put(click, consumer);
        return this;
    }

    public NPC onClick(Player player, Interact click){
        if (hasClick(click))
            clicks.get(click).accept(player);
        return this;
    }

    public NPC setCollides(boolean collides) {
        player.collides = collides;
        updateAll();
        reloadAll();
        return this;
    }

    public boolean hasClick(Interact click) {
        return clicks.containsKey(click);
    }

}

