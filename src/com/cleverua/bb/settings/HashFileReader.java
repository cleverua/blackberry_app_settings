package com.cleverua.bb.settings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.io.Base64InputStream;
import net.rim.device.api.io.Base64OutputStream;

public class HashFileReader extends FileReader {
    private static final char NL = '\n';
    private static final char EQ = '=';
    private static final String UTF_8 = "UTF-8";
    
    private Hashtable hash;
    
    public HashFileReader(String fileName, Hashtable hash) {
        super(fileName);
        this.hash = hash;
    }

    public void save() throws IOException {
        FileConnection fc = null;
        OutputStream out = null;
        OutputStreamWriter writer = null;
        try {
            fc = (FileConnection) Connector.open(fileName, Connector.READ_WRITE);
            if (!fc.exists()) {
                fc.create();
            }

            fc.truncate(0);

            out = fc.openOutputStream();
            writer = new OutputStreamWriter(out);

            for(Enumeration e = hash.keys(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                writer.write(key + EQ + encode(hash.get(key).toString()) + NL);
            }

        } finally {
            Utils.safelyCloseStream(writer);
            Utils.safelyCloseStream(out);
            Utils.safelyCloseStream(fc);
        }
    }

    protected void readFile() throws IOException{
        synchronized (this) {
            FileConnection fc = null;
            InputStream in = null;

            try {
                fc = (FileConnection) Connector.open(fileName, Connector.READ);
                if(fc.exists()) {
                    in = fc.openInputStream();
                    parse(Utils.getStringFromStream(in));
                }
            } finally {
                Utils.safelyCloseStream(fc);
                Utils.safelyCloseStream(in);
            }               
        }
    }
    
    private void parse(String data) throws IOException{
        String working = data;
        int index = working.indexOf(NL);

        while (index != -1) {
            String tmp = "";
            if (index > 0) {
                tmp = working.substring(0, index);
                toProperty(tmp);
            }
            working = working.substring(index + 1);
            index = working.indexOf(NL);
        }
        toProperty(working);
    }
    
    private void toProperty(String str) throws IOException   {
        if(str.length() == 0) {
            return;
        }
        int equalsPosition = str.indexOf(EQ);
        String key = str.substring(0, equalsPosition).trim();
        String value;
        try {
            value = decode(str.substring(equalsPosition + 1).trim());
            hash.put(key, value);
        } catch (UnsupportedEncodingException e) {
            ;//do not put key-value
        }
    }
    
    private static String encode(String stringToEncode) throws IOException {
        byte[] bytesToEncode = stringToEncode.getBytes(UTF_8);
        return Base64OutputStream.encodeAsString(bytesToEncode, 0, bytesToEncode.length, false, false);
    }
    
    private static String decode(String stringToDecode) throws UnsupportedEncodingException, IOException {
        return new String(Base64InputStream.decode(stringToDecode), UTF_8);
    }
}
