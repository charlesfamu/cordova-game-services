function GameServices() {}

GameServices.prototype.coolMethod = function(options, success, error) {
    cordova.exec(success, error, "GameServices", "coolMethod", [options]);
};

GameServices.install = function () {
  if (!window.plugins) {
    window.plugins = {};
  }

  window.plugins.gameservices = new GameServices();
  return window.plugins.gameservices;
}

cordova.addConstructor(GameServices.install);
