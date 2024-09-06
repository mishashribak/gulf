package com.app.khaleeji.Response.searchLocationPackage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Dcube on 12-10-2018.
 */

public class SearchLocationPrediction {

    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("matched_substrings")
    @Expose
    private List<SearchedLocationMatchedSubstring> matchedSubstrings = null;
    @SerializedName("place_id")
    @Expose
    private String placeId;
    @SerializedName("reference")
    @Expose
    private String reference;
    @SerializedName("structured_formatting")
    @Expose
    private SearchedLocationStructuredFormatting structuredFormatting;
    @SerializedName("terms")
    @Expose
    private List<SearchedLocationTerm> terms = null;
    @SerializedName("types")
    @Expose
    private List<String> types = null;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<SearchedLocationMatchedSubstring> getMatchedSubstrings() {
        return matchedSubstrings;
    }

    public void setMatchedSubstrings(List<SearchedLocationMatchedSubstring> matchedSubstrings) {
        this.matchedSubstrings = matchedSubstrings;
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

    public SearchedLocationStructuredFormatting getStructuredFormatting() {
        return structuredFormatting;
    }

    public void setStructuredFormatting(SearchedLocationStructuredFormatting structuredFormatting) {
        this.structuredFormatting = structuredFormatting;
    }

    public List<SearchedLocationTerm> getTerms() {
        return terms;
    }

    public void setTerms(List<SearchedLocationTerm> terms) {
        this.terms = terms;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }


}
