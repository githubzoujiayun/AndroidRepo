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

#import "MTCheckBoxAutomator.h"

@implementation MTCheckBoxAutomator

- (NSArray *)xPathBasePaths {
    return @[@"//input[@type='checkbox']"];
}

- (NSString *) xPath {
    if ([mtEvent.monkeyID rangeOfString:@"xpath="].location != NSNotFound) {
        return [super xPath];
    }
    
    NSString *path = [self basePath];
    NSString *convertedCommand = [NSString stringWithFormat:@"//input[@type='checkbox']%@",path];

    if ([self isOrdinal])
        convertedCommand = [NSString
                            stringWithFormat:@"(//input[@type='checkbox'])%@",path];
    
    return convertedCommand;
}

- (NSString *) valueForElement:(MTElement *)e {
    NSString *value = nil;
    if ([self useDefaultProperty]) {
        if ([[e isChecked] boolValue])
            value = @"on";
        else
            value = @"off";
    } else {
        return [super valueForElement:e];
    }
    return value;
}

@end
