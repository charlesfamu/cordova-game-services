package com.littlemathgenius.cordova.plugins.gameservices;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.R.id;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.games.leaderboard.*;
import com.google.android.gms.games.Games;

import org.apache.cordova.*;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * This class echoes a string called from JavaScript.
 */
public class GameServices extends CordovaPlugin implements
  GoogleApiClient.ConnectionCallbacks,
  GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "GameServicesPlugin";

    private static final String ACTION_SIGNIN = "signIn";
    private static final String ACTION_SIGNOUT = "signOut";
    private static final String ACTION_IS_SIGNEDIN = "isSignedIn";

    private static final String ACTION_SUBMIT_SCORE = "submitScore";
    private static final String ACTION_SUBMIT_SCORE_NOW = "submitScoreNow";
    private static final String ACTION_GET_PLAYER_SCORE = "getPlayerScore";
    private static final String ACTION_LOAD_TOP_SCORES = "getTopScores";

    private static final int RC_SIGN_IN = 9001;
    private static final int RC_GAMESERVICES = 4195819;
    private static final int errorMessageCode = 12500;
    private static final int maxResults = 25;
    private static final int ACTIVITY_CODE_SHOW_LEADERBOARD = 0;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInflow = true;
    private boolean mConnecting = false;
    private boolean mExplicitSignout = false;
    // SignInFailureReason mSignInFailureReason = null;

    private GoogleApiClient mGoogleApiClient;
    private CallbackContext savedCallbackContext;
    // private MyGameProgress mGameProgress;
    Activity mActivity;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
      super.initialize(cordova, webView);
      mActivity = cordova.getActivity();
    }

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
      JSONObject options = args.optJSONObject(0);

      if (ACTION_SIGNIN.equals(action)) {
        cordova.setActivityResultCallback(this);

        if (isGooglePlayServicesAvailable()) {
          if (mGoogleApiClient == null) {
            buildGoogleApliClient();
          }

          if (mGoogleApiClient.isConnected()) {
            callbackContext.success();
          }

          signIn(callbackContext);
        } else {
          callbackContext.error("Google Play Services are unavailable.");
        }

        return true;
      } else if (ACTION_SIGNOUT.equals(action)) {
        if (isGooglePlayServicesAvailable()) {
          if (mGoogleApiClient == null) {
            buildGoogleApliClient();
          }

          gameSignOut(callbackContext);
        } else {
          callbackContext.error("Google Play Services are unavailable.");
        }

        return true;
      } else if (ACTION_IS_SIGNEDIN.equals(action)) {
        if (isGooglePlayServicesAvailable()) {
          if (mGoogleApiClient == null) {
            buildGoogleApliClient();
          }

          isSignedIn(callbackContext);
        } else {
          callbackContext.error("Google Play Services are unavailable.");
        }

        return true;
      } else if (ACTION_SUBMIT_SCORE.equals(action)) {
        submitScore(options, callbackContext);
        return true;
      } else if (ACTION_SUBMIT_SCORE_NOW.equals(action)) {
        submitScoreNow(options, callbackContext);
        return true;
      } else if (ACTION_GET_PLAYER_SCORE.equals(action)) {
        getPlayerScore(options, callbackContext);
        return true;
      } else if (ACTION_LOAD_TOP_SCORES.equals(action)) {
        getTopScores(options, callbackContext);
        return true;
      } else {
        Log.i(TAG, "This action doesn't exist");
        return false;
      }
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
      // Games.setViewForPopups(mGoogleApiClient, mActivity.setContentView(webView));
    }

    private void isSignedIn(final CallbackContext callbackContext) {
      Log.i(TAG, "In isSignedIn method");
      mActivity.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          boolean response = (mGoogleApiClient != null && mGoogleApiClient.isConnected());

          if (response) {
            callbackContext.success("SignedIn");
          } else {
            callbackContext.success("SignedOut");
          }
        }
      });
    }

    @Override
    public void onRestoreStateForActivityResult(Bundle state, CallbackContext callbackContext) {
      super.onRestoreStateForActivityResult(state, callbackContext);
      this.savedCallbackContext = callbackContext;
      Log.i(TAG, "onRestoreStateForActivityResult: called");
    }

    @Override
    public void onConnectionSuspended(int cause) {
      mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
      Log.i(TAG, "Unresolvable failure in connecting to Google APIs");
      if (mResolvingConnectionFailure) {
        return;
      }

      mResolvingConnectionFailure = true;

      if (!BaseGameUtils.resolveConnectionFailure(mActivity, mGoogleApiClient, result, RC_SIGN_IN, errorMessageCode)) {
        mResolvingConnectionFailure = false;
      }
    }

    private void signIn(final CallbackContext callbackContext) {
      Log.i(TAG, "In signIn Method");
      mActivity.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
          mActivity.startActivityForResult(signInIntent, RC_SIGN_IN);

          PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
          pluginResult.setKeepCallback(true);
          savedCallbackContext = callbackContext;
          callbackContext.sendPluginResult(pluginResult);
        }
      });
    }

    private void gameSignOut(final CallbackContext callbackContext) {
      Games.signOut(mGoogleApiClient).setResultCallback(
        new ResultCallback<Status>() {
          @Override
          public void onResult(Status status) {
            if (status.isSuccess()) {
              mGoogleApiClient.disconnect();
              callbackContext.success();
            } else {
              callbackContext.error(status.getStatusCode());
            }
          }
        }
      );
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
      super.onActivityResult(requestCode, responseCode, intent);
      Log.i(TAG, "Is in onActivityResult method");

      if (requestCode != RC_SIGN_IN) {
        Log.i(TAG, "One of our activities finished up, but isn't the sign in activity.");
        return;
      }
      // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...)
      if (responseCode == Activity.RESULT_OK) {
        Log.i(TAG, "Sign in activity successful, calling handleSignInResult");
        mResolvingConnectionFailure = false;
        handleSignInResult(Auth.GoogleSignInApi.getSignInResultFromIntent(intent));
      } else if (responseCode == Activity.RESULT_CANCELED) {
        PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        savedCallbackContext.sendPluginResult(pluginResult);
      } else {
        savedCallbackContext.error(responseCode);
      }
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
        connect();
        savedCallbackContext.success();
      } else {
        Log.i(TAG, "Wasn't signed in.");
        savedCallbackContext.error(signInResult.getStatus().getStatusCode());
      }
    }

    private void submitScore(final JSONObject options, final CallbackContext callbackContext) throws JSONException {
      Log.i(TAG, "submitScore");

      final boolean connected = mGoogleApiClient != null && mGoogleApiClient.isConnected();
      mActivity.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          try {
            if (connected) {
              Games.Leaderboards.submitScore(mGoogleApiClient, options.getString("leaderboardId"), options.getInt("score"));
              callbackContext.success();
            } else {
              callbackContext.error("submitScore: not yet signed in.");
            }
          } catch (Exception e) {
            Log.w(TAG, "submitScore: unexpected error", e);
            callbackContext.error("submitScore: error using improper function parameters.");
          }
        }
      });
    }

    private void submitScoreNow(final JSONObject options, final CallbackContext callbackContext) throws JSONException {
      Log.i(TAG, "submitScoreNow");

      final boolean connected = mGoogleApiClient != null && mGoogleApiClient.isConnected();
      mActivity.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          try {
            if (connected) {
              PendingResult<Leaderboards.SubmitScoreResult> result = Games.Leaderboards.submitScoreImmediate(mGoogleApiClient, options.getString("leaderboardId"), options.getInt("score"));
              result.setResultCallback(new ResultCallback<Leaderboards.SubmitScoreResult>() {
                @Override
                public void onResult(Leaderboards.SubmitScoreResult submitScoreResult) {
                  if (submitScoreResult.getStatus().isSuccess()) {
                    ScoreSubmissionData scoreSubmissionData = submitScoreResult.getScoreData();
                    if (scoreSubmissionData != null) {
                      try {
                        ScoreSubmissionData.Result scoreResult = scoreSubmissionData.getScoreResult(LeaderboardVariant.TIME_SPAN_ALL_TIME);
                        JSONObject result = new JSONObject();
                        result.put("leaderboardId", scoreSubmissionData.getLeaderboardId());
                        result.put("playerId", scoreSubmissionData.getPlayerId());
                        result.put("formattedScore", scoreResult.formattedScore);
                        result.put("newBest", scoreResult.newBest);
                        result.put("rawScore", scoreResult.rawScore);
                        result.put("scoreTag", scoreResult.scoreTag);
                        callbackContext.success(result);
                      } catch (JSONException e) {
                        Log.w(TAG, "submitScoreNow: unexpected error", e);
                        callbackContext.error("submitScoreNow: error while submitting score");
                      }
                    } else {
                      callbackContext.error("submitScoreNow: can't submit the score");
                    }
                  } else {
                    callbackContext.error("submitScoreNow error: " + submitScoreResult.getStatus().getStatusMessage());
                  }
                }
              });
            } else {
              callbackContext.error("submitScoreNow: not yet signed in");
            }
          } catch (Exception e) {
            Log.i(TAG, "submitScoreNow: unexpected error", e);
            callbackContext.error("submitScoreNow: error using improper function parameters.");
          }
        }
      });
    }

    private void getPlayerScore(final JSONObject options, final CallbackContext callbackContext) throws JSONException {
      Log.i(TAG, "In getPlayerScore method");

      final boolean connected = mGoogleApiClient != null && mGoogleApiClient.isConnected();

      cordova.getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          try {
            if (connected) {
              PendingResult<Leaderboards.LoadPlayerScoreResult> result = Games.Leaderboards.loadCurrentPlayerLeaderboardScore(mGoogleApiClient, options.getString("leaderboardId"), LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC);
              result.setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
                @Override
                public void onResult(Leaderboards.LoadPlayerScoreResult playerScoreResult) {
                  if (playerScoreResult.getStatus().isSuccess()) {
                    JSONObject result = new JSONObject();
                    LeaderboardScore score = playerScoreResult.getScore();
                    if (score != null) {
                      try {
                        result.put("playerScore", score.getRawScore());
                        Log.i(TAG, "getPlayerScore: captured score correctly");
                        callbackContext.success(result);
                      } catch (JSONException e) {
                        Log.w(TAG, "getPlayerScore: unexpected error", e);
                        callbackContext.error("getPlayerScore: error while retrieving score");
                      }
                    } else {
                      try {
                        result.put("playerScore", JSONObject.NULL);
                        Log.i(TAG, "getPlayerScore: empty score");
                        callbackContext.success(result);
                      } catch (JSONException e) {
                        Log.w(TAG, "getPlayerScore: unexpected error", e);
                        callbackContext.error("getPlayerScore: unable to set score to null");
                      }
                    }
                  } else {
                    callbackContext.error("getPlayerScore error: " + playerScoreResult.getStatus().getStatusMessage());
                  }
                }
              });
            } else {
              callbackContext.error("getPlayerScore: not yet signed in");
            }
          } catch (Exception e) {
            Log.i(TAG, "getPlayerScore: unexpected error", e);
            callbackContext.error("getPlayerScore: error using improper function parameters.");
          }
        }
      });
    }

    private void getTopScores(final JSONObject options, final CallbackContext callbackContext) throws JSONException {
      Log.i(TAG, "In getTopScores method");

      final boolean connected = mGoogleApiClient != null && mGoogleApiClient.isConnected();

      cordova.getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          try {
            if (connected) {
              Intent leaderboardIntent = Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, options.getString("leaderboardId"));
              mActivity.startActivityForResult(leaderboardIntent, ACTIVITY_CODE_SHOW_LEADERBOARD);
              callbackContext.success();
            } else {
              Log.w(TAG, "getTopScores: not yet signed in");
              callbackContext.error("getTopScores: not yet signed in");
            }
          } catch (Exception e) {
            Log.i(TAG, "getTopScores: unexpected error", e);
            callbackContext.error("getTopScores: error using improper function parameters.");
          }
        }
      });
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
                savedCallbackContext.success();
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
        return false;
	    } else {
        Log.d(TAG, "** Google Play Services are available **");
        return true;
      }

    }
}
