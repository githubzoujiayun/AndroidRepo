//
//  MTHTTPVirtualDirectory+Remove.m
//  iWebDriver
//
//  Created by Yu Chen on 6/1/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import "MTHTTPVirtualDirectory+Remove.h"

@implementation MTHTTPVirtualDirectory (Remove)
- (void) removeChildren {  
  // Remove NSMutableDictionary *contents
  NSString* contentName;
  for (contentName in [contents allKeys]) {
    id content = [contents objectForKey:contentName]; 
    [contents removeObjectForKey:contentName];

    if ([content isKindOfClass:[MTHTTPVirtualDirectory class]]) {
      [(MTHTTPVirtualDirectory*)content removeChildren];
    }

  }    
    
  //Remove id<MTHTTPResource> index	
  if ([index isKindOfClass:[MTHTTPVirtualDirectory class]]) {
    [(MTHTTPVirtualDirectory*)index removeChildren];
  }   
}
@end
