//
//  UIViewController+MTReady.m
//  MonkeyTalk
//
//  Created by Kyle Balogh on 10/14/13.
//  Copyright (c) 2013 Gorilla Logic, Inc. All rights reserved.
//

#import "UIViewController+MTReady.h"
#import "MonkeyTalk.h"
#import "NSObject+MTReady.h"
#import "MTUtils.h"

@implementation UIViewController (MTReady)
+ (void)load {
    if (self == [UIViewController class]) {
        // previously swizzled viewDidAppear: to reset -[MonkeyTalk isPushingController]
        // now we do this in UINavigationController+MTReady
        
        if ([MTUtils isMinimumVersion:@"8.0"]) {
            [NSObject swizzle:@"presentViewController:animated:completion:" with:@"mtPresentViewController:animated:completion:" for:self];
        }
    }
}

- (void)mtPresentViewController:(UIViewController *)viewControllerToPresent animated:(BOOL)flag completion:(void (^)(void))completion {
    [self mtPresentViewController:viewControllerToPresent animated:flag completion:^{
        if (completion) {
            completion();
        }
        [MonkeyTalk sharedMonkey].isPushingController = NO;
    }];
    [MonkeyTalk sharedMonkey].isPushingController = YES;
}
@end
