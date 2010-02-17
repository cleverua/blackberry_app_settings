package com.cleverua.bb.settings;

import java.io.IOException;

public abstract class FileReader {
    protected String fileName;
    
    public FileReader (String fileName) {
        this.fileName = fileName;
    }
    
    public abstract void save() throws IOException;
    protected abstract void readFile() throws IOException;
    
    public void read() throws IOException {
        if(!Utils.isFilePresent(fileName)) {
            Utils.createDirIncludingAncestors(fileName);
             save();
         } else {
             readFile();
         }  
    }
}
