# receipt-scanner-android-demo

## Installation

Add to your `build.gradle.kts` dependencies:
```agsl
implementation("com.ourcart:receiptscanner:1.0.5")
```

Add in settings.gradle.kts new maven repository:
```agsl
maven {
  url = uri("s3://public-maven.ourcart.com/release" )
  credentials(AwsCredentials::class) {
    accessKey = "<provided by ourcart>"
    secretKey ="<provided by ourcart>"
  }
}
```

Also add to file `gradle.properties` those lines:
```agsl
android.useAndroidX=true
android.enableJetifier=true
```


## Quickstart
To use receipt scanner you must just initialize its instance and run method `start`, it will start an actions that will allow users to send receipts to Ourcart
```java
ReceiptScanner receiptScanner = new ReceiptScanner(
    false,
    "<api key>",
    "US",
    "<client code>",
    "cab123"
);

receiptScanner.start(this); // "this" is a context
```

Input for `ReceiptScanner` constructor:
- **isProduction** (_boolean_) - currently library is hardcoded to staging
- **apiKey** (_string_) - string for Ourcart requests, must match country, environment, and clientCode, provided to you by Ourcart
- **clientCountry** (_string_) - country code for Ourcart requests, provided to you by Ourcart
- **clientCode** (_string_) - client code for Ourcart requests, provided to you by Ourcart
- **clientUserId** (_string_) - id of client to be sended and associated with receipts, it can be any string but have it be a real string associated with currently logged in user, it will help us block fraudulent users and will provide consistent data.

## ReceiptScanner documentation
`ReceiptScanner` class have many methods allowing you to change initial data, make customization, and react in parent app to user interaction in ReceiptScanner flow:

- ### Constructor (_required_):

    #### Arguments:
  - **isProduction** (_boolean_) - currently library is hardcoded to staging
  - **apiKey** (_string_) - string for Ourcart requests, must match country, environment, and clientCode, provided to you by Ourcart
  - **clientCountry** (_string_) - country code for Ourcart requests, provided to you by Ourcart
  - **clientCode** (_string_) - client code for Ourcart requests, provided to you by Ourcart
  - **clientUserId** (_string_) - id of client to be sended and associated with receipts, it can be any string but have it be a real string associated with currently logged in user, it will help us block fraudulent users and will provide consistent data.

- ### setApiKey (_optional_)
    This method allows you to change apiKey provided in constructor.

    #### Arguments:
    - **apiKey** (_string_) - string for Ourcart requests, must match country, environment, and clientCode, provided to you by Ourcart

- ### setClientCountry (_optional_)
    This method allows you to change clientCountry provided in constructor.

  #### Arguments:
    - **clientCountry** (_string_) - country code for Ourcart requests, provided to you by Ourcart

- ### setClientCode (_optional_)
  This method allows you to change clientCode provided in constructor.

  #### Arguments:
    - **clientCode** (_string_) - client code for Ourcart requests, provided to you by Ourcart

- ### setClientUserId (_optional_)
    This method allows you to change clientUserId provided in constructor.

  #### Arguments:
    - **clientUserId** (_string_) - id of client to be sended and associated with receipts, it can be any string but have it be a real string associated with currently logged in user, it will help us block fraudulent users and will provide consistent data.

- ### setUserInteractionListener (_optional_)
    This method allows you to lister to users interaction, it takes a implementation of EventRunnable interface (with just one method void `onEvent(String eventName)`) or a method that will receive the name of user interaction

  #### Arguments:
    - **listener**  (_EventRunnable_) - method/ instance that allows you to react on user interactions it will pass the name of interaction as a string, possible values:
      - "`Receipt Upload Page Views`"
      - "`Snap a Receipt Clicks`"
      - "`Submit Clicks (Regular Receipt)`"
      - "`Snap a Receipt Complete`"
      - "`Upload File Clicks`"
      - "`Upload File Complete`"

Example:
```java
receiptScanner.setUserInteractionListener(eventName -> {
    Log.i(TAG, "eventName: " + eventName);
});
```

- ### setTranscriptionCallback (_optional_)
    This sets url and auth token for callback to request, not needed for most of users, don't use if not instructed by Ourcart team

  #### Arguments:
  - **url** (_string_) - callback url
  - **auth** (_string_) - token

