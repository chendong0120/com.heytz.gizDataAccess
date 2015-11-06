var exec = require('cordova/exec');

exports.login = function (username, success, error) {
    exec(success, error, "GWSdkBLEwrapper", "login", [username]);
};

exports.writeData = function(token,productKey,deviceSn,data,success, error){
    exec(success, error, "GWSdkBLEwrapper", "writeData",[token,productKey,deviceSn,data]);
};

exports.readData = function(token,productKey,deviceSn,startTime,endTime,success, error){
    exec(success, error, "GWSdkBLEwrapper", "readData",[token,productKey,deviceSn,startTime,endTime]);
};
