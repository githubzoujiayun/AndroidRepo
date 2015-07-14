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

#import "MTWebRecorder.h"
#import "MonkeyTalk.h"
#import "MTCommandEvent.h"
#import "MTWebJs.h"

@implementation MTWebRecorder

- (NSString *)webRecorderJSScript {
    NSString *jsScript = MTWebJsString;

    // fix regex escaping
    jsScript = [jsScript
        stringByReplacingOccurrencesOfString:@"var match = /\nr|\nn/.exec(comp[\"monkeyId\"]);"
                                  withString:@"var match = /\\r|\\n/.exec(comp[\"monkeyId\"]);"];

    return jsScript;
}

- (void)jsMessageKey:(NSString *)val {
    NSDictionary *json =
        [NSJSONSerialization JSONObjectWithData:[val dataUsingEncoding:NSUTF8StringEncoding]
                                        options:0
                                          error:nil];
    NSString *component = [json objectForKey:@"component"];
    NSString *monkeyId = [json objectForKey:@"monkeyId"];
    NSString *action = [json objectForKey:@"action"];
    NSArray *args = [json objectForKey:@"args"];
    
    if (!component) {
        component = @"View";
    }
    
    if ([args isKindOfClass:[NSString class]] && ((NSString *)args).length > 0) {
        [MonkeyTalk recordWebComponents:component monkeyID:monkeyId command:action args:[NSArray arrayWithObject:(NSString *)args]];
        return;
    }
    [MonkeyTalk recordWebComponents:component monkeyID:monkeyId command:action args:args];
}

@end