package com.doxa360.android.betacaller;

import android.util.Log;
import android.widget.Toast;

import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.User;
import com.google.firebase.iid.FirebaseInstanceId;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Apple on 21/01/2017.
 */

public class FirebaseInstanceIdService extends com.google.firebase.iid.FirebaseInstanceIdService {

    private static final String TAG = "FirebaseInstance";
    private HollaNowApiInterface hollaNowApiInterface;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "refreshed: "+refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        if (refreshedToken!=null) {
            sendRegistrationToServer(refreshedToken);
        }
    }

    private void sendRegistrationToServer(String refreshedToken) {
//        do retrofit update device id thingy here

        HollaNowSharedPref mSharedPref = new HollaNowSharedPref(getApplicationContext());
        mSharedPref.setDeviceId(refreshedToken);
//        User currentUser = mSharedPref.getCurrentUser();
//
//        currentUser.setDeviceId(refreshedToken);
//        if (MyToolBox.isNetworkAvailable(getApplicationContext())) {
//
//            Call<User> call = hollaNowApiInterface.editUserProfile(currentUser);
//            call.enqueue(new Callback<User>() {
//                @Override
//                public void onResponse(Call<User> call, Response<User> response) {
//                    if (response.code() == 200) {
//                        Log.e(TAG, "success " + response.body().toString());
//                        Toast.makeText(getApplicationContext(), "Profile successfully updated", Toast.LENGTH_SHORT).show();
////                    finish();
//                    } else {
//                        Log.e(TAG, "error: " + response.errorBody().toString());
//                        Toast.makeText(getApplicationContext(), "Error updating profile", Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//
//                @Override
//                public void onFailure(Call<User> call, Throwable t) {
//                    Log.e(TAG, "failed: " + t.getMessage());
//                    Toast.makeText(getApplicationContext(), "Network error. Try again", Toast.LENGTH_LONG).show();
//
//                }
//            });
//
//        }



    }
}
