//
//  MyUIKeyInput.m
//  SubstrateWork
//
//  Created by Johan on 21/02/2020.
//  Copyright © 2020 Johan. All rights reserved.
//

#import "MyUIKeyInput.h"

@implementation MyUIKeyInput



@synthesize textStore;



- (id)initWithFrame:(CGRect)frame {
    if ((self = [super initWithFrame:frame])) {
        // Initialization code
        self.textStore = [NSMutableString string];
        [self.textStore appendString:@"Touch screen to edit."];

        self.backgroundColor = [UIColor whiteColor];
    }
    return self;
}

- (void)dealloc {
}

#pragma mark -
#pragma mark Respond to touch and become first responder.

- (BOOL)canBecomeFirstResponder { return YES; }

-(void) touchesBegan: (NSSet *) touches withEvent: (UIEvent *) event {
    [self becomeFirstResponder];
}

#pragma mark -
#pragma mark Drawing

- (void)drawRect:(CGRect)rect {
    CGRect rectForText = CGRectInset(rect, 20.0, 20.0);
    UIRectFrame(rect);
    [self.textStore drawInRect:rectForText withFont:[UIFont fontWithName:@"Helvetica" size:24.0f]];
}

#pragma mark -
#pragma mark UIKeyInput Protocol Methods

- (BOOL)hasText {
    fprintf(stderr, "hastext? \n");
    if (textStore.length > 0) {
        return YES;
    }
    return NO;
}

- (void)insertText:(NSString *)theText {
    fprintf(stderr, "INSERT111 TEXT\n");
    [self.textStore appendString:theText];
    [self setNeedsDisplay];
}

- (void)deleteBackward {
    NSRange theRange = NSMakeRange(self.textStore.length-1, 1);
    [self.textStore deleteCharactersInRange:theRange];
    [self setNeedsDisplay];
}

@end
