package xyz.upperlevel.spigot.book;

import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public final class NmsBookHelper {
    private static final String version;

    private static final Class<?> craftMetaBookClass;
    private static final Field craftMetaBookField;
    private static final Method chatSerializerA;

    private static final Method craftPlayerGetHandle;
    private static final Method entityPlayerOpenBook;
    private static final Object[] hands;

    //Older versions
    /*private static final Field entityHumanPlayerConnection;
    private static final Method playerConnectionSendPacket;

    private static final Constructor<?> packetPlayOutCustomPayloadConstructor;
    private static final Constructor<?> packetDataSerializerConstructor;*/

    private static final Method nmsItemStackSave;
    private static final Constructor<?> nbtTagCompoundConstructor;

    private static final Method craftItemStackAsNMSCopy;

    static {
        version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            craftMetaBookClass = getCraftClass("inventory.CraftMetaBook");
            craftMetaBookField = craftMetaBookClass.getDeclaredField("pages");
            Class<?> chatSerializer;
            try {
                chatSerializer = getNmsClass("IChatBaseComponent$ChatSerializer");
            } catch (Exception e) {
                chatSerializer = getNmsClass("ChatSerializer");
            }
            chatSerializerA = chatSerializer.getDeclaredMethod("a", String.class);

            final Class<?> craftPlayerClass = getCraftClass("entity.CraftPlayer");
            craftPlayerGetHandle = craftPlayerClass.getMethod("getHandle");

            final Class<?> entityPlayerClass = getNmsClass("EntityPlayer");
            final Class<?> itemStackClass = getNmsClass("ItemStack");
            final Class<?> enumHandClass = getNmsClass("EnumHand");
            entityPlayerOpenBook = entityPlayerClass.getMethod("a", itemStackClass, enumHandClass);
            hands = enumHandClass.getEnumConstants();
            //Older versions
            /*entityHumanPlayerConnection = entityPlayerClass.getField("playerConnection");
            final Class<?> playerConnectionClass = getNmsClass("PlayerConnection");
            playerConnectionSendPacket = playerConnectionClass.getMethod("sendPacket", getNmsClass("Packet"));

            final Class<?> packetDataSerializerClasss = getNmsClass("PacketDataSerializer");
            packetPlayOutCustomPayloadConstructor = getNmsClass("PacketPlayOutCustomPayload").getConstructor(String.class, packetDataSerializerClasss);
            packetDataSerializerConstructor = packetDataSerializerClasss.getConstructor(ByteBuf.class);*/

            final Class<?> craftItemStackClass = getCraftClass("inventory.CraftItemStack");
            craftItemStackAsNMSCopy = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
            Class<?> nmsItemStackClazz = getNmsClass("ItemStack");
            Class<?> nbtTagCompoundClazz = getNmsClass("NBTTagCompound");
            nmsItemStackSave = nmsItemStackClazz.getMethod("save", nbtTagCompoundClazz);
            nbtTagCompoundConstructor = nbtTagCompoundClazz.getConstructor();

        } catch (Exception e) {
            throw new IllegalStateException("Cannot initiate reflections for " + version, e);
        }
    }


    @SuppressWarnings("unchecked")//reflections = unchecked warnings
    public static void setPages(BookMeta meta, BaseComponent[][] components) {
        try {
            List<Object> pages = (List<Object>) craftMetaBookField.get(meta);
            pages.clear();
            for(BaseComponent[] c : components) {
                final String json = ComponentSerializer.toString(c);
                //System.out.println("page:" + json); //Debug
                pages.add(chatSerializerA.invoke(null, json));
            }
        } catch (Exception e) {
            throw new UnsupportedVersionException(e);
        }
    }

    public static void openBook(Player player, ItemStack book, boolean offHand) {
        //nms(player).openBook(nms(player), nms(book), hand);
        try {
            //Older versions:
            /*playerConnectionSendPacket.invoke(
                    entityHumanPlayerConnection.get(toNms(player)),
                    createBookOpenPacket()
            );*/
            entityPlayerOpenBook.invoke(
                    toNms(player),
                    nmsCopy(book),
                    hands[offHand ? 1 : 0]
            );
        } catch (Exception e) {
            throw new UnsupportedVersionException(e);
        }
    }

    //Older versions
    /*public static Object createBookOpenPacket() {
        //new PacketPlayOutCustomPayload("MC|BOpen", new PacketDataSerializer(Unpooled.buffer())));
        try {
            return packetPlayOutCustomPayloadConstructor.newInstance(
                    "MC|BOpen",
                    packetDataSerializerConstructor.newInstance(Unpooled.buffer())
            );
        } catch (Exception e) {
            throw new UnsupportedVersionException(e);
        }
    }*/

    public static BaseComponent[] itemToComponents(ItemStack item) {
       return jsonToComponents(itemToJson(item));
    }

    public static BaseComponent[] jsonToComponents(String json) {
        return new BaseComponent[] {
                new TextComponent(json)
        };
    }

    private static String itemToJson(ItemStack item) {
        try {
            //net.minecraft.server.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
            Object nmsItemStack = nmsCopy(item);


            //net.minecraft.server.NBTTagCompound compound = new NBTTagCompound();
            //compound = nmsItemStack.save(compound);
            Object emptyTag = nbtTagCompoundConstructor.newInstance();
            Object json = nmsItemStackSave.invoke(nmsItemStack, emptyTag);
            return json.toString();
        } catch (Exception e) {
            throw new UnsupportedVersionException(e);
        }
    }


    private static class UnsupportedVersionException extends RuntimeException {
        @Getter
        private final String version = NmsBookHelper.version;

        public UnsupportedVersionException(Exception e) {
            super("Error while executing reflections, submit to developers the following log (version: " + NmsBookHelper.version + ")", e);
        }
    }


    public static Object toNms(Player player) throws InvocationTargetException, IllegalAccessException {
        return craftPlayerGetHandle.invoke(player);
    }

    public static Object nmsCopy(ItemStack item) throws InvocationTargetException, IllegalAccessException {
        return craftItemStackAsNMSCopy.invoke(null, item);
    }

    public static Class<?> getNmsClass(String className) {
        try {
            return Class.forName("net.minecraft.server." + version + "." + className);
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Class<?> getCraftClass(String path) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + version + "." + path);
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
