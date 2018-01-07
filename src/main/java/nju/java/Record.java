package nju.java;

import java.io.*;
import java.util.Calendar;
import java.util.Scanner;

public class Record {
    private static String argFileName = "huluwa.log";
    private String fileName = argFileName, inputFileName = argFileName;
    private File file;
    boolean isInited = false, isInitedInput = false;
    private FileOutputStream fileOutput;
    private Scanner scanner;
    private int milliseconds = 0;
    private String fileNamePath = ".", inputFileNamePath = ".";

    static void setArgFileName(String fn) {
        if (fn != null)
            argFileName = fn;
    }


    public void setFileName(String path, String fn) {
        if (fn != null && path != null) {
            fileName = fn;
            fileNamePath = path;
        }
    }

    public void setInputFileName(String path, String fn) {
        if (fn != null && path != null) {
            inputFileNamePath = path;
            inputFileName = fn;
        }
    }

    public boolean init(boolean isContinue) {
        isInited = false;
        milliseconds = getMilliseconds();
        file = new File(fileNamePath, fileName);
//        if (!isContinue) {
//            if (file.exists()) {
//                file.delete();
//            }
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//                return;
//            }
//        }
        try {
            fileOutput = new FileOutputStream(file, isContinue);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        isInited = true;
        return file.length() == 0;
    }

    public void init() {
        init(false);
    }

    public Record() {
    }

    private int getMilliseconds() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MINUTE) * 60 * 1000
                + calendar.get(Calendar.SECOND) * 1000
                + calendar.get(Calendar.MILLISECOND);
    }

    public boolean write(String s) {
        if (!isInited)
            return false;
        int m = getMilliseconds();
        try {
            fileOutput.write((m - milliseconds + " ").getBytes());
//            fileOutput.write((0 + " ").getBytes());
            milliseconds = m;
            fileOutput.write(s.getBytes());
            fileOutput.write("\n".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean initInput() {
        isInitedInput = false;
        try {
            scanner = new Scanner(new File(inputFileNamePath, inputFileName));
        } catch (FileNotFoundException e) {
//            e.printStackTrace();
            System.err.println("Cannot open " + inputFileName);
            return false;
        }
        isInitedInput = true;
        return true;
    }

    public int readInt() {
        if (!isInitedInput || !scanner.hasNext())
            return -1;
        return scanner.nextInt();
    }

    public boolean readBoolean() {
        return isInitedInput && scanner.hasNext() && scanner.nextBoolean();
    }

    public boolean hasNext() {
        return isInitedInput && scanner.hasNext();
    }
}
