package com.themobileknowledge.uwbconnectapp.logger;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class LoggerHelper {

    private final static String logsfileName = "uwbconnect";
    private final static String logsFileMimeTypeTxt = "text/plain";
    private final static String logsFileMimeTypeCsv = "text/csv";

    // Helper title for logs display
    private final static String logsHeader = "[Time][Demo][Event][DevName][DevMac][Distance][Azimuth][Elevation]\n";

    public enum LogEvent {

        LOG_EVENT_DEMO_START("DEMO_START"),
        LOG_EVENT_DEMO_STOP("DEMO_STOP"),
        LOG_EVENT_DEMO_FINISHED("DEMO_FINISHED"),
        LOG_EVENT_BLE_SCAN_START("BLE_SCAN_START"),
        LOG_EVENT_BLE_SCAN_STOP("BLE_SCAN_STOP"),
        LOG_EVENT_BLE_DEV_SCANNED("BLE_DEV_SCANNED"),
        LOG_EVENT_BLE_DEV_CONNECTING("BLE_DEV_CONNECTING"),
        LOG_EVENT_BLE_DEV_CONNECTED("BLE_DEV_CONNECTED"),
        LOG_EVENT_BLE_DEV_DISCONNECTED("BLE_DEV_DISCONNECTED"),
        LOG_EVENT_UWB_RANGING_START("UWB_RANGING_START"),
        LOG_EVENT_UWB_RANGING_RESULT("UWB_RANGING_RESULT"),
        LOG_EVENT_UWB_RANGING_ERROR("UWB_RANGING_ERROR"),
        LOG_EVENT_UWB_RANGING_PEER_DISCONNECTED("UWB_RANGING_PEER_DISCONNECTED"),
        LOG_EVENT_UWB_RANGING_STOP("UWB_RANGING_STOP");

        private final String event;

        LogEvent(String event) {
            this.event = event;
        }

        @Override
        public String toString() {
            return event;
        }
    }

    private final Context mContext;

    private boolean mLogsEnabled = true;
    private String demoName = "Default";

    public LoggerHelper(Context mContext) {
        this.mContext = mContext;
    }

    public void setDemoName(String demoName) {
        this.demoName = demoName;
    }

    public void setLogsEnabled(boolean isEnabled) {
        mLogsEnabled = isEnabled;
    }

    public void log(String event) {
        logConsole(demoName, event);
        if (mLogsEnabled) {
            logFile(demoName, event);
        }
    }

    public void log(String event, String deviceName, String deviceMac) {
        logConsole(demoName, event, deviceName, deviceMac);
        if (mLogsEnabled) {
            logFile(demoName, event, deviceName, deviceMac);
        }
    }

    public void log(String event, String deviceName, String deviceMac, String distance, String angleAzimuth, String angleElevation) {
        logConsole(demoName, event, deviceName, deviceMac, distance, angleAzimuth, angleElevation);
        if (mLogsEnabled) {
            logFile(demoName, event, deviceName, deviceMac, distance, angleAzimuth, angleElevation);
        }
    }

    public String readLogs() {
        return readFile(0);
    }

    public String readLogs(int maxLines) {
        return readFile(maxLines);
    }

    public void exportLogsTxt() {
        String filename = getTimeExport() + "_" + logsfileName + ".txt";

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, logsFileMimeTypeTxt);
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
        if (uri != null) {
            String textToExport = logsHeader.concat(readLogs());

            try {
                OutputStream outputStream = resolver.openOutputStream(uri);
                outputStream.write(textToExport.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        contentValues.clear();
        contentValues.put(MediaStore.Audio.Media.IS_PENDING, 0);
        resolver.update(uri, contentValues, null, null);
    }

    public void exportLogsCsv() {
        String filename = getTimeExport() + "_" + logsfileName + ".csv";

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, logsFileMimeTypeCsv);
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
        if (uri != null) {
            String textToExport = logsHeader.concat(readLogs());
            String textToExportCsv = convertToCsv(textToExport);

            try {
                OutputStream outputStream = resolver.openOutputStream(uri);
                outputStream.write(textToExportCsv.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        contentValues.clear();
        contentValues.put(MediaStore.Audio.Media.IS_PENDING, 0);
        resolver.update(uri, contentValues, null, null);
    }

    public void clearLogs() {
        mContext.deleteFile(logsfileName);
    }

    private void writeFile(String message) {
        try {
            FileOutputStream fileOutputStream = mContext.openFileOutput(logsfileName, Context.MODE_APPEND);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(message);
            outputStreamWriter.write("\n");
            outputStreamWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readFile(int maxLines) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            int numLines = 0;
            if (maxLines > 0) {
                Scanner scannerNumLines = new Scanner(mContext.openFileInput(logsfileName));
                while (scannerNumLines.hasNextLine()) {
                    scannerNumLines.nextLine();
                    numLines++;
                }

                scannerNumLines.close();
            }

            int index = 0;
            Scanner scanner = new Scanner(mContext.openFileInput(logsfileName));
            while (scanner.hasNextLine()) {
                String nextLine = scanner.nextLine();
                if (maxLines <= 0 ||
                        numLines - index <= maxLines) {
                    stringBuilder.append(nextLine);
                    stringBuilder.append("\n");
                }

                index++;
            }

            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    private void logConsole(String param1) {
        String logMessage = "[" + getTimeLogs() + "]" + "[" + param1 + "]";
        System.out.println(logMessage);
    }

    private void logConsole(String param1, String param2) {
        String logMessage = "[" + getTimeLogs() + "]" + "[" + param1 + "]" + "[" + param2 + "]";
        System.out.println(logMessage);
    }

    private void logConsole(String param1, String param2, String param3) {
        String logMessage = "[" + getTimeLogs() + "]" + "[" + param1 + "]" + "[" + param2 + "][" + param3 + "]";
        System.out.println(logMessage);
    }

    private void logConsole(String param1, String param2, String param3, String param4) {
        String logMessage = "[" + getTimeLogs() + "]" + "[" + param1 + "]" + "[" + param2 + "][" + param3 + "][" + param4 + "]";
        System.out.println(logMessage);
    }

    private void logConsole(String param1, String param2, String param3, String param4, String param5) {
        String logMessage = "[" + getTimeLogs() + "]" + "[" + param1 + "]" + "[" + param2 + "][" + param3 + "][" + param4 + "][" + param5 + "]";
        System.out.println(logMessage);
    }

    private void logConsole(String param1, String param2, String param3, String param4, String param5, String param6) {
        String logMessage = "[" + getTimeLogs() + "]" + "[" + param1 + "]" + "[" + param2 + "][" + param3 + "][" + param4 + "][" + param5 + "][" + param6 + "]";
        System.out.println(logMessage);
    }

    private void logConsole(String param1, String param2, String param3, String param4, String param5, String param6, String param7) {
        String logMessage = "[" + getTimeLogs() + "]" + "[" + param1 + "]" + "[" + param2 + "][" + param3 + "][" + param4 + "][" + param5 + "][" + param6 + "][" + param7 + "]";
        System.out.println(logMessage);
    }

    private void logFile(String param1) {
        String logMessage = "[" + getTimeLogs() + "]" + "[" + param1 + "]";
        writeFile(logMessage);
    }

    private void logFile(String param1, String param2) {
        String logMessage = "[" + getTimeLogs() + "]" + "[" + param1 + "]" + "[" + param2 + "]";
        writeFile(logMessage);
    }

    private void logFile(String param1, String param2, String param3) {
        String logMessage = "[" + getTimeLogs() + "]" + "[" + param1 + "]" + "[" + param2 + "][" + param3 + "]";
        writeFile(logMessage);
    }

    private void logFile(String param1, String param2, String param3, String param4) {
        String logMessage = "[" + getTimeLogs() + "]" + "[" + param1 + "]" + "[" + param2 + "][" + param3 + "][" + param4 + "]";
        writeFile(logMessage);
    }

    private void logFile(String param1, String param2, String param3, String param4, String param5) {
        String logMessage = "[" + getTimeLogs() + "]" + "[" + param1 + "]" + "[" + param2 + "][" + param3 + "][" + param4 + "][" + param5 + "]";
        writeFile(logMessage);
    }

    private void logFile(String param1, String param2, String param3, String param4, String param5, String param6) {
        String logMessage = "[" + getTimeLogs() + "]" + "[" + param1 + "]" + "[" + param2 + "][" + param3 + "][" + param4 + "][" + param5 + "][" + param6 + "]";
        writeFile(logMessage);
    }

    private void logFile(String param1, String param2, String param3, String param4, String param5, String param6, String param7) {
        String logMessage = "[" + getTimeLogs() + "]" + "[" + param1 + "]" + "[" + param2 + "][" + param3 + "][" + param4 + "][" + param5 + "][" + param6 + "][" + param7 + "]";
        writeFile(logMessage);
    }

    private String getTimeLogs() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
        LocalDateTime localDateTime = LocalDateTime.now();
        return dateTimeFormatter.format(localDateTime);
    }

    private String getTimeExport() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime localDateTime = LocalDateTime.now();
        return dateTimeFormatter.format(localDateTime);
    }

    private String convertToCsv(String input) {
        StringBuilder stringBuilder = new StringBuilder();

        String[] lines = input.split("\\r?\\n");
        for (String line : lines) {
            if (!line.isEmpty()) {
                stringBuilder.append(line.substring(1, line.length() - 1).replace("][", ","));
                stringBuilder.append("\n");
            }
        }

        return stringBuilder.toString();
    }
}
