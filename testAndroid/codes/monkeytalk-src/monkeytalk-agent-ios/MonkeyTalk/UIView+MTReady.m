/*  MonkeyTalk - a cross-platform functional testing tool
 Copyright (C) 2012 Gorilla Logic, Inc.

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */

#import "UIView+MTReady.h"
#import "UISwitch+MTReady.h"
#import <UIKit/UIKit.h>
#import <objc/runtime.h>
#import "MTCommandEvent.h"
#import "MonkeyTalk.h"
#import "UIControl+MTready.h"
#import "TouchSynthesis.h"
#import "MTUtils.h"
#import "UITabBarButtonProxy.h"
#import "UIToolbarTextButtonProxy.h"
#import "UIPushButtonProxy.h"
#import "UISegmentedControlProxy.h"
#import "UITableViewCellContentViewProxy.h"
#import "MTVerifyCommand.h"
#import "MTGestureCommand.h"
#import "NSString+MonkeyTalk.h"
#import "MTGetVariableCommand.h"
#import "MTOrdinalView.h"
#import <QuartzCore/QuartzCore.h>
#import "UIGestureRecognizerProxy.h"
#import "UIGestureRecognizer+MTReady.h"
#import "MTConvertType.h"
#import "NSRegularExpression+MonkeyTalk.h"
#import "UITabBar+MTReady.h"

@implementation UIView (MTReady)
static NSArray* privateClasses;
static NSArray* ignoreClasses;

+ (void)load {
	if (self == [UIView class]) {
		// These are private classes that receive UI events, but the corresponding public class is a superclass. We'll record the event on the first superclass that's public.
		// This might be a config file someday
		privateClasses = [[NSArray alloc] initWithObjects:@"UIPickerTable", @"UITableViewCellContentView",
						  @"UITableViewCellDeleteConfirmationControl", @"UITableViewCellEditControl", @"UIAutocorrectInlinePrompt", nil];
        ignoreClasses = [[NSArray alloc] initWithObjects:@"UIKeyboardLayoutStar", @"UIPickerTableViewWrapperCell", @"_UIWebViewScrollView",
                         @"UIWebBrowserView", @"UITextEffectsWindow", @"UIPickerTableViewTitledCell",
                         @"_UISwitchInternalViewNeueStyle1", nil];

        Method originalMethod = class_getInstanceMethod(self, @selector(initWithFrame:));
        Method replacedMethod = class_getInstanceMethod(self, @selector(mtInitWithFrame:));
        method_exchangeImplementations(originalMethod, replacedMethod);

        Method gestureMethodOriginal = class_getInstanceMethod(self, @selector(addGestureRecognizer:));
        Method gestureMethodReplaced = class_getInstanceMethod(self, @selector(mtAddGestureRecognizer:));
        method_exchangeImplementations(gestureMethodOriginal, gestureMethodReplaced);
	}
}

- (void) mtAssureAutomationInit {

}

- (BOOL)shouldCallResponderTouches {
    NSArray *ignored = @[@"_UIAlertControllerActionView"];
    
    for (NSString *class in ignored) {
        if (self.class == NSClassFromString(class)) {
            return NO;
        }
    }
    
    return YES;
}

- (id)mtInitWithFrame:(CGRect)aRect {

	// Should be able to move this whole thing into mtAssureAutomationInit

	// This is actually for UIControl, but UIControl inherits this method
	// Calls original initWithFrame (that we swapped in load method)
    [self mtInitWithFrame:aRect];
	if (self) {
		if ([self isKindOfClass:[UIControl class]]) {
			[(UIControl*)self performSelector:@selector(subscribeToMonkeyEvents)];
		}
	}

	return self;

}

- (BOOL)shouldIgnoreTouch {
    if ([self isKindOfClass:NSClassFromString(@"_UIAlertControllerActionView")]) {
        return NO;
    }
    
    for (NSString *class in ignoreClasses) {
        // Ignore taps on picker cell
        // Ignore web components
        // Ignore taps on keyboard and other text effect overlay windows
        if ([self isKindOfClass:objc_getClass([class UTF8String])]) {
            return YES;
        }

        // Ignore taps on views inside collectionviews if they are not buttons
        // and do not have any gesture recognizers
        UIView *superView = self.superview;
        while (superView) {
            if (superView.gestureRecognizers.count == 0 &&
                [superView isKindOfClass:NSClassFromString(@"UICollectionViewCell")] &&
                ![self isKindOfClass:[UIButton class]]) {
                return YES;
            }

            superView = superView.superview;
        }
    }
    return NO;
}

