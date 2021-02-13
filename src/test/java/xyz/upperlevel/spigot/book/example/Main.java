package xyz.upperlevel.spigot.book.example;

import net.md_5.bungee.api.chat.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.spigot.book.BookUtil;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("booktest").setExecutor(new BookTestCommand());
    }

    public class BookTestCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            if (sender instanceof Player) {
                ItemStack book = createGeneralBook((Player) sender, args.length < 1 ? "" : args[0]);

                if (book != null) {
                    BookUtil.openPlayer((Player) sender, book);
                } else {
                    sender.sendMessage("Subcommands: empty, color, command, link, game, general or none");
                }
            }

            return true;
        }

        private ItemStack createGeneralBook(Player p, String type) {
            switch (type.toLowerCase()) {
                case "":
                    return createPresentationBook(p);
                case "empty":
                    return createEmptyBook(p);
                case "color":
                    return createColorBook(p);
                case "command":
                    return createCommandBook(p);
                case "link":
                    return createLinkBook(p);
                case "game":
                    return createGameBook(p);
                case "general":
                    return createGeneralBook(p);
                default:
                    return null;
            }
        }

        private ItemStack createEmptyBook(Player p) {
            return BookUtil.writtenBook().build();
        }

        private ItemStack createColorBook(Player p) {
            return BookUtil.writtenBook()
                    .author("SnowyCoder")
                    .title(ChatColor.RED + "C" + ChatColor.BLUE + "o" + ChatColor.AQUA + "l" + ChatColor.GREEN + "o" + ChatColor.GOLD + "r")
                    .pages(
                            new BookUtil.PageBuilder()
                                    .add("None = Black")

                                    .newLine()//.newLine = .add("\n")
                                    .add(
                                            BookUtil.TextBuilder.of("Single")
                                                .color(ChatColor.BLACK)
                                                .build()
                                    )
                                    .add(
                                            BookUtil.TextBuilder.of(" Line")
                                                    .color(ChatColor.RED)
                                                    .build()
                                    )
                                    .add(
                                            BookUtil.TextBuilder.of(" Multiple")
                                                    .color(ChatColor.GREEN)
                                                    .build()
                                    )
                                    .add(
                                            BookUtil.TextBuilder.of(" Colors")
                                                    .color(ChatColor.BLUE)
                                                    .build()
                                    )

                                    .newLine()
                                    .add(
                                            BookUtil.TextBuilder.of("Null color")
                                                    .color(ChatColor.GREEN)
                                                    .build()
                                    )
                                    .add(
                                            BookUtil.TextBuilder.of(" = Carry")
                                                    .color(null)
                                                    .build()
                                    )
                                    .add(
                                            BookUtil.TextBuilder.of(" on")
                                                    .color(null)
                                                    .build()
                                    )

                                    .newLine()
                                    .add(
                                            BookUtil.TextBuilder.of("Null color at the beginning")
                                                    .color(null)
                                                    .build()
                                    )
                                    .add(
                                            BookUtil.TextBuilder.of(" = Black")
                                                    .color(ChatColor.DARK_AQUA)
                                                    .build()
                                    )
                            .build()
                    )
                    .build();
        }

        private ItemStack getHorseItem() {
            ItemStack item = new ItemStack(Material.MONSTER_EGG);
            SpawnEggMeta meta = (SpawnEggMeta) item.getItemMeta();
            meta.setSpawnedType(EntityType.HORSE);
            item.setItemMeta(meta);
            return item;
        }

        private ItemStack createCommandBook(Player p) {
            return BookUtil.writtenBook()
                    .author("SnowyCoder")
                    .title("Command test")
                    .pages(
                            new BookUtil.PageBuilder()
                                    .add(
                                            BookUtil.TextBuilder.of("Suicide")
                                                    .onHover(BookUtil.HoverAction.showText("Wanna try?"))
                                                    .onClick(BookUtil.ClickAction.runCommand("/kill"))
                                                    .color(ChatColor.RED)
                                                    .build()
                                    )
                                    .newLine()
                                    .add(
                                            BookUtil.TextBuilder.of("Auto Kick")
                                                    .onHover(BookUtil.HoverAction.showText("Useful!"))
                                                    .onClick(BookUtil.ClickAction.runCommand("/kick " + p.getName()))
                                                    .color(ChatColor.YELLOW)
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();
        }

        private ItemStack createLinkBook(Player p) {
            return BookUtil.writtenBook()
                    .author("SnowyCoder")
                    .title("Link")
                    .pages(
                            new BookUtil.PageBuilder()
                                    .add(
                                            BookUtil.TextBuilder.of("Youtube")
                                                    .color(ChatColor.RED)
                                                    .onClick(BookUtil.ClickAction.openUrl("https://www.youtube.com"))
                                                    .onHover(BookUtil.HoverAction.showText(ChatColor.RED + "Open Youtube!"))
                                                    .build()
                                    )
                                    .newLine()
                                    .add(
                                            BookUtil.TextBuilder.of("Spigot")
                                                    .color(ChatColor.GOLD)
                                                    .onClick(BookUtil.ClickAction.openUrl("https://www.spigotmc.org"))
                                                    .onHover(BookUtil.HoverAction.showText(ChatColor.AQUA + "Open Spigot!"))
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();
        }

        private ItemStack createGameBook(Player p) {
            return BookUtil.writtenBook()
                    .author("SnowyCoder")
                    .title("Game Book")
                    .pages(
                            new BookUtil.PageBuilder()
                                    .add(
                                            BookUtil.TextBuilder.of("Next page")
                                                    .onClick(BookUtil.ClickAction.changePage(2))
                                                    .build()
                                    )
                                    .build(),
                            new BookUtil.PageBuilder()
                                    .add(
                                            BookUtil.TextBuilder.of("2nd")
                                                    .onClick(BookUtil.ClickAction.changePage(3))
                                                    .build()
                                    )
                                    .build(),
                            new BookUtil.PageBuilder()
                                    .add(
                                            BookUtil.TextBuilder.of("3nd")
                                                    .onClick(BookUtil.ClickAction.changePage(1))
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();
        }

        private ItemStack createGeneralBook(Player p) {
            return BookUtil.writtenBook()
                    .author("SnowyCoder")
                    .title("The Test-ament")
                    .pages(
                            new BaseComponent[]{
                                    new TextComponent("Introduction page")
                            },
                            new BookUtil.PageBuilder()
                                    .add(new TextComponent("visit "))
                                    .add(
                                            BookUtil.TextBuilder.of("Spigot")
                                                    .color(ChatColor.GOLD)
                                                    .style(ChatColor.BOLD, ChatColor.ITALIC)
                                                    .onClick(BookUtil.ClickAction.openUrl("https://www.spigotmc.org"))
                                                    .onHover(BookUtil.HoverAction.showText("Open spigot!"))
                                                    .build()
                                    )
                                    .add(" or ")
                                    .add(
                                            new ComponentBuilder("Bukkit")
                                                    .color(net.md_5.bungee.api.ChatColor.BLUE)
                                                    .bold(true)
                                                    .italic(true)
                                                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://bukkit.org"))
                                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("Open bukkit!")}))
                                                    .create()
                                    )
                                    .newLine()
                                    .add("I think that the ")
                                    .add(
                                            BookUtil.TextBuilder.of("TextBuilder")
                                                    .color(ChatColor.AQUA)
                                                    .style(ChatColor.BOLD)
                                                    .onClick(BookUtil.ClickAction.changePage(3))
                                                    .onHover(BookUtil.HoverAction.showText("TextBuilder's page"))
                                                    .build()
                                    )
                                    .add(" is really useful to ")
                                    .add(
                                            BookUtil.TextBuilder.of("you")
                                                    .color(ChatColor.AQUA)
                                                    .style(ChatColor.BOLD)
                                                    .onClick(BookUtil.ClickAction.runCommand("/kill"))//lol
                                                    .onHover(BookUtil.HoverAction.showText("Simulate suicide"))
                                                    .build()
                                    )
                                    .build(),
                            new BookUtil.PageBuilder()
                                    .add("TextBuilder's page")
                                    .newLine().newLine()
                                    .add("Isn't this amazing?")
                                    .build()
                    )
                    .build();
        }

        private ItemStack createPresentationBook(Player p) {
            return BookUtil.writtenBook()
                    .author("SnowyCoder")
                    .title("The Test-ament")
                    .pages(
                            new BookUtil.PageBuilder()
                                    .add(
                                            BookUtil.TextBuilder.of("     Introduction") //5 spaces = center align
                                                    .color(ChatColor.AQUA)
                                                    .style(ChatColor.BOLD, ChatColor.ITALIC)
                                                    .onHover(BookUtil.HoverAction.showText("This is the BookApi introduction"))
                                                    .build()
                                    )
                                    .newLine()
                                    .newLine()
                                    .add(
                                            BookUtil.TextBuilder.of("Hello")
                                                    .color(ChatColor.RED)
                                                    .build()

                                    )
                                    .add(
                                            BookUtil.TextBuilder.of(" There")
                                                    .color(ChatColor.BLUE)
                                                    .build()
                                    )
                                    .newLine()
                                    .add("This ")
                                    .add(
                                            BookUtil.TextBuilder.of("API")
                                                    .color(ChatColor.BLUE)
                                                    .style(ChatColor.BOLD)
                                                    .onClick(BookUtil.ClickAction.openUrl("https://github.com/upperlevel/spigot-book-api"))
                                                    .onHover(BookUtil.HoverAction.showText("Go to BookApi's site!"))
                                                    .build()
                                    )
                                    .add(" supports ")
                                    .add(
                                            BookUtil.TextBuilder.of("links")
                                                    .color(ChatColor.GOLD)
                                                    .style(ChatColor.ITALIC)
                                                    .onClick(BookUtil.ClickAction.openUrl("https://www.spigotmc.org"))
                                                    .onHover(BookUtil.HoverAction.showText("Open spigot!"))
                                                    .build()
                                    )
                                    .add(", ")
                                    .add(
                                            BookUtil.TextBuilder.of("page links")
                                                    .color(ChatColor.RED)
                                                    .style(ChatColor.ITALIC)
                                                    .onClick(BookUtil.ClickAction.changePage(2))
                                                    .onHover(BookUtil.HoverAction.showText("Go to the second page"))
                                                    .build()
                                    )
                                    .add(", ")
                                    .add(
                                            BookUtil.TextBuilder.of("commands")
                                                    .color(ChatColor.BLUE)
                                                    .style(ChatColor.ITALIC)
                                                    .onClick(BookUtil.ClickAction.runCommand("/kick " + p.getName()))
                                                    .onHover(BookUtil.HoverAction.showText("Go out of the server"))
                                                    .build()
                                    )
                                    .add(" and everything that the MC protocol offers!")
                                    .newLine()
                                    .add("I think that ")
                                    .add(
                                            BookUtil.TextBuilder.of("YOU")
                                                    .color(ChatColor.GOLD)
                                                    .style(ChatColor.BOLD, ChatColor.UNDERLINE, ChatColor.ITALIC)
                                                    .onClick(BookUtil.ClickAction.openUrl("https://www.google.it/search?q=you're+awesome"))
                                                    .onHover(BookUtil.HoverAction.showText(ChatColor.GOLD + "You're awesome!"))
                                                    .build()
                                    )
                                    .add(" should use ")
                                    .add(
                                            BookUtil.TextBuilder.of("this API")
                                                    .color(ChatColor.AQUA)
                                                    .style(ChatColor.BOLD)
                                                    .onClick(BookUtil.ClickAction.openUrl("https://www.spigotmc.org/resources/commandapi.41052"))
                                                    .onHover(BookUtil.HoverAction.showText("Go in the main page"))
                                                    .build()
                                    )
                                    .build(),
                            new BookUtil.PageBuilder()
                                    .add(
                                            BookUtil.TextBuilder.of("Hey")
                                                    .color(ChatColor.RED)
                                                    .build()
                                    )
                                    .newLine()
                                    .add("This book has a second page!")
                                    .newLine()
                                    .add(
                                            BookUtil.TextBuilder.of("Go back")
                                                    .color(ChatColor.BLUE)
                                                    .style(ChatColor.ITALIC)
                                                    .onHover(BookUtil.HoverAction.showText("Return to the main page"))
                                                    .onClick(BookUtil.ClickAction.changePage(1))
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();
        }
    }
}
