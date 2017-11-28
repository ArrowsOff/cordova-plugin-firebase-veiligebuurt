package cordova.plugin.firebase.veiligebuurt;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class FirebaseVeiligebuurtPlugin extends CordovaPlugin {

    private static final String TAG = "FirebaseVeiligebuurtPlugin";

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void pluginInitialize() {
        final Context context = this.cordova.getActivity().getApplicationContext();

        this.cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                Log.d(TAG, "Starting Firebase Veiligebuurt plugin");

                mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);

                // Enable analytics collection by default
                mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
            }
        });
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("logEvent".equals(action)) {
            this.logEvent(callbackContext, args.getString(0), args.getJSONObject(1));
            return true;
        } else if ("logError".equals(action)) {
            this.logError(callbackContext, args.getString(0));
            return true;
        } else if ("setUserId".equals(action)) {
            this.setUserId(callbackContext, args.getString(0));
            return true;
        } else if ("setUserProperty".equals(action)) {
            this.setUserProperty(callbackContext, args.getString(0), args.getString(1));
            return true;
        } else if ("setEnabled".equals(action)) {
            this.setEnabled(callbackContext, args.getBoolean(0));
            return true;
        } else if ("setCurrentScreen".equals(action)) {
            this.setCurrentScreen(callbackContext, args.getString(0));
            return true;
        }

        return false;
    }

    private void logEvent(final CallbackContext callbackContext, final String name, final JSONObject params) throws JSONException {
        final Bundle bundle = new Bundle();
        Iterator iter = params.keys();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            Object value = params.get(key);

            if (value instanceof Integer || value instanceof Double) {
                bundle.putFloat(key, ((Number) value).floatValue());
            } else {
                bundle.putString(key, value.toString());
            }
        }

        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    mFirebaseAnalytics.logEvent(name, bundle);
                    callbackContext.success();
                } catch (Exception e) {
                    callbackContext.error(e.getMessage());
                }
            }
        });
    }

    private void logError(final CallbackContext callbackContext, final String message) throws JSONException {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    FirebaseCrash.report(new Exception(message));
                    callbackContext.success(1);
                } catch (Exception e) {
                    FirebaseCrash.log(e.getMessage());
                    e.printStackTrace();
                    callbackContext.error(e.getMessage());
                }
            }
        });
    }

    private void setUserId(final CallbackContext callbackContext, final String userId) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    mFirebaseAnalytics.setUserId(userId);
                    callbackContext.success();
                } catch (Exception e) {
                    callbackContext.error(e.getMessage());
                }
            }
        });
    }

    private void setUserProperty(final CallbackContext callbackContext, final String name, final String value) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    mFirebaseAnalytics.setUserProperty(name, value);
                    callbackContext.success();
                } catch (Exception e) {
                    callbackContext.error(e.getMessage());
                }
            }
        });
    }

    private void setEnabled(CallbackContext callbackContext, final boolean enabled) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    mFirebaseAnalytics.setAnalyticsCollectionEnabled(enabled);
                    callbackContext.success();
                } catch (Exception e) {
                    callbackContext.error(e.getMessage());
                }
            }
        });
    }

    private void setCurrentScreen(final CallbackContext callbackContext, final String screenName) {
        // This must be called on the main thread
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                try {
                    mFirebaseAnalytics.setCurrentScreen(cordova.getActivity(), screenName, null);
                    callbackContext.success();
                } catch (Exception e) {
                    callbackContext.error(e.getMessage());
                }
            }
        });
    }
}
