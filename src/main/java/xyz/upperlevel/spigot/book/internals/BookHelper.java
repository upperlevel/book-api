package xyz.upperlevel.spigot.book.internals;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public interface BookHelper {

    /**
     * Sets the pages of the book to the components json equivalent
     *
     * @param meta       the book meta to change
     * @param components the pages of the book
     */
    void setPages(BookMeta meta, BaseComponent[][] components);

    /**
     * Opens the book to a player (the player needs to have the book in one of his
     * hands)
     *
     * @param player  the player
     * @param book    the book to open
     */
    void openBook(Player player, ItemStack book);

    /**
     * Translates an ItemStack to his Chat-Component equivalent
     *
     * @param item the item to be converted
     * @return a Chat-Component equivalent of the parameter
     */
    default BaseComponent[] itemToComponents(ItemStack item) {
        return new BaseComponent[] { new TextComponent(itemToJson(item)) };
    }

    /**
     * Translates an ItemStack to his json equivalent
     *
     * @param item the item to be converted
     * @return a json equivalent of the parameter
     */
    default String itemToJson(ItemStack item) {
        // There's no alternative for now
        // This will load all the NMS utility if it wasn't loaded already
        return NmsUtil.itemToJson(item);
    }
}
