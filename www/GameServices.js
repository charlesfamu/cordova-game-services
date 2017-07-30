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

GameServices.install = function () {
  if (!window.plugins) {
    window.plugins = {};
  }

  window.plugins.gameservices = new GameServices();
  return window.plugins.gameservices;
}

cordova.addConstructor(GameServices.install);
