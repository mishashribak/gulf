package ApiRequest;

import com.google.gson.JsonObject;
import com.app.khaleeji.Response.AddCommentResponse;
import com.app.khaleeji.Response.AddMemoryResponse;
import com.app.khaleeji.Response.AddUsersToGroupResponse;
import com.app.khaleeji.Response.Basic_Response;
import com.app.khaleeji.Response.BlockFriendsResponse;
import com.app.khaleeji.Response.BlockUnblockResponse;
import com.app.khaleeji.Response.CheckInResponse;
import com.app.khaleeji.Response.CreateGroupResponse;
import com.app.khaleeji.Response.DeleteCommentResponse;
import com.app.khaleeji.Response.DeleteGroupResponse;
import com.app.khaleeji.Response.DiscoverResponse;
import com.app.khaleeji.Response.FetchMediaLikeResponse;
import com.app.khaleeji.Response.FriendlistResponse;
import com.app.khaleeji.Response.GetSlctdMediaDetailResponse;
import com.app.khaleeji.Response.HotspotUpdateResponse;
import com.app.khaleeji.Response.MemoryUpdateResponse;
import com.app.khaleeji.Response.Search.SearchByNameResponse;
import com.app.khaleeji.Response.VisibilityResponse;
import com.app.khaleeji.Response.fetchHotspotAndFrndsDetail.LastHotSpotResponse;
import com.app.khaleeji.Response.groupListPackage.GroupListReponse;
import com.app.khaleeji.Response.groupListPackage.GroupMemberListResponse;
import com.app.khaleeji.Response.GulfProfileMetUp;
import com.app.khaleeji.Response.GulfProfileResponse;
import com.app.khaleeji.Response.GulfSearchmetup;
import com.app.khaleeji.Response.GulfUpdateProfile;
import com.app.khaleeji.Response.HotSpotDetailsResponse;
import com.app.khaleeji.Response.HotSpotResponse;
import com.app.khaleeji.Response.HotSpotUserResponse;
import com.app.khaleeji.Response.Logout_Response;
import com.app.khaleeji.Response.MapCategoryResponse;
import com.app.khaleeji.Response.MediaLikeReponse;
import com.app.khaleeji.Response.MobileResponse;
import com.app.khaleeji.Response.NearByResponse;
import com.app.khaleeji.Response.NotficationsSettings;
import com.app.khaleeji.Response.NotificationResponse;
import com.app.khaleeji.Response.OtpResponse;
import com.app.khaleeji.Response.RemoveMediaResponse;
import com.app.khaleeji.Response.ReportMediaResponse;
import com.app.khaleeji.Response.Search.SearchFriendsModel;
import com.app.khaleeji.Response.TimeLineResponseData;
import com.app.khaleeji.Response.UnblockFrndResponse;
import com.app.khaleeji.Response.blockistPackage.BlockListResponse;
import com.app.khaleeji.Response.fetchDailiesOfFriends.FetchDailiesOfFriendsResponse;
import com.app.khaleeji.Response.fetchHotspotAndFrndsDetail.FetchHotspotAndFrndsDetailResponse;
import com.app.khaleeji.Response.fetchHotspotTimeLine.FetchHotspotTimelineResponse;
import com.app.khaleeji.Response.fetchMemoryPackage.FetchMemoryResponse;
import com.app.khaleeji.Response.friendsNotInGroupPackage.GetFriendsNotInGroupResponse;
import com.app.khaleeji.Response.groupListPackage.GroupListResponse;
import com.app.khaleeji.Response.groupParticipantsListPackage.GroupParticipantResponse;
import com.app.khaleeji.Response.nearByLocation.NearByLocationResponse;
import com.app.khaleeji.Response.searchLocationPackage.SearchLocationResponse;

import java.util.List;
import java.util.Map;

import Model.CameraHotspotShare;
import com.app.khaleeji.Response.MediaViewsListResponse;

