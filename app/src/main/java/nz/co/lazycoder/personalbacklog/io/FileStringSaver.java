package nz.co.lazycoder.personalbacklog.io;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Saves strings to a specified file synchronously.
 *
 * Created by ktchernov on 21/09/2014.
 */
public class FileStringSaver implements StringSaver {
    private static final String TAG = FileStringSaver.class.getSimpleName();

    private final File fileToSaveTo;

    public FileStringSaver(File fileToSaveTo) {
        this.fileToSaveTo = fileToSaveTo;
    }

    @Override
    public boolean saveString(String string) {
        try {
            Log.v(TAG, "Saved:\n" + string);
            fileToSaveTo.getParentFile().mkdirs();
            FileWriter fileWriter = new FileWriter(fileToSaveTo);
            fileWriter.write(string);
            fileWriter.flush();
        }
        catch(IOException ex) {
            Log.e(TAG, "Error saving", ex);
            return false;
        }
        return true;
    }
}