- ### setInitialScreenHeading (_optional_)
    This sets the string of header at initial view

    #### Arguments:
    - **title** (_string_) - new text that will be displayed at initial screen as title,  
  default: “`Receipt upload`”

Example:
```java
receiptScanner.setInitialScreenHeading("Welcome to the documentation");
```
![example_1](https://s3.amazonaws.com/ourcart.platform.assets/images/example1.jpg)

- ### setInitialScreenSubHeading (_optional_)
  This sets the string of text at initial view under title

  #### Arguments:
    - **text** (_string_) - new text that will be displayed at initial screen under title,  
default: “`Snap your paper receipt or upload \ndigital receipt file.`”

Example:
```java
receiptScanner.setInitialScreenSubHeading("Another documentation example, have a nice day\n lalala");
```
![example_2](https://s3.amazonaws.com/ourcart.platform.assets/images/example2.jpg)

- ### setFinalScreenHeading (_optional_)
  This sets the string of header at last view

  #### Arguments:
    - **title** (_string_) - new text that will be displayed at last screen as title,  
default: “`Upload complete`”

Example:
```java
receiptScanner.setFinalScreenHeading("Final Title example");
```
![example_3](https://s3.amazonaws.com/ourcart.platform.assets/images/example3.jpg)

- ### setFinalScreenSubHeading (_optional_)
  This sets the string of text at last view under title

  #### Arguments:
    - **text** (string) - new text that will be displayed at last screen under title,  
default: “`You\'ll be notified once your\nreceipt is verified.`”

Example:
```java
receiptScanner.setFinalScreenSubHeading(
    "Final text example, last"
);
```
![example_4](https://s3.amazonaws.com/ourcart.platform.assets/images/example4.jpg)

- ### setFinalScreenManualReviewHeading (_optional_)
  This sets the string of header at last view for "Manual review" screen

  #### Arguments:
  - **text** (string) - new text that will be displayed as header at "Manual review" screen,  
    default: “`Upload complete`”

Example:
```java
receiptScanner.setFinalScreenManualReviewHeading(
    "Manual review title"
);
```

- ### setFinalScreenManualReviewSubHeading (_optional_)
  This sets the string of text at "Manual review" screen under title

  #### Arguments:
  - **text** (string) - new text that will be displayed as header at "Manual review" screen,  
    default: “`We couldn\'t process your\n receipt automatically.\nIt will now go to manual review,\n which may take up to 48 hours.`”

Example:
```java
receiptScanner.setFinalScreenManualReviewSubHeading(
    "Your receipt will be reviewd in 48 h."
);
```

- ### setTutorialStrings (_optional_)
  This sets array of strings that will override the texts of each step in tutorial, null or no value does not change the default text,

  #### Arguments:
    - **texts** (_String[]_) - texts that overwrite default texts in tutorial

Example:  
You want to change the text of first and third step in tutorial
```java
receiptScanner.setTutorialStrings(
    new String[]{"Text for step1", null, "test for step 3"}
);
```


- ### setTutorialDrawables (_optional_)
  This sets array of strings that will override the texts of each step in tutorial, null or no value does not change the default text,

  #### Arguments:
    - **Texts** (_Drawing[]_) - drawings that overwrite default images in tutorial

Example:  
You want to change the image of first and third step in tutorial
```java
receiptScanner.setTutorialDrawables(
  new Drawable[]{
    getDrawable(R.drawable.ic_launcher_background),
    null,
    getDrawable(R.drawable.ic_launcher_foreground),
  }
);
```

- ### setTutorialConfigOverride (_optional_)
  This sets a boolean value that allow you to change the number of steps in tutorial, but you must set all the texts and drawables with `setTutorialStrings` and `setTutorialDrawables`, the length of strings and drawables bust be the same.

  #### Arguments:
    - **isOverride** (_boolean_) - default: `false`

- ### setCloseListener (_optional_)
  This method allows you to lister to user closing of “Receipt Scanner”, it takes a implementation of CloseRunnable or a lambda that will get current context.

  #### Arguments:
    - **listener**  (_Runnable_) - method/instance that allows you to react on closing the flow

Example:
```java
receiptScanner.setCloseListener((Context context) -> {
// react for user clicking done or x
});
```
- ### setPreValidation (_optional_)
  By default after user sends receipt there will be waiting time to check the result of transcription and present user with potential validation error. If you want to move user to the end screen without waiting for validation of receipt use this method to set flag to false. User will have manual review screen displayed.

  #### Arguments:
  - **wait flag**  (_boolean_) - name of bucket provided by ourcart  
  default: true

    Example:
```java
receiptScanner.setPreValidation(false);
```
- ### setDoneListener (_optional_)
  This method allows you to lister to clicking at "Done" button at the end of flow, if no listener is set clicking at "Done" will reset the flow and displays initial view.

  #### Arguments:
  - **listener**  (_Runnable_) - method/instance that allows you to react on clicking "Done" button at the end of flow

- ### reset (_optional_)
  Resets the scanner and displays initial view

- ### start (_optional_)
  This starts the flow and opens the base action of receipt scanner

  #### Arguments:
    - **context**  (_Context_)


## Customization of colors:
Receipt scanner allows you to set colors of its views by overwriting default values, same is true for font used in it. To do it go to file colors.xml and set new values:

Example:  
Lets set some random colors
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>

    <color name="ourcartPrimaryColor">#ff0000</color>
    <color name="ourcartTextColor">#0000ff</color>
    <color name="ourcartTextActionButtonColor">#00ffff</color>
    <color name="ourcartBackgroundPrimaryColor">#ffff00</color>
    <color name="ourcartBackgroundSecondaryColor">#ff00ff</color>
</resources>
```

,and the result is:  

![example_5](https://s3.amazonaws.com/ourcart.platform.assets/images/example5.jpg)  

... beautiful.

### Colors to customize:
- **Main colors**
  - **ourcartPrimaryColor** (default: `#92C91F`) - color of links, action buttons, and many ornaments, should be the main color of the company
  - **ourcartTextColor** (default: `#333333`) - color of almost all texts
  - **ourcartBackgroundPrimaryColor** (default: `#F9F9F9`) - background of main app actions
  - **ourcartBackgroundSecondaryColor** (default: `#ffffff`) - action bars and modals
- **Primary button** - buttons: Crop, Done, Continue to Upload, Next, Submit
  - **ourcartActionButtonBackgroundColor** (default: `ourcartPrimaryColor`) - background of tile buttons at the first view (pink buttons in the example above)
  - **ourcartActionButtonTextColor** (default: `#ffffff`) - change only if you dont want it to be "text color", color of text at the "tile buttons"
  - **ourcartActionButtonIconColor** (default: `#ffffff`) - change only if you dont want it to be "primary color", color of icon at the "tile buttons"
- **Link button** - Link buttons: Tutorial, Retake, Retake All, I’ll upload later
  - **ourcartActionTextBackgroundColor** (default: `#00000000`) - background of tile buttons at the first view (ping in the example)
  - **ourcartActionTextTextColor** (default: `ourcartPrimaryColor`) - change only if you dont want it to be "text color", color of text at the "tile buttons"
  - **ourcartActionTextIconColor** (default: `ourcartPrimaryColor`) - change only if you dont want it to be "primary color", color of icon at the "tile buttons"
- **Tile button** - two buttons on initial screen: Upload Receipt, Snap a Receipt
  - **ourcartTileButtonBackgroundColor** (default: `#ffffff`) - background of tile buttons at the first view (ping in the example)
  - **ourcartTileButtonTextColor** (default: `ourcartTextColor`) - change only if you dont want it to be "text color", color of text at the "tile buttons"
  - **ourcartTileButtonIconColor** (default: `ourcartPrimaryColor`) - change only if you dont want it to be "primary color", color of icon at the "tile buttons"

## Customization of font:
Same as color you can overwrite font by adding “`ourcartFontFamily`” to styles, default font is `Poppins`.

Example:  
Add to your styles
```xml
<item name="ourcartFontFamily" type="font">@font/poppins</item>
```

## Customization of text:
Some texts can be set programmatically by methods **setInitialScreenHeading**, **setInitialScreenSubHeading**, **setFinalScreenHeading**, **setFinalScreenSubHeading**, **setFinalScreenManualReviewHeading**, **setFinalScreenManualReviewSubHeading** and **setTutorialStrings**. 
Texts set in this way will have highest priority and will be take president over other ways.

Aside from methods similarly to colors receipt scanner allows you to overwrite default texts. To do it go to file `strings.xml` and set new values. This is also a way to add translations or set texts that can be programmatically if they are static.

Example:
Add to your `strings.xml`
```xml
<string name="OURCART_AUTO_CAPTURE_ON">Auto-capture is on from no on</string>
```

All texts with default values in xml format:
```xml
<string name="OURCART_select_files">Select Files</string>
<string name="OURCART_too_many_files">Maximum 6 files allowed</string>
<string name="OURCART_too_big_file_size">Max allowed file size is 12MB</string>
<string name="OURCART_adjust_angle">The angle is incorrect. Hold your camera directly above the receipt.</string>
<string name="OURCART_FLAW_GENERIC">Some details couldn\'t be recognized. Please ensure the entire receipt is visible and clear.</string>
<string name="OURCART_SENDING_ERROR">Something went wrong. Please try again later.</string>
<string name="OURCART_PROCESSING_ERROR">Something went wrong. Please try again later.</string>
<string name="OURCART_DIMENSION_RATIO_ERROR">The receipt is too long. Please click Retake, choose Long Receipt, zoom in, and capture it in up to 6 parts.</string>
<string name="OURCART_looking_for_receipts">Looking for receipt...</string>
<string name="OURCART_AUTO_MANUAL_ON">No receipt found.\n Capture manually.</string>
<string name="OURCART_HOLD_STEADY">Hold your camera steady,\n we are capturing...</string>
<string name="OURCART_AUTO_CAPTURE_ON">Auto-capture is on</string>
<string name="OURCART_AUTO_CAPTURE_OFF">Auto-capture is off</string>
<string name="OURCART_REGULAR_RECEIPT_MODE">Regular receipt mode</string>
<string name="OURCART_LONG_RECEIPT_MODE">Long receipt mode</string>
<string name="OURCART_upload_pick_text" formatted="false">If your receipt is digital, please make sure to <b>download it first</b> to your device, and then upload.</string>
<string name="OURCART_upload_pick_upload">Continue to upload</string>
<string name="OURCART_upload_pick_upload_later"><u>I\'ll upload later</u></string>
<string name="OURCART_upload_pick_gallery">Gallery</string>
<string name="OURCART_upload_pick_document">Document</string>
<string name="OURCART_regular_receipt">Regular Receipt</string>
<string name="OURCART_long_receipt">Long Receipt</string>
<string name="OURCART_submit">Submit</string>
<string name="OURCART_back"><u>Back</u></string>
<string name="OURCART_retake"><u>Retake</u></string>
<string name="OURCART_retake_all"><u>Retake all</u></string>
<string name="OURCART_close">Close</string>
<string name="OURCART_crop">Crop</string>
<string name="OURCART_next">Next</string>
<string name="OURCART_done">Done</string>
<string name="OURCART_upload_receipt">Upload Receipt</string>
<string name="OURCART_snap_receipt">Snap a Receipt</string>
<string name="OURCART_init_page_bottom">For more info open</string>
<string name="OURCART_tutorial"><u>Tutorial</u></string>
<string name="OURCART_initial_screen_heading">Receipt upload</string>
<string name="OURCART_initial_screen_sub_heading" formatted="false">Snap your paper <b>receipt or upload</b> \ndigital receipt file.</string>
<string name="OURCART_final_screen_heading">Upload complete</string>
<string name="OURCART_final_screen_sub_heading">You\'ll be notified once your\nreceipt is verified.</string>
<string name="OURCART_final_screen_manual_review_heading">Upload complete</string>
<string name="OURCART_final_screen_manual_review_sub_heading" formatted="false">We couldn\'t process your\n receipt automatically.\nIt will now go to <b>manual review,\n</b> which may take <b>up to 48 hours.</b></string>
<string name="OURCART_tutorial_1" formatted="false">The image should be \nclearly readable and \n<b>include all the information</b> \non the receipt.</string>
<string name="OURCART_tutorial_2" formatted="false">Snap with <b>minimal \nbackground</b>, align receipt \nborders to the camera frame.</string>
<string name="OURCART_tutorial_3" formatted="false">Snap a clear picture in \n<b>good lighting</b>. Make sure it is\n not blurry or dark</string>
<string name="OURCART_tutorial_4" formatted="false">The receipt should be clear\n and <b>free of wrinkles</b>.</string>
<string name="OURCART_tutorial_5" formatted="false">Please <b>do not write</b> \non the receipt.</string>
<string name="OURCART_tutorial_6" formatted="false">We support common \n<b>image formats:</b> \nPDF, JPG, JPEG, PNG, GIF.</string>
<string name="OURCART_tutorial_7" formatted="false">If your receipt is digital,\n please make sure to\n <b>download it first</b> to your\n device, and then upload.</string>
```

#### In the text there are tags similar to html ones:
- `<b>` - make the text bold, also add attribute to string `formatted="false"` may not work without it
- `<u>` - add underline to text

### Placement of most texts:
![readme_1](https://s3.amazonaws.com/ourcart.platform.assets/images/readme1.jpg)
![readme_2](https://s3.amazonaws.com/ourcart.platform.assets/images/readme1_1.jpg)
![readme_3](https://s3.amazonaws.com/ourcart.platform.assets/images/readme2.jpg)
![readme_4](https://s3.amazonaws.com/ourcart.platform.assets/images/readme3.jpg)
![readme_5](https://s3.amazonaws.com/ourcart.platform.assets/images/readme4.jpg)
![readme_6](https://s3.amazonaws.com/ourcart.platform.assets/images/readme5.jpg)
![readme_7](https://s3.amazonaws.com/ourcart.platform.assets/images/readme6.jpg)
![readme_8](https://s3.amazonaws.com/ourcart.platform.assets/images/readme7.jpg)
![readme_9](https://s3.amazonaws.com/ourcart.platform.assets/images/readme8.jpg)
![readme_10](https://s3.amazonaws.com/ourcart.platform.assets/images/readme9.jpg)
![readme_11](https://s3.amazonaws.com/ourcart.platform.assets/images/readme10.jpg)
![readme_12](https://s3.amazonaws.com/ourcart.platform.assets/images/readme11.jpg)
![readme_13](https://s3.amazonaws.com/ourcart.platform.assets/images/readme12.jpg)
![readme_14](https://s3.amazonaws.com/ourcart.platform.assets/images/readme13.jpg)
![readme_14](https://s3.amazonaws.com/ourcart.platform.assets/images/readme14.jpg)

### Explanation of some texts:
 - `OURCART_too_many_files` (default: "Maximum 6 files allowed") - displayed when user pick more then 6 files from the device
 - `OURCART_too_big_file_size` (default: "Max allowed file size is 12MB") - displayed when user selected a file larger then 12 MB
 - `OURCART_FLAW_GENERIC` (default: "Some details couldn\'t be recognized. Please ensure the entire receipt is visible and clear") - error displayed if prevalidation returns any flaws in receipt
 - `OURCART_SENDING_ERROR` (default: "Something went wrong. Please try again later.") - error displayed if any problem occurred during prevalidation requests
 - `OURCART_PROCESSING_ERROR` (default: "Something went wrong. Please try again later.") - error displayed if any problem occurred in HTTP request like HTTP 403 in case of potential scam
 - `OURCART_DIMENSION_RATIO_ERROR` (default: "The receipt is too long. Please click Retake, choose Long Receipt, zoom in, and capture it in up to 6 parts.") - error displayed if result of cropping have ration of height to width over 6:1
 - `OURCART_looking_for_receipts` (default: "Looking for receipt...") - message during scanning when no receipt is present
 - `OURCART_AUTO_MANUAL_ON` (default: "No receipt found.\n Capture manually.") - during scanning for a receipt after 10s automatic capturing will be turn off and this message will be displayed
 - `OURCART_HOLD_STEADY` (default: "Hold your camera steady,\n we are capturing...") - shortly displayed when during scanning automatic capturing is taking a picture
 - `OURCART_AUTO_CAPTURE_ON` (default: "Auto-capture is on") - message during scanning when user manually switch mode to autocapture, photo will be taken automatically when receipt is found
 - `OURCART_AUTO_CAPTURE_OFF` (default: "Auto-capture is off") - message during scanning when user manually switch mode to manual capture, photo will be taken only after clicking on a button in the middle at the bottom
