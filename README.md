cordova-plugin-game-services
==================================

Cordova Plugin For Google Play Games Services.

Includes the new Google Play Services (GoogleApiAvailability) and methods for Leaderboards (will develop Achievements later).

## Install

```
cordova plugin add https://github.com/charlesfamu/cordova-game-services.git --variable APP_ID=you_app_id_here
```

## Usage

### Authentication

#### Sign in
You should do this as soon as your `deviceready` event has been fired. The plugin handles the various auth scenarios for you.

```
cordova.plugins.gameservices.signIn((response) => {
    //response
  });
```

#### Sign out
You should provide the option for users to sign out

```
cordova.plugins.gameservices.signOut((response: string) => {
    if (response == 'OK') {
      //signOut successful
    }
  });
```

#### Auth status
To check if the user is already logged in (eg. to determine weather to show the Log In or Log Out button), use the following

```
cordova.plugins.gamesservices.isSignedIn((response: string) => {
	if (response === 'SignedIn') {
    //signIn successful
  }
});
```

#### Player Information
Get player score information for a given leaderboard.

```
cordova.plugins.gamesservices.getPlayerScore( { leaderboardId } );
```


### Leaderboards

#### Submit Score

Ensure you have had a successful callback from `window.plugins.gameservices.signIn()` first before attempting to submit a score. You should also have set up your leaderboard(s) in Google Play Game Console and use the leaderboard identifier assigned there as the `leaderboardId`.

```
cordova.plugins.gameservices.submitScore( { score, leaderboardId });
```

#### Sumit Score Now

Ensure you have had a successful callback from `window.plugins.gameservices.signIn()` first before attempting to submit a score. You should also have set up your leaderboard(s) in Google Play Game Console and use the leaderboard identifier assigned there as the `leaderboardId`.

This method submit the score immediately.

```
cordova.plugins.gameservices.submitScoreNow({ score, leaderboardId });
```

#### Show specific leaderboard

Launches directly into the specified leaderboard:

```
cordova.plugins.gameservices.getTopScores( { leaderboardId });
```

## Platform

Currently, only Android is supported


## License

[MIT License](License)
