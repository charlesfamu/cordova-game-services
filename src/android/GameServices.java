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

    private GoogleApiClient mGoogleApiClient;
    private CallbackContext savedCallbackContext;
    Activity mActivity;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
      super.initialize(cordova, webView);
      mActivity = cordova.getActivity();
    }

    @Override
  	public void onActivityResult(int request, int response, Intent intent) {
  		super.onActivityResult(request, response, intent);
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
          cordova.getActivity().runOnUiThread(new Runnable() {
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
      if (!this.mGoogleApiClient.isConnected()) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(this.mGoogleApiClient);
        cordova.getActivity().startActivityForResult(signInIntent, RC_SIGN_IN);
      }
      return;
    }

    private void signOut() {
    }

    private void buildGoogleApliClient() {
      GoogleSignInOptions.Builder gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
      gso.requestEmail().requestProfile();

      Log.i(TAG, "Building GoogleApiClient");
      GoogleApiClient.Builder builder = new GoogleApiClient.Builder(webView.getContext())
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(Games.API)
        .addApi(Auth.GOOGLE_SIGN_IN_API, gso.build());

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
