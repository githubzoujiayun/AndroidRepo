//
//  UIAlertView+MTReady.m
//  MonkeyTalk
//
//  Created by Henry Harris on 6/9/14.
//  Copyright (c) 2014 Gorilla Logic, Inc. All rights reserved.
//

#import "UIAlertView+MTReady.h"
#import <objc/runtime.h>
#import "MonkeyTalk.h"
#import "MTUtils.h"

@implementation UIAlertView (MTReady)

SEL originalSelector;
SEL overrideSelector;

+ (void)load
{
    if([[[UIDevice currentDevice] systemVersion] floatValue] >= 7.0)
    {
        originalSelector = @selector(show);
        overrideSelector = @selector(override_show);
    
        Method originalMethod = class_getInstanceMethod(UIAlertView.class, originalSelector);
        Method replacedMethod = class_getInstanceMethod(UIAlertView.class, overrideSelector);

        method_exchangeImplementations(originalMethod, replacedMethod);
    }
    
}
- (void)override_show
{
    [self performSelector:overrideSelector withObject:nil afterDelay:DBL_MIN];
    
    if ([MTUtils isMinimumVersion:@"8.0"]) {
        [MonkeyTalk sharedMonkey].currentAlertView = self;
    }
}
@end
