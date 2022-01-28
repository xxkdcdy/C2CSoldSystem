package com.cdy.mobiledesign.util;

public class Goods {
    private String good_code;    //订单号
    private String good_address; //包裹地址
    private String good_weight;  //包裹重量
    private String good_fee;     //配送费用
    private String good_time;    //预期时间
    private String good_state;   //订单状态
    private String owner;        //订单所有者信息

    public Goods(String good_code, String good_address, String good_weight, String good_fee, String good_time, String good_state, String owner) {
        this.good_code = good_code;
        this.good_address = good_address;
        this.good_weight = good_weight;
        this.good_fee = good_fee;
        this.good_time = good_time;
        this.good_state = good_state;
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getGood_code() {
        return good_code;
    }

    public void setGood_code(String good_code) {
        this.good_code = good_code;
    }

    public String getGood_address() {
        return good_address;
    }

    public void setGood_address(String good_address) {
        this.good_address = good_address;
    }

    public String getGood_weight() {
        return good_weight;
    }

    public void setGood_weight(String good_weight) {
        this.good_weight = good_weight;
    }

    public String getGood_fee() {
        return good_fee;
    }

    public void setGood_fee(String good_fee) {
        this.good_fee = good_fee;
    }

    public String getGood_time() {
        return good_time;
    }

    public void setGood_time(String good_time) {
        this.good_time = good_time;
    }

    public String getGood_state() {
        return good_state;
    }

    public void setGood_state(String good_state) {
        this.good_state = good_state;
    }
}
