package xyz.upperlevel.spigot.book;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.*;

public final class BookUtil {
    private static final boolean canTranslateDirectly;

    static {
        boolean success = true;
        try {
            ChatColor.BLACK.asBungee();
        } catch (NoSuchMethodError e) {
            success = false;
        }
        canTranslateDirectly = success;
    }


    /**
     * Opens a book GUI to the player
     * @param p the player
     * @param book the book to be opened
     */
    @SuppressWarnings("deprecation")
    public static void openPlayer(Player p, ItemStack book) {
        CustomBookOpenEvent event = new CustomBookOpenEvent(p, book, false);
        //Call the CustomBookOpenEvent
        Bukkit.getPluginManager().callEvent(event);
        //Check if it's cancelled
        if(event.isCancelled())
            return;
        p.closeInventory();
        //Store the previous item
        ItemStack hand = p.getItemInHand();

        p.setItemInHand(event.getBook());
        p.updateInventory();

        //Opening the GUI
        NmsBookHelper.openBook(p, event.getBook(), event.getHand() == CustomBookOpenEvent.Hand.OFF_HAND);

        //Returning whatever was on hand.
        p.setItemInHand(hand);
        p.updateInventory();
    }

    /**
     * Creates a BookBuilder instance with a written book as the Itemstack's type
     * @return
     */
    public static BookBuilder writtenBook() {
        return new BookBuilder(new ItemStack(Material.WRITTEN_BOOK));
    }


    /**
     * Helps the user to create a book
     */
    public static class BookBuilder {
        private final BookMeta meta;
        private final ItemStack book;

        /**
         * Creates a new instance of the BookBuilder from an ItemStack representing the book item
         * @param book the book's ItemStack
         */
        public BookBuilder(ItemStack book) {
            this.book = book;
            this.meta = (BookMeta)book.getItemMeta();
        }

        /**
         * Sets the title of the book
         * @param title the title of the book
         * @return the BookBuilder's calling instance
         */
        public BookBuilder title(String title) {
            meta.setTitle(title);
            return this;
        }

        /**
         * Sets the author of the book
         * @param author the author of the book
         * @return the BookBuilder's calling instance
         */
        public BookBuilder author(String author) {
            meta.setAuthor(author);
            return this;
        }

        /**
         * Sets the pages of the book without worrying about json or interactivity
         * @param pages text-based pages
         * @return the BookBuilder's calling instance
         */
        public BookBuilder pagesRaw(String... pages) {
            meta.setPages(pages);
            return this;
        }

        /**
         * Sets the pages of the book without worrying about json or interactivity
         * @param pages text-based pages
         * @return the BookBuilder's calling instance
         */
        public BookBuilder pagesRaw(List<String> pages) {
            meta.setPages(pages);
            return this;
        }

        /**
         * Sets the pages of the book
         * @param pages the pages of the book
         * @return the BookBuilder's calling instance
         */
        public BookBuilder pages(BaseComponent[]... pages) {
            NmsBookHelper.setPages(meta, pages);
            return this;
        }

        /**
         * Sets the pages of the book
         * @param pages the pages of the book
         * @return the BookBuilder's calling instance
         */
        public BookBuilder pages(List<BaseComponent[]> pages) {
            NmsBookHelper.setPages(meta, pages.toArray(new BaseComponent[0][]));
            return this;
        }

        /**
         * Sets the generation of the book
         * Only works from MC 1.10
         * @param generation the Book generation
         * @return the BookBuilder calling instance
         */
        public BookBuilder generation(BookMeta.Generation generation) {
            meta.setGeneration(generation);
            return this;
        }

        /**
         * Creates the book
         * @return the built book
         */
        public ItemStack build() {
            book.setItemMeta(meta);
            return book;
        }
    }

    /**
     * Helps the user creating a book's page
     */
    public static class PageBuilder {
        private List<BaseComponent> text = new ArrayList<>();

        /**
         * Adds a simple black-colored text to the page
         * @param text the text to add
         * @return the PageBuilder's calling instance
         */
        public PageBuilder add(String text) {
            this.text.add(TextBuilder.of(text).build());
            return this;
        }

        /**
         * Adds a component to the page
         * @param component the component to add
         * @return the PageBuilder's calling instance
         */
        public PageBuilder add(BaseComponent component) {
            this.text.add(component);
            return this;
        }

        /**
         * Adds one or more components to the page
         * @param components the components to add
         * @return the PageBuilder's calling instance
         */
        public PageBuilder add(BaseComponent... components) {
            this.text.addAll(Arrays.asList(components));
            return this;
        }

        /**
         * Adds one or more components to the page
         * @param components the components to add
         * @return the PageBuilder's calling instance
         */
        public PageBuilder add(Collection<BaseComponent> components) {
            this.text.addAll(components);
            return this;
        }

