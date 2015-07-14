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

#import "MTInputAutomator.h"

@implementation MTInputAutomator

- (NSArray *)xPathBasePaths {
    return @[@"//input[(@type!='radio' and @type!='checkbox' and @type!='button') or not(@type) or string-length(@type)=0]"];
}

- (NSString *) valueForElement:(MTElement *)e {
    NSString *value = nil;
    if ([self useDefaultProperty]) {
        // If there is no text, but the value attribute is set
        // use value attribute as default
        if ([e.text length] == 0 &&
            ![e attribute:@"value"])
            value = e.text;
        else
            value = [e attribute:@"value"];
    } else
        return [super valueForElement:e];
    
    return value;
}

- (NSString *) xPath {
    if ([mtEvent.monkeyID rangeOfString:@"xpath="].location != NSNotFound) {
        return [super xPath];
    }
    
    NSString *xp = [super xPath];
    
    xp = [xp stringByReplacingOccurrencesOfString:@"//Input" withString:@"//input[(@type!='radio' and @type!='checkbox' and @type!='button') or not(@type) or string-length(@type)=0]"];
    
    return xp;
}

@end
