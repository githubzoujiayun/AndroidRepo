#import <Foundation/Foundation.h>
#import "MTHTTPResponse.h"


@interface MTHTTPRedirectResponse : NSObject <MTHTTPResponse>
{
	NSString *redirectPath;
}

- (id)initWithPath:(NSString *)redirectPath;

@end
