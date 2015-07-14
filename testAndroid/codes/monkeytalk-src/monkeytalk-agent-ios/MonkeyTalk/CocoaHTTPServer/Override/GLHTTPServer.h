//
//  GLHTTPServer.h
//  MonkeyVNC
//
//  Created by Kyle Balogh on 6/1/13.
//  Copyright (c) 2013 Gorilla Logic. All rights reserved.
//

#import "MTHTTPServer.h"

@class MTGCDAsyncSocket;
@class MTWebSocket;

@interface GLHTTPServer : MTHTTPServer
@property (nonatomic, readonly) NSString *address;
@end
