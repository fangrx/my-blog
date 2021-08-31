package com.nonelonely.common.data;

public class RunInfo {
    //true:代表超时
    private Boolean timeOut;

    private Long compilerTakeTime;
    private String compilerMessage;
    private Boolean compilerSuccess;

    private Long runTakeTime;
    private String runMessage;
    private Boolean runSuccess;

    public Boolean getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Boolean timeOut) {
        this.timeOut = timeOut;
    }

    public Long getCompilerTakeTime() {
        return compilerTakeTime;
    }

    public void setCompilerTakeTime(Long compilerTakeTime) {
        this.compilerTakeTime = compilerTakeTime;
    }

    public String getCompilerMessage() {
        return compilerMessage;
    }

    public void setCompilerMessage(String compilerMessage) {
        this.compilerMessage = compilerMessage;
    }

    public Boolean getCompilerSuccess() {
        return compilerSuccess;
    }

    public void setCompilerSuccess(Boolean compilerSuccess) {
        this.compilerSuccess = compilerSuccess;
    }

    public Long getRunTakeTime() {
        return runTakeTime;
    }

    public void setRunTakeTime(Long runTakeTime) {
        this.runTakeTime = runTakeTime;
    }

    public String getRunMessage() {
        return runMessage;
    }

    public void setRunMessage(String runMessage) {
        this.runMessage = runMessage;
    }

    public Boolean getRunSuccess() {
        return runSuccess;
    }

    public void setRunSuccess(Boolean runSuccess) {
        this.runSuccess = runSuccess;
    }
}
