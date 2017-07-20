function GameServices() {}

GameServices.prototype.login = function(options, success, error) {
    cordova.exec(success, error, "GameServices", "login", [options]);
};

GameServices.install = function () {
  if (!window.plugins) {
    window.plugins = {};
  }

  window.plugins.gameservices = new GameServices();
  return window.plugins.gameservices;
}

cordova.addConstructor(GameServices.install);
