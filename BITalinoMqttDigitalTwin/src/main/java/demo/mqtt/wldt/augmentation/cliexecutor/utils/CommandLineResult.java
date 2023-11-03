package demo.mqtt.wldt.augmentation.cliexecutor.utils;

/**
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project java-python-cli-executor
 * @created 20/09/2021 - 15:59
 */
public class CommandLineResult {

    private int exitCode;

    private String outputLog;

    private String errorLog;

    public CommandLineResult() {
    }

    public CommandLineResult(int code, String outputLog, String errorLog) {
        this.exitCode = code;
        this.outputLog = outputLog;
        this.errorLog = errorLog;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public String getOutputLog() {
        return outputLog;
    }

    public void setOutputLog(String outputLog) {
        this.outputLog = outputLog;
    }

    public String getErrorLog() {
        return errorLog;
    }

    public void setErrorLog(String errorLog) {
        this.errorLog = errorLog;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CommandLineResult{");
        sb.append("exitCode=").append(exitCode);
        sb.append(", outputLog='").append(outputLog).append('\'');
        sb.append(", errorLog='").append(errorLog).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
