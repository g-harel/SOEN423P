/*

Author: Andres Vazquez
Course: SOEN 423

*/

package Utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class OperationLogger {
    public synchronized static void log(String filePath, String logEntry) {
        File logFile = new File(filePath);
        PrintWriter out;
    
        try {
            if (logFile.exists() && !logFile.isDirectory()) {
                out = new PrintWriter(new FileOutputStream(new File(filePath), true));
            }
            else {
                logFile.getParentFile().mkdirs();
                logFile.createNewFile();
                out = new PrintWriter(filePath);
            }
        
            out.append(logEntry).append("\n");
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public synchronized static void clearLogs(File logsDirectory) {
        if(logsDirectory.exists()){
            File[] files = logsDirectory.listFiles();
            if(null!=files){
                for(int i=0; i<files.length; i++) {
                    if(files[i].isDirectory()) {
                        clearLogs(files[i]);
                    }
                    else {
                        files[i].delete();
                    }
                }
            }
        }
        logsDirectory.delete();
    }
    
    public synchronized static void deleteLogFile(File logFile) {
        logFile.delete();
    }
}
