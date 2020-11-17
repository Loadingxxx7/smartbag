package com.xzh.smartbag_2;

import java.util.List;

/**
 * Created by Lenovo on 2020/3/26.
 */
public class Datastreams {
    private List<Datapoints> datapoints;
    private String id;

    public void setDatapoints(List<Datapoints> datapoints){
        this.datapoints = datapoints;
    }
    public List<Datapoints> getDatapoints(){
        return datapoints;
    }

    public void setId(){
        this.id = id;
    }
    public String getId(){
        return id;
    }
}
