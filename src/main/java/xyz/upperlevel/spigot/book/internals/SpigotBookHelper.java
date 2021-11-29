package xyz.upperlevel.spigot.book.internals;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

/**
 * New "helper", uses newer spigot APIs.
 *
 * Unfortunately not everything is possible with spigot and we still need to use NMS to save an ItemStack.
 */
public class SpigotBookHelper implements BookHelper {
    @Override
    public void setPages(BookMeta meta, BaseComponent[][] components) {
        meta.spigot().setPages(components);
    }

    @Override
    public void openBook(Player player, ItemStack book) {
        player.openBook(book);
    }

    public static boolean isSupported() {
        try {
            Player.class.getMethod("openBook", ItemStack.class);
            BookMeta.Spigot.class.getMethod("setPages", BaseComponent[][].class);
        } catch (NoSuchMethodException e) {
            return false;
        }

        return true;
    }
}
