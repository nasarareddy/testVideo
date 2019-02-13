package cordova.plugin.videocall;
import android.content.Context;
import android.content.Intent;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class videocall extends CordovaPlugin {

    public CallbackContext callbackContext;
    private CordovaInterface cordova;
    private String roomId;
    private String token;
    private String  remoteName;


    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        this.cordova=cordova;
    }
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Context context = cordova.getActivity().getApplicationContext();
        if(action.equals("new_activity")) {
           // this.openNewActivity(context);
           this.openRoom(args);
            return true;
        }else if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void openNewActivity(Context context) {
        Intent intent = new Intent(context, NewActivity.class);
        this.cordova.getActivity().startActivity(intent);
    }

    public void openRoom(final JSONArray args) {
        try {
            
            this.roomId = args.getString(0);
            this.token = args.getString(1);
            this.remoteName = args.getString(2);
            final CordovaPlugin that = this;
            final String token = this.token;
            final String roomId = this.roomId;

            LOG.d("TOKEN", token);
            LOG.d("ROOMID", roomId);
     		cordova.getThreadPool().execute(new Runnable() {
                public void run() {

                    Intent intentTwilioVideo = new Intent(that.cordova.getActivity().getBaseContext(), ConversationActivity.class);
        			intentTwilioVideo.putExtra("token", token);
                    intentTwilioVideo.putExtra("roomId", roomId);  
                     intentTwilioVideo.putExtra("remoteName", remoteName); 
                    // avoid calling other phonegap apps
                    //intentTwilioVideo.setPackage(that.cordova.getActivity().getApplicationContext().getPackageName());
                    //that.cordova.startActivityForResult(that, intentTwilioVideo);
                    //that.cordova.getActivity().startActivity(intentTwilioVideo);
                    that.cordova.startActivityForResult(that, intentTwilioVideo, 0);
                }
                    
            });
        } catch (JSONException e) {
            //Log.e(TAG, "Invalid JSON string: " + json, e);
            //return null;
        }
    }

    public Bundle onSaveInstanceState() {
        Bundle state = new Bundle();
        state.putString("token", this.token);
        state.putString("roomId", this.roomId);
        return state;
    }

    public void onRestoreStateForActivityResult(Bundle state, CallbackContext callbackContext) {
        this.token = state.getString("token");
        this.roomId = state.getString("roomId");
        this.callbackContext = callbackContext;
    }
}