- (void) handleMonkeyTouchEvent:(NSSet*)touches withEvent:(UIEvent*)event {

	// Test for special UI classes that require special handling of record
    if ([self shouldIgnoreTouch]) {
        NSLog(@"ignore: %@",self);
        return;
    }

    if ([self isKindOfClass:[UIButton class]] &&
        [self.superview isKindOfClass:[UITableViewCell class]]) {
        // Is an accessory button to be handled by UITableView
        UITouch* touch = [touches anyObject];
        UITableViewCell *cell = (UITableViewCell *)self.superview;
        UITableView *tableView = (UITableView *)cell.superview;
        NSString *row = [NSString stringWithFormat:@"%i",[tableView indexPathForCell:cell].row + 1];
        NSString *section = [NSString stringWithFormat:@"%i",[tableView indexPathForCell:cell].section + 1];
        NSMutableArray *args = [[NSMutableArray alloc] init];

        [args addObject:row];

        if ([tableView indexPathForCell:cell].section > 0)
            [args addObject:section];

        if (touch.phase == UITouchPhaseEnded)
            [MonkeyTalk recordFrom:tableView command:MTCommandSelectIndicator args:args];

        return;
    }else if ([self isKindOfClass:objc_getClass("UITableViewCellReorderControl")]) {
        // Do not record tap on reorder control component
        return;
    } else {
		// DEFAULT
		// By default we simply record that they touched the view
		UITouch* touch = [touches anyObject];

		if (touch.phase == UITouchPhaseMoved) {
            if ([self isKindOfClass:objc_getClass("UITableViewCellContentView")]) {
                return;
            }
            if (!([self isKindOfClass:[UITableView class]] || [self isKindOfClass:[UITextView class]])) {
                CGPoint loc = [touch locationInView:self];
                MTCommandEvent* command = [[MonkeyTalk sharedMonkey] lastCommandPosted];
                if (([command.command isEqualToString:MTCommandMove ignoreCase:YES]  ||
                     [command.command isEqualToString:MTCommandDrag ignoreCase:YES])
                    && [command.monkeyID isEqualToString:[self monkeyID]]) {
                    [[MonkeyTalk sharedMonkey] deleteCommand:[[MonkeyTalk sharedMonkey] commandCount] - 1];
                    NSMutableArray* args = [NSMutableArray arrayWithArray:command.args];
                    [args addObjectsFromArray:[NSArray arrayWithObjects:[NSString stringWithFormat:@"%1.0f", loc.x],
                                               [NSString stringWithFormat:@"%1.0f", loc.y],
                                               nil]];

                    MTCommandEvent *moveEvent = [[MTCommandEvent alloc]
                                                 init:MTCommandDrag className:[NSString stringWithUTF8String:class_getName([self class])]
                                                 monkeyID:[self monkeyID]
                                                 args:args];

                    [MonkeyTalk buildCommand:moveEvent];
                    return;
                }

                else {
                    NSMutableArray* args = [NSMutableArray arrayWithArray:command.args];
                    [args addObjectsFromArray:[NSArray arrayWithObjects:[NSString stringWithFormat:@"%1.0f", loc.x],
                                               [NSString stringWithFormat:@"%1.0f", loc.y],
                                               nil]];

                    MTCommandEvent *moveEvent = [[MTCommandEvent alloc]
                                                 init:MTCommandDrag className:[NSString stringWithUTF8String:class_getName([self class])]
                                                 monkeyID:[self monkeyID]
                                                 args:args];

                    [MonkeyTalk buildCommand:moveEvent];
                    return;
                }
            }
		} else if (touch.phase == UITouchPhaseBegan) {
            // Added if condition inorder to avoid recording a tap event on uipicker view since the self object
            // was holding the UITableViewCellContentView.

            if ([self.superview isKindOfClass:objc_getClass("UIPickerTableViewTitledCell")]) {
                [MonkeyTalk sharedMonkey].currentTapCommand = nil;
                return;
            }

            // We ignore recording of tap begin events on uitableviewcell
            else if ([self.superview isKindOfClass:objc_getClass("UITableViewCell")])
            {
                [MonkeyTalk sharedMonkey].currentTapCommand = nil;
                return;
            }
            else
            {

                [MonkeyTalk sharedMonkey].currentTapCommand = [[MTCommandEvent alloc]
                                                               init:MTCommandTap className:[NSString stringWithUTF8String:class_getName([self class])]
                                                               monkeyID:[self monkeyID]
                                                               args:nil];
                return;
            }
        } else if (touch.phase == UITouchPhaseCancelled) {
            [MonkeyTalk sharedMonkey].currentTapCommand = nil;
        } else if (touch.phase == UITouchPhaseEnded) {

            CGPoint loc = [touch locationInView:self];
            NSUInteger touchCount = [touch tapCount];

            if ([self.superview isKindOfClass:objc_getClass("UITableViewCellScrollView")]) {
                // ios 7 UITableViewCellContentView
                UITableViewCell *cell = (UITableViewCell *)self;
                while (![cell isKindOfClass:[UITableViewCell class]]) {
                    if (!self.superview) {
                        break;
                    }
                    cell = (UITableViewCell *)cell.superview;
                }

                [cell handleMonkeyTouchEvent:touches withEvent:event];
                [MonkeyTalk sharedMonkey].currentTapCommand = nil;
                return;
            }


            if ([self.superview isKindOfClass:objc_getClass("UIPickerTableViewTitledCell")]) {
                [MonkeyTalk sharedMonkey].currentTapCommand = nil;
                return;
            }

            else if ([self.superview isKindOfClass:objc_getClass("UITableViewCell")])
            {
                [self.superview handleMonkeyTouchEvent:touches withEvent:event];
                [MonkeyTalk sharedMonkey].currentTapCommand = nil;
                return;
            }

            // Handle Tap
            // MT6: No coordinates for tap
            NSMutableArray* args = nil;
            if (touch.tapCount == 1 || touch.tapCount == 2) {
                args = [NSMutableArray arrayWithObject:[NSString stringWithFormat:@"%1.0d", touch.tapCount]];
            }



            if ([self isKindOfClass:objc_getClass("UITabBarButton")]) {
                UITabBarButtonProxy* but = (UITabBarButtonProxy *)self;
                NSString* label = nil;
                UILabel *buttonLabel = [MTUtils isOs5Up] ? nil : (UILabel *)but->_label;

                // Fixes error in iOS5+ caused by UITabBarSwappableImageView
                if (![MTUtils isOs5Up] && [buttonLabel respondsToSelector:@selector(text)]) {
                    UILabel *buttonLabel = (UILabel *)but->_label;
                    label = [buttonLabel text];
                } else {
                    for (UILabel *foundLabel in [but subviews]) {
                        if ([foundLabel isKindOfClass:objc_getClass("UITabBarButtonLabel")])
                            label = foundLabel.text;
                    }
                }

                UITabBar *tabBar = (UITabBar *)self.superview;

                if ([tabBar isKindOfClass:[UITabBar class]]) {
                    [tabBar handleTabBar:tabBar];
                    return;
                }

            } else if ([self isKindOfClass:objc_getClass("UISwitch")] ||
                       [self isKindOfClass:objc_getClass("_UISwitchInternalView")]) {
                UISwitch *aSwitch = nil;

                if ([self isKindOfClass:objc_getClass("_UISwitchInternalView")])
                    aSwitch = (UISwitch *)self.superview;
                else
                    aSwitch = (UISwitch *)self;

                [aSwitch handleSwitchTouchEvent:touches withEvent:event];
            }
            else {
                MTCommandEvent* command = [[MonkeyTalk sharedMonkey] lastCommandPosted];

                if (command.args.count > 0 && [command.command isEqualToString:MTCommandDrag]) {
                    [MonkeyTalk recordFrom:self command:MTCommandDrag args:command.args];
                    [[MonkeyTalk sharedMonkey].commands removeAllObjects];
                }
                else
                {
                    switch (touchCount)
                    {
                        case 1:
                            // Added the code to record tap coordinates.
                            args = [NSMutableArray arrayWithArray:@[[NSString stringWithFormat:@"%1.0f",loc.x],
                                                                    [NSString stringWithFormat:@"%1.0f",loc.y]]];
                            [self performSelector:@selector(mtHandleSingleTap:) withObject:args afterDelay:.2];
                            break;
                        case 2:
                            args = [NSMutableArray arrayWithArray:@[[NSString stringWithFormat:@"%1.0f",loc.x],
                                                                    [NSString stringWithFormat:@"%1.0f",loc.y]]];
                            [NSObject cancelPreviousPerformRequestsWithTarget:self selector:@selector(mtHandleSingleTap:) object:args];
                            [self performSelector:@selector(mtHandleDoubleTap:) withObject:args afterDelay:.2];
                            break;

                        default:
                            break;
                    }

                }
            }
            return;
        }
	} //End Default record operations
}

