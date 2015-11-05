var exec = require('cordova/exec');

exports.login = function (ssid, wifikey, appId, success, error) {
    exec(success, error, "GizDataAccess", "login", [username, wifikey, appId]);
};

exports.writeData = function(token,productKey,deviceSn,data,success, error){
    exec(success, error, "GizDataAccess", "writeData",[token,productKey,deviceSn,data]);
};

exports.readData = function(token,productKey,deviceSn,startTime,endTime,success, error){
    exec(success, error, "GizDataAccess", "readData",[token,productKey,deviceSn,startTime,endTime]);
};
