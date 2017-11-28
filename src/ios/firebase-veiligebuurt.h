#import <Cordova/CDV.h>

@interface FirebaseVeiligebuurtPlugin : CDVPlugin

- (void)logEvent:(CDVInvokedUrlCommand*)command;
- (void)setUserId:(CDVInvokedUrlCommand*)command;
- (void)setUserProperty:(CDVInvokedUrlCommand*)command;
- (void)setEnabled:(CDVInvokedUrlCommand*)command;
- (void)setCurrentScreen:(CDVInvokedUrlCommand*)command;

@end