function GameServices() {}

GameServices.prototype.login = function(success, error) {
    cordova.exec(success, error, "GameServices", "login");
};

GameServices.install = function () {
  if (!window.plugins) {
    window.plugins = {};
  }

  window.plugins.gameservices = new GameServices();
  return window.plugins.gameservices;
}

cordova.addConstructor(GameServices.install);
