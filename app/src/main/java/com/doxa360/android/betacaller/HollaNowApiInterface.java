package com.doxa360.android.betacaller;

import com.doxa360.android.betacaller.model.CallNote;
import com.doxa360.android.betacaller.model.Industry;
import com.doxa360.android.betacaller.model.NotificationModel;
import com.doxa360.android.betacaller.model.Post_ShoutoutModel;
import com.doxa360.android.betacaller.model.bimps;
import com.doxa360.android.betacaller.model.User;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;

import org.json.JSONException;

import java.util.List;
import java.util.StringTokenizer;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
//import okhttp3.Response;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.Response;

/**
 * Created by Apple on 03/01/2017.
 */

public interface HollaNowApiInterface {

    @POST(BetaCaller.SIGN_UP_API)
    Call<User> signUpUser(@Body User user);


    @POST(BetaCaller.SIGN_IN_API)
    Call<User> signInUser(@Body User user);

    @POST(BetaCaller.SIGN_IN_USER_API)
    Call<User> signInUsername(@Body User user);

    @GET(BetaCaller.GET_USER_DETAILS_API)
    Call<User> getUserDetails(@Query(BetaCaller.TOKEN) String token);

    @POST(BetaCaller.EDIT_PROFILE_API)
    Call<User> editUserProfile(@Body User user,
                               @Query(BetaCaller.TOKEN) String token);

    @POST(BetaCaller.EDIT_PHONE_API)
    Call<User> editUserPhone(@Body User user,
                             @Query(BetaCaller.TOKEN) String token,
                             @Query("country") String country);

    @GET(BetaCaller.SEARCH_PHONE_API)
    Call<List<User>> searchPhoneAvailability(@Query(BetaCaller.PHONE) String phone,
                                             @Query(BetaCaller.TOKEN) String token);

    @GET(BetaCaller.SEARCH_API)
    Call<List<User>> search(@Query(BetaCaller.SEARCH) String search,
                            @Query(BetaCaller.TOKEN) String token);

    @GET(BetaCaller.GET_INDUSTRY_API)
    Call<List<Industry>> getIndustry(@Query(BetaCaller.TOKEN) String token);

    @GET(BetaCaller.GET_USER_BY_INDUSTRY_API)
    Call<List<User>> getUserByIndustry(@Query(BetaCaller.TOKEN) String token,
                                       @Query (BetaCaller.INDUSTRY) String industry,
                                        @Query("country") String country);

    @Multipart
    @POST(BetaCaller.UPLOAD_PHOTO_API)
    Call<ResponseBody> uploadProfilePhoto(@Query("token") String token,
                                          @Part("file_name") RequestBody description,
                                          @Part MultipartBody.Part file);


    @GET(BetaCaller.NEARBY_USERS_API)
    Call<List<User>> getNearbyUsers(@Query(BetaCaller.LATTITUDE) float lat,
                                    @Query(BetaCaller.LONGITUDE) float lng,
                                    @Query(BetaCaller.DISTANCE_KM) int distKm,
                                    @Query(BetaCaller.TOKEN) String token);

   // @GET(BetaCaller.CALLNOTE_BY_PHONE)
  //  Call<CallNote> getCallNoteByPhone(@Query(BetaCaller.PHONE) String phoneNumber,
        //                              @Query(BetaCaller.TOKEN) String token);

    @GET(BetaCaller.USERS_BY_CONTACTS)
    Call<List<User>> getUsersByContactList(@Query(BetaCaller.CONTACT_LIST) String contacts,
                                           @Query(BetaCaller.TOKEN) String token);

    @POST(BetaCaller.SAVE_CALLNOTE)
    Call<CallNote> saveCallNote(@Query("token") String token,
                                @Body CallNote callNote);

    @GET(BetaCaller.NOTIFICATION)
    Call<JsonElement> notifyUser1();

    @Multipart
    @POST(BetaCaller.CONTACT_MANAGEMENT)
    Call<ResponseBody> contactManagement(@Part MultipartBody.Part file,
                                        //@Query("token") String token,
                                         @Query("device_id") String deviceId,
                                          @Query("username") String username); // @Part("zip") RequestBody file
    @POST(BetaCaller.CALLNOTE)
    Call<JsonElement> callnotePhone(@Query("token") String token,
                                    @Query("from") String from,
                                    @Query("to") String to,
                                    @Query("note") String note,
                                    @Query("username") String username,
                                    @Query("name") String name,
                                        @Query("photo") String photo); //For callnote

    @GET(BetaCaller.RETRIEVE_CONTACT)
    Call<JsonElement> retrieve_contact(@Query("username") String username,
                                        @Query("token") String token);

    @GET(BetaCaller.CALLNOTE_BY_PHONE)
      Call<JsonElement> getCallNoteByPhone(@Query("username") String username,@Query("token") String token);

    @Multipart
    @POST(BetaCaller.SALES_PICTURES)
    Call<ResponseBody> sendSalesPictures(@Part MultipartBody.Part file,
                                         @Query("username") String username,
                                         @Query("color") String color,
                                         @Query("content")String salesContents);

    @GET(BetaCaller.RETRIEVE_SALES_PICTURES)
    Call<JsonElement> receiveSalesPictures(@Query("username") String username);

    @POST(BetaCaller.SEND_USER_NOTIFICATION)
    Call<JsonElement> notifyUserContact(@Query("username") String username,
                                        @Query("user_notification") String userNotification);

    @GET(BetaCaller.RECEIVE_USER_NOTIFICATION)
    Call<List<NotificationModel>> receiveNotification(@Query("username") String username);

    @POST(BetaCaller.NO_OF_LIKES)
    Call<JsonElement> postLikes(@Query("username") String username,
                                       @Query("post") String msg,
                                       @Query("likes") int no_of_likes,
                                       @Query("no_post") int no_of_post
                                       //@Query("name") String name,
                                       // @Query("photo") String photo
    );

    @GET(BetaCaller.SEARCH_VISIBILITY)
    Call<JsonElement> toggles(@Query("username") String search,
                              @Query("token") String token);

    @GET(BetaCaller.NIGERIAN_NEWS)
    Call<JsonElement> newsFeed(@Query("country") String country,
                               @Query("apikey") String apiKey);

//    @POST(BetaCaller.BACKUP_CONTACTS)
    // Call<CallNote> saveCallNote(@Query("token") String token,
    //                             @Body CallNote callNote);
}
