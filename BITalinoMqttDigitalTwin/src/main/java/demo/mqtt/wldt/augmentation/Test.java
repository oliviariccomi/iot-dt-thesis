package demo.mqtt.wldt.augmentation;

import demo.mqtt.wldt.augmentation.cliexecutor.utils.CommandLineExecutor;
import demo.mqtt.wldt.augmentation.cliexecutor.utils.CommandLineResult;
import demo.mqtt.wldt.augmentation.cliexecutor.utils.LinuxCliExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project java-python-cli-executor
 * @created 20/09/2021 - 15:26
 */
public class Test {
    private static final Logger logger = LoggerFactory.getLogger(Test.class);


    public static void main(String[] args) {

        try{

            //String command = "keytool -list -keystore example.client.chain.p12 -storepass changeit";
            //      command = command + input arguments required by the .py script
            String command = "python /Users/olivia1/Desktop/thesis-management-master/BioSPPyData/csvReaders/csvReader.py /Users/olivia1/Desktop/AriannaC_100Hz.csv /Users/olivia1/Desktop/AriannaC_100Hz_stressors.csv /Users/olivia1/Desktop/thesis-management-master/BioSPPyData/data/ecg/normal/ecg_ariannaC_100Hz.txt /Users/olivia1/Desktop/thesis-management-master/BioSPPyData/data/ecg/stressed/ecg_ariannaC_100Hz_stressors.txt";

            CommandLineExecutor commandLineExecutor = new LinuxCliExecutor();
            CommandLineResult cliResult = commandLineExecutor.executeCommand(command);

            logger.debug("############ Cli Command Line Result #############");
            logger.info("Command Result: {}", cliResult.getExitCode());
            logger.info("Error Log: {}", cliResult.getErrorLog());
            logger.info("Console Log: {}", cliResult.getOutputLog());

        }catch (Exception e){
            e.printStackTrace();
        }
        
    }

}