        /**
         * Adds a newline to the page (equivalent of adding \n to the previous component)
         * @return the PageBuilder's calling instance
         */
        public PageBuilder newLine() {
            this.text.add(new TextComponent("\n"));
            return this;
        }

        /**
         * Builds the page
         * @return an array of BaseComponents representing the page
         */
        public BaseComponent[] build() {
            return text.toArray(new BaseComponent[0]);
        }


        /**
         * Creates a new PageBuilder instance wih the parameter as the initial text
         * @param text the initial text of the page
         * @return a new PageBuilder with the parameter as the initial text
         */
        public static PageBuilder of(String text) {
            return new PageBuilder().add(text);
        }

        /**
         * Creates a new PageBuilder instance wih the parameter as the initial component
         * @param text the initial component of the page
         * @return a new PageBuilder with the parameter as the initial component
         */
        public static PageBuilder of(BaseComponent text) {
            return new PageBuilder().add(text);
        }

        /**
         * Creates a new PageBuilder instance wih the parameter as the initial components
         * @param text the initial components of the page
         * @return a new PageBuilder with the parameter as the initial components
         */
        public static PageBuilder of(BaseComponent... text) {
            PageBuilder res = new PageBuilder();
            for(BaseComponent b : text)
                res.add(b);
            return res;
        }
    }

    /**
     * A more user-friendly version of the Chat Component API designed mainly for book-based operations
     * NOTE: some (of the more useless) features don't work in some MC versions for client-based errors
     */
    @Setter
    @Getter
    @Accessors(fluent = true, chain = true)
    public static class TextBuilder {
        private String text = "";
        private ClickAction onClick = null;
        private HoverAction onHover = null;
        private ChatColor color = ChatColor.BLACK;

        @Setter(AccessLevel.NONE)//We're overwriting it
        private ChatColor[] style;

        /**
         * Sets the color of the text, or takes the previous color (if null is passed)
         * @param color the color of the text
         * @return the calling TextBuilder's instance
         */
        public TextBuilder color(ChatColor color) {
            if(color != null && !color.isColor())
                throw new IllegalArgumentException("Argument isn't a color!");
            this.color = color;
            return this;
        }

        /**
         * Sets the style of the text
         * @param style the style of the text
         * @return the calling TextBuilder's instance
         */
        public TextBuilder style(ChatColor... style) {
            for(ChatColor c : style)
                if(!c.isFormat())
                    throw new IllegalArgumentException("Argument isn't a style!");
            this.style = style;
            return this;
        }

        /**
         * Creates the component representing the built text
         * @return the component representing the built text
         */
        public BaseComponent build() {
            TextComponent res = new TextComponent(text);
            if(onClick != null)
                res.setClickEvent(new ClickEvent(onClick.action(), onClick.value()));
            if(onHover != null)
                res.setHoverEvent(new HoverEvent(onHover.action(), onHover.value()));
            if(color != null) {
                if (canTranslateDirectly)
                    res.setColor(color.asBungee());
                else
                    res.setColor(net.md_5.bungee.api.ChatColor.getByChar(color.getChar()));
            }
            if(style != null) {
                for(ChatColor c : style) {
                    switch (c) {
                        case MAGIC:
                            res.setObfuscated(true);
                            break;
                        case BOLD:
                            res.setBold(true);
                            break;
                        case STRIKETHROUGH:
                            res.setStrikethrough(true);
                            break;
                        case UNDERLINE:
                            res.setUnderlined(true);
                            break;
                        case ITALIC:
                            res.setItalic(true);
                            break;
                    }
                }
            }
            return res;
        }

        /**
         * Creates a new TextBuilder with the parameter as his initial text
         * @param text initial text
         * @return a new TextBuilder with the parameter as his initial text
         */
        public static TextBuilder of(String text) {
            return new TextBuilder().text(text);
        }
    }

    /**
     * A class representing the actions a client can do when a component is clicked
     */
    public interface ClickAction {
        /**
         * Get the Chat-Component action
         * @return the Chat-Component action
         */
        ClickEvent.Action action();

        /**
         * The value paired to the action
         * @return the value paired tot the action
         */
        String value();


        /**
         * Creates a command action: when the player clicks, the command passed as parameter gets executed with the clicker as sender
         * @param command the command to be executed
         * @return a new ClickAction
         */
        static ClickAction runCommand(String command) {
            return new SimpleClickAction(ClickEvent.Action.RUN_COMMAND, command);
        }

        /**
         * Creates a suggest_command action: when the player clicks, the book closes and the chat opens with the parameter written into it
         * @param command the command to be suggested
         * @return a new ClickAction
         */
        static ClickAction suggestCommand(String command) {
            return new SimpleClickAction(ClickEvent.Action.SUGGEST_COMMAND, command);
        }

