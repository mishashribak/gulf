
package com.app.khaleeji.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GulfSearchmetup {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("message_original")
    @Expose
    private String messageOriginal;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessageOriginal() {
        return messageOriginal;
    }

    public void setMessageOriginal(String messageOriginal) {
        this.messageOriginal = messageOriginal;
    }




    public class Data {

        @SerializedName("current_page")
        @Expose
        private Integer currentPage;
        @SerializedName("data")
        @Expose
        private List<Datum> data = null;
        @SerializedName("from")
        @Expose
        private Integer from;
        @SerializedName("last_page")
        @Expose
        private Integer lastPage;
        @SerializedName("next_page_url")
        @Expose
        private Object nextPageUrl;
        @SerializedName("path")
        @Expose
        private String path;





        @SerializedName("per_page")
        @Expose
        private Integer perPage;
        @SerializedName("prev_page_url")
        @Expose
        private Object prevPageUrl;
        @SerializedName("to")
        @Expose
        private Integer to;
        @SerializedName("total")
        @Expose
        private Integer total;




        public Integer getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(Integer currentPage) {
            this.currentPage = currentPage;
        }

        public List<Datum> getData() {
            return data;
        }

        public void setData(List<Datum> data) {
            this.data = data;
        }

        public Integer getFrom() {
            return from;
        }

        public void setFrom(Integer from) {
            this.from = from;
        }

        public Integer getLastPage() {
            return lastPage;
        }

        public void setLastPage(Integer lastPage) {
            this.lastPage = lastPage;
        }

        public Object getNextPageUrl() {
            return nextPageUrl;
        }

        public void setNextPageUrl(Object nextPageUrl) {
            this.nextPageUrl = nextPageUrl;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public Integer getPerPage() {
            return perPage;
        }

        public void setPerPage(Integer perPage) {
            this.perPage = perPage;
        }

        public Object getPrevPageUrl() {
            return prevPageUrl;
        }

        public void setPrevPageUrl(Object prevPageUrl) {
            this.prevPageUrl = prevPageUrl;
        }

        public Integer getTo() {
            return to;
        }

        public void setTo(Integer to) {
            this.to = to;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }


        public class Datum {

            @SerializedName("id")
            @Expose
            private Integer id;
            @SerializedName("user_id")
            @Expose
            private Integer userId;
            @SerializedName("full_name")
            @Expose
            private String fullName;
            @SerializedName("country_id")
            @Expose
            private String countryId;
            @SerializedName("gender")
            @Expose
            private String gender;
            @SerializedName("height")
            @Expose
            private String height;
            @SerializedName("high_school")
            @Expose
            private String highSchool;
            @SerializedName("university")
            @Expose
            private String university;
            @SerializedName("company_name")
            @Expose
            private String companyName;
            @SerializedName("bio")
            @Expose
            private String bio;
            @SerializedName("privacy")
            @Expose
            private String privacy;
            @SerializedName("profile_picture")
            @Expose
            public String profilePicture;

            @SerializedName("question")
            @Expose
            private String question;

            public String getAnswer() {
                return answer;
            }

            public void setAnswer(String answer) {
                this.answer = answer;
            }

            @SerializedName("answer")
            @Expose
            private String answer;
            @SerializedName("created_at")
            @Expose
            private String createdAt;

            @SerializedName("request_accept")
            @Expose
            private String request_accept;

            @SerializedName("all_other_to_seeprofile")
            @Expose
            private Integer all_other_to_seeprofile;

            @SerializedName("message_me")
            @Expose
            private Integer message_me;

            @SerializedName("username")
            @Expose
            private String username;

            @SerializedName("address")
            @Expose
            private String address;


            public  String getFromrequser() {
                return fromrequser;
            }

            public void setFromrequser(String fromrequser) {
                this.fromrequser = fromrequser;
            }

            @SerializedName("from_req_user")
            @Expose
            public String fromrequser;


            @SerializedName("is_friend")
            @Expose
            public Boolean isfriend;


            @SerializedName("from_user")
            @Expose
            public String fromuser;

            public String getFromuser() {
                return fromuser;
            }

            public void setFromuser(String fromuser) {
                this.fromuser = fromuser;
            }

            @SerializedName("to_user")
            @Expose
            private Integer touser;
            @SerializedName("updated_at")
            @Expose
            private String updatedAt;

            public Integer getTouser() {
                return touser;
            }

            public void setTouser(Integer touser) {
                this.touser = touser;
            }
            public boolean getIstop() {
                return istop;
            }

            public void setIstop(boolean istop) {
                this.istop = istop;
            }


            public boolean istop = false;


            public Boolean getIsfriend() {
                return isfriend;
            }

            public void setIsfriend(Boolean isfriend) {
                this.isfriend = isfriend;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public Integer getUserId() {
                return userId;
            }

            public void setUserId(Integer userId) {
                this.userId = userId;
            }

            public String getFullName() {
                return fullName;
            }

            public void setFullName(String fullName) {
                this.fullName = fullName;
            }

            public String getCountryId() {
                return countryId;
            }

            public void setCountryId(String countryId) {
                this.countryId = countryId;
            }

            public String getGender() {
                return gender;
            }

            public void setGender(String gender) {
                this.gender = gender;
            }

            public String getHeight() {
                return height;
            }

            public void setHeight(String height) {
                this.height = height;
            }

            public String getHighSchool() {
                return highSchool;
            }

            public void setHighSchool(String highSchool) {
                this.highSchool = highSchool;
            }

            public String getUniversity() {
                return university;
            }

            public void setUniversity(String university) {
                this.university = university;
            }

            public String getCompanyName() {
                return companyName;
            }

            public void setCompanyName(String companyName) {
                this.companyName = companyName;
            }

            public String getBio() {
                return bio;
            }

            public void setBio(String bio) {
                this.bio = bio;
            }

            public String getPrivacy() {
                return privacy;
            }

            public void setPrivacy(String privacy) {
                this.privacy = privacy;
            }

            public String getProfilePicture() {
                return profilePicture;
            }

            public void setProfilePicture(String profilePicture) {
                this.profilePicture = profilePicture;
            }

            public String getQuestion() {
                return question;
            }

            public void setQuestion(String question) {
                this.question = question;
            }

            public String getCreatedAt() {
                return createdAt;
            }

            public void setCreatedAt(String createdAt) {
                this.createdAt = createdAt;
            }

            public String getUpdatedAt() {
                return updatedAt;
            }

            public void setUpdatedAt(String updatedAt) {
                this.updatedAt = updatedAt;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getRequest_accept() {
                return request_accept;
            }

            public void setRequest_accept(String request_accept) {
                this.request_accept = request_accept;
            }

            public Integer getAll_other_to_seeprofile() {
                return all_other_to_seeprofile;
            }

            public void setAll_other_to_seeprofile(Integer all_other_to_seeprofile) {
                this.all_other_to_seeprofile = all_other_to_seeprofile;
            }

            public Integer getMessage_me() {
                return message_me;
            }

            public void setMessage_me(Integer message_me) {
                this.message_me = message_me;
            }


        }


    }
}
