package xyz.upperlevel.spigot.book.internals;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.spigot.book.UnsupportedVersionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tiny NMS utility, it should be loaded only if the NMS helper is used or if the developer
 * uses ItemStack hovering (since it requires saving the NMS NBT as a JSON, and that is currently impossible)
 */
public final class NmsUtil {
    public static final String version;
    public static final int major, minor;

    private static final Method craftPlayerGetHandle;

    private static final Method nmsItemStackSave;
    private static final Constructor<?> nbtTagCompoundConstructor;

    private static final Method craftItemStackAsNMSCopy;

    static  {
        version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            Pattern pattern = Pattern.compile("v([0-9]+)_([0-9]+)");
            Matcher m = pattern.matcher(version);
            if (m.find()) {
                major = Integer.parseInt(m.group(1));
                minor = Integer.parseInt(m.group(2));
            } else {
                throw new IllegalStateException(
                        "Cannot parse version \"" + version + "\", make sure it follows \"v<major>_<minor>...\"");
            }


            final Class<?> craftPlayerClass = getCraftClass("entity.CraftPlayer");
            craftPlayerGetHandle = craftPlayerClass.getMethod("getHandle");

            final Class<?> craftItemStackClass = getCraftClass("inventory.CraftItemStack");
            craftItemStackAsNMSCopy = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
            Class<?> nbtTagCompoundClass = getNmsClass("NBTTagCompound", "nbt", true);


            final Class<?> itemStackClass = getNmsClass("ItemStack", "world.item", true);
            Method itemStackSave;
            try {
                // Mojang mappings
                itemStackSave = itemStackClass.getMethod("b", nbtTagCompoundClass);
            } catch (NoSuchMethodException e) {
                // Spigot mappings
                itemStackSave = itemStackClass.getMethod("save", nbtTagCompoundClass);
            }
            nmsItemStackSave = itemStackSave;
            nbtTagCompoundConstructor = nbtTagCompoundClass.getConstructor();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot initiate reflections for " + version, e);
        }
    }

    public static String itemToJson(ItemStack item) {
        try {
            // net.minecraft.server.ItemStack nmsItemStack =
            // CraftItemStack.asNMSCopy(itemStack);
            Object nmsItemStack = nmsCopy(item);

            // net.minecraft.server.NBTTagCompound compound = new NBTTagCompound();
            // compound = nmsItemStack.save(compound);
            Object emptyTag = nbtTagCompoundConstructor.newInstance();
            Object json = nmsItemStackSave.invoke(nmsItemStack, emptyTag);
            return json.toString();
        } catch (Exception e) {
            throw new UnsupportedVersionException(e);
        }
    }

    /**
     * Gets the EntityPlayer handled by the argument
     *
     * @param player the Player handler
     * @return the handled class
     * @throws InvocationTargetException when some problems are found with the
     *                                   reflection
     * @throws IllegalAccessException    when some problems are found with the
     *                                   reflection
     */
    public static Object toNms(Player player) throws InvocationTargetException, IllegalAccessException {
        return craftPlayerGetHandle.invoke(player);
    }

    /**
     * Creates a NMS copy of the parameter
     *
     * @param item the ItemStack to be nms-copied
     * @return a NMS-ItemStack that is the equivalent of the one passed as argument
     * @throws InvocationTargetException when some problems are found with the
     *                                   reflection
     * @throws IllegalAccessException    when some problems are found with the
     *                                   reflection
     */
    public static Object nmsCopy(ItemStack item) throws InvocationTargetException, IllegalAccessException {
        return craftItemStackAsNMSCopy.invoke(null, item);
    }

    /**
     * Use {@link #getNmsClass(String, String, boolean)} to make sure of compatibility with mc 1.17+.
     * @param className
     *     The simple name of the class
     * @param required
     *     If true a {@link RuntimeException} will be thrown when the class cannot be found
     * @return Net Minecraft Server class with the provided name or null (if not required)
     */
    public static Class<?> getNmsClass(String className, boolean required) {
        return getNms17PlusClass("server." + NmsUtil.version + "." + className, required);
    }

    /**
     * @param className
     *     The simple name of the class
     * @param post17middlePackage
     *     The package the class have been relocated to after mc 1.17
     * @param required
     *     If true a {@link RuntimeException} will be thrown when the class cannot be found
     *
     * @return Net Minecraft Server class. Either {@code net.minecraft.server.$className} if pre 1.17 or {@code net.minecraft.$post17middlePackage.$className} if v1.17+
     */
    public static Class<?> getNmsClass(String className, String post17middlePackage, boolean required) {
        Class<?> pre = getNmsClass(className, false);
        if (pre != null) {
            return pre;
        }
        return getNms17PlusClass(post17middlePackage + "." + className, required);
    }

    private static Class<?> getNms17PlusClass(String className, boolean required) {
        try {
            return Class.forName("net.minecraft." + className);
        } catch (ClassNotFoundException e) {
            if (required) {
                throw new RuntimeException("Cannot find NMS class " + className, e);
            }
            return null;
        }
    }

    public static Class<?> getCraftClass(String path) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + NmsUtil.version + "." + path);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot find CraftBukkit class at path: " + path, e);
        }
    }


    private NmsUtil() {}
}
