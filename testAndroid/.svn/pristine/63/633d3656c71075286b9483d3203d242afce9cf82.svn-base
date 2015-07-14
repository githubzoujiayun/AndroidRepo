//
//  GLHTTPConnection.m
//  MonkeyVNC
//
//  Created by Kyle Balogh on 6/1/13.
//  Copyright (c) 2013 Gorilla Logic. All rights reserved.
//

#import "GLHTTPConnection.h"
#import "MTHTTPFileResponse.h"
#import "NSData+MTBase64.h"
#import "SBJsonMT.h"
#import "MTHTTPDataResponse.h"
#import "MTHTTPMessage.h"
#import "NSString+SBJSONMT.h"
#import "MTWireResponder.h"

@implementation GLHTTPConnection
#pragma GCC diagnostic ignored "-Wdeprecated-declarations"

- (NSObject<MTHTTPResponse> *)httpResponseForMethod:(NSString *)method URI:(NSString *)path {
    //HTTPLogTrace();
    
    // Override me to provide custom responses.
    
    return [MTWireResponder wireResponseFromQuery:path withData:bodyData];
}

- (NSObject<MTHTTPResponse> *)pingResponse {
    NSDictionary *pingResponse = [NSDictionary dictionaryWithObjectsAndKeys:@"OK",@"message",nil, @"data", nil];
    NSData *pingData = [NSJSONSerialization dataWithJSONObject:pingResponse options:0 error:nil];
    return [[MTHTTPDataResponse alloc] initWithData:pingData];
}


/**
 * Returns whether or not the server will accept messages of a given method
 * at a particular URI.
 **/
- (BOOL)supportsMethod:(NSString *)method atPath:(NSString *)path {
    //HTTPLogTrace();
    
    // Override me to support methods such as POST.
    //
    // Things you may want to consider:
    // - Does the given path represent a resource that is designed to accept this method?
    // - If accepting an upload, is the size of the data being uploaded too big?
    //   To do this you can check the requestContentLength variable.
    //
    // For more information, you can always access the HTTPMessage request variable.
    //
    // You should fall through with a call to [super supportsMethod:method atPath:path]
    //
    // See also: expectsRequestBodyFromMethod:atPath:
    
    if ([method isEqualToString:@"GET"]) {
        return YES;
    }
    
    if ([method isEqualToString:@"HEAD"]) {
        return YES;
    }
    
    if ([method isEqualToString:@"POST"]) {
        return YES;
    }
    
    return NO;
}

#pragma mark - Handle POST data

/**
 * This method is called after receiving all HTTP headers, but before reading any of the request body.
 **/
- (void)prepareForBodyWithSize:(UInt64)contentLength {
    // Override me to allocate buffers, file handles, etc.
    bodyData = nil;
}

/**
 * This method is called to handle data read from a POST / PUT.
 * The given data is part of the request body.
 **/
- (void)processBodyData:(NSData *)postDataChunk {
    // Override me to do something useful with a POST / PUT.
    // If the post is small, such as a simple form, you may want to simply append the data to the request.
    // If the post is big, such as a file upload, you may want to store the file to disk.
    //
    // Remember: In order to support LARGE POST uploads, the data is read in chunks.
    // This prevents a 50 MB upload from being stored in RAM.
    // The size of the chunks are limited by the POST_CHUNKSIZE definition.
    // Therefore, this method may be called multiple times for the same POST request.
    
    // may need to
    bodyData = postDataChunk;
}

/**
 * This method is called after the request body has been fully read but before the HTTP request is processed.
 **/
- (void)finishBody {
    // Override me to perform any final operations on an upload.
    // For example, if you were saving the upload to disk this would be
    // the hook to flush any pending data to disk and maybe close the file.
}

@end
