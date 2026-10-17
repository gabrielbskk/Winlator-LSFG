package com.winlator.core;

import androidx.annotation.NonNull;

import java.util.Iterator;

public class KeyValueSet implements Iterable<String[]> {
    private String data = "";

    public KeyValueSet() {}

    public KeyValueSet(Object data) {
        this(data != null ? data.toString() : null);
    }

    public KeyValueSet(String data) {
        this.data = data != null && !data.isEmpty() ? data : "";
    }

    /**
     * Ignore malformed imported fields rather than crashing when a segment
     * has no '='. Keep the original serialized string intact for rollback.
     */
    private int[] indexOfKey(String key) {
        int start = 0;
        while (start < data.length()) {
            int separator = data.indexOf(',', start);
            int end = separator >= 0 ? separator : data.length();
            int equals = data.indexOf('=', start);
            if (equals > start && equals < end && data.substring(start, equals).equals(key)) {
                return new int[]{start, end};
            }
            start = end + 1;
        }
        return null;
    }

    public String get(String key) {
        return get(key, "");
    }

    public String get(String key, String fallback) {
        if (data.isEmpty()) return fallback;
        for (String[] keyValue : this) if (keyValue[0].equals(key)) return keyValue[1];
        return fallback;
    }

    public float getFloat(String key) {
        return getFloat(key, 0.0f);
    }

    public float getFloat(String key, float fallback) {
        try {
            String value = get(key);
            return !value.isEmpty() ? Float.parseFloat(value) : fallback;
        }
        catch (NumberFormatException e) {
            return fallback;
        }
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int fallback) {
        try {
            String value = get(key);
            return !value.isEmpty() ? Integer.parseInt(value) : fallback;
        }
        catch (NumberFormatException e) {
            return fallback;
        }
    }

    public String getHexString(String key, int fallback) {
        int result;
        try {
            String value = get(key);
            result = !value.isEmpty() ? Integer.parseInt(value) : fallback;
        }
        catch (NumberFormatException e) {
            result = fallback;
        }
        return "0x"+String.format("%08x", result);
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean fallback) {
        String value = get(key);
        return !value.isEmpty() ? value.equals("1") || value.equals("t") || value.equals("true") : fallback;
    }

    public KeyValueSet put(String key, Object value) {
        int[] range = indexOfKey(key);
        if (range != null) {
            data = StringUtils.replace(data, range[0], range[1], key+"="+value);
        }
        else data = (!data.isEmpty() ? data+"," : "")+key+"="+value;
        return this;
    }

    @NonNull
    @Override
    public Iterator<String[]> iterator() {
        return new Iterator<String[]>() {
            private int cursor = 0;
            private int fieldStart = -1;
            private int equals;
            private int fieldEnd;

            {
                advance();
            }

            private void advance() {
                fieldStart = -1;
                while (cursor < data.length()) {
                    int start = cursor;
                    int separator = data.indexOf(',', start);
                    int end = separator >= 0 ? separator : data.length();
                    cursor = end + 1;
                    int equalPosition = data.indexOf('=', start);
                    if (equalPosition > start && equalPosition < end) {
                        fieldStart = start;
                        equals = equalPosition;
                        fieldEnd = end;
                        return;
                    }
                }
            }

            @Override
            public boolean hasNext() {
                return fieldStart >= 0;
            }

            @Override
            public String[] next() {
                if (!hasNext()) throw new java.util.NoSuchElementException();
                String[] entry = new String[]{
                        data.substring(fieldStart, equals),
                        data.substring(equals + 1, fieldEnd)
                };
                advance();
                return entry;
            }
        };
    }

    @NonNull
    @Override
    public String toString() {
        return data;
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
}
