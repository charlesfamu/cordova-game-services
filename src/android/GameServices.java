package com.littlemathgenius.cordova.plugins.gameservices;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Log;

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
    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInflow = true;

    private GoogleApiClient mGoogleApiClient;
    private CallbackContext savedCallbackContext;
    Activity mActivity;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
      super.initialize(cordova, webView);
      mActivity = cordova.getActivity();
    }

    @Override
  	public void onActivityResult(int requestCode, int responseCode, Intent intent) {
  		super.onActivityResult(requestCode, responseCode, intent);
      Log.i(TAG, "In onActivityResult");

      // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...)
      if (requestCode == RC_SIGN_IN) {
        Log.i(TAG, "One of our activities finished up");
        mResolvingConnectionFailure = false;
        if (responseCode == RESULT_OK) {
          handleSignInResult(Auth.GoogleSignInApi.getSignInResultFromIntent(intent));
        } else {
          BaseGameUtils.showActivityResultError(this, requestCode, resultCode, R.string.sign_in_failed);
        }
      }
  	}

    @Override
    public void onConnected(Bundle connectionHint) {
      // if (this.mGoogleApiClient.hasConnectedApi(Games.API)) {
      //   Auth.GoogleSignInApi.silentSignIn(this.mGoogleApiClient).setResultCallback(
      //     new ResultCallback() {
      //       @Override
      //       public void onResult(GoogleSignInResult googleSignInResult) {
      //         // In this case, we are sure the result is a success.
      //         GoogleSignInAccount acct = googleSignInResult.getGoogleSignInAccount());
      //          // Use the API client as normal.
      //         Player player = Games.API.getCurrentPlayer(mApiClient);
      //       }
      //     }
      //   );
      // } else {
      //   onSignedOut();
      // }
    }

    @Override
    public void onConnectionSuspended(int cause) {}

    @Override
    public void onConnectionFailed(ConnectionResult result) {
      Log.i(TAG, "Unresolvable failure in connecting to Google APIs");
      this.savedCallbackContext.error(result.getErrorCode());
    }

    @Override
    public void onStop(){
  		super.onStop();
  	}

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
      this.savedCallbackContext = callbackContext;

      if (ACTION_LOGIN.equals(action)) {
        if (this.isGooglePlayServicesAvailable()) {
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

    private void handleSignInResult(GoogleSignInResult signInResult) {
      if (this.mGoogleApiClient == null) {
        this.savedCallbackContext.error("GoogleApiClient was never initialized.");
        return;
      }

      if (signInResult == null) {
        this.savedCallbackContext.error("GoogleSignInResult is null.");
        return;
      }

      Log.i(TAG, "Handling SignIn Result");

      if (signInResult.isSuccess()) {
        this.mGoogleApiClient.connect();
        GoogleSignInAccount acct = signInResult.getSignInAccount();
        JSONObject result = new JSONObject();

        try {
          Log.i(TAG, "trying to get account information");

          result.put("email", acct.getEmail());

          //only gets included if requested (See Line 164).
          result.put("idToken", acct.getIdToken());

          //only gets included if requested (See Line 166).
          result.put("serverAuthCode", acct.getServerAuthCode());

          result.put("userId", acct.getId());
          result.put("displayName", acct.getDisplayName());
          result.put("familyName", acct.getFamilyName());
          result.put("givenName", acct.getGivenName());
          result.put("imageUrl", acct.getPhotoUrl());

          this.savedCallbackContext.success(result);
        } catch (JSONException e) {
          this.savedCallbackContext.error("Trouble parsing result, error: " + e.getMessage());
        }
      } else {
        Log.i(TAG, "Wasn't signed in.");
        this.savedCallbackContext.error(signInResult.getStatus().getStatusCode());
      }
    }

    private void signIn() {
      Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(this.mGoogleApiClient);
      cordova.getActivity().startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
      if (this.mGoogleApiClient == null) {
        this.savedCallbackContext.error("Please use login before logging out.")
        return;
      }

      ConnectionResult apiConnect = this.mGoogleApiClient.blockingConnect();

      if (apiConnect.isSuccess()) {
        Auth.GoogleSignInApi.signOut(this.mGoogleApiClient).setResultCallback(
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
        )
      }
    }

    private void revokeAccess() {
      if (this.mGoogleApiClient == null) {
        this.savedCallbackContext.error("Please use login before disconnecting");
        return;
      }

      ConnectionResult apiConnect = mGoogleApiClient.blockingConnect();

      if (apiConnect.isSuccess()) {
        Auth.GoogleSignInApi.revokeAccess(this.mGoogleApiClient).setResultCallback(
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
        )
      }
    }

    private void buildGoogleApliClient() {
      GoogleSignInOptions.Builder gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);

      Log.i(TAG, "Building GoogleApiClient");
      GoogleApiClient.Builder builder = new GoogleApiClient.Builder(webView.getContext())
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .setViewForPopups(webView)
        .addApi(Auth.GOOGLE_SIGN_IN_API, gso.build())
        .addApi(Games.API);

      this.mGoogleApiClient = builder.build();

      Log.i(TAG, "GoogleApiClient built");
    }

    private boolean isGooglePlayServicesAvailable() {
      GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
	    int status = googleApiAvailability.isGooglePlayServicesAvailable(this.mActivity);

      if (status != ConnectionResult.SUCCESS) {
        if (googleApiAvailability.isUserResolvableError(status)) {
          googleApiAvailability.getErrorDialog(this.mActivity, status, 2404).show();
        }
        Log.e(TAG, "Google Play Services are unavailable");
        this.savedCallbackContext.error("Google Play Services are unavailable");
        return false;
	    } else {
        Log.d(TAG, "** Google Play Services are available **");
        return true;
      }

    }
}
