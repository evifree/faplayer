/*
 *     Generated by class-dump 3.1.1.
 *
 *     class-dump is Copyright (C) 1997-1998, 2000-2001, 2004-2006 by Steve Nygard.
 */

#import <BackRow/BRCenteredMenuController.h>

#import "BRMenuListItemProviderProtocol.h"

@class BRController, NSString;

@interface BRParentalControlsPasscodeChangedController : BRCenteredMenuController <BRMenuListItemProvider>
{
    BRController *_guardedController;
    NSString **_menuItemNameKeys;
    BOOL _passcodeMatched;
}

- (id)initWithMatch:(BOOL)fp8 guarding:(id)fp12;
- (void)dealloc;
- (void)itemSelected:(long)fp8;
- (id)itemForRow:(long)fp8;
- (long)itemCount;
- (id)titleForRow:(long)fp8;
- (float)heightForRow:(long)fp8;
- (BOOL)rowSelectable:(long)fp8;

@end
