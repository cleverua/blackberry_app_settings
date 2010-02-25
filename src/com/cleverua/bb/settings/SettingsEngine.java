package com.cleverua.bb.settings;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.io.LineReader;

public
class SettingsEngine extends Hashtable {
    private static final String UTF_8 = "UTF-8";
    private static final String KEY_VALUES_SEPARATORS = "=: \t\r\n\f";
    private static final String STRICT_KEY_VALUE_SEPARATORS = "=:";
    private static final String SPECIAL_SAVE_CHARS = "=: \t\r\n\f#!";
    private static final String WHITE_SPACE_CHARS = " \t\r\n\f";

    private String settingsFileName;

    public SettingsEngine (String settingsFileName) {
        this.settingsFileName = settingsFileName;
    }

    public synchronized Object setProperty(String key, String value) {
        return put(key, value);
    }

    public void load() throws SettingsEngineException {
        try {
            Utils.createFileIfNotPresent(settingsFileName);
        } catch (IOException e) {
            throw new SettingsEngineException(e);
        }
        readFile();
    }

    public void save() throws SettingsEngineException {
        try {
            Utils.createFileIfNotPresent(settingsFileName);
        } catch (IOException e) {
            throw new SettingsEngineException(e);
        }
        saveFile();
    }

    public String getProperty(String key) {
        Object oval = super.get(key);
        String sval = (oval instanceof String) ? (String)oval : null;
        return sval;
    }

    public String getProperty(String key, String defaultValue) {
        String val = getProperty(key);
        return (val == null) ? defaultValue : val;
    }

    public Enumeration propertyNames() {
        Hashtable h = new Hashtable();
        enumerate(h);
        return h.keys();
    }

    public void list(OutputStream out) throws SettingsEngineException {
        try {
        Hashtable h = new Hashtable();
        enumerate(h);
        for (Enumeration e = h.keys() ; e.hasMoreElements() ;) {
            String key = (String)e.nextElement();
            String val = (String)h.get(key);
            out.write(getOutputLine(key + '=' + val));
        }
        } catch (Exception e) {
            throw new SettingsEngineException(e);
        }
    }

    private synchronized void load(InputStream inStream) throws SettingsEngineException {
        LineReader lineReader = new LineReader(inStream);
        try {
            while (true) {
                byte[] lineBytes;
                try {
                    lineBytes = lineReader.readLine();
                } catch (EOFException e) {
                    return; // empty file, that's ok
                }
                String line = new String(lineBytes, UTF_8);

                if (line.length() > 0) {
                    // Continue lines that end in slashes if they are not comments
                    char firstChar = line.charAt(0);
                    if ((firstChar != '#') && (firstChar != '!')) {
                        while (continueLine(line)) {
                            String nextLine = new String(lineReader.readLine(), UTF_8);
                            String loppedLine = line.substring(0, line.length()-1);
                            // Advance beyond whitespace on new line
                            int startIndex=0;
                            for(startIndex=0; startIndex<nextLine.length(); startIndex++)
                                if (WHITE_SPACE_CHARS.indexOf(nextLine.charAt(startIndex)) == -1)
                                    break;
                            nextLine = nextLine.substring(startIndex,nextLine.length());
                            line = new String(loppedLine+nextLine);
                        }

                        // Find start of key
                        int len = line.length();
                        int keyStart;
                        for(keyStart=0; keyStart<len; keyStart++) {
                            if(WHITE_SPACE_CHARS.indexOf(line.charAt(keyStart)) == -1)
                                break;
                        }

                        // Blank lines are ignored
                        if (keyStart == len)
                            continue;

                        // Find separation between key and value
                        int separatorIndex;
                        for(separatorIndex=keyStart; separatorIndex<len; separatorIndex++) {
                            char currentChar = line.charAt(separatorIndex);
                            if (currentChar == '\\')
                                separatorIndex++;
                            else if(KEY_VALUES_SEPARATORS.indexOf(currentChar) != -1)
                                break;
                        }

                        // Skip over whitespace after key if any
                        int valueIndex;
                        for (valueIndex=separatorIndex; valueIndex<len; valueIndex++)
                            if (WHITE_SPACE_CHARS.indexOf(line.charAt(valueIndex)) == -1)
                                break;

                        // Skip over one non whitespace key value separators if any
                        if (valueIndex < len)
                            if (STRICT_KEY_VALUE_SEPARATORS.indexOf(line.charAt(valueIndex)) != -1)
                                valueIndex++;

                        // Skip over white space after other separators if any
                        while (valueIndex < len) {
                            if (WHITE_SPACE_CHARS.indexOf(line.charAt(valueIndex)) == -1)
                                break;
                            valueIndex++;
                        }
                        String key = line.substring(keyStart, separatorIndex);
                        String value = (separatorIndex < len) ? line.substring(valueIndex, len) : "";

                        // Convert then store key and value
                        key = loadConvert(key);
                        value = loadConvert(value);
                        put(key, value);
                    }
                }
            }
        } catch (Exception e) {
            throw new SettingsEngineException(e);  
        } 
    }

