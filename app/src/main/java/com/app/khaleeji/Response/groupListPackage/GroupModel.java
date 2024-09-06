package com.app.khaleeji.Response.groupListPackage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GroupModel {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("status")
    @Expose
    private Integer status;

    @SerializedName("group_name")
    @Expose
    private String groupName;

    @SerializedName("created_by")
    @Expose
    private Integer created_by;

    @SerializedName("group_users")
    @Expose
    private String groupUsers;

    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    @SerializedName("is_blocked")
    @Expose
    private Integer isBlocked;

    @SerializedName("users")
    @Expose
    private List<GroupMemberModel> data;

    public List<GroupMemberModel> getData() {
        return data;
    }

    public void setData(List<GroupMemberModel> data) {
        this.data = data;
    }

    public Integer getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(Integer isBlocked) {
        this.isBlocked = isBlocked;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getGroupUsers() {
        return groupUsers;
    }

    public void setGroupUsers(String groupUsers) {
        this.groupUsers = groupUsers;
    }

    public Integer getCreated_by() {
        return created_by;
    }

    public void setCreated_by(Integer created_by) {
        this.created_by = created_by;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String group_name) {
        this.groupName = group_name;
    }

    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
