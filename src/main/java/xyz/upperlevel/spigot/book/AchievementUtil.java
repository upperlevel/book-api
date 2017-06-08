package xyz.upperlevel.spigot.book;

import org.bukkit.Achievement;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Achievement.*;

public final class AchievementUtil {
    private static final Map<Achievement, String> achievements = new EnumMap<Achievement, String>(Achievement.class) {{
        put(OPEN_INVENTORY, "openInventory");
        put(MINE_WOOD, "mineWood");
        put(BUILD_WORKBENCH, "buildWorkBench");
        put(BUILD_PICKAXE, "buildPickaxe");
        put(BUILD_FURNACE, "buildFurnace");
        put(ACQUIRE_IRON, "aquireIron");
        put(BUILD_HOE, "buildHoe");
        put(MAKE_BREAD, "makeBread");
        put(BAKE_CAKE,"bakeCake");
        put(BUILD_BETTER_PICKAXE,"buildBetterPickaxe");
        put(COOK_FISH,"cookFish");
        put(ON_A_RAIL,"onARail");
        put(BUILD_SWORD,"buildSword");
        put(KILL_ENEMY,"killEnemy");
        put(KILL_COW,"killCow");
        put(FLY_PIG,"flyPig");
        put(SNIPE_SKELETON,"snipeSkeleton");
        put(GET_DIAMONDS,"diamonds");
        put(NETHER_PORTAL,"portal");
        put(GHAST_RETURN,"ghast");
        put(GET_BLAZE_ROD,"blazerod");
        put(BREW_POTION,"potion");
        put(END_PORTAL,"thEnd");
        put(THE_END,"theEnd2");
        put(ENCHANTMENTS,"enchantments");
        put(OVERKILL,"overkill");
        put(BOOKCASE,"bookacase");
        put(EXPLORE_ALL_BIOMES,"exploreAllBiomes");
        put(SPAWN_WITHER,"spawnWither");
        put(KILL_WITHER,"killWither");
        put(FULL_BEACON,"fullBeacon");
        put(BREED_COW,"breedCow");
        put(DIAMONDS_TO_YOU,"diamondsToYou");
        put(OVERPOWERED, "overpowered");
    }};


    /**
     * Gets the json id from the bukkit achievement passed as argument
     * @param achievement the achievement
     * @return the achievement's id or null if not found
     */
    public static String toId(Achievement achievement) {
        return achievements.get(achievement);
    }

    private AchievementUtil(){}
}
