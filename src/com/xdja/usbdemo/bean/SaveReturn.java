package com.xdja.usbdemo.bean;


public class SaveReturn {
    
    String rtnCode;
    String rtnMsg;
    Object data;
    
    public String getRtnCode() {
        return rtnCode;
    }
    
    public void setRtnCode(String rtnCode) {
        this.rtnCode = rtnCode;
    }
    
    public String getRtnMsg() {
        return rtnMsg;
    }
    
    public void setRtnMsg(String rtnMsg) {
        this.rtnMsg = rtnMsg;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }

}
