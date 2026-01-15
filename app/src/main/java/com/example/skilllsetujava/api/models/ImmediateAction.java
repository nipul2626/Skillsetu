package com.example.skilllsetujava.api.models;

import java.io.Serializable;
import java.util.List;

public class ImmediateAction implements Serializable {

    private String action;
    private String priority;
    private String why;
    private List<String> resources;

    public String getAction() {
        return action;
    }

    public String getPriority() {
        return priority;
    }

    public String getWhy() {
        return why;
    }

    public List<String> getResources() {
        return resources;
    }
}
