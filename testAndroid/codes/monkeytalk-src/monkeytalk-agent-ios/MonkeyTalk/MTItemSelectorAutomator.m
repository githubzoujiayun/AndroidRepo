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

#import "MTItemSelectorAutomator.h"
#import "MTConstants.h"
#import "NSString+MonkeyTalk.h"

@implementation MTItemSelectorAutomator

- (NSArray *)xPathBasePaths {
    return @[@"//select"];
}

- (NSString *) xPath {
    // Selector finds select web element
    if ([mtEvent.monkeyID rangeOfString:@"xpath="].location != NSNotFound) {
        return [super xPath];
    }
    
    NSString *convertedCommand = [super xPath];
    NSString *path = [self basePath];
    
    if (mtEvent.args && [mtEvent.args count] > 0) {
        // If there are args, add option to xPath
        
        if ([mtEvent.command isEqualToString:MTCommandSelectIndex ignoreCase:YES])
            convertedCommand = [convertedCommand stringByAppendingFormat:@"/option[%@]",
                                [mtEvent.args objectAtIndex:0]];
        else if ([mtEvent.command isEqualToString:MTCommandSelect ignoreCase:YES])
            convertedCommand = [convertedCommand stringByAppendingFormat:@"/option[@value='%@' or ./text()='%@']", [mtEvent.args objectAtIndex:0], [mtEvent.args objectAtIndex:0]];
    }
    
    return convertedCommand;
}

- (MTElement *) selectElement {
    return [self elementFromXpath:[super xPath]];
}

- (MTElement *) optionAtIndex:(NSInteger)index {
    NSString *path = [NSString stringWithFormat:@"%@/option[%i]",[super xPath],index];
    return [self elementFromXpath:path];
}

- (void) deselectAll {
    MTElement *selectElement = [self selectElement];
    NSInteger size = [[selectElement attribute:@"length"] integerValue];
    
    for (int i = 0; i < size; i++) {
        MTElement *option = [self optionAtIndex:i];
        
        if ([option.isChecked integerValue] == 1) {
            [option toggleSelected];
        }
        
    }
    
}

- (void) selectItems {
    if (mtEvent.args && [mtEvent.args count] > 0) {
        // If there are args, add option to xPath
        NSString *path = [super xPath];
        MTElement *select = [self elementFromXpath:path];
        
        for (NSString *arg in mtEvent.args) {
            MTElement *option;
            
            if ([mtEvent.command isEqualToString:MTCommandSelectIndex ignoreCase:YES])
                path = [path stringByAppendingFormat:@"/option[%@]",
                                    arg];
            else if ([mtEvent.command isEqualToString:MTCommandSelect ignoreCase:YES])
                path = [path stringByAppendingFormat:@"/option[@value='%@' or ./text()=%@]", arg, arg];
            
            option = [self elementFromXpath:path];
            
            [option toggleSelected];
            
            // TODO: need to detect when setting the value is required
            // [select setValue:[NSString stringWithFormat:@"'%@'",arg]];
        }
    }
}

- (BOOL) playBackOnElement {
    // Find size based on element attribute size of parent
    // Iterate through select element
    // Check isChecked on each option
    // Toggle checked if checked
    if ([mtEvent.command isEqualToString:MTCommandGet ignoreCase:YES] ||
        [mtEvent.command rangeOfString:[MTCommandVerify lowercaseString]].location != NSNotFound)
        [super playBackOnElement];
    
    MTElement *selectElement = [self selectElement];
    
    if ([selectElement attribute:@"multiple"])
        [self deselectAll];
    
    if ([mtEvent.command isEqualToString:MTCommandClear ignoreCase:YES])
        return YES;
    
    [self selectItems];
    
    return YES;
}

- (NSString *) valueForElement:(MTElement *)e {
    NSString *value = nil;
    if ([self useDefaultProperty]) {
        NSInteger *selectedIndex = [[e attribute:@"selectedIndex"] integerValue] + 1;
        
        MTElement *option = [self optionAtIndex:selectedIndex];
        
        value = option.text;
    } else if ([self hasAttribute]) {
        if ([[self attribute] isEqualToString:@"size"])
            value = [e attribute:@"length"];
        else if ([[self attribute] isEqualToString:@"item"])
            value = e.text;
        else
            return [super valueForElement:e];
    }
    return value;
}

@end
