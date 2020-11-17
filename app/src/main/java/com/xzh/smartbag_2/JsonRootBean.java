package com.xzh.smartbag_2;



/**
 * Created by Lenovo on 2019/11/19.
 */
public class JsonRootBean {
    private int errno;
    private Data data;
    private String error;

    public void setErrno(int errno){
        this.errno = errno;
    }
    public int getErrno(){
        return errno;
    }

    public void setData(Data data){
        this.data = data;
    }
    public Data getData(){
        return data;
    }

    public void setError(String error){
        this.error = error;
    }
    public String getError(){
        return error;
    }
}


