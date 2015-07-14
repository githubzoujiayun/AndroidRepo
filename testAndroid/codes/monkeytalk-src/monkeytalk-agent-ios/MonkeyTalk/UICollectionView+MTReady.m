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

#import "UICollectionView+MTReady.h"
#import <objc/runtime.h>
#import "MTCommandEvent.h"
#import "MonkeyTalk.h"
#import "UIView+MTReady.h"
#import "NSObject+MTReady.h"
#import "TouchSynthesis.h"
#import "NSString+MonkeyTalk.h"
#import "UIGestureRecognizer+MTReady.h"

@interface UICollectionView (Intercept)
- (void)selectItemAtIndexPath:(NSIndexPath *)indexPath
                     animated:(BOOL)animated
               scrollPosition:(UICollectionViewScrollPosition)scrollPosition;

- (void)originalCollectionView:(UICollectionView *)collectionView
      didSelectItemAtIndexPath:(NSIndexPath *)indexPath;

@end

@implementation UICollectionView (MTReady)

- (NSString *)mtComponent {
    return MTComponentGrid;
}

+ (void)load {
    if (self == [UICollectionView class]) {

        Method originalMethod = class_getInstanceMethod(self, @selector(setDelegate:));
        Method replacedMethod = class_getInstanceMethod(self, @selector(mtSetGridDelegate:));
        method_exchangeImplementations(originalMethod, replacedMethod);
    }
}

- (void)mtSetGridDelegate:(NSObject<UICollectionViewDelegate> *)del {

    [del interceptMethod:@selector(collectionView:didSelectItemAtIndexPath:)
              withMethod:@selector(mtCollectionView:didSelectItemAtIndexPath:)
                 ofClass:[self class]
              renameOrig:@selector(originalCollectionView:didSelectItemAtIndexPath:)
                   types:"v@:@i@"];

    [self mtSetGridDelegate:del];
}

- (NSString *)valueForProperty:(NSString *)prop withArgs:(NSArray *)args {
    NSString *result;
    NSInteger arg1Int = 0;
    NSInteger arg2Int = 0;
    BOOL shouldReturnArray = NO;
    BOOL arrayHasSection = NO;

    // Return items in first row/component by default
    if ([prop isEqualToString:@"value" ignoreCase:YES]) {
        prop = @"item";
        //        shouldReturnArray = YES;
    }

    if ([args count] > 0) {
        for (int i = 0; i < [args count]; i++) {
            NSString *arg = [args objectAtIndex:i];
            if (i == 0 && ([arg isEqualToString:@"[]"] || [arg isEqualToString:@"["])) {
                //                shouldReturnArray = YES;
            } else if (i == 1 && shouldReturnArray && [arg isEqualToString:@"]"]) {
                arrayHasSection = YES;
            }

            arg = [arg stringByReplacingOccurrencesOfString:@"[" withString:@""];
            arg = [arg stringByReplacingOccurrencesOfString:@"]" withString:@""];

            if (i == 0 && [arg intValue] > 0)
                arg1Int = [arg intValue] - 1;
            else if (i == 1 && [arg intValue] > 0)
                arg2Int = [arg intValue] - 1;
        }
    }

    if ([prop isEqualToString:@"size" ignoreCase:YES]) {
        result = [NSString stringWithFormat:@"%i", (int)[self numberOfItemsInSection:arg1Int]];
    } else
        result = [self valueForKeyPath:prop];

    return result;
}

- (void)mtCollectionView:(UICollectionView *)collectionView
    didSelectItemAtIndexPath:(NSIndexPath *)indexPath;
{
    NSString *section = [NSString stringWithFormat:@"%i", (int)indexPath.section + 1];
    NSString *row = [NSString stringWithFormat:@"%i", (int)indexPath.row + 1];
    NSMutableArray *argsArray = [[NSMutableArray alloc] initWithObjects:row, nil];

    if (indexPath.section > 0)
        [argsArray addObject:section];

    [MonkeyTalk recordFrom:collectionView command:MTCommandSelectIndex args:argsArray];

    if ([self respondsToSelector:@selector(originalCollectionView:didSelectItemAtIndexPath:)]) {
        [self originalCollectionView:collectionView didSelectItemAtIndexPath:indexPath];
    }
}

