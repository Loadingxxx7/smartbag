package com.xzh.smartbag_2;

import java.util.List;

/**
 * Created by Lenovo on 2020/3/26.
 */
public class Datapoints {
    private String at;
    private List<value> value;

    public void setAt(){
        this.at = at;
    }
    public String getAt(){
        return at;
    }

    public void setValue(List<value> value){
        this.value = value;
    }
    public List<com.xzh.smartbag_2.value> getValue(){
        return value;
    }
}
