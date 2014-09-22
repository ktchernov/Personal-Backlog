package nz.co.lazycoder.personalbacklog.io;

/**
 * Abstraction to save a string to somewhere (e.g. file on disk).
 *
 * Created by ktchernov on 21/09/2014.
 */
public interface StringSaver {
    public boolean saveString(final String string);
}
