package be.kakumi.kachat.commands;

import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.models.Channel;
import be.kakumi.kachat.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChannelsCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if (strings.length > 0) {
                try {
                    player.openInventory(createInventory(Integer.parseInt(strings[0]), player));
                } catch (NumberFormatException ex) {
                    player.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.INVALID_NUMBER_ARG));
                }
            } else {
                player.openInventory(createInventory(1, player));
            }
        } else {
            commandSender.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.MUST_BE_A_PLAYER));
        }

        return true;
    }

    private Inventory createInventory(int page, Player player) {
        int slotMax = 54;
        String name = KAChatAPI.getInstance().getMessageManager().get(MessageManager.INVENTORY_CHANNELS_TITLE, false);
        List<Channel> channelList = KAChatAPI.getInstance().getChannels().stream().filter(Channel::isListed).collect(Collectors.toList());
        int startIndex = (page - 1) * slotMax;
        if (page > 1) { //We remove arrows from previous page
            startIndex = startIndex - ((page - 2) * 2) - 1;
        }
        int slot = 0;

        //Count max page
        int maxPage = (int) Math.ceil(channelList.size() / (double) slotMax);
        int size = channelList.size() % slotMax;

        size = size + ((maxPage - 1) * 2); //-1 Because we removed last page and next page arrows
        maxPage = maxPage + (int) Math.floor(size / (double) slotMax);

        //Count inventory size
        int sizeInv = (int) Math.ceil(channelList.size() / (double) 9);
        if (sizeInv > 6) sizeInv = 6;

        Inventory inventory = Bukkit.createInventory(player, sizeInv * 9, name + " §b- §r" + page + "§b/§r" + maxPage);

        for(int i = startIndex; i < channelList.size() && slot < slotMax; i++) {
            if (slot == 45 && page > 1) {
                slot++;
            }

            Channel channel = channelList.get(i);
            List<String> lore = new ArrayList<>();
            String prefix = channel.getPrefix().replace("&", "§");
            lore.add(KAChatAPI.getInstance().getMessageManager().get(MessageManager.INVENTORY_CHANNELS_LORE_PREFIX, false) + (prefix.equals("") ? "/" : prefix));
            lore.add(KAChatAPI.getInstance().getMessageManager().get(MessageManager.INVENTORY_CHANNELS_LORE_RANGE, false) + (channel.getRange() == 0 ? KAChatAPI.getInstance().getMessageManager().get(MessageManager.UNLIMITED, false) : channel.getRange()));
            lore.add(KAChatAPI.getInstance().getMessageManager().get(MessageManager.INVENTORY_CHANNELS_LORE_PRIVATE, false) + (channel.isForInside() ? KAChatAPI.getInstance().getMessageManager().get(MessageManager.YES, false) : KAChatAPI.getInstance().getMessageManager().get(MessageManager.NO, false)));
            lore.add(KAChatAPI.getInstance().getMessageManager().get(MessageManager.INVENTORY_CHANNELS_LORE_WORLD, false) + (channel.getWorld().equals("") ? "/" : channel.getWorld()));
            lore.add(KAChatAPI.getInstance().getMessageManager().get(MessageManager.INVENTORY_CHANNELS_LORE_SYMBOL, false) + (channel.getOverrideSymbol().equals("") ? "/" : channel.getOverrideSymbol()));

            if (!(channel.getPermissionToUse().equals("") || player.hasPermission(channel.getPermissionToUse()))) {
                lore.add("");
                lore.add(KAChatAPI.getInstance().getMessageManager().get(MessageManager.NO_PERMISSION, false));
            }

            ItemStack item = new ItemStack(Material.BOOK, 1);
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.setDisplayName("§f" + channel.getCommand());
            meta.setLore(lore);
            item.setItemMeta(meta);

            inventory.setItem(slot, item);
            slot++;
        }

        if (page > 1) {
            ItemStack item = new ItemStack(Material.ARROW, 1);
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.setDisplayName(KAChatAPI.getInstance().getMessageManager().get(MessageManager.INVENTORY_PREVIOUS_PAGE, false));
            item.setItemMeta(meta);

            inventory.setItem(45, item);
        }

        if (page < maxPage) {
            ItemStack item = new ItemStack(Material.ARROW, 1);
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.setDisplayName(KAChatAPI.getInstance().getMessageManager().get(MessageManager.INVENTORY_NEXT_PAGE, false));
            item.setItemMeta(meta);

            inventory.setItem(53, item);
        }

        return inventory;
    }
}
