package ru.dargen.tycoon.utils;

import net.minecraft.server.v1_12_R1.NBTBase;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ItemBuilder extends ItemStack {

    public ItemBuilder(ItemStack item){
        super(item);
    }

    public ItemBuilder(Material material){
        super(material);
    }

    public ItemBuilder(int typeid){
        super(typeid);
    }

    public ItemBuilder(Material material,int durability){
        super(material,1,(short)durability);
    }

    public ItemBuilder(Material material,int amount,int durability){
        super(material,amount,(short)durability);
    }

    public ItemBuilder setName(String iname){
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(iname);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setDurability(int durability){
        setDurability((short)durability);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean is) {
        ItemMeta meta = getItemMeta();
        meta.setUnbreakable(is);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setItemAmount(int amount){
        setAmount(amount);
        return this;
    }

    public ItemBuilder setItemType(Material type){
        setType(type);
        return this;
    }

    public ItemBuilder setItemTypeId(int type){
        setTypeId(type);
        return this;
    }

    public ItemBuilder addLoreLine(String line){
        ItemMeta meta = getItemMeta();
        List<String> lore = new ArrayList<>();
        if(meta.hasLore())
            lore=new ArrayList<>(meta.getLore());
        lore.add(line);
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLoreLine(int index,String line){
        ItemMeta meta = getItemMeta();
        List<String> lore = new ArrayList<>();
        if(meta.hasLore())
            lore=new ArrayList<>(meta.getLore());
        lore.set(index, line);
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeLoreLine(String line){
        ItemMeta meta = getItemMeta();
        List<String> lore = new ArrayList<>();
        if(meta.hasLore())
            lore=new ArrayList<>(meta.getLore());
        lore.remove(line);
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeLoreLine(int index){
        ItemMeta meta = getItemMeta();
        List<String> lore = new ArrayList<>();
        if(meta.hasLore())
            lore=new ArrayList<>(meta.getLore());
        lore.remove(index);
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setItemLore(List<String> lore){
        ItemMeta meta = getItemMeta();
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setItemLore(String... lore){
        setItemLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder addLore(List<String> collect) {
        ItemMeta meta = getItemMeta();
        List<String> lore = new ArrayList<>();
        if(meta.hasLore())
            lore = new ArrayList<>(meta.getLore());
        lore.addAll(collect);
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder addLore(String... collect) {
        return addLore(Arrays.asList(collect));
    }

    public ItemBuilder addItemEnchant(Enchantment enchantment, int level) {
        ItemMeta meta = getItemMeta();
        meta.addEnchant(enchantment, level, true);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeLore(){
        ItemMeta meta = getItemMeta();
        List<String> lore = meta.getLore();
        lore.clear();
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder addFlags(ItemFlag... flags){
        ItemMeta meta = getItemMeta();
        meta.addItemFlags(flags);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeFlag(ItemFlag... flags){
        ItemMeta meta = getItemMeta();
        meta.removeItemFlags(flags);
        setItemMeta(meta);
        return this;
    }

    public List<String> getLore(){
        ItemMeta meta = getItemMeta();
        return meta.hasLore() ? meta.getLore() : new ArrayList<>();
    }

    public ItemBuilder setDyeColor(DyeColor color) {
        setDurability(color.getDyeData());
        return this;
    }

    public ItemBuilder setWoolColor(DyeColor color) {
        if (!getType().equals(Material.WOOL))
            return this;
        setDurability(color.getDyeData());
        return this;
    }

    public ItemBuilder setLeatherArmorColor(Color color) {
        try {
            LeatherArmorMeta im = (LeatherArmorMeta)getItemMeta();
            im.setColor(color);
            setItemMeta(im);
        } catch (ClassCastException classCastException) {}
        return this;
    }

    public ItemBuilder setSkullOwner(String owner) {
        try {
            SkullMeta im = (SkullMeta)getItemMeta();
            im.setOwner(owner);
            setItemMeta(im);
        } catch (ClassCastException classCastException) {}
        return this;
    }

    public ItemBuilder setTag(String tag, NBTBase value){
        net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(this);
        NBTTagCompound nmsTag = nms.getTag();
        if (nmsTag == null)
            nmsTag = new NBTTagCompound();
        nmsTag.set(tag, value);
        nms.setTag(nmsTag);
        return new ItemBuilder(CraftItemStack.asBukkitCopy(nms));
    }

    public <T extends NBTBase> T getTag(String tag){
        net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(this);
        NBTTagCompound nmsTag = nms.getTag();
        if (nmsTag == null)
            nmsTag = new NBTTagCompound();
        return nmsTag.get(tag) == null ? null : (T) nmsTag.get(tag);
    }

    public Map<String, NBTBase> getTags(){
        net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(this);
        NBTTagCompound nmsTag = nms.getTag();
        if (nmsTag == null)
            nmsTag = new NBTTagCompound();
        return nmsTag.map;
    }

    public ItemBuilder setTags(Map<String, NBTBase> tags){
        net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(this);
        NBTTagCompound nmsTag = nms.getTag();
        if (nmsTag == null)
            nmsTag = new NBTTagCompound();
        for (String key : tags.keySet()) {
            nmsTag.set(key, tags.get(key));
        }
        return new ItemBuilder(CraftItemStack.asBukkitCopy(nms))    ;
    }


}