-(void) mtHandleSingleTap: (NSArray *) args
{
    // Use the monkeyID from when touch began
    if ([MonkeyTalk sharedMonkey].currentTapCommand) {
        [MonkeyTalk sendRecordEvent:[MonkeyTalk sharedMonkey].currentTapCommand];
        [MonkeyTalk sharedMonkey].currentTapCommand = nil;
    } else {
        [MonkeyTalk recordFrom:self command:MTCommandTap args:args];
    }
}

-(void) mtHandleDoubleTap:(NSArray *) args
{
    [MonkeyTalk recordFrom:self command:MTCommandDoubleTap args:args];
}

- (void) handleMonkeyMotionEvent:(UIEvent*)event {
	[MonkeyTalk recordFrom:nil command:MTCommandShake];
}

- (BOOL) shouldRecordMonkeyTouch:(UITouch*)touch {
	// By default, we only record TouchEnded
    //	return (touch.phase == UITouchPhaseEnded);
    return YES;
}

- (void) playbackMonkeyEvent:(id)event {
	// We should actually call this on all components from up in the run loop
	[self mtAssureAutomationInit];

	// By default we generate a touch in the center of the view
	MTCommandEvent* ev = event;

    // DEFAULT
    if ([[ev.command lowercaseString] rangeOfString:@"verify"].location != NSNotFound) {
        [MTVerifyCommand handleVerify:ev];
        return;
    } else if ([ev.command isEqualToString:MTCommandGet ignoreCase:YES]) {
        [MTGetVariableCommand execute:ev];
        return;
    } else if ([ev.command isEqualToString:@"find" ignoreCase:YES]) {
        CGFloat originalWidth = self.layer.borderWidth;
        CGColorRef originalColor = self.layer.borderColor;

        self.layer.borderWidth = 4;
        self.layer.borderColor = [UIColor colorWithRed:6/255.0 green:102/255.0 blue:214/255.0 alpha:1].CGColor;

        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            [NSThread sleepForTimeInterval:1];

            dispatch_async(dispatch_get_main_queue(), ^{
                self.layer.borderWidth = originalWidth;
                self.layer.borderColor = originalColor;
            });
        });
        return;
    }

    if ([ev.command isEqualToString:MTCommandMove ignoreCase:YES]) {
        int i;
        CGPoint prevPoint;
        for (i = 0; i < ([ev.args count]); i += 2) {
            CGPoint point;
            point.x = [[ev.args objectAtIndex:i] floatValue];
            point.y = [[ev.args objectAtIndex:i+1] floatValue];
            if (i == 0) {
                prevPoint = point;
            }

            [UIEvent performMoveInView:self from:prevPoint to:point];

            prevPoint = point;
        }
        return;
    } else if ([ev.command isEqualToString:MTCommandTouchDown ignoreCase:YES]) {
        CGPoint point;
        point.x = [[ev.args objectAtIndex:0] floatValue];
        point.y = [[ev.args objectAtIndex:1] floatValue];
        [UIEvent performTouchBeganInView:self atPoint:point];

        return;
    } else if ([ev.command isEqualToString:MTCommandTouchUp ignoreCase:YES]) {
        CGPoint point;
        point.x = [[ev.args objectAtIndex:0] floatValue];
        point.y = [[ev.args objectAtIndex:1] floatValue];
        [UIEvent performTouchUpInView:self at:point];

        return;
    } else if ([ev.command isEqualToString:MTCommandSwipe ignoreCase:YES]) {
        [MTGestureCommand handleSwipe:ev];
        return;
    } else if ([ev.command isEqualToString:MTCommandPinch ignoreCase:YES]) {
        [MTGestureCommand handlePinch:ev];
        return;
    } else if ([ev.command isEqualToString:MTCommandDrag ignoreCase:YES]) {
        [UIGestureRecognizer playbackMonkeyEvent:ev];
        return;
    } else if ([self isKindOfClass:objc_getClass("UITabBarButton")] ||
               [self isKindOfClass:objc_getClass("UITabBar")]) {
        UITabBar *tabBar = nil;

        if ([self isKindOfClass:objc_getClass("UITabBar")])
            tabBar = (UITabBar *)self;
        else
            tabBar = (UITabBar *)self.superview;

        if ([tabBar isKindOfClass:[UITabBar class]]) {
            [tabBar playbackTabBarEvent:event];
        }

        return;
    } else if ([ev.command isEqualToString:MTCommandTap ignoreCase:YES]) {
		if([self.superview isKindOfClass:objc_getClass("UIAlertView")] &&
           UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad &&
           [[[UIDevice currentDevice] systemVersion] floatValue] < 7.0) {
            UIInterfaceOrientation orientation = [[UIApplication sharedApplication] statusBarOrientation];
            if(orientation == UIDeviceOrientationLandscapeLeft || orientation == UIDeviceOrientationLandscapeRight)
            {
                [self showTapAtLocation:CGPointMake(self.frame.size.width/2, self.frame.size.height/2)];
                UIAlertView *alertView = (UIAlertView *)self.superview;
                id<UIAlertViewDelegate> delegate = alertView.delegate;
                [alertView dismissWithClickedButtonIndex:(self.tag - 1) animated:YES];
                [delegate alertView:alertView clickedButtonAtIndex:(self.tag - 1)];
                return;
            }
        } else if ([self isKindOfClass:NSClassFromString(@"_UIAlertControllerActionView")] ||
                   (self.superview.superview && [self.superview.superview isKindOfClass:NSClassFromString(@"_UIAlertControllerActionView")])) {
            // Using self.superview.superview for labels inside alert view
            UIAlertView *alertView = [MonkeyTalk sharedMonkey].currentAlertView;
            if (alertView) {
                UICollectionView *collectionView = nil;
                UICollectionViewCell *cell = nil;
                
                UIResponder *nextResponder = self.nextResponder;
                
                while (nextResponder) {
                    nextResponder = nextResponder.nextResponder;
                    
                    if ([nextResponder isKindOfClass:[UICollectionViewCell class]]) {
                        cell = (UICollectionViewCell *)nextResponder;
                    }
                    
                    if ([nextResponder isKindOfClass:[UICollectionView class]]) {
                        collectionView = (UICollectionView *)nextResponder;
                        break;
                    }
                }
                
                if (collectionView && cell) {
                    NSIndexPath *indexPath = [collectionView indexPathForCell:cell];
                    [self showTapAtLocation:CGPointMake(self.frame.size.width/2, self.frame.size.height/2)];
                    
                    id<UIAlertViewDelegate> delegate = alertView.delegate;
                    
                    if ([delegate respondsToSelector:@selector(alertView:clickedButtonAtIndex:)]) {
                        [delegate alertView:alertView clickedButtonAtIndex:indexPath.item];
                    }
                    
                    [alertView dismissWithClickedButtonIndex:indexPath.item animated:YES];
                    
                    [MonkeyTalk sharedMonkey].currentAlertView = nil;
                    return;
                }
            }
        }
        CGPoint point;
        if ([ev.args count] == 1) {
            NSInteger touchCount = [[ev.args objectAtIndex:0] intValue];
            point.x = self.frame.size.width/2;
            point.y = self.frame.size.height/2;

            [self showTapAtLocation:point];

            [UIEvent performTouchInView:self at:point withCount:touchCount];
        }
        else {
            point.x = ev.args.count > 0 && [ev.args objectAtIndex:0] ? [[ev.args objectAtIndex:0] floatValue] : self.frame.size.width/2;
            point.y = ev.args.count > 1 && [ev.args objectAtIndex:1] ? [[ev.args objectAtIndex:1] floatValue] : self.frame.size.height/2;

            [self showTapAtLocation:point];

            [UIEvent performTouchInView:self at:point withCount:1];
        }
        return;
    }
    else if ([ev.command isEqualToString:MTCommandDoubleTap ignoreCase:YES]) {
        CGPoint point;
        if ([self isKindOfClass:[UIImageView class]])
        {
            [UIGestureRecognizer playbackMonkeyEvent:ev];
            return;
        }
        else
        {
            if (([ev.args count]) > 1) {
                NSInteger touchCount = 2;
                point.x = [[ev.args objectAtIndex:0] floatValue];
                point.y = [[ev.args objectAtIndex:1] floatValue];
                [UIEvent performTouchInView:self at:point withCount:touchCount];
            }
            else
                ev.lastResult = @"Requires 1 or 2 arguments (x and y Coordinates)";
            return;
        }
    }
    else if ([ev.command isEqualToString:MTCommandLongPress ignoreCase:YES]) {
        [UIGestureRecognizer playbackMonkeyEvent:ev];
        return;
    }

        // Try to execute the command on class
        [[MonkeyTalk sharedMonkey] playbackExecMethod:ev];
}

