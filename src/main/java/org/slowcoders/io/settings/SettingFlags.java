package org.slowcoders.io.settings;

public interface SettingFlags {
    int NotNull     = 1 << 0;
    int NotEmpty    = 1 << 1;
    int NotExport   = 1 << 2;
    int NotAutoSave = 1 << 3;
}
