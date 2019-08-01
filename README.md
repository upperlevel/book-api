# spigot-book-api
This project contains more APIs that help the developer in using the books.
Follows a brief documentation on how to use it.

To include the API in your project put this into our pom.xml (maven)
```xml
<dependencies>
  ...
  <dependency>
    <groupId>xyz.upperlevel.spigot.book</groupId>
    <artifactId>spigot-book-api</artifactId>
    <version>1.3</version>
  </dependency>
</dependencies>
```

After that you'll be able to use the class BookUtil for Book printing/interaction, the full guide is posted in the [wiki](../../wiki).
Some usage examples:

```java
ItemStack book = BookUtil.writtenBook()
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
                    .onHover(BookUtil.HoverAction.showText("Kill yasself"))
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

BookUtil.openPlayer(player, book);
```