- (void) removeView:(UIView *)view {
    [view removeFromSuperview];
}

- (BOOL) isMTEnabled {
    // Record containers if they have gesture recognizers
    if ([self.gestureRecognizers count] > 0)
        return YES;

	// Don't record private classes
	for (NSString* className in privateClasses) {
		if ([self isKindOfClass:objc_getClass([className UTF8String])]) {
			return NO;
		}
	}

	// Don't record containers
	return ![self isMemberOfClass:[UIView class]] && ![MTUtils isKeyboard:self];
}

- (NSString *) keyForMonkeyId {
    return [NSString stringWithFormat:@"%@",self];
}

- (BOOL) hasMonkeyIdAssigned {
    return [[MonkeyTalk sharedMonkey].componentMonkeyIds objectForKey:[self keyForMonkeyId]];
}

- (NSArray*) rawMonkeyIDCandidates {
    NSMutableArray* candidates=[NSMutableArray arrayWithCapacity:3];
    NSString *varName = [self iVarName];

    if ([self respondsToSelector:@selector(accessibilityIdentifier)]) {
        NSString* candidate=self.accessibilityIdentifier;
        if (candidate!= nil && [candidate length] > 0) {
            [candidates addObject:candidate];
        }
    }

    if ([self respondsToSelector:@selector(accessibilityLabel)]) {
        NSString* candidate=self.accessibilityLabel;
        if (candidate!= nil && [candidate length] > 0) {
            [candidates addObject:candidate];
        }
    }

    if (self.tag !=0) {
        NSString* candidate=[NSString stringWithFormat:@"%ld",(long)self.tag];
        if (candidate!= nil && [candidate length] > 0) {
            [candidates addObject:candidate];
        }
    }

    if (varName) {
        [candidates addObject:varName];
    }

    return candidates;
}

