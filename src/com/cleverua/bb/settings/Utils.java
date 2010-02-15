package com.cleverua.bb.settings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.system.Characters;

public class Utils {
    public static boolean isFilePresent(String path) {
        FileConnection fc = null;
        
        try {
            fc = (FileConnection) Connector.open(path);
            return fc.exists();

        } catch (Exception e) {
            return false;
        } finally {
            safelyCloseStream(fc);
        }
    }
    
    public static void safelyCloseStream(FileConnection stream) {
        if (stream != null) {
            try {
                stream.close();
                stream = null;
            } catch (Exception e) { /* that's ok */ }
        }
    }
    
    public static void safelyCloseStream(InputStream stream) {
        if (stream != null) {
            try {
                stream.close();
                stream = null;
            } catch (Exception e) { /* that's ok */ }
        }
    }
    
    public static void safelyCloseStream(OutputStream stream) {
        if (stream != null) {
            try {
                stream.close();
                stream = null;
            } catch (Exception e) { /* that's ok */ }
        }
    }
    
    public static void safelyCloseStream(OutputStreamWriter stream) {
        if (stream != null) {
            try {
                stream.close();
                stream = null;
            } catch (Exception e) { /* that's ok */ }
        }
    }
    
    public static String getStringFromStream(InputStream in) throws IOException {
        StringBuffer sb = new StringBuffer();
        int c;
        while ((c = in.read()) != -1) {
            sb.append((char) c);
        }
        return sb.toString();
    }
    
    public static void createDirIncludingAncestors(String dirPath) throws IOException {
        int index = dirPath.indexOf(":///");
        if(index == -1) { 
            throw new IllegalArgumentException();
        }
        String rootOfPath = dirPath.substring(0, index + 4); // e.g. "file:///"
        String restOfPath = dirPath.substring(rootOfPath.length());
        int solidusIndex = -1;
        while (true) {
            solidusIndex = restOfPath.indexOf(Characters.SOLIDUS, solidusIndex + 1);
            if(solidusIndex < 0) {
                break;
            }
            createDir(rootOfPath + restOfPath.substring(0, solidusIndex + 1));
        }
    }
    
    public static void createDir(String dirPath) throws IOException {
        FileConnection fc = null;
        
        try {
            fc = (FileConnection) Connector.open(dirPath);
            if(!fc.exists()) {
                fc.mkdir();
            }

        } finally {
            safelyCloseStream(fc);
        }
    }
}
