package xyz.upperlevel.spigot.book;


import lombok.Getter;
import xyz.upperlevel.spigot.book.internals.NmsUtil;

/**
 * An error thrown when this NMS-helper class doesn't support the running MC
 * version
 */
public class UnsupportedVersionException extends RuntimeException {
    /**
     * The current running version
     */
    @Getter
    private final String version = NmsUtil.version;

    public UnsupportedVersionException(Exception e) {
        super("Error while executing reflections, submit to developers the following log (version: "
                + NmsUtil.version + ")", e);
    }
}