- (NSString*) monkeyID {
	NSString *currentID = [self baseMonkeyID];

    // Ignoring TextInputTraits class while getting monkeyID; returning nil;
    if ([currentID isKindOfClass:NSClassFromString(@"UITextInputTraits")]) {
        return nil;
    }

    if ([currentID rangeOfString:@"#mt"].location == 0) {
        NSArray *views = [self.class orderedViews];
        NSInteger ordinal = [views indexOfObject:[NSValue valueWithNonretainedObject:self]]+1;
        return [NSString stringWithFormat:@"#%i",ordinal];
    }

    NSArray *views = [self.class orderedViewsWithMonkeyId:currentID];
    NSInteger index = [views indexOfObject:[NSValue valueWithNonretainedObject:self]]+1;
    if (index > 1) {
        currentID = [currentID stringByAppendingFormat:@"(%i)",index];
    }

	return currentID;
}

-(NSString *)propertyName:(id)property parent:(id)parent {
    unsigned int numIvars = 0;
    NSString *key=nil;
    Ivar * ivars = class_copyIvarList([parent class], &numIvars);

    for(int i = 0; i < numIvars; i++) {
        Ivar thisIvar = ivars[i];

        if ([[NSString stringWithUTF8String:ivar_getTypeEncoding(thisIvar)].lowercaseString rangeOfString:@"@"].location == NSNotFound) {
            continue;
        }

        id iv = object_getIvar(parent, thisIvar);

        if (iv == property) {
            key = [NSString stringWithUTF8String:ivar_getName(thisIvar)];
            break;
        }
    }
    free(ivars);
    return key;
}

