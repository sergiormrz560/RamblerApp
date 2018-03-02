import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by bt on 2/28/18.
 *
 * App automatically synced with Firebase
 * Had to register for Wordpress plugin
 *
 */

// Class for Registering the AppID with servers
public class FirebaseDeviceRegistration extends FirebaseInstanceIdService {

    private static final String TAG = "Registering";

    String urlAdress = "http://dk5.151.myftpupload.com/pnfw/register/"; // URL of user registration for the staging site

    // onTokenRefresh = Get Updated InstanceID Token
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        final String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed Token: " + refreshedToken);

        // This is where the InstanceID should be sent to the server for registration
        registerForPushNotifications(refreshedToken);
    }

    // Sends the HTTP Post Request to register for push notifications
    private void registerForPushNotifications(final String token) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Set Headers
                    URL url = new URL(urlAdress);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    // Create JSON Object
                    JSONObject jsonParam = getMessage(token);

                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    // getMessage = Create the JSONObject to register for Push Notifications For Wordpress
    private JSONObject getMessage(String token){
        JSONObject obj = new JSONObject();
        try {
            obj.put("token", token);
            obj.put("os", "Android");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
