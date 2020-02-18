package org.slowcoders.pal;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.slowcoders.io.util.NPAsyncScheduler;
import org.slowcoders.serialize.android.BaseApplication;
import org.slowcoders.util.Debug;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class PALConfig implements PAL.Impl {

    private static final Storage storage = new Storage(BaseApplication.getContext());
    private static final AsyncExecutor executor = new AsyncExecutor();

    @Override
    public Storage getStorage() {
        return storage;
    }

    @Override
    public NPAsyncScheduler.Executor getAsyncExecutor() {
        return new AsyncExecutor();
    }

    @Override
    public boolean isDebugVerbose() {
        return false;
    }

    @Override
    public boolean isDebugMode() {
        return false;
    }

    public static class AsyncExecutor extends Handler implements NPAsyncScheduler.Executor {

        private static int EXECUTE_TASK = 1;

        private AsyncExecutor() {
            super(Looper.getMainLooper());
        }

        @Override
        public void triggerAsync() {
            if (!this.hasMessages(EXECUTE_TASK)) {
                this.sendEmptyMessage(EXECUTE_TASK);
            }
        }

        public void handleMessage(Message msg) {
            if (msg.what != EXECUTE_TASK) {
                return;
            }
            try {
                NPAsyncScheduler.executePendingTasks();
            } catch (Exception e) {
                Debug.ignoreException(e);
            }
        }

        @Override
        public boolean isInMainThread() {
            return (Looper.myLooper() == Looper.getMainLooper());
        }
    }

    public static class Storage implements org.slowcoders.pal.io.Storage {

        private Context context;

        private Storage(Context context) {
            this.context = context;
        }

        @Override
        public InputStream openInputStream(URI contentUri) throws IOException {
            ContentResolver cr = context.getContentResolver();
            return cr.openInputStream(Uri.parse(contentUri.toString()));
        }

        @Override
        public File getPreferenceDirectory() {
            return context.getExternalCacheDir();
        }

        @Override
        public String getDatabaseDirectory() {
            return context.getExternalCacheDir().toString() + "/database";
        }

        @Override
        public File getDownloadDirectory() {
            return context.getExternalCacheDir();
        }

        @Override
        public File getCacheDirectory() {
            return context.getExternalCacheDir();
        }
    }
}