- (NSString *)iVarName {
    UIResponder *parent = self.nextResponder;
    NSString *varName = nil;

    while (parent) {
        parent = parent.nextResponder;
        varName = [self propertyName:self parent:parent];

        if (varName) {
            break;
        }
    }

    return varName;
}

// the MonkeyID sans ordinal
- (NSString *) baseMonkeyID {
    if ([self isKindOfClass:objc_getClass("UITabBarButton")]) {
		UITabBarButtonProxy* but = (UITabBarButtonProxy *)self;
		NSString* label = nil;
        UILabel *buttonLabel = [MTUtils isOs5Up] ? nil : (UILabel *)but->_label;

        // Fixes error in iOS5+ caused by UITabBarSwappableImageView
        if (![MTUtils isOs5Up] && [buttonLabel respondsToSelector:@selector(text)])
            label = [buttonLabel text];
        else {
            for (UILabel *foundLabel in [but subviews]) {
                if ([foundLabel isKindOfClass:objc_getClass("UITabBarButtonLabel")])
                    label = foundLabel.text;
            }
        }
		if (label != nil) {
			return label;
		}
	} else if ([self isKindOfClass:objc_getClass("UIToolbarTextButton")]) {
		UIToolbarTextButtonProxy* but = (UIToolbarTextButtonProxy *)self;

        // _info doesn't seem to be available in iOS5+
		if (![MTUtils isOs5Up] && [but->_info isKindOfClass:objc_getClass("UIPushButton")]) {
			NSString*label = [(UIPushButtonProxy *)but->_info title];

            return label;
		}

        for (UIView *found in but.subviews) {
            if ([found isKindOfClass:[UIButton class]]) {
                UIButton *button = (UIButton *)found;
                return button.titleLabel.text;
            } else if ([found isKindOfClass:[UILabel class]]) {
                UILabel *l = (UILabel *)found;
                return l.text;
            }
        }
	} else if ([self isKindOfClass:objc_getClass("UITableViewCellContentViewProxy")]) {
		UISegmentedControlProxy *but = (UISegmentedControlProxy *)self;
		NSMutableString* label = [[NSMutableString alloc] init];
		int i;
		for (i = 0; i < [but numberOfSegments]; i++) {
			NSString* title = [but titleForSegmentAtIndex:i];
			if (title == nil) {
				goto use_default;
			}
			[label appendString:title];
		}
		return label;
	}

use_default:;

    NSArray* rawCandidates = [self rawMonkeyIDCandidates];
    for (int rawNdx=0; rawNdx<rawCandidates.count; rawNdx++) {
        NSString* candidate=rawCandidates[rawNdx];
        if (candidate !=nil && candidate.length>0) {
            return candidate;
        }
    }

    return [[MonkeyTalk sharedMonkey] monkeyIDfor:self];
}

