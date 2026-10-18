package com.winlator.core;

import android.content.Context;
import android.os.Environment;

import com.winlator.container.Container;
import com.winlator.win32.MSLink;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public abstract class WineStartMenuCreator {
    public static File findDownloadedVesktop(File downloadsDir) {
        if (downloadsDir == null || !downloadsDir.isDirectory()) return null;
        File direct = new File(downloadsDir, "vesktop.exe");
        if (direct.isFile()) return direct;
        File[] directories = downloadsDir.listFiles(file ->
            file.isDirectory() && file.getName().toLowerCase().startsWith("vesktop"));
        if (directories == null) return null;
        File selected = null;
        for (File directory : directories) {
            File executable = new File(directory, "vesktop.exe");
            if (executable.isFile() && (selected == null || executable.lastModified() > selected.lastModified()))
                selected = executable;
        }
        return selected;
    }

    static String toWindowsPath(Container container, File file) {
        return toWindowsPath(container.getDrives(), file);
    }

    static String toWindowsPath(String drives, File file) {
        String absolutePath = file.getAbsolutePath();
        String selectedLetter = null;
        int selectedLength = -1;
        int separator = drives.indexOf(':');
        while (separator > 0) {
            String driveLetter = String.valueOf(drives.charAt(separator - 1));
            int nextSeparator = drives.indexOf(':', separator + 1);
            String configuredPath = drives.substring(separator + 1,
                nextSeparator != -1 ? nextSeparator - 1 : drives.length());
            String drivePath = new File(configuredPath).getAbsolutePath();
            if ((absolutePath.equals(drivePath) || absolutePath.startsWith(drivePath + File.separator)) &&
                drivePath.length() > selectedLength) {
                selectedLetter = driveLetter;
                selectedLength = drivePath.length();
            }
            separator = nextSeparator;
        }
        if (selectedLetter != null) {
            String relativePath = absolutePath.substring(selectedLength).replace(File.separatorChar, '/');
            return selectedLetter + ":" + relativePath;
        }
        return "Z:" + absolutePath.replace(File.separatorChar, '/');
    }

    private static void addDownloadedVesktop(JSONArray data, Container container) throws JSONException {
        File executable = findDownloadedVesktop(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
        if (executable == null) return;
        JSONObject entry = new JSONObject();
        entry.put("name", "Vesktop (Downloads)");
        entry.put("path", toWindowsPath(container, executable));
        entry.put("cmdArgs", "--no-sandbox --disable-gpu-sandbox");
        data.getJSONObject(0).getJSONArray("children").put(entry);
    }

    public static void syncVesktopCompanion(Container container) {
        File startupDir = new File(container.getRootDir(),
            ".wine/drive_c/users/xuser/AppData/Roaming/Microsoft/Windows/Start Menu/Programs/StartUp");
        File outputFile = new File(startupDir, "Aurora Vesktop RPC Companion.lnk");
        File executable = findDownloadedVesktop(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
        if (!container.getExtra("vesktopRpcCompanion").equals("t") || executable == null) {
            outputFile.delete();
            return;
        }
        startupDir.mkdirs();
        MSLink.LinkInfo linkInfo = new MSLink.LinkInfo();
        linkInfo.targetPath = toWindowsPath(container, executable);
        linkInfo.arguments = "--start-minimized --no-sandbox --disable-gpu-sandbox";
        linkInfo.iconLocation = linkInfo.targetPath;
        MSLink.createFile(linkInfo, outputFile);
    }

    private static int parseShowCommand(String value) {
        if (value.equals("SW_SHOWMAXIMIZED")) {
            return MSLink.SW_SHOWMAXIMIZED;
        }
        else if (value.equals("SW_SHOWMINNOACTIVE")) {
            return MSLink.SW_SHOWMINNOACTIVE;
        }
        else return MSLink.SW_SHOWNORMAL;
    }

    private static void createMenuEntry(JSONObject item, File currentDir) throws JSONException {
        if (item.has("children")) {
            currentDir = new File(currentDir, item.getString("name"));
            currentDir.mkdirs();

            JSONArray children = item.getJSONArray("children");
            for (int i = 0; i < children.length(); i++) createMenuEntry(children.getJSONObject(i), currentDir);
        }
        else {
            File outputFile = new File(currentDir, item.getString("name")+".lnk");
            MSLink.LinkInfo linkInfo = new MSLink.LinkInfo();
            linkInfo.targetPath = item.getString("path");
            linkInfo.arguments = item.optString("cmdArgs");
            linkInfo.iconLocation = item.optString("iconLocation", linkInfo.targetPath);
            linkInfo.iconIndex = item.optInt("iconIndex", 0);
            if (item.has("showCommand")) linkInfo.showCommand = parseShowCommand(item.getString("showCommand"));
            MSLink.createFile(linkInfo, outputFile);
        }
    }

    private static void removeMenuEntry(JSONObject item, File currentDir) throws JSONException {
        if (item.has("children")) {
            currentDir = new File(currentDir, item.getString("name"));

            JSONArray children = item.getJSONArray("children");
            for (int i = 0; i < children.length(); i++) removeMenuEntry(children.getJSONObject(i), currentDir);

            if (FileUtils.isEmpty(currentDir)) currentDir.delete();
        }
        else (new File(currentDir, item.getString("name")+".lnk")).delete();
    }

    private static void removeOldMenu(File containerStartMenuFile, File startMenuDir) {
        if (!containerStartMenuFile.isFile()) return;
        try {
            JSONArray data = new JSONArray(FileUtils.readString(containerStartMenuFile));
            for (int i = 0; i < data.length(); i++) removeMenuEntry(data.getJSONObject(i), startMenuDir);
        }
        catch (JSONException e) {
            containerStartMenuFile.delete();
        }
    }

    public static void create(Context context, Container container) {
        File startMenuDir = container.getStartMenuDir();
        File containerStartMenuFile = new File(container.getRootDir(), ".startmenu");
        removeOldMenu(containerStartMenuFile, startMenuDir);
        syncVesktopCompanion(container);

        try {
            JSONArray data = new JSONArray(FileUtils.readString(context, "wine_startmenu.json"));
            addDownloadedVesktop(data, container);
            FileUtils.writeString(containerStartMenuFile, data.toString());
            for (int i = 0; i < data.length(); i++) createMenuEntry(data.getJSONObject(i), startMenuDir);
        }
        catch (JSONException e) {}
    }
}
