package nz.co.lazycoder.personalbacklog.io;

import android.os.AsyncTask;

/**
 * Queues save operations and executes them on a separate thread.
 *
 * Created by ktchernov on 21/09/2014.
 */
public class AsyncSaveQueuer implements  SaveQueuer {

    private final StringSaver saver;

    public AsyncSaveQueuer(StringSaver saver) {
        this.saver = saver;
    }

    /** Trigger a save operation, will be done on a separate thread */
    public void queueSave(String serialized, SaveListener listener) {
        SaveTask task = new SaveTask(listener);
        task.execute(serialized);
    }


    /** Takes a string JSON as a parameter, and writes it to disk */
    private class SaveTask extends AsyncTask<String, Void, Boolean> {

        private final SaveListener listener;

        public SaveTask(SaveListener listener) {
            this.listener = listener;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            return saver.saveString(params[0]);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (listener != null)
                listener.onSaveComplete(success);
        }
    }

}
