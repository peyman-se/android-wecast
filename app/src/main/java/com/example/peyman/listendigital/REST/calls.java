package com.example.peyman.listendigital.REST;

import com.example.peyman.listendigital.Models.Channel;
import com.example.peyman.listendigital.Models.Media;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Peyman on 7/9/16.
 */

public interface Calls {

    @GET("/api/channels/popular")
    Call<ArrayList<Channel>> getPopular();

    @GET("/api/channels/{channelId}/media")
    Call<ArrayList<Media>> getMedia(
            @Path("channelId") Long channelId
    );

    @GET("/api/channels/{channelId}")
    Call<Channel> getChannel(
            @Path("channelId") Long channelId
    );

    @GET("/api/media/subscribed")
    Call<ArrayList<Media>> getLatestSubscribedMedia();

    @FormUrlEncoded
    @POST("/api/auth/login")
    Call<String> login(
            @Field("email") String email,
            @Field("password") String password
    );


//    @Multipart
//    @POST("ANDROID/test.php")
//    Call<ResponseBody> upload(@Part("description") RequestBody description,
//                              @Part MultipartBody.Part file);
//
//    @FormUrlEncoded
//    @POST("ANDROID/Data.php")
//    Call<List<QuestionObject>> getQuestion(
//            @Field("subject") String subject,
//            @Field("aCase") String aCase);
//
//    @FormUrlEncoded
//    @POST("ANDROID/Data.php")
//    Call<ArrayList<Match>> fetchUserMatchHistory(
//            @Field("subject") String subject,
//            @Field("codeMelli") Integer codeMelli
//    );
//
//    @FormUrlEncoded
//    @POST("ANDROID/Data.php")
//    Call<Integer> updatePassword(
//            @Field("subject") String subject,
//            @Field("codeMelli") Integer codeMelli,
//            @Field("oldPassword") String oldPassword,
//            @Field("newPassword") String newPassword
//    );
//
//    @FormUrlEncoded
//    @POST("ANDROID/Data.php")
//    Call<UserObject> getUserObjectDetails(
//            @Field("subject") String subject,
//            @Field("codeMelli") Integer codeMelli
//    );
//
//    @FormUrlEncoded
//    @POST("ANDROID/Data.php")
//    Call<ArrayList<Match>> fetchBestRanks(
//            @Field("subject") String subject
//    );
}