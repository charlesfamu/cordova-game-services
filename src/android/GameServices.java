package com.littlemathgenius.cordova.plugins.gameservices;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.*;
import com.google.android.gms.games.achievement.Achievements;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Games.GamesOptions;
import com.google.android.gms.games.GamesActivityResultCodes;

/**
 * This class echoes a string called from JavaScript.
 */
public class GameServices extends CordovaPlugin implements
        GameHelper.GameHelperListener {

    public static final String TAG = "GameServicesPlugin";
    public static final int CLIENT_GAMES = GameHelper.CLIENT_GAMES;
    public static final int CLIENT_PLUS = GameHelper.CLIENT_PLUS;
    public static final int CLIENT_ALL = GameHelper.CLIENT_ALL;

    private static final String ACTION_LOGIN = "login";
    private static final String ACTION_LOGOUT = "logout";
    private static final String ACTION_IS_SIGNEDIN = "isSignedIn";
    private static final String ACTION_TOGGLE_DEBUG = "toggleDebugLog";

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

    private GameHelper gameHelper;
    private int mRequestedClients = CLIENT_GAMES;
    private boolean mDebugLog = false;
    Activity mActivity = null;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
      super.initialize(cordova, webView);

      mActivity = cordova.getActivity();
      if (gameHelper == null) {
        gameHelper = new GameHelper(mActivity, CLIENT_GAMES);
        gameHelper.enableDebugLog(mDebugLog);
      }

      gameHelper.setup(this);
      cordova.setActivityResultCallback(this);
    }

	  @Override
  	public void onActivityResult(int request, int response, Intent intent) {
  		super.onActivityResult(request, response, intent);
  		gameHelper.onActivityResult(request, response, intent);
  	}

    @Override
    public void onStop(){
  		super.onStop();
  		signout();
  	}

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {

      GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
	    int res = googleAPI.isGooglePlayServicesAvailable(mActivity);

      if (res != ConnectionResult.SUCCESS) {
        Log.e(LOG_TAG, "Google Play Services are unavailable");
        callbackContext.error("Unavailable");
        return true;
	    } else {
        Log.d(LOG_TAG, "** Google Play Services are available **");
      }

      if (ACTION_TOGGLE_DEBUG.equals(action)) {
        toggleDebugLog();
      } else if (ACTION_LOGIN.equals(action)) {
        cordova.getActivity().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            if (!gameHelper.isSignedIn()) {
              signIn();
            }
          }
        });
      } else if (ACTION_LOGOUT.equals(action)) {
        signOut();
      } else if (ACTION_IS_SIGNEDIN.equals(action)) {
        
      }
    }

    private void signIn() {
      gameHelper.onStart(mActivity);
    }

    private void signOut() {
      gameHelper.onStop();
    }

    protected void toggleDebugLog() {
      mDebugLog = !mDebugLog;
      if (gameHelper != null) {
          mHelper.enableDebugLog(mDebugLog);
      }
    }
}