import Model.NewTagResponse;
import Model.OTPResponseTest;
import Model.SearchTagResponse;
import Model.ZipData;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface ApiInterface {

    @POST("otpSignup")
    @FormUrlEncoded
    Call<OtpResponse> getOtp(@FieldMap Map<String, String> params);

    @GET("user/verifyOtpMobileNoRequest")
    Call<MobileResponse> getOtpMobileVerification(@QueryMap Map<String, Object> params);

    @POST("signup")
    @Multipart
    Call<OtpResponse> getsignup(
                @Part("full_name") RequestBody fullname,
                @Part("username") RequestBody username,
                @Part("email") RequestBody email,
                @Part("mobile_number") RequestBody mobileNo,
                @Part("password") RequestBody password,
                @Part("country_id") RequestBody countryId,
                @Part("device_type") RequestBody deviceType,
                @Part("device_id") RequestBody deviceId,
                @Part("lat") RequestBody lat,
                @Part("lng") RequestBody lng,
                @Part("all_other_to_seeDOB") RequestBody allowDob,
                @Part("dob") RequestBody dob,
                @Part("gender") RequestBody gender,
                @Part("question") RequestBody question,
                @Part("country") RequestBody country,
                @Part MultipartBody.Part profile_picture,
                @Part MultipartBody.Part bg_picture,
                @Part("language") RequestBody lang);


    @POST("signin")
    @FormUrlEncoded
    Call<OtpResponse> getLogin(@FieldMap Map<String, String> params);

    @POST("logout")
    @FormUrlEncoded
    Call<Logout_Response> logout(@Field("user_id") int userId);

    @GET("getProfile/{user_id}")
    Call<GulfProfileResponse> getGulfProfile(@Header("Authorization") String auth, @Path("user_id") String userid);

    @POST("getProfileWithFriendshipFlag")
    @FormUrlEncoded
    Call<GulfProfileResponse> getProfileWithFriendshipFlag(@Field("id") int userId, @Field("other_id") int otherId);


    //updateProfile
    @POST("updateProfile")
    @Multipart
    Call<GulfUpdateProfile> updateProfile(
            @Part("user_id") RequestBody id,
            @Part("full_name") RequestBody fullname,
            @Part("email") RequestBody email,
            @Part("country_id") RequestBody country_id,
            @Part("gender") RequestBody gender,
            @Part("country") RequestBody country,
            @Part("dob") RequestBody dob,
            @Part("visibility") RequestBody visibility,
            @Part("privacy") RequestBody privacy,
            @Part("bio") RequestBody bio,
            @Part("question") RequestBody question,
            @Part("all_other_see_username") RequestBody all_other_see_username,
            @Part("all_other_to_seeprofile") RequestBody all_other_to_seeprofile,
            @Part("all_other_to_seeDOB") RequestBody all_other_to_seeDOB,
            @Part("mobile_number") RequestBody mobile_number,
            @Part("username") RequestBody username,
            @Part("all_other_to_seeMyfriends") RequestBody all_other_to_seeMyfriends,
            @Part MultipartBody.Part profile_picture,
            @Part MultipartBody.Part bg_picture);


    @POST("forgot")
    @FormUrlEncoded
    Call<Basic_Response> forgot(@FieldMap Map<String, Object> params);

    @POST("changeUsernameEmailMobile")
    @FormUrlEncoded
    Call<Basic_Response> changeUsernameEmailMobile(@FieldMap Map<String, Object> params);


    @GET("user/changeMobileNoRequest")
    Call<MobileResponse> changemobilenumber(@QueryMap Map<String, Object> params);

    @POST("resetPasswordId")
    @FormUrlEncoded
    Call<Basic_Response> resetPasswordId(@FieldMap Map<String, Object> params);


    @POST("changePassword")
    @FormUrlEncoded
    Call<Basic_Response> changePassword(@FieldMap Map<String, Object> params);

    @POST("user/report")
    @FormUrlEncoded
    Call<Basic_Response> report(@FieldMap Map<String, Object> params);

    @GET("activeStatusLog")
    Call<String> activeStatusLog(@QueryMap Map<String, Object> params);

    @GET("friendsList")
    Call<FriendlistResponse> friendRequestList(@Header("Authorization") String auth, @QueryMap Map<String, Object> params);

    @POST("user/nearBy")
    @FormUrlEncoded
    Call<NearByResponse> getNearByProfiles(@FieldMap Map<String, Object> params);

    @POST("hotspot/newlist")
    @FormUrlEncoded
    Call<HotSpotResponse> getHotspotlist(@FieldMap Map<String, Object> params);

    @POST("user/checkin")
    @FormUrlEncoded
    Call<CheckInResponse> checkin(@FieldMap Map<String, Object> params);

    @GET("user/getSetting")
    Call<NotficationsSettings> getSettingList(@QueryMap Map<String, Object> params);

    @POST("hotspot/category")
    @FormUrlEncoded
    Call<MapCategoryResponse> getmap_category(@FieldMap Map<String, Object> params);

    @POST("addmeetupMedia")
    @FormUrlEncoded
    Call<AddMemoryResponse> addMemoryService(@FieldMap Map<String, Object> params);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("addmeetupMediaNew")
    Call<AddMemoryResponse> addMemoryServiceNew(@Body JsonObject idsPost);

    @POST("mediaLike")
    @FormUrlEncoded
    Call<MediaLikeReponse> mediaLikeService(@FieldMap Map<String, Object> params);

    @POST("getSelectedMediaDetails")
    @FormUrlEncoded
    Call<GetSlctdMediaDetailResponse> getSelectedMediaDetailsService(@FieldMap Map<String, Object> params);

    @POST("postComment")
    @FormUrlEncoded
    Call<AddCommentResponse> addCommentService(@FieldMap Map<String, Object> params);

    @POST("reportedMedia")
    @FormUrlEncoded
    Call<RemoveMediaResponse> reportMediaService(@FieldMap Map<String, Object> params);


    @POST("deleteComment")
    @FormUrlEncoded
    Call<DeleteCommentResponse> deleteCommentService(@FieldMap Map<String, Object> params);

    @POST("removeMedia")
    @FormUrlEncoded
    Call<RemoveMediaResponse> removeMediaService(@FieldMap Map<String, Object> params);

    @POST("usersLikedMedia")
    @FormUrlEncoded
    Call<FetchMediaLikeResponse> fetchMediaLikeUsers (@Field("media_id") int mediaId);

    @POST("deleteUserAccount")
    @FormUrlEncoded
    Call<Basic_Response> deleteUserAccount(@Field("user_id") String userId);

    @POST("deactiveUserAccount")
    @FormUrlEncoded
    Call<Basic_Response> deactivateUserAccount(@Field("user_id") String userId);

    @POST("MediaViewers")
    @FormUrlEncoded
    Call<MediaViewsListResponse> getMediaViews(@Field("media_id") int media_id);

    @POST("viewMedia")
    @FormUrlEncoded
    Call<RemoveMediaResponse> viewMedia(@Field("user_id") int userId,
                                           @Field("media_id") int media_id);
    //new apis
    @POST("seachForTop")
    @FormUrlEncoded
    Call<SearchByNameResponse> seachForTop(@Field("user_id") int userId);

    @POST("seachForUserBynameOrUsername")
    @FormUrlEncoded
    Call<SearchByNameResponse> seachForFriendsByname(@Field("user_id") int userId,
                                                     @Field("name") String name);
    @POST("seachForHastag")
    @FormUrlEncoded
    Call<NewTagResponse> seachForHastag(@Field("user_id") int userId,
                                        @Field("hashtag") String tag,
                                        @Field("type") String type);

    @POST("seachForUserByhastag")
    @FormUrlEncoded
    Call<SearchTagResponse> seachForUserByhastag(@Field("user_id") int userId,
                                                 @Field("type") String hashtag);

    @POST("tags/count")
    @FormUrlEncoded
    Call<JsonObject> countTag(@Field("user_id") int userId);

    @POST("hashtag/memories")
    @FormUrlEncoded
    Call<JsonObject> getMemoriesByHashTag(@Field("user_id") int userId, @Field("hashtag") String hashtag);

    @POST("seachForUserBylocation")
    @FormUrlEncoded
    Call<SearchByNameResponse> seachForUserBylocation(@Field("user_id") int userId, @Field("lat") String lat,
                                                      @Field("lng") String lng, @Field("name") String name);

    @POST("sendFriendRequest")
    @FormUrlEncoded
    Call<Basic_Response> sendFriendRequest(@FieldMap Map<String, Object> params);

    @POST("updateAnswer")
    @FormUrlEncoded
    Call<Basic_Response> updateAnswer(@FieldMap Map<String, Object> params);

    @POST("unFriend")
    @FormUrlEncoded
    Call<Basic_Response> unFriend(@FieldMap Map<String, Object> params);

    @POST("rejectFriendRequest")
    @FormUrlEncoded
    Call<Basic_Response> rejectFriendRequest(@FieldMap Map<String, Object> params);

    @POST("acceptFriendRequest")
    @FormUrlEncoded
    Call<Basic_Response> acceptFriendRequest(@FieldMap Map<String, Object> params);

    @POST("user/block/list")
    @FormUrlEncoded
    Call<SearchFriendsModel> searchBlockedUsers(@Field("user_id") int userId);

    @POST("user/unblock")
    @FormUrlEncoded
    Call<Basic_Response> sendUnblock(@FieldMap Map<String, Object> params);

    @POST("user/block")
    @FormUrlEncoded
    Call<Basic_Response> sendBlock(@FieldMap Map<String, Object> params);

    @POST("user/setSetting")
    @FormUrlEncoded
    Call<Basic_Response> setSettingList(@FieldMap Map<String, Object> params);

    @POST("getHotspotDetails")
    @FormUrlEncoded
    Call<HotSpotDetailsResponse> getHotspotDetails(@FieldMap Map<String, Object> params);

    @POST("hotspot/userList")
    @FormUrlEncoded
    Call<HotSpotUserResponse> getHotspotUserlist(@FieldMap Map<String, Object> params);

    @POST("user/GroupCreate")
    @FormUrlEncoded
    Call<CreateGroupResponse> groupCreate(@FieldMap Map<String, Object> params);

    @POST("user/groups")
    @FormUrlEncoded
    Call<GroupListReponse> getGroupList(@Field("created_by") int createrId);

    @POST("removeGroup")
    @FormUrlEncoded
    Call<Basic_Response> removeGroup(@Field("id") int id);

    @POST("user/GroupUpdate")
    @FormUrlEncoded
    Call<Basic_Response> groupUpdate(@Field("id") int groupId, @Field("group_users") String groupUsers);

    @POST("setMessageMe")
    @FormUrlEncoded
    Call<Basic_Response> setMessageMeApi(@FieldMap Map<String, Object> params);

    @POST("hotspotsUpdates")
    @FormUrlEncoded
    Call<HotspotUpdateResponse> hotspotsUpdates(@Field("userid") int userId);

    @POST("home/dailies")
    @FormUrlEncoded
    Call<JsonObject> getDailies(@FieldMap Map<String, Object> params);

    @POST("home/memories")
    @FormUrlEncoded
    Call<JsonObject> getMemories(@FieldMap Map<String, Object> params);

    @POST("updateStatus")
    @FormUrlEncoded
    Call<AddCommentResponse> updateStatus(@FieldMap Map<String, Object> params);

    @POST("getLastHotspot")
    @FormUrlEncoded
    Call<LastHotSpotResponse> getLastHotspot(@Field("user_id") int userId);

    @POST("notifications")
    @FormUrlEncoded
    Call<NotificationResponse> getNotifications(@Field("user_id") int userId);

    @POST("getVisibility")
    @FormUrlEncoded
    Call<VisibilityResponse> getVisibility(@Field("user_id") int userId);

    @POST("updateVisibility")
    @FormUrlEncoded
    Call<VisibilityResponse> updateVisibility(@Field("user_id") int userId, @Field("visibility") String visibility);

    @POST("user/email")
    @FormUrlEncoded
    Call<VisibilityResponse> getEmail(@Field("userid") int userId);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("sendChatNotificaitons")
    Call<Basic_Response> sendChatNotificaitons(@Body JsonObject idsPost);

    @POST("updateLocation")
    @FormUrlEncoded
    Call<Basic_Response> updateLocation(@FieldMap Map<String, Object> params);

    @GET
    Call<SearchLocationResponse> fetchSearchedLocations(@Url String url);

    @GET
    Call<NearByLocationResponse> fetchNearByLocations(@Url String url);

    @POST("user/block/check")
    @FormUrlEncoded
    Call<JsonObject> isBlocked(@FieldMap Map<String, Object> params);

    @POST("user/forget/username")
    @FormUrlEncoded
    Call<JsonObject> forgotUsername(@FieldMap Map<String, Object> params);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("mobile/numbers")
    Call<JsonObject> getContactUsersByNumber(@Body JsonObject object);

    @POST("profile/username")
    @FormUrlEncoded
    Call<JsonObject> getIdFromUsername(@Field("id") int yourId, @Field("username") String username);

    @GET("user/mobile/visibilty")
    Call<JsonObject> getMobileVisibility(@Query("user_id") String userid);

    @POST("user/mobile/visibilty")
    @FormUrlEncoded
    Call<JsonObject> setMobileVisibility(@Field("user_id") int yourId, @Field("all_other_to_seeMobile") int all_other_to_seeMobile);

    @POST("status/history")
    @FormUrlEncoded
    Call<JsonObject> getStatusHistory(@Field("user_id") int yourId, @Field("from_user") int from_user);

    @POST("status/remove")
    @FormUrlEncoded
    Call<JsonObject> removeStatus(@Field("status_id") String status_id);

    @POST("status/comment/remove")
    @FormUrlEncoded
    Call<JsonObject> removeStatusComment(@Field("comment_id") String comment_id);

    @POST("status/comment/reply/remove")
    @FormUrlEncoded
    Call<JsonObject> removeStatusCommentReply(@Field("reply_id") String reply_id);

    @POST("status/report")
    @FormUrlEncoded
    Call<JsonObject> reportStatus(@FieldMap Map<String, Object> params);

    @POST("status/comments")
    @FormUrlEncoded
    Call<JsonObject> getStatusComments(@Field("status_id") String status_id, @Field("from_user") String from_user);

    @POST("status/comment/replies")
    @FormUrlEncoded
    Call<JsonObject> getStatusCommentReplies(@Field("comment_id") String comment_id);

    @POST("status/comment")
    @FormUrlEncoded
    Call<JsonObject> commentStatus(@FieldMap Map<String, Object> params);

    @POST("status/comment/reply")
    @FormUrlEncoded
    Call<JsonObject> statusCommentReply(@FieldMap Map<String, Object> params);

    @POST("status/comment/like")
    @FormUrlEncoded
    Call<JsonObject> likeStatusComment(@FieldMap Map<String, Object> params);

    @POST("status/like")
    @FormUrlEncoded
    Call<JsonObject> likeStatus(@FieldMap Map<String, Object> params);

    @POST("status/comment/reply/like")
    @FormUrlEncoded
    Call<JsonObject> likeStatusReplyComment(@FieldMap Map<String, Object> params);

    @POST("status/likers")
    @FormUrlEncoded
    Call<JsonObject> getStatusLikers(@Field("status_id") String status_id);

    @POST("status/viewers")
    @FormUrlEncoded
    Call<JsonObject> getStatusViewers(@Field("status_id") String status_id);

    @POST("dislikeFriendRequest")
    @FormUrlEncoded
    Call<JsonObject> dislikeFriendRequest(@Field("to_user") Integer to_user, @Field("from_user") Integer from_user );
}
