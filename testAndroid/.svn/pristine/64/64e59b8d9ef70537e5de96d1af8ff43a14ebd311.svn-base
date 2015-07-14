#import <Foundation/Foundation.h>
#import "MTHTTPResponse.h"

@class MTHTTPConnection;


@interface MTHTTPFileResponse : NSObject <MTHTTPResponse>
{
	MTHTTPConnection *connection;
	
	NSString *filePath;
	UInt64 fileLength;
	UInt64 fileOffset;
	
	BOOL aborted;
	
	int fileFD;
	void *buffer;
	NSUInteger bufferSize;
}

- (id)initWithFilePath:(NSString *)filePath forConnection:(MTHTTPConnection *)connection;
- (NSString *)filePath;

@end
