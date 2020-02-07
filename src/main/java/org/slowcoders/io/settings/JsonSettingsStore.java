package org.slowcoders.io.settings;

import org.slowcoders.io.serialize.IOAdapter;
import org.slowcoders.io.serialize.JSONReader;
import org.slowcoders.io.serialize.JSONWriter;
import org.slowcoders.pal.PAL;
import org.slowcoders.util.Debug;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class JsonSettingsStore implements SettingStorage {

    public static final JsonSettingsStore instance = new JsonSettingsStore();
    public static final Logger log = LoggerFactory.getLogger("SettingStore");

    private final File root;

    private JsonSettingsStore() {
        root = PAL.getStorage().getPreferenceDirectory();
    }

    public final File getTopDirectory() {
        return this.root;
    }

    @Override
    public void load(SettingProperties settings, String storagePath, boolean notifyPropertyChange) {
        File f = new File(root, storagePath + ".json");
        try {
            if (f.exists()) {
                Reader reader = new InputStreamReader(new FileInputStream(f));
                JSONReader in = new JSONReader(IOAdapter.getLoader(true), reader);
                while (!in.isClosed()) {
                    String name = in.readKey();
                    ObservableOption p = settings.findRawOption(name);
                    if (p != null) {
                        Object v = p.getSerializeInfo().getAdapter().read(in);
                        if (p.updateUnsafe(v) && notifyPropertyChange) {
                            p.notifyChanged();
                        }
                    } else {
                        Object value = in.readAny();
                        System.err.println("Skip property " + name + ": " + value);
                    }
                }
            } else {
                saveTo(settings, f);
            }
        }
        catch (Exception e) {
            log.error("Loading Fail: " + storagePath, e);
            try {
                saveTo(settings, f);
            }
            catch (Exception e2) {
                Debug.wtf(e2);
            }
        }
    }

    @Override
    public void save(SettingProperties settings, String storagePath) {
        try {
            File f = new File(root, storagePath + ".json");
            saveTo(settings, f);
        }
        catch (Exception e) {
            Debug.fatal(e);
        }
    }

    final void saveTo(SettingProperties settings, File f) throws Exception {
        StringBuilder builder = new StringBuilder();
        JSONWriter writer = new JSONWriter(null, settings.getClass().getName(), true, builder);
        for (Object p2 : settings.options()) {
            ObservableOption p = (ObservableOption) p2;
            String name = p.getSerializeInfo().getName();
            Object value = p.get();
            writer.writeString(name);
            Debug.Assert(p.getSerializeInfo().getAdapter() != null);
            try {
                p.getSerializeInfo().getAdapter().write(value, writer);
            } catch (Exception e) {
                p.getSerializeInfo().getAdapter().write(value, writer);
            }
        }
        writer.close();

        f.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(f);
        OutputStreamWriter sw = new OutputStreamWriter(out);
        sw.append(writer.toString());
        sw.flush();
        sw.close();
    }

    public String[] listSettings(String storagePath) {
        File f = new File(root, storagePath);
        if (!f.isDirectory()) return null;
        return f.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".json");
            }
        });
    }

    @Override
    public void delete(String storagePath) {
        try {
            File f = new File(root, storagePath + ".json");
            if (f.exists()) {
                f.delete();
            }
            File dir = new File(root, storagePath);
            if (dir.exists() && dir.isDirectory()) {
                deleteDir(dir);
            }
        }
        catch (Exception e) {
            Debug.fatal(e);
        }
    }

    private void deleteDir(File dir) {
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                this.deleteDir(f);
            }
            else {
                f.delete();
            }
        }
        dir.delete();
    }
}
