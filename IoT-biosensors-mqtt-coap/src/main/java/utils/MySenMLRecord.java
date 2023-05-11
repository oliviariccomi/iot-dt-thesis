package utils;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 +---------------+------+---------+
 |         SenML | JSON | Type    |
 +---------------+------+---------+
 |     Base Name | bn   | String  | --> macaddress
 |          Name | n    | String  | --> type
 |  Frequency    | f    | Number  | --> frequency
 |  Sequence num | sn   | Array   | --> sequence number
 |        Value  | v    | Array   | --> values
 |          Time | t    | Array   | --> timestamp
 +---------------+------+---------+
 */

public class MySenMLRecord {

    private String bn;

    private String n;

    private int s, f, d1, d2, d3, d4;

    private Double v;

    private Long t;


    public MySenMLRecord() {
    }

    public MySenMLRecord(String bn, String n, int s, int f, int d1, int d2, int d3, int d4, Double v, Long t) {
        this.bn = bn;
        this.n = n;
        this.s = s;
        this.f = f;
        this.d1 = d1;
        this.d2 = d2;
        this.d3 = d3;
        this.d4 = d4;
        this.v = v;
        this.t = t;
    }
    @JsonProperty("bn")
    public String getBn() {
        return bn;
    }

    public void setBn(String bn) {
        this.bn = bn;
    }
    @JsonProperty("n")
    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }
    @JsonProperty("s")
    public int getS() {
        return s;
    }

    public void setS(int s) {
        this.s = s;
    }
    @JsonProperty("f")
    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }
    @JsonProperty("d1")
    public int getD1() {
        return d1;
    }

    public void setD1(int d1) {
        this.d1 = d1;
    }
    @JsonProperty("d2")
    public int getD2() {
        return d2;
    }

    public void setD2(int d2) {
        this.d2 = d2;
    }
    @JsonProperty("d3")
    public int getD3() {
        return d3;
    }

    public void setD3(int d3) {
        this.d3 = d3;
    }
    @JsonProperty("d4")
    public int getD4() {
        return d4;
    }

    public void setD4(int d4) {
        this.d4 = d4;
    }
    @JsonProperty("v")
    public Double getV() {
        return v;
    }

    public void setV(Double v) {
        this.v = v;
    }
    @JsonProperty("t")
    public Long getT() {
        return t;
    }

    public void setT(Long t) {
        this.t = t;
    }

    @Override
    public String toString() {
        return "SenML [ " + (bn != null ? "bn=" + bn + "  " : "") + (n != null ? "n=" + n + "  " : "") +
                "s=" + s + "  " + "d1=" + d1 + "  " +
                "d1=" + d1 + "  " + "d2=" + d2 + "  " +
                "d3=" + d3 + "  " + "d4=" + d4 + "  " +
                (v != null ? "v=" + v + "  " : "") + "f=" + f + "  " +
                (t != null ? "t=" + t + "  " : "") + "]";

    }
}

