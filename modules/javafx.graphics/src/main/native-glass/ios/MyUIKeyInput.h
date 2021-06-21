//
//  MyUIKeyInput.h
//  SubstrateWork
//
//  Created by Johan on 21/02/2020.
//  Copyright © 2020 Johan. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface MyUIKeyInput : UIView<UIKeyInput> {
    NSMutableString *textStore;
}

@property (nonatomic, retain) NSMutableString *textStore;

@end

NS_ASSUME_NONNULL_END