        /**
         * Creates a open_utl action: when the player clicks the url passed as argument will open in the browser
         * @param url the url to be opened
         * @return a new ClickAction
         */
        static ClickAction openUrl(String url) {
            if(url.startsWith("http://") || url.startsWith("https://"))
                return new SimpleClickAction(ClickEvent.Action.OPEN_URL, url);
            else
                throw new IllegalArgumentException("Invalid url: \"" + url + "\", it should start with http:// or https://");
        }

        /**
         * Creates a change_page action: when the player clicks the book page will be set at the value passed as argument
         * @param page the new page
         * @return a new ClickAction
         */
        static ClickAction changePage(int page) {
            return new SimpleClickAction(ClickEvent.Action.CHANGE_PAGE, Integer.toString(page));
        }

        @Getter
        @Accessors(fluent = true)
        @RequiredArgsConstructor
        class SimpleClickAction implements ClickAction {
            private final ClickEvent.Action action;
            private final String value;
        }
    }

    /**
     * A class representing the actions a client can do when a component is hovered
     */
    public interface HoverAction {
        /**
         * Get the Chat-Component action
         * @return the Chat-Component action
         */
        HoverEvent.Action action();
        /**
         * The value paired to the action
         * @return the value paired tot the action
         */
        BaseComponent[] value();


        /**
         * Creates a show_text action: when the component is hovered the text used as parameter will be displayed
         * @param text the text to display
         * @return a new HoverAction instance
         */
        static HoverAction showText(BaseComponent... text) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_TEXT, text);
        }

        /**
         * Creates a show_text action: when the component is hovered the text used as parameter will be displayed
         * @param text the text to display
         * @return a new HoverAction instance
         */
        static HoverAction showText(String text) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_TEXT, new TextComponent(text));
        }

        /**
         * Creates a show_item action: when the component is hovered some item information will be displayed
         * @param item a component array representing item to display
         * @return a new HoverAction instance
         */
        static HoverAction showItem(BaseComponent... item) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_ITEM, item);
        }

        /**
         * Creates a show_item action: when the component is hovered some item information will be displayed
         * @param item the item to display
         * @return a new HoverAction instance
         */
        static HoverAction showItem(ItemStack item) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_ITEM, NmsBookHelper.itemToComponents(item));
        }

        /**
         * Creates a show_entity action: when the component is hovered some entity information will be displayed
         * @param entity a component array representing the item to display
         * @return a new HoverAction instance
         */
        static HoverAction showEntity(BaseComponent... entity) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_ENTITY, entity);
        }

        /**
         * Creates a show_entity action: when the component is hovered some entity information will be displayed
         * @param uuid the entity's UniqueId
         * @param type the entity's type
         * @param name the entity's name
         * @return a new HoverAction instance
         */
        static HoverAction showEntity(UUID uuid, String type, String name) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_ENTITY,
                    NmsBookHelper.jsonToComponents(
                            "{id:\"" + uuid + "\",type:\"" + type + "\"name:\"" + name + "\"}"
                    )
            );
        }

        /**
         * Creates a show_entity action: when the component is hovered some entity information will be displayed
         * @param entity the item to display
         * @return a new HoverAction instance
         */
        static HoverAction showEntity(Entity entity) {
            return showEntity(entity.getUniqueId(), entity.getType().getName(), entity.getName());
        }

        /**
         * Creates a show_achievement action: when the component is hovered the achievement information will be displayed
         * @param achievementId the id of the achievement to display
         * @return a new HoverAction instance
         */
        static HoverAction showAchievement(String achievementId) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_ACHIEVEMENT, new TextComponent("achievement." + achievementId));
        }

        /**
         * Creates a show_achievement action: when the component is hovered the achievement information will be displayed
         * @param achievement the achievement to display
         * @return a new HoverAction instance
         */
        static HoverAction showAchievement(Achievement achievement) {
            return showAchievement(AchievementUtil.toId(achievement));
        }

        /**
         * Creates a show_achievement action: when the component is hovered the statistic information will be displayed
         * @param statisticId the id of the statistic to display
         * @return a new HoverAction instance
         */
        static HoverAction showStatistic(String statisticId) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_ACHIEVEMENT, new TextComponent("statistic." + statisticId));
        }

        @Getter
        @Accessors(fluent = true)
        class SimpleHoverAction implements HoverAction {
            private final HoverEvent.Action action;
            private final BaseComponent[] value;

            public SimpleHoverAction(HoverEvent.Action action, BaseComponent... value) {
                this.action = action;
                this.value = value;
            }
        }
    }

}
