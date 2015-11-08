var exec = require('cordova/exec');

exports.login = function (appid,username,password, success, error) {
    exec(success, error, "GWSdkBLEwrapper", "login", [appid,username,password]);
};

exports.writeData = function(appid,token,productKey,deviceSn,data,success, error){
    exec(success, error, "GWSdkBLEwrapper", "writeData",[appid,token,productKey,deviceSn,data]);
};

exports.readData = function(appid,token,productKey,deviceSn,startTime,endTime,limit,skip,success, error){
    exec(success, error, "GWSdkBLEwrapper", "readData",[appid,token,productKey,deviceSn,startTime,endTime,limit,skip]);
};
