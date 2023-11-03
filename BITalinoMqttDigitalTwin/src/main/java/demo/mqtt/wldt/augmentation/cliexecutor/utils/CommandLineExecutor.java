package demo.mqtt.wldt.augmentation.cliexecutor.utils;

import demo.mqtt.wldt.augmentation.cliexecutor.exception.CommandLineException;

/**
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project openness-connector
 * @created 01/10/2020 - 09:04
 */
public interface CommandLineExecutor {

    public CommandLineResult executeCommand(String command) throws CommandLineException, CommandLineException;

}
