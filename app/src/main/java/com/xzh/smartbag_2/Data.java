package com.xzh.smartbag_2;

import java.util.List;

/**
 * Created by Lenovo on 2020/3/26.
 */
public class Data {
    private int count;
    private List<Datastreams> datastreams;


    public void setCount(int count){
        this.count = count;
    }
    public int getCount(){
        return count;
    }

    public void setDatastreams(List<Datastreams> datastreams){
        this.datastreams = datastreams;
    }
    public List<Datastreams> getDatastreams(){
        return datastreams;
    }

}