- (void)playbackMonkeyEvent:(MTCommandEvent *)event {
    if ([event.command isEqualToString:MTCommandTap]) {
        [super playbackMonkeyEvent:event];
        return;
    }

    if ([[event args] count] == 0) {
        event.lastResult = [NSString
            stringWithFormat:@"Requires 1 or more arguments, but has %i", (int)[event.args count]];
        return;
    }

    if ([event.command isEqualToString:MTCommandSelectIndex ignoreCase:YES]) {
        NSInteger row = [event.args count] > 0 ? [[event.args objectAtIndex:0] intValue] : 0;
        NSInteger section = [event.args count] > 1 ? [[event.args objectAtIndex:1] intValue] : 0;

        if (row > 0)
            row -= 1;
        if (section > 0)
            section -= 1;

        NSIndexPath *indexPath = nil;

        indexPath = [NSIndexPath indexPathForRow:row inSection:section];

        // Handle errors if we can't find cell
        if (indexPath == nil) {
            event.lastResult = [NSString
                stringWithFormat:@"Could not find cell %@ in collection view with monkeyID %@",
                                 [event.args objectAtIndex:0], event.monkeyID];
            return;
        } else if (section + 1 > [self numberOfSections]) {
            event.lastResult = [NSString
                stringWithFormat:@"Selection out of bounds -- can't select item %i in section %i, "
                                 @"because collection only has %i %@",
                                 (int)row + 1, (int)section + 1, (int)[self numberOfSections],
                                 [NSString pluralStringFor:@"section"
                                                 withCount:[self numberOfSections]]];
            return;
        } else if (row + 1 > [self numberOfItemsInSection:section]) {
            event.lastResult = [NSString
                stringWithFormat:@"Selection out of bounds -- can't select item %i, because "
                                 @"section %i only has %i %@",
                                 (int)row + 1, (int)section + 1,
                                 (int)[self numberOfItemsInSection:section],
                                 [NSString pluralStringFor:@"item"
                                                 withCount:[self numberOfItemsInSection:section]]];
            return;
        }

        BOOL isCellVisible = NO;

        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            if (!isCellVisible) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self scrollToItemAtIndexPath:indexPath atScrollPosition:UICollectionViewScrollPositionNone animated:YES];
                });

                // Set isAnimating to wait to send response
                // Allows for scroll animation to be seen
                [MonkeyTalk sharedMonkey].isAnimating = YES;
                [NSThread sleepForTimeInterval:0.33];
                [MonkeyTalk sharedMonkey].isAnimating = NO;
            }

            dispatch_async(dispatch_get_main_queue(), ^{
                UICollectionViewCell *cell = [self cellForItemAtIndexPath:indexPath];
                // select the cell
                if ([self.delegate respondsToSelector:@selector(collectionView:didSelectItemAtIndexPath:)]) {
                    
                    if([self.delegate respondsToSelector:@selector(collectionView:didHighlightItemAtIndexPath:)])
                        [self.delegate collectionView:self didHighlightItemAtIndexPath:indexPath];
                    
                    //if is selected deselect
                    NSArray *selectedCells = [self indexPathsForSelectedItems];
                    if(selectedCells.count > 0 && [self.delegate respondsToSelector:@selector(collectionView:didDeselectItemAtIndexPath:)])
                        [self.delegate collectionView:self didDeselectItemAtIndexPath:[[self indexPathsForSelectedItems] objectAtIndex:0]];
                    
                    [self selectItemAtIndexPath:indexPath animated:YES scrollPosition:0];
                    [self.delegate collectionView:self didSelectItemAtIndexPath:indexPath];

                } else {
                    // support playback for apps using storyboards
                    [UIEvent performTouchInView:cell];
                }
            });
        });
    } else if ([event.command isEqualToString:MTCommandScrollToIndex ignoreCase:YES]) {
        NSInteger row = [event.args count] > 0 ? [[event.args objectAtIndex:0] intValue] : 0;
        NSInteger section = [event.args count] > 1 ? [[event.args objectAtIndex:1] intValue] : 0;

        if (row > 0)
            row -= 1;
        if (section > 0)
            section -= 1;

        NSIndexPath *path = [NSIndexPath indexPathForRow:row inSection:section];

        if (path == nil) {
            event.lastResult = [NSString
                stringWithFormat:@"Could not find index %@ %@ in collection view with monkeyID %@",
                                 [event.args objectAtIndex:0], [event.args objectAtIndex:0],
                                 event.monkeyID];
            return;
        } else if (section + 1 > [self numberOfSections]) {
            event.lastResult = [NSString
                                stringWithFormat:@"Scroll out of bounds -- can't scroll to item %i in section %i, "
                                @"because collection view only has %i %@",
                                (int)row + 1, (int)section + 1, (int)[self numberOfSections],
                                [NSString pluralStringFor:@"section"
                                                withCount:[self numberOfSections]]];
            return;
        } else if (row + 1 > [self numberOfItemsInSection:section]) {
            event.lastResult = [NSString
                stringWithFormat:@"Scroll out of bounds -- can't scroll to item %i, because "
                                 @"section %i only has %i %@",
                                 (int)row + 1, (int)section + 1,
                                 (int)[self numberOfItemsInSection:section],
                                 [NSString pluralStringFor:@"item"
                                                 withCount:[self numberOfItemsInSection:section]]];
            return;
        }
        [self scrollToItemAtIndexPath:path atScrollPosition:UICollectionViewScrollPositionNone animated:YES];
    } else {
        [super playbackMonkeyEvent:event];
        return;
    }
}

@end
