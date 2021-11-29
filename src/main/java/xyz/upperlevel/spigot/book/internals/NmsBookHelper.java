package xyz.upperlevel.spigot.book.internals;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import xyz.upperlevel.spigot.book.UnsupportedVersionException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static xyz.upperlevel.spigot.book.internals.NmsUtil.*;

/**
 * The NMS helper for all the Book-API
 *
 * Note: we added SpigotBookHelper and it should be more compatible,
 * This WILL NOT WORK after 1.17.x due to spigot dropping NMS methods names remaps.
 */
public class NmsBookHelper implements BookHelper {
    private final boolean doubleHands;

    private final Field craftMetaBookField;
    // Converts JSON string to IChatBaseComponent
    private final Method chatSerializerA;

    // Only present in versions >= 1.16.4 (otherwise null)
    private final Method craftMetaBookInternalAddPageMethod;

    // This method takes an enum that represents the player's hand only in versions
    // >= 1.9
    // In the other versions it only takes the nms item
    private final Method entityPlayerOpenBook;
    // only version >= 1.9
    private final Object[] hands;

    public NmsBookHelper() {
        doubleHands = major <= 1 && minor >= 9;
        try {
            Class<?> craftMetaBookClass = getCraftClass("inventory.CraftMetaBook");

            craftMetaBookField = craftMetaBookClass.getDeclaredField("pages");
            craftMetaBookField.setAccessible(true);

            Method cmbInternalAddMethod = null;
            try {
                //method is protected
                cmbInternalAddMethod = craftMetaBookClass.getDeclaredMethod("internalAddPage", String.class);
                cmbInternalAddMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                // Internal data change in 1.16.4
                // To detect if the server is using the new internal format we check if the internalAddPageMethod exists
                // see https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/commits/560b65c4f8a15619aaa4a1737c7040f21e725cce
            }
            craftMetaBookInternalAddPageMethod = cmbInternalAddMethod;

            Class<?> chatSerializer = getNmsClass("IChatBaseComponent$ChatSerializer", "network.chat", false);
            if (chatSerializer == null) {
                //ChatSerializer was renamed to IChatBaseComponent$ChatSerializer
                // this class will only exists when below on version below 1.17
                chatSerializer = getNmsClass("ChatSerializer", true);
            }

            // On versions < 1.16.4 the CraftMetaBook accepted IChatBaseComponent
            // This method converts JSON strings to its IChatBaseComponent equivalent
            chatSerializerA = chatSerializer.getDeclaredMethod("a", String.class);

            final Class<?> entityPlayerClass = getNmsClass("EntityPlayer", "server.level", true);
            final Class<?> itemStackClass = getNmsClass("ItemStack", "world.item", true);
            if (doubleHands) {
                final Class<?> enumHandClass = getNmsClass("EnumHand", "world", true);

                Method openBookMethod;

                try {
                    // In 1.14.4 The method was renamed from "a" to "openBook"
                    // There is no way to test for the "fix" number in the version so we just try-catch it
                    openBookMethod = entityPlayerClass.getMethod("a", itemStackClass, enumHandClass);
                } catch (NoSuchMethodException e) {
                    openBookMethod = entityPlayerClass.getMethod("openBook", itemStackClass, enumHandClass);
                }

                entityPlayerOpenBook = openBookMethod;

                hands = enumHandClass.getEnumConstants();
            } else {
                entityPlayerOpenBook = entityPlayerClass.getMethod("openBook", itemStackClass);
                hands = null;
            }

        } catch (Exception e) {
            throw new IllegalStateException("Cannot initiate reflections for " + version, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked") // reflections = unchecked warnings
    public void setPages(BookMeta meta, BaseComponent[][] components) {
        try {
            List<Object> pages = (List<Object>) craftMetaBookField.get(meta);
            if (pages != null) {
                pages.clear();
            }
            for (BaseComponent[] c : components) {
                if(c == null) {
                    continue;
                }
                final String json = ComponentSerializer.toString(c);
                if (craftMetaBookInternalAddPageMethod != null) {
                    craftMetaBookInternalAddPageMethod.invoke(meta, json);
                } else {
                    // Are pages always not null pre 1.16?
                    pages.add(chatSerializerA.invoke(null, json));
                }
            }
        } catch (Exception e) {
            throw new UnsupportedVersionException(e);
        }
    }

    @Override
    public void openBook(Player player, ItemStack book) {
        // nms(player).openBook(nms(player), nms(book), hand);
        try {
            if (doubleHands) {
                entityPlayerOpenBook.invoke(toNms(player), nmsCopy(book), hands[0]);
            } else {
                entityPlayerOpenBook.invoke(toNms(player), nmsCopy(book));
            }
        } catch (Exception e) {
            throw new UnsupportedVersionException(e);
        }
    }
}
