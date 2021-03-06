package com.gf.doughflow.run;

import com.gf.doughflow.swing.UIHandler;
import com.gf.doughflow.workspace.AccountRegistry;
import com.gf.doughflow.workspace.DFProperties;
import com.gf.doughflow.workspace.WorkSpace;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

public class DoughFlow {

    private final long FILELISTENER_DELAY_MS = 2000;
    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public void start(String propFilePath) {

        DFProperties prop = new DFProperties(propFilePath);
        AccountRegistry.init(prop.readAccounts());
        UIHandler wdc = new UIHandler();
        String workDir = wdc.showDirChooser(prop.getDefaultWorkDir());
        prop.setAndWriteDefaultWorkDir(workDir);
        WorkSpace ws = new WorkSpace(workDir);
        ws.importData(wdc);

        try {
            //start homebank
            Process process = new ProcessBuilder(prop.getHomebankExecuteable(),
                    ws.getActualFile().getAbsolutePath()).start();

            //make local backups
            long lastmodOld = ws.getActualFile().lastModified();
            while (process.isAlive()) {
                Thread.sleep(FILELISTENER_DELAY_MS);
                long lastmod = ws.getActualFile().lastModified();
                if (lastmod > lastmodOld) {
                    ws.createBackup(null);
                    lastmodOld = lastmod;
                }
            }
        } catch (IOException e) {
            logger.severe(e.getMessage());
        } catch (InterruptedException e) {
            logger.severe(e.getMessage());
        } finally {
            wdc.close();
        }
    }

    public static void main(String args[]) {
        String propFile = "./doughflow.properties";
        if (args.length > 0) {
            propFile = args[0];
        }
        new DoughFlow().start(propFile);
    }
}
