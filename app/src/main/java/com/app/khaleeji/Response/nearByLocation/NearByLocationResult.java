package com.app.khaleeji.Response.nearByLocation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Dcube on 11-10-2018.
 */

public class NearByLocationResult {

    @SerializedName("geometry")
    @Expose
    private NearByLocationGeometry geometry;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("photos")
    @Expose
    private List<NearByLocationPhoto> photos = null;
    @SerializedName("place_id")
    @Expose
    private String placeId;
    @SerializedName("reference")
    @Expose
    private String reference;
    @SerializedName("scope")
    @Expose
    private String scope;
    @SerializedName("types")
    @Expose
    private List<String> types = null;
    @SerializedName("vicinity")
    @Expose
    private String vicinity;
    @SerializedName("opening_hours")
    @Expose
    private NearByLocationOpeningHours openingHours;
    @SerializedName("plus_code")
    @Expose
    private NearByLocationPlusCode plusCode;
    @SerializedName("rating")
    @Expose
    private Double rating;

    public NearByLocationGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(NearByLocationGeometry geometry) {
        this.geometry = geometry;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<NearByLocationPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<NearByLocationPhoto> photos) {
        this.photos = photos;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public NearByLocationOpeningHours getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(NearByLocationOpeningHours openingHours) {
        this.openingHours = openingHours;
    }

    public NearByLocationPlusCode getPlusCode() {
        return plusCode;
    }

    public void setPlusCode(NearByLocationPlusCode plusCode) {
        this.plusCode = plusCode;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

}
