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
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.*;

public final class BookUtil {
    public static void openPlayer(Player p, ItemStack book) {
        ItemStack hand = p.getItemInHand();

        p.setItemInHand(book);

        //Opening the GUI
        NmsBookHelper.openBook(p, book, false);

        //Returning whatever was on hand.
        p.setItemInHand(hand);
    }

    public static BookBuilder writtenBook() {
        return new BookBuilder(new ItemStack(Material.WRITTEN_BOOK));
    }


    public static class BookBuilder {
        private final BookMeta meta;
        private final ItemStack book;

        public BookBuilder(ItemStack book) {
            this.book = book;
            this.meta = (BookMeta)book.getItemMeta();
        }

        public BookBuilder title(String title) {
            meta.setTitle(title);
            return this;
        }

        public BookBuilder author(String author) {
            meta.setAuthor(author);
            return this;
        }

        public BookBuilder pagesJson(String... jsonPages) {
            meta.setPages(jsonPages);
            return this;
        }

        public BookBuilder pagesJson(List<String> jsonPages) {
            meta.setPages(jsonPages);
            return this;
        }

        public BookBuilder pages(BaseComponent[]... pages) {
            NmsBookHelper.setPages(meta, pages);
            return this;
        }

        public BookBuilder pages(List<BaseComponent[]> pages) {
            NmsBookHelper.setPages(meta, pages.toArray(new BaseComponent[0][]));
            return this;
        }

        /**
         * Only works from MC 1.10
         * @param generation the Book generation
         * @return the BookBuilder instance
         */
        public BookBuilder generation(BookMeta.Generation generation) {
            meta.setGeneration(generation);
            return this;
        }

        public ItemStack build() {
            book.setItemMeta(meta);
            return book;
        }
    }

    public static class PageBuilder {
        private List<BaseComponent> text = new ArrayList<>();

        public PageBuilder add(String text) {
            this.text.add(TextBuilder.of(text).build());
            return this;
        }

        public PageBuilder add(BaseComponent component) {
            this.text.add(component);
            return this;
        }

        public PageBuilder add(BaseComponent... component) {
            this.text.addAll(Arrays.asList(component));
            return this;
        }

        public PageBuilder add(Collection<BaseComponent> component) {
            this.text.addAll(component);
            return this;
        }

        public PageBuilder newLine() {
            this.text.add(new TextComponent("\n"));
            return this;
        }

        public BaseComponent[] build() {
            return text.toArray(new BaseComponent[0]);
        }

        public static PageBuilder of(String text) {
            return new PageBuilder().add(text);
        }

        public static PageBuilder of(BaseComponent text) {
            return new PageBuilder().add(text);
        }

        public static PageBuilder of(BaseComponent... text) {
            PageBuilder res = new PageBuilder();
            for(BaseComponent b : text)
                res.add(b);
            return res;
        }
    }

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

        public TextBuilder color(ChatColor color) {
            if(color != null && !color.isColor())
                throw new IllegalArgumentException("Argument isn't a color!");
            this.color = color;
            return this;
        }

        public TextBuilder style(ChatColor... style) {
            for(ChatColor c : style)
                if(!c.isFormat())
                    throw new IllegalArgumentException("Argument isn't a style!");
            this.style = style;
            return this;
        }

        public BaseComponent build() {
            TextComponent res = new TextComponent(text);
            if(onClick != null)
                res.setClickEvent(new ClickEvent(onClick.action(), onClick.value()));
            if(onHover != null)
                res.setHoverEvent(new HoverEvent(onHover.action(), onHover.value()));
            if(color != null)
                res.setColor(color.asBungee());
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

        public static TextBuilder of(String text) {
            return new TextBuilder().text(text);
        }
    }

    public interface ClickAction {
        ClickEvent.Action action();
        String value();

        static ClickAction runCommand(String command) {
            return new SimpleClickAction(ClickEvent.Action.RUN_COMMAND, command);
        }

        /**
         * NOT WORKING (client issue)
         * @param command
         * @return
         */
        static ClickAction suggestCommand(String command) {
            return new SimpleClickAction(ClickEvent.Action.SUGGEST_COMMAND, command);
        }

        static ClickAction openUrl(String url) {
            if(url.startsWith("http://") || url.startsWith("https://"))
                return new SimpleClickAction(ClickEvent.Action.OPEN_URL, url);
            else
                throw new IllegalArgumentException("Invalid url: \"" + url + "\", it should start with http:// or https://");
        }

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

    public interface HoverAction {
        HoverEvent.Action action();
        BaseComponent[] value();

        static HoverAction showText(BaseComponent... text) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_TEXT, text);
        }

        static HoverAction showText(String text) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_TEXT, new TextComponent(text));
        }

        static HoverAction showItem(BaseComponent... item) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_ITEM, item);
        }

        static HoverAction showItem(ItemStack item) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_ITEM, NmsBookHelper.itemToComponents(item));
        }

        static HoverAction showEntity(BaseComponent... entity) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_ENTITY, entity);
        }

        static HoverAction showEntity(UUID uuid, String type, String name) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_ENTITY,
                    NmsBookHelper.jsonToComponents(
                            "{id:\"" + uuid + "\",type:\"" + type + "\"name:\"" + name + "\"}"
                    )
            );
        }

        static HoverAction showEntity(Entity entity) {
            return showEntity(entity.getUniqueId(), entity.getType().getName(), entity.getName());
        }


        static HoverAction showAchievement(String achievementId) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_ACHIEVEMENT, new TextComponent("achievement." + achievementId));
        }

        static HoverAction showAchievement(Achievement achievement) {
            return showAchievement(AchievementUtil.toId(achievement));
        }

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
