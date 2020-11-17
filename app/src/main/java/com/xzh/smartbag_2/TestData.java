package com.xzh.smartbag_2;

import java.util.List;

/**
 * Created by Lenovo on 2020/3/26.
 */
public class TestData {

    /**
     * errno : 0
     * data : {"count":1,"datastreams":[{"datapoints":[{"at":"2019-07-24 15:30:57.000","value":{"lon":"113.69384164119406","lat":"23.990991052374305"}}],"id":"LatLng"}]}
     * error : succ
     */

    private int errno;
    private DataBean data;
    private String error;

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public static class DataBean {
        /**
         * count : 1
         * datastreams : [{"datapoints":[{"at":"2019-07-24 15:30:57.000","value":{"lon":"113.69384164119406","lat":"23.990991052374305"}}],"id":"LatLng"}]
         */

        private int count;
        private List<DatastreamsBean> datastreams;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<DatastreamsBean> getDatastreams() {
            return datastreams;
        }

        public void setDatastreams(List<DatastreamsBean> datastreams) {
            this.datastreams = datastreams;
        }

        public static class DatastreamsBean {
            /**
             * datapoints : [{"at":"2019-07-24 15:30:57.000","value":{"lon":"113.69384164119406","lat":"23.990991052374305"}}]
             * id : LatLng
             */

            private String id;
            private List<DatapointsBean> datapoints;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public List<DatapointsBean> getDatapoints() {
                return datapoints;
            }

            public void setDatapoints(List<DatapointsBean> datapoints) {
                this.datapoints = datapoints;
            }

            public static class DatapointsBean {
                /**
                 * at : 2019-07-24 15:30:57.000
                 * value : {"lon":"113.69384164119406","lat":"23.990991052374305"}
                 */

                private String at;
                private ValueBean value;

                public String getAt() {
                    return at;
                }

                public void setAt(String at) {
                    this.at = at;
                }

                public ValueBean getValue() {
                    return value;
                }

                public void setValue(ValueBean value) {
                    this.value = value;
                }

                public static class ValueBean {
                    /**
                     * lon : 113.69384164119406
                     * lat : 23.990991052374305
                     */

                    private String lon;
                    private String lat;
                    private String number;

                    public String getLon() {
                        return lon;
                    }

                    public void setLon(String lon) {
                        this.lon = lon;
                    }

                    public String getLat() {
                        return lat;
                    }

                    public void setLat(String lat) {
                        this.lat = lat;
                    }

                    public void setNumber(String number){
                        this.number = number;
                    }
                    public String getNumber(){
                        return number;
                    }

                }
            }
        }
    }
}