- (NSInteger) mtOrdinal {
    if ([self hasMonkeyIdAssigned]) {
        NSDictionary *monkeyDict = [[MonkeyTalk sharedMonkey].componentMonkeyIds objectForKey:[self keyForMonkeyId]];
        NSInteger ordinal = [[monkeyDict objectForKey:@"ordinal"] integerValue];

        return ordinal;
    }

    return [MTOrdinalView componentOrdinalForView:self withMonkeyID:nil];
}

- (BOOL) swapsWith:(NSString*)className {
	if ([self isKindOfClass:objc_getClass("UIToolbarTextButton")] && [className isEqualToString:@"UINavigationButton"]) {
		return YES;
	}

	if ([self isKindOfClass:objc_getClass("UINavigationButton")] && [className isEqualToString:@"UIToolbarTextButton"]) {
		return YES;
	}

	return NO;

}


+ (NSString*) objcCommandEvent:(MTCommandEvent*)command {

	NSMutableString* args = [[NSMutableString alloc] init];
	if (!command.args) {
		[args setString:@"nil"];
	} else {
		[args setString:@"[NSArray arrayWithObjects:"];
		NSString* arg;
		for (arg in command.args) {
			[args appendFormat:@"@\"%@\", ", [MTUtils stringByOcEscapingQuotesAndNewlines:arg]];
		}
		[args appendString:@"nil]"];
	}

	return [NSString stringWithFormat:@"[MTCommandEvent command:@\"%@\" className:@\"%@\" monkeyID:@\"%@\" delay:@\"%@\" timeout:@\"%@\" args:%@]", command.command, command.className, command.monkeyID, command.playbackDelay, command.playbackTimeout, args];

}

