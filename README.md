# receipt-scanner-android-demo

## Installation

Add to your `build.gradle.kts` dependencies:
```agsl
implementation("com.ourcart:receiptscanner:0.0.3")
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
- ### setS3Bucket (_optional_)
  This is only relavant for some customers, dont use it if not instructed by ourcart

  #### Arguments:
  - **bucket name**  (_String_) - name of bucket provided by ourcart
    Example:
```java
receiptScanner.setS3Bucket("new bucket name");
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
- **ourcartPrimaryColor** (default: `#92C91F`) - color of links, action buttons, and many ornaments, should be the main color of the company
- **ourcartTextColor** (default: `#333333`) - color of almost all texts
- **ourcartTextActionButtonColor** (default: `#ffffff`) - color of text on action buttons, override only if primaryColor is to light and white text will not be readable on it as a background
- **ourcartBackgroundPrimaryColor** (default: `#F9F9F9`) - background of main app actions
- **ourcartBackgroundSecondaryColor** (default: `#ffffff`) - action bars and modals
- **ourcartTileButtonBackgroundColor** (default: `#ffffff`) - background of tile buttons at the first view (ping in the example)
- **ourcartTileButtonTextColor** (default: `ourcartTextColor`) - change only if you dont want it to be "text color", color of text at the "tile buttons"
- **ourcartTileButtonIconColor** (default: `ourcartPrimaryColor`) - change only if you dont want it to be "primary color", color of icon at the "tile buttons"
- **ourcartLinkColor** (default: `ourcartPrimaryColor`) - change only if you dont want it to be "primary color", color of clicable text like "Tutorial", "Retake", "Back", so actions that are not buttons

## Customization of font:
Same as color you can overwrite font by adding “`ourcartFontFamily`” to styles, default font is `Poppins`.

Example:  
Add to your styles
```xml
<item name="ourcartFontFamily" type="font">@font/poppins</item>
```

