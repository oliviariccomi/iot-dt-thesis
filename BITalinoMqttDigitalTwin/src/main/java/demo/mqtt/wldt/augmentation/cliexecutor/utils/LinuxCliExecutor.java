package demo.mqtt.wldt.augmentation.cliexecutor.utils;

import demo.mqtt.wldt.augmentation.cliexecutor.exception.CommandLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project openness-connector
 * @created 30/09/2020 - 21:34
 */
public class LinuxCliExecutor implements CommandLineExecutor {

    private static final Logger logger = LoggerFactory.getLogger(LinuxCliExecutor.class);

    public CommandLineResult executeCommand(String command) throws CommandLineException {

        try{

            logger.info("Executing command: {}", command);

            Runtime runtime = Runtime.getRuntime();
            Process pr = runtime.exec(command);

            pr.waitFor();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            BufferedReader errorBufferReader = new BufferedReader(new InputStreamReader(pr.getErrorStream()));

            String line = null;
            String errorLine = null;

            StringBuilder consoleLog = new StringBuilder();
            StringBuilder errorLog = new StringBuilder();

            while((line = bufferedReader.readLine()) != null || (errorLine = errorBufferReader.readLine()) != null) {
                if(line != null) consoleLog.append(line).append('\n');
                if(errorLine != null) errorLog.append(errorLine).append('\n');
            }

            return new CommandLineResult(pr.exitValue(), consoleLog.toString(), errorLog.toString());

        }catch (Exception e){
            throw new CommandLineException(String.format("Error Executing Command: %s Error: %s", command, e.getLocalizedMessage()));
        }

    }

}