+ (NSString*) qunitCommandEvent:(MTCommandEvent*)command {

	NSMutableString* args = [[NSMutableString alloc] init];
	if (!command.args) {
		[args setString:@"null"];
	} else {
		//[args setString:@"["];
        for (int i = 0; i < [command.args count]; i++) {
            if (i == [command.args count]-1)
                [args appendFormat:@"\"%@\"", [MTUtils stringByOcEscapingQuotesAndNewlines:[command.args objectAtIndex:i]]];
            else
                [args appendFormat:@"\"%@\", ", [MTUtils stringByOcEscapingQuotesAndNewlines:[command.args objectAtIndex:i]]];
        }
		//[args appendString:@"]"];
	}

    if ([args length] == 0)
        [args appendString:@"null"];

	return [NSString stringWithFormat:@"\"%@\", \"%@\", \"%@\", \"%@\", \"%@\", %@", command.command, command.className, command.monkeyID, command.playbackDelay, command.playbackTimeout, args];

}

- (void) mtAddGestureRecognizer:(UIGestureRecognizer *)gestureRecognizer {
    // Ordinals not working with gestures (private classes)

    [self mtAddGestureRecognizer:gestureRecognizer];
}

- (UIWebView *) currentWebView
{
    for (UIView *subview in [MonkeyTalk allComponents])
    {
        if ([subview isKindOfClass:[UIWebView class]])
            return (UIWebView *) subview;
    }

    return nil;
}


- (void) showTapAtLocation:(CGPoint)touchCenter {
    UIWindow *rootWindow = [MTUtils rootWindow];
    touchCenter = [self convertPoint:touchCenter toView:rootWindow];

    UIView *touchView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 20, 20)];
    touchView.layer.cornerRadius = 10;
    touchView.backgroundColor = [UIColor colorWithRed:6/255.0 green:102/255.0 blue:214/255.0 alpha:1];
    touchView.center = touchCenter;
    touchView.transform = CGAffineTransformMakeScale(1, 1);

    [rootWindow addSubview:touchView];

    [UIView animateWithDuration:0.4 animations:^{
        touchView.alpha = 0;
        touchView.transform = CGAffineTransformMakeScale(3, 3);
    } completion:^(BOOL finished){
        [touchView removeFromSuperview];
    }];
}

- (void)highlightView {
    CGFloat originalWidth = self.layer.borderWidth;
    CGColorRef originalColor = self.layer.borderColor;

    self.layer.borderWidth = 4;
    self.layer.borderColor = [UIColor colorWithRed:6/255.0 green:102/255.0 blue:214/255.0 alpha:1].CGColor;

    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        [NSThread sleepForTimeInterval:1];

        dispatch_async(dispatch_get_main_queue(), ^{
            self.layer.borderWidth = originalWidth;
            self.layer.borderColor = originalColor;
        });
    });
}

+ (NSString*) uiAutomationCommand:(MTCommandEvent*)command {
    return nil;
}

@end
