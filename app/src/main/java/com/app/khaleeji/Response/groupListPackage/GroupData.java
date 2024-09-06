package com.app.khaleeji.Response.groupListPackage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Dcube on 21-11-2018.
 */

public class GroupData {

    @SerializedName("groups")
    @Expose
    private List<Group> groups = null;


    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }



}
