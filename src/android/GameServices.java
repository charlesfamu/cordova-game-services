package com.littlemathgenius.cordova.plugins.gameservices;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Log;
import android.R.id;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.games.achievement.Achievements;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Games.GamesOptions;
import com.google.android.gms.games.GamesActivityResultCodes;

import java.security.MessageDigest;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * This class echoes a string called from JavaScript.
 */
public class GameServices extends CordovaPlugin implements
  GoogleApiClient.ConnectionCallbacks,
  GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "GameServicesPlugin";

    private static final String ACTION_LOGIN = "login";
    private static final String ACTION_LOGOUT = "logout";
    private static final String ACTION_IS_SIGNEDIN = "isSignedIn";

    private static final String ACTION_SUBMIT_SCORE = "submitScore";
    private static final String ACTION_SUBMIT_SCORE_NOW = "submitScoreNow";
    private static final String ACTION_GET_PLAYER_SCORE = "getPlayerScore";
    private static final String ACTION_SHOW_ALL_LEADERBOARDS = "showAllLeaderboards";
    private static final String ACTION_SHOW_LEADERBOARD = "showLeaderboard";

    private static final String ACTION_UNLOCK_ACHIEVEMENT = "unlockAchievement";
    private static final String ACTION_UNLOCK_ACHIEVEMENT_NOW = "unlockAchievementNow";
    private static final String ACTION_INCREMENT_ACHIEVEMENT = "incrementAchievement";
    private static final String ACTION_INCREMENT_ACHIEVEMENT_NOW = "incrementAchievementNow";
    private static final String ACTION_SHOW_ACHIEVEMENTS = "showAchievements";
    private static final String ACTION_SHOW_PLAYER = "showPlayer";
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_GAMESERVICES = 4195819;
    private static final int errorMessageCode = 12500;
    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInflow = true;
    private boolean mConnecting = false;
    // SignInFailureReason mSignInFailureReason = null;

    private GoogleApiClient mGoogleApiClient;
    private CallbackContext savedCallbackContext;
    // private MyGameProgress mGameProgress;
    Activity mActivity;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
      super.initialize(cordova, webView);
      mActivity = cordova.getActivity();
      buildGoogleApliClient();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
      Log.i(TAG, "onConected: connected!");
      succeedSignIn();
    }

    private void succeedSignIn() {
      Log.i(TAG, "succeedSignIn: signedIn");
      // mSignInFailureReason = null;
      mConnecting = false;
    }

    @Override
    public void onConnectionSuspended(int cause) {
      mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
      Log.i(TAG, "Unresolvable failure in connecting to Google APIs");
      // this.savedCallbackContext.error(result.getErrorCode());
      if (mResolvingConnectionFailure) {
        return;
      }

      mResolvingConnectionFailure = true;

      if (!BaseGameUtils.resolveConnectionFailure(mActivity, mGoogleApiClient, result, RC_SIGN_IN, errorMessageCode)) {
        mResolvingConnectionFailure = false;
      }
    }

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
      savedCallbackContext = callbackContext;

      if (ACTION_LOGIN.equals(action)) {
        cordova.setActivityResultCallback(this);
        if (isGooglePlayServicesAvailable()) {
          this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
              if (mGoogleApiClient == null) {
                buildGoogleApliClient();
              }
              signIn();
            }
          });
        }
      } else if (ACTION_LOGOUT.equals(action)) {
        signOut();
      } else if (ACTION_IS_SIGNEDIN.equals(action)) {

      } else if (ACTION_SUBMIT_SCORE.equals(action)) {

      } else if (ACTION_SUBMIT_SCORE_NOW.equals(action)) {

      } else if (ACTION_GET_PLAYER_SCORE.equals(action)) {

      } else if (ACTION_SHOW_ALL_LEADERBOARDS.equals(action)) {

      } else if (ACTION_UNLOCK_ACHIEVEMENT.equals(action)) {

      } else if (ACTION_UNLOCK_ACHIEVEMENT_NOW.equals(action)) {

      } else if (ACTION_INCREMENT_ACHIEVEMENT.equals(action)) {

      } else if (ACTION_INCREMENT_ACHIEVEMENT_NOW.equals(action)) {

      } else if (ACTION_SHOW_ACHIEVEMENTS.equals(action)) {

      } else if (ACTION_SHOW_PLAYER.equals(action)) {

      } else {
          Log.i(TAG, "This action doesn't exist");
          return false;
      }

      return true;
    }

    private void signIn() {
      Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
      cordova.getActivity().startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // public SignInFailureReason getSignInError() {
    //   return mSignInFailureReason;
    // }

    // public static class SignInFailureReason {
    //   public static final int NO_ACTIVITY_RESULT_CODE = -100;
    //   int mServiceErrorCode = 0;
    //   int mActivityResultCode = NO_ACTIVITY_RESULT_CODE;
    //
    //   public int getServiceErrorCode() {
    //     return mServiceErrorCode;
    //   }
    //
    //   public int getActivityResultCode() {
    //     return mActivityResultCode;
    //   }
    //
    //   public SignInFailureReason(int serviceErrorCode, int activityResultCode) {
    //     mServiceErrorCode = serviceErrorCode;
    //     mActivityResultCode = activityResultCode;
    //   }
    //
    //   public SignInFailureReason(int serviceErrorCode) {
    //     this(serviceErrorCode, NO_ACTIVITY_RESULT_CODE);
    //   }
    //
    //   @Override
    //   public String toString() {
    //     return "SignInFailureReason(serviceErrorCode:"
    //             + GameHelperUtils.errorCodeToString(mServiceErrorCode)
    //             + ((mActivityResultCode == NO_ACTIVITY_RESULT_CODE) ? ")"
    //             : (",activityResultCode:"
    //             + GameHelperUtils
    //             .activityResponseCodeToString(mActivityResultCode) + ")"));
    //   }
    // }

    @Override
  	public void onActivityResult(int requestCode, int responseCode, Intent intent) {
  		super.onActivityResult(requestCode, responseCode, intent);
      Log.i(TAG, "In onActivityResult");

      if (requestCode != RC_SIGN_IN) {
        Log.i(TAG, "One of our activities finished up, but isn't the sign in activity.");
        return;
      }
      // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...)
      if (responseCode == Activity.RESULT_OK) {
        Log.i(TAG, "Sign in activity successful, calling handleSignInResult");
        mResolvingConnectionFailure = false;
        handleSignInResult(Auth.GoogleSignInApi.getSignInResultFromIntent(intent));
      }

      // else {
      //   BaseGameUtils.showActivityResultError(mActivity, requestCode, responseCode, errorMessageCode);
      // }
  	}

    private void handleSignInResult(GoogleSignInResult signInResult) {
      if (mGoogleApiClient == null) {
        savedCallbackContext.error("GoogleApiClient was never initialized.");
        return;
      }

      if (signInResult == null) {
        savedCallbackContext.error("GoogleSignInResult is null.");
        return;
      }

      Log.i(TAG, "Handling SignIn Result");

      if (signInResult.isSuccess()) {
        savedCallbackContext.success();
        connect();
      } else {
        Log.i(TAG, "Wasn't signed in.");
        savedCallbackContext.error(signInResult.getStatus().getStatusCode());
      }
    }

    private void connect() {
      if (mGoogleApiClient.isConnected()) {
        Log.i(TAG, "Already connected.");
        return;
      }
      Log.i(TAG, "Starting Connection.");
      mConnecting = true;
      mGoogleApiClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL);
    }

    private void signOut() {
      if (mGoogleApiClient == null) {
        savedCallbackContext.error("Please use login before logging out.");
        return;
      }

      ConnectionResult apiConnect = mGoogleApiClient.blockingConnect();

      if (apiConnect.isSuccess()) {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
          new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
              if (status.isSuccess()) {
                savedCallbackContext.success("Logged user out.");
              } else {
                savedCallbackContext.error(status.getStatusCode());
              }
            }
          }
        );
      }
    }

    private void revokeAccess() {
      if (mGoogleApiClient == null) {
        savedCallbackContext.error("Please use login before disconnecting");
        return;
      }

      ConnectionResult apiConnect = mGoogleApiClient.blockingConnect();

      if (apiConnect.isSuccess()) {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
          new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
              if (status.isSuccess()) {
                savedCallbackContext.success("Revoked user access.");
              } else {
                savedCallbackContext.error(status.getStatusCode());
              }
            }
          }
        );
      }
    }

    private void buildGoogleApliClient() {
      GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build();

      Log.i(TAG, "Building GoogleApiClient");
      GoogleApiClient.Builder builder = new GoogleApiClient.Builder(webView.getContext())
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .setViewForPopups(mActivity.findViewById(android.R.id.content))
        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
        .addApi(Games.API);

      mGoogleApiClient = builder.build();

      Log.i(TAG, "GoogleApiClient built");
    }

    private boolean isGooglePlayServicesAvailable() {
      GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
	    int status = googleApiAvailability.isGooglePlayServicesAvailable(mActivity);

      if (status != ConnectionResult.SUCCESS) {
        if (googleApiAvailability.isUserResolvableError(status)) {
          googleApiAvailability.getErrorDialog(mActivity, status, 2404).show();
        }
        Log.e(TAG, "Google Play Services are unavailable");
        savedCallbackContext.error("Google Play Services are unavailable");
        return false;
	    } else {
        Log.d(TAG, "** Google Play Services are available **");
        return true;
      }

    }
}
