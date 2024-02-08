package com.zacharee1.systemuituner;

import com.zacharee1.systemuituner.data.SavedOption;

interface IShizukuOperationsService {
    boolean writeGlobal(String key, String value, String pkg) = 1;
    boolean writeSecure(String key, String value, String pkg) = 2;
    boolean writeSystem(String key, String value, String pkg) = 3;

    String readGlobal(String key) = 4;
    String readSecure(String key) = 5;
    String readSystem(String key) = 6;

    SavedOption[] listSettings() = 7;

    void destroy() = 16777114;
}