//
//  GLHTTPConnection.h
//  MonkeyVNC
//
//  Created by Kyle Balogh on 6/1/13.
//  Copyright (c) 2013 Gorilla Logic. All rights reserved.
//

#import "MTHTTPConnection.h"

@interface GLHTTPConnection : MTHTTPConnection {
    NSData *bodyData;
}

@end
