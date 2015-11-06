//
//  GWSdkBLEwrapper.m
//  giz
//
//  Created by 陈东东 on 15/11/5.
//  Copyright © 2015年 陈东东. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <GizDataAccess/GizDataAccess.h>
#import <Cordova/CDV.h>

@interface GWSdkBLEwrapper: CDVPlugin<GizDataAccessSourceDelegate> {
    // Member variables go here.
    NSString * _appId;
    GizDataAccessSource* gdaSource ;
    GizDataAccessLogin* gdaLogin;
}
-(void)login:(CDVInvokedUrlCommand *)command;
-(void)writeData:(CDVInvokedUrlCommand *)command;
-(void)readData:(CDVInvokedUrlCommand *)command;
@property (strong,nonatomic) CDVInvokedUrlCommand * commandHolder;

@end
@implementation GWSdkBLEwrapper



-(void)pluginInitialize:(CDVInvokedUrlCommand *) command{
    gdaSource = [[GizDataAccessSource alloc] initWithDelegate:self];
    //gdaLogin = [[GizDataAccessLogin alloc] initWithDelegate:self];

}

-(void)login:(CDVInvokedUrlCommand *)command
{
  self.commandHolder = command;

    [gdaLogin loginAnonymous];
}
- (void)gizDataAccessDidLogin:(GizDataAccessLogin *)login uid:(NSString *)uid token:(NSString *)token result:(GizDataAccessErrorCode)result message:(NSString *)message {
    if(result == kGizDataAccessErrorNone) {
        // 登录成功
        // ……
        NSLog(@"登录成功");
         NSDictionary *d = [NSDictionary dictionaryWithObjectsAndKeys:
                                   @"登陆成功", @"state",
                                   nil];
          CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:d];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:self.commandHolder.callbackId];
    }
}
/**
 支持批量数据上传。最后一个参数data为数组类型，可以指定每一组数据的产生时间和内容，格式为标准的JSON。上传结果，通过数据上传的委托返回。
 
 data示例： @{@"ts": [timestamp], @"attrs": @{ [dynamic_keys]: [dynamic_values], ... } }

 */
-(void)writeData:(CDVInvokedUrlCommand *)command{
    NSString *token=command.arguments[0];
    NSString *productKey=command.arguments[1];
    NSString *deviceSn=command.arguments[2];
    NSArray *data=command.arguments[3];
    
    [gdaSource saveData:token productKey:productKey deviceSN:deviceSn data:data];
}

- (void)gizDataAccessDidSaveData:(GizDataAccessSource *)source result:(GizDataAccessErrorCode)result message:(NSString *)message {
    if(result == kGizDataAccessErrorNone) {
        // 数据上传成功
        // ……
    }
}

//获取数据时，需指定起止时间段。如果limit值为0将只返回20条数据，若skip值为负数，则获取失败。数据获取结果，通过获取数据的委托返回。获取到的数据，按照时间排序，最新的数据排在最前面。

-(void)readData:(CDVInvokedUrlCommand *)command{
    NSString *token=command.arguments[0];
    NSString *productKey=command.arguments[1];
    NSString *deviceSn=command.arguments[2];
    int16_t startTime=command.arguments[3];
    int16_t endTime=command.arguments[4];

     [gdaSource loadData:token productKey:productKey deviceSN:deviceSn startTime:startTime endTime:endTime limit:20 skip:0];
}
/*实现获取数据的委托方法
 
 获取到的数据内容，按以下键值对方式提供：
 
 @"attrs" : { [dynamic_keys] : [dynamic_values], ... },
 
 @"uid" : [uid],
 
 @"sn" : [sn],
 
 @"ts" : [ts],
 
 @"product_key" : [product_key]
 */
- (void)gizDataAccessDidLoadData:(GizDataAccessSource *)source data:(NSArray *)data result:(GizDataAccessErrorCode)result errorMessage:(NSString *)message {
    if(result == kGizDataAccessErrorNone) {
        // 获取数据成功
        for(NSDictionary *dict in data) {
            NSString* sn = [dict valueForKey:@"device_sn"];
            NSString* productkey = [dict valueForKey:@"product_key"];
            NSString* uid = [dict valueForKey:@"uid"];
            NSNumber *nTS = [dict valueForKey:@"ts"];
            NSDictionary* attributes = [dict valueForKey:@"attrs"];
            
            NSLog(@"sn:%@ productkey:%@ uid:%@ nTS:%@ attributes:%@", sn, productkey, uid, nTS, attributes);
        }
    }
}
@end

