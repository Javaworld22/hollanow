package com.doxa360.android.betacaller;

import com.digits.sdk.android.internal.Beta;
import com.doxa360.android.betacaller.model.CallNote;
import com.doxa360.android.betacaller.model.Industry;
import com.doxa360.android.betacaller.model.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

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
                             @Query(BetaCaller.TOKEN) String token);

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
                                       @Query (BetaCaller.INDUSTRY) String industry);

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

    @GET(BetaCaller.CALLNOTE_BY_PHONE)
    Call<CallNote> getCallNoteByPhone(@Query(BetaCaller.PHONE) String phoneNumber,
                                        @Query(BetaCaller.TOKEN) String token);

    @GET(BetaCaller.USERS_BY_CONTACTS)
    Call<List<User>> getUsersByContactList(@Query(BetaCaller.CONTACT_LIST) String contacts,
                                           @Query(BetaCaller.TOKEN) String token);

    @POST(BetaCaller.SAVE_CALLNOTE)
    Call<CallNote> saveCallNote(@Query("token") String token,
                                    @Body CallNote callNote);


//    @POST(BetaCaller.BACKUP_CONTACTS)
//    Call<CallNote> saveCallNote(@Query("token") String token,
//                                @Body CallNote callNote);
}
