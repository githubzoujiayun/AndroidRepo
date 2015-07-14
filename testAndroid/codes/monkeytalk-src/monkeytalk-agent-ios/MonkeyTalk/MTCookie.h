//
//  MTCookie.h
//  iWebDriver
//
//  Copyright 2009 Google Inc.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

#import <Foundation/Foundation.h>
#import "MTHTTPVirtualDirectory.h"

@class MTWebDriverResponse;

// This |MTHTTPVirtualDirectory| matches the /hub/:session/cookie
// directory in the WebDriver REST service.
@interface MTCookie : MTHTTPVirtualDirectory {
  int sessionId_;
}

- (id)initWithSessionId:(int)sessionId;

+ (MTCookie*)cookieWithSessionId:(int)sessionId;

- (NSURL *)currentUrl;
- (NSArray *)getCookies;
- (void)addCookie:(NSDictionary *)cookie;
- (void)deleteAllCookies;

@end

// This |MTHTTPVirtualDirectory| matches the /hub/:session/cookie/:name directory
// in the WebDriver REST service.
@interface MTNamedCookie : MTHTTPVirtualDirectory {
  @private
  NSString* name_;
}

+ (MTNamedCookie *)namedCookie:(NSString *)name;

- (id)initWithName:(NSString *)name;

- (NSDictionary *)getCookie;

- (void)deleteCookie;

@end
