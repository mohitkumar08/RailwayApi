package com.example.mylibrary;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by bit on 13/10/17.
 */

public class YumSharedPref {
    private static final int MODE_PRIVATE = 0;
    private static YumSharedPref pref;
    private static SharedPreferences appSharedPrefs;
    private static SharedPreferences.Editor prefsEditor;
    private static String PREF_FILE = "MY_FILE.XML";
    private static Encryption encryption;
    private static boolean isEncryptionException = false;
    private static final char[] key = {'m', 'o', 'h', 'i', 't'};
    private static final char[] salt = {'k', 'u', 'm', 'a', 'r'};

    private static final byte[] iv = new byte[16];

    YumSharedPref(Context context) {
        appSharedPrefs = context.getSharedPreferences(PREF_FILE, MODE_PRIVATE);
        prefsEditor = appSharedPrefs.edit();
        try {
            encryption = Encryption.getDefault(String.valueOf(key), String.valueOf(salt), iv);
        } catch (Exception e) {
            isEncryptionException = true;
            e.printStackTrace();
        }
    }

    public static YumSharedPref initialize(Context context) {
        if (pref == null) {
            pref = new YumSharedPref(context);
        }
        return pref;
    }

    public static String getStringValue(String keyName) {
        if (appSharedPrefs.contains(keyName)) {
            if (isEncryptionException) {
                return appSharedPrefs.getString(keyName, null);
            } else {
                return encryption.decryptOrNull(appSharedPrefs.getString(keyName, null));
            }
        }
        return null;

    }

    public static void setStringValue(String keyName, String value) {
        if (isEncryptionException) {
            prefsEditor.putString(keyName, value);
        } else {
            prefsEditor.putString(keyName, encryption.encryptOrNull(value));
        }
        prefsEditor.commit();
    }

    public static String geValue(String keyName) {
        if (appSharedPrefs.contains(keyName)) {
            return appSharedPrefs.getString(keyName, null);
        }
        return "";

    }

}
