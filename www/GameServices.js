function GameServices() {}

GameServices.prototype.signIn = function(success, error) {
  cordova.exec(success, error, "GameServices", "signIn", []);
};

GameServices.prototype.isSignedIn = function(success, error) {
  cordova.exec(success, error, "GameServices", "isSignedIn", []);
};

GameServices.prototype.signOut = function(success, error) {
  cordova.exec(success, error, "GameServices", "signOut", []);
}

GameServices.prototype.submitScore = function(options, success, error) {
  cordova.exec(success, error, "GameServices", "submitScore", [options]);
}

GameServices.prototype.submitScoreNow = function(options, success, error) {
  cordova.exec(success, error, "GameServices", "submitScoreNow", [options]);
}

GameServices.prototype.getPlayerScore = function(options, success, error) {
  cordova.exec(success, error, "GameServices", "getPlayerScore", [options]);
}

GameServices.prototype.getTopScores = function(options, success, error) {
  cordova.exec(success, error, "GameServices", "getTopScores", [options]);
}

GameServices.install = function () {
  if (!window.plugins) {
    window.plugins = {};
  }

  window.plugins.gameservices = new GameServices();
  return window.plugins.gameservices;
}

cordova.addConstructor(GameServices.install);
