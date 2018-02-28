import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by bt on 2/28/18.
 */

public class FirebaseDeviceRegistration extends FirebaseInstanceIdService {

    private static final String TAG = "DeviceRegistrationFirebase";

    // onTokenRefresh = Get Updated InstanceID Token
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed Token: " + refreshedToken);

        // This is where the InstanceID should be sent to the serve for registration
    }
}
