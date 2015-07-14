//
//  GLHTTPServer.m
//  MonkeyVNC
//
//  Created by Kyle Balogh on 6/1/13.
//  Copyright (c) 2013 Gorilla Logic. All rights reserved.
//

#import "GLHTTPServer.h"
#import "MTGCDAsyncSocket.h"
#import "MTHTTPConnection.h"
#import "MTWebSocket.h"
#import "MTHTTPLogging.h"
#import "GLHTTPConnection.h"

#import <sys/types.h>
#import <sys/socket.h>
#import <ifaddrs.h>
#import <arpa/inet.h>

@implementation GLHTTPServer

/**
 * Standard Constructor.
 * Instantiates an HTTP server, but does not start it.
 **/
- (id)init {
    if ((self = [super init])) {
        //HTTPLogTrace();
        
        // Setup underlying dispatch queues
        serverQueue = dispatch_queue_create("HTTPServer", NULL);
        connectionQueue = dispatch_queue_create("GLHTTPConnection", NULL);
        
        IsOnServerQueueKey = &IsOnServerQueueKey;
        IsOnConnectionQueueKey = &IsOnConnectionQueueKey;
        
        void *nonNullUnusedPointer = (__bridge void *)self;         // Whatever, just not null
        
        dispatch_queue_set_specific(serverQueue, IsOnServerQueueKey, nonNullUnusedPointer, NULL);
        dispatch_queue_set_specific(connectionQueue, IsOnConnectionQueueKey, nonNullUnusedPointer, NULL);
        
        // Initialize underlying GCD based tcp socket
        asyncSocket = [[MTGCDAsyncSocket alloc] initWithDelegate:self delegateQueue:serverQueue];
        
        // Use default connection class of HTTPConnection
        connectionClass = [GLHTTPConnection self];
        
        // By default bind on all available interfaces, en1, wifi etc
        interface = nil;
        
        // Use a default port of 0
        // This will allow the kernel to automatically pick an open port for us
        port = 0;
        
        // Configure default values for bonjour service
        
        // Bonjour domain. Use the local domain by default
        domain = @"local.";
        
        // If using an empty string ("") for the service name when registering,
        // the system will automatically use the "Computer Name".
        // Passing in an empty string will also handle name conflicts
        // by automatically appending a digit to the end of the name.
        name = @"";
        
        // Initialize arrays to hold all the HTTP and webSocket connections
        connections = [[NSMutableArray alloc] init];
        webSockets  = [[NSMutableArray alloc] init];
        
        connectionsLock = [[NSLock alloc] init];
        webSocketsLock  = [[NSLock alloc] init];
        
        // Register for notifications of closed connections
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(connectionDidDie:)
                                                     name:HTTPConnectionDidDieNotification
                                                   object:nil];
        
        // Register for notifications of closed websocket connections
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(webSocketDidDie:)
                                                     name:WebSocketDidDieNotification
                                                   object:nil];
        
        isRunning = NO;
    }
    return self;
}

- (NSString *)address {
    struct ifaddrs *head;
    if (getifaddrs(&head)) {
        return @"unknown";
    }
    
    // Default to return localhost.
    NSString *address = @"127.0.0.1";
    
    // |head| contains the first element in a linked list of interface addresses.
    // Iterate through the list.
    for (struct ifaddrs *ifaddr = head;
         ifaddr != NULL;
         ifaddr = ifaddr->ifa_next) {
        struct sockaddr *sock = ifaddr->ifa_addr;
        
        NSString *interfaceName = [NSString stringWithUTF8String:ifaddr->ifa_name];
        
        // Ignore localhost.
        if ([interfaceName isEqualToString:@"lo0"]) {
            continue;
        }
        
        // Ignore IPv6 for now.
        if (sock->sa_family == AF_INET && [interfaceName isEqualToString:@"en0"]) {
            struct in_addr inaddr = ((struct sockaddr_in *)sock)->sin_addr;
            char *addressName = inet_ntoa(inaddr);
            address = [NSString stringWithUTF8String:addressName];
            break;
        }
    }
    
    freeifaddrs(head);
    
    return address;
}

@end