    private synchronized void save(OutputStream out, String header) throws SettingsEngineException {
        try {
            if (header != null) {
                out.write(getOutputLine('#' + header));
            }
            out.write(getOutputLine('#' + new Date().toString()));
            for (Enumeration e = keys(); e.hasMoreElements();) {
                String key = (String)e.nextElement();
                String val = (String)get(key);
                key = saveConvert(key, true);

                /* No need to escape embedded and trailing spaces for value, hence
                 * pass false to flag.
                 */
                val = saveConvert(val, false);
                out.write(getOutputLine(key + '=' + val));
            }
            out.flush();
        } catch (Exception e) {
            throw new SettingsEngineException(e);
        }
    }
    
    private byte[] getOutputLine(String str) throws UnsupportedEncodingException {
        return (str + '\n').getBytes(UTF_8);
    }
    
    /*
     * Returns true if the given line is a line that must
     * be appended to the next line
     */
    private boolean continueLine (String line) {
        int slashCount = 0;
        int index = line.length() - 1;
        while((index >= 0) && (line.charAt(index--) == '\\'))
            slashCount++;
        return (slashCount % 2 == 1);
    }

    /*
     * Changes special saved chars to their original forms
     */
    private String loadConvert (String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);

        for(int x=0; x<len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 't')
                    aChar = '\t';
                else if (aChar == 'r')
                    aChar = '\r';
                else if (aChar == 'n')
                    aChar = '\n';
                else if (aChar == 'f')
                    aChar = '\f';
            }
            outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }

    /*
     * Writes out any of the characters in specialSaveChars
     * with a preceding slash
     */
    private String saveConvert(String theString, boolean escapeSpace) {
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len*2);

        for(int x=0; x<len; x++) {
            char aChar = theString.charAt(x);
            switch(aChar) {
                case ' ':
                    if (x == 0 || escapeSpace) 
                        outBuffer.append('\\');

                    outBuffer.append(' ');
                    break;
                case '\\':outBuffer.append('\\').append('\\');
                break;
                case '\t':outBuffer.append('\\').append('t');
                break;
                case '\n':outBuffer.append('\\').append('n');
                break;
                case '\r':outBuffer.append('\\').append('r');
                break;
                case '\f':outBuffer.append('\\').append('f');
                break;
                default:
                    if (SPECIAL_SAVE_CHARS.indexOf(aChar) != -1) {
                        outBuffer.append('\\');
                    } 
                    outBuffer.append(aChar);
            }
        }
        return outBuffer.toString();
    }

    private void saveFile() throws SettingsEngineException {
        FileConnection fc = null;
        OutputStream out = null;
        try {
            fc = (FileConnection) Connector.open(settingsFileName, Connector.READ_WRITE);
            fc.truncate(0);
            out = fc.openOutputStream();
            save(out, null);
        } catch (IOException e) {
            throw new SettingsEngineException(e);
        } finally {
            Utils.safelyCloseStream(out);
            Utils.safelyCloseStream(fc);
        }
    }

    private void readFile() throws SettingsEngineException {
        FileConnection fc = null;
        InputStream in = null;

        try {
            fc = (FileConnection) Connector.open(settingsFileName, Connector.READ);
            in = fc.openInputStream();
            load(in);
        } catch (IOException e) {
            throw new SettingsEngineException(e);
        } finally {
            Utils.safelyCloseStream(in);
            Utils.safelyCloseStream(fc);
        }               
    }

    private synchronized void enumerate(Hashtable h) {
        for (Enumeration e = keys() ; e.hasMoreElements() ;) {
            String key = (String)e.nextElement();
            h.put(key, get(key));
        }
    }
}
