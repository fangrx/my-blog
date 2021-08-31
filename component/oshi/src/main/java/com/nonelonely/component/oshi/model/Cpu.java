package com.nonelonely.component.oshi.model;

import com.nonelonely.common.utils.Arith;

/**
 * 描述:
 * ${DESCRIPTION}
 *
 * @author nonelonely
 * @create 2020-12-07 0:28
 * 个人博客地址：https://www.nonelonely.com
 */
public class Cpu {
    /**
     * 核心数
     */
    private int cpuNum;

    /**
     * CPU总的使用率
     */
    private double total;

    /**
     * CPU系统使用率
     */
    private double sys;

    /**
     * CPU用户使用率
     */
    private double used;

    /**
     * CPU当前等待率
     */
    private double wait;

    /**
     * CPU当前空闲率
     */
    private double free;

    public int getCpuNum() {
        return cpuNum;
    }

    public void setCpuNum(int cpuNum) {
        this.cpuNum = cpuNum;
    }

    public double getTotal() {
        return Arith.round(Arith.mul(total, 100), 2);
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getSys() {
        return Arith.round(Arith.mul(sys / total, 100), 2);
    }

    public void setSys(double sys) {
        this.sys = sys;
    }

    public double getUsed() {
        return Arith.round(Arith.mul(used / total, 100), 2);
    }

    public void setUsed(double used) {
        this.used = used;
    }

    public double getWait() {
        return Arith.round(Arith.mul(wait / total, 100), 2);
    }

    public void setWait(double wait) {
        this.wait = wait;
    }

    public double getFree() {
        return Arith.round(Arith.mul(free / total, 100), 2);
    }

    public void setFree(double free) {
        this.free = free;
    }

}
