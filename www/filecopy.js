var exec = require("cordova/exec");

exports.coolMethod = function (uri, path, success, error) {
  exec(success, error, "filecopy", "coolMethod", [uri, path]);
};
