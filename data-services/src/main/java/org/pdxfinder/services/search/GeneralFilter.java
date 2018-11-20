package org.pdxfinder.services.search;

/*
 * Created by csaba on 19/11/2018.
 */
public abstract class GeneralFilter {



    public String name;

    public String urlParam;

    public GeneralFilter(String name, String urlParam) {
        this.name = name;
        this.urlParam = urlParam;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlParam() {
        return urlParam;
    }

    public void setUrlParam(String urlParam) {
        this.urlParam = urlParam;
    }
}
