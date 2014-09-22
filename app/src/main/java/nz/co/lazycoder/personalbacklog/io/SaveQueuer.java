package nz.co.lazycoder.personalbacklog.io;

/**
 * Interface to queue a save operation.
 *
 * Created by ktchernov on 22/09/2014.
 */
public interface SaveQueuer {
    public interface SaveListener {
        public void onSaveComplete(boolean success);
    }

    public void queueSave(String serialized, SaveListener listener);
}
