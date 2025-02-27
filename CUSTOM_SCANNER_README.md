# receipt-scanner-android-demo

## Installation

Add to your `build.gradle.kts` dependencies:
```agsl
implementation("com.ourcart:receiptscanner:1.2.1")
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
This is quick example of how to run scanner and send receipts to ourcart.
```java
ReceiptScanner.ApiConfig apiConfig = new ReceiptScanner.ApiConfig();
apiConfig.isProd = false;
apiConfig.apiKey = "<api key>";
apiConfig.clientCountry = "US";
apiConfig.clientCode = "<client code>";
apiConfig.clientUserId = "cab123";

ReceiptScanner.ScannerConfig scannerConfig = new ReceiptScanner.ScannerConfig() {
  @Override
  public void onHelpClick(Context ctx) {
    Log.e("TAG", "onHelpClick");
  }

  @Override
  public void onReceiptSnapped(List<Bitmap> bitmaps, Context ctx) {
    ReceiptScanner.sendReceipt(bitmaps, apiConfig).thenAccept(edgeData -> {
      Log.e("TAG", "Files sended");
    }).exceptionally((e) -> {
      Log.e("TAG", "error");
      return null;
    });
  }

  @Override
  public void onCloseClicked(Context ctx) {
    Log.e("TAG", "onCloseClicked");
  }
};

ReceiptScanner.startScanner(getContext(), uiSettings, scannerConfig);
```


## ReceiptScanner documentation
`ReceiptScanner` have static methods that can allow you to run scanner, validate, get border points, and send receipt to ourcart:

- ### ReceiptScanner.startScanner:
  Starts scanner activity

    #### Arguments:
  - **context** (_Context_) - current context
  - **uiSettings** (_UISettings_)(Optional) - instance with changes to Ui, color, fonts, text sizes, and icons for every place
  - **scannerConfig** (_ScannerConfig_) - instance with methods for handling user interactions and default mode

- ### ReceiptScanner.validateReceipt
  Validates bitmaps returns instance of ValidationResult

    #### Arguments:
    - **bitmaps** (_List&lt;Bitmap>_)

- ### ReceiptScanner.validateReceipt
  Validates pdf from uri returns instance of ValidationResult.
  Can throw `FileTypeException` if file is not a pdf, or `FileSizeException` if dile is over 12 MB.

  #### Arguments:
  - **context** (_Context_) - current context
  - **pdfFileUri** (_Uri_) - uri of pdf

- ### ReceiptScanner.sendReceipt
  Send bitmaps to ourcart

  #### Arguments:
  - **bitmaps** (_List&lt;Bitmap>_)
  - **apiConfig** (_ApiConfig_) - config specific for a client

- ### ReceiptScanner.sendReceipt
  Send pdf to ourcart.
  Can throw `FileTypeException` if file is not a pdf, or `FileSizeException` if dile is over 12 MB.

  #### Arguments:
  - **context** (_Context_) - current context
  - **pdfFileUri** (_Uri_) - uri of pdf
  - **apiConfig** (_ApiConfig_) - config specific for a client

- ### ReceiptScanner.getEdgePointsData
  Takes list of bitmaps and returns list of `EdgeData` instances with bitmap and points that are edges of the receipt in order: top-left, top-right, bottom-left, bottom-right/
  If no receipt was detected the points will be on the corners of bitmap.

  #### Arguments:
  - **bitmaps** (_List&lt;Bitmap>_)
  - **withCroppedBitmaps** (_List&lt;boolean>_)(_Optional_) - if set to true `EdgeData` will contain an already cropped bitmap in `croppedBitmap`

  - ### ReceiptScanner.cropBitmap
    Takes list of bitmaps and returns list of `EdgeData` instances with bitmap and points that are edges of the receipt in order: top-left, top-right, bottom-left, bottom-right/
    If no receipt was detected the points will be on the corners of bitmap.

    #### Arguments:
    - **bitmap** (_Bitmap_)
    - **points** (_Map<Integer, PointF>_) - edges of the receipt in order edges of the receipt in order (like from `EdgeData`): 
      - 1: top-left
      - 2: top-right
      - 3: bottom-left
      - 4: bottom-right


## ApiConfig documentation
`ApiConfig` instance of this class must me provided to send files to ourcart, all fields must me set:

Example:
```java
ReceiptScanner.ApiConfig apiConfig = new ReceiptScanner.ApiConfig();
apiConfig.isProd = false;
apiConfig.apiKey = Config.API_KEY;
apiConfig.clientCountry = Config.COUNTRY_CODE;
apiConfig.clientCode = Config.CLIENT_CODE;
apiConfig.clientUserId = Config.CLIENT_USER_ID;
```
### **isProd** (_boolean_) 
determine should be send to production or staging environment.
### **apiKey** (_string_)
string for Ourcart requests, must match country, environment, and clientCode, provided to you by Ourcart
### **clientCountry** (_string_)
country code for Ourcart requests, provided to you by Ourcart
### **clientCode** (_string_)
client code for Ourcart requests, provided to you by Ourcart
### **clientUserId** (_string_)
id of client to be sended and associated with receipts, it can be any string but have it be a real string associated with currently logged in user, it will help us block fraudulent users and will provide consistent data.

## ScannerConfig documentation
`ScannerConfig` instance of this class must me provided to handle user interactions and output from scanner Activity.
### **isRetakeMode** (_boolean_)
Set to `true` if you wan scanner to be in "Retake mode", it is for retaking one picture without automatic capturing and ability to change the mode to "long receipt".
After taking one picture activity will be finished and `onReceiptSnapped` will be executed.
### **isLongMode** (_boolean_)
Should scanner be in "`Long receipt mode`" by default.

### **capturingDuration** (_int_)
Time in  of milliseconds before image will be capture when valid receipt have been detected during automatic mode
### **enableAutomaticMode** (_boolean_)
Should Automatic Mode be enabled for `Regular receipt mode`
### **validateAngle** (_boolean_)
Should angel be checked.


### **onHelpClick** (_(Context ctx) -> void_)
Callback method executed by clicking on "help" icon in top right, gives access current context.
### **onCloseClicked** (_(Context ctx) -> void_)
Callback method executed by clicking on "Close" icon in top left, gives access current context.
### **onReceiptSnapped** (_(List<Bitmap> bitmaps, Context ctx) -> void_)
Callback method executed when Clicking on "`Next`" button in "`Long receipt mode`", or taking a single picture in "`Regular mode`" eider by `auto` or `manual` capture.

## UISettings documentation
`UISettings` instance of this class must me provided to set parameters of every part of scanner Activity.
### **showHelpIcon** (_boolean_) (default: true)
Should help icon be displayed
### **showTargetBorder** (_boolean_) (default: true)
Should aiming help (suggested place the receipt should be in) be displayed
### **targetBorderColor** (_Integer_) (default: #ffffff)
Color of aiming help (suggested place the receipt should be in) be displayed
### **closeDrawable** (_Drawable_)
Icon to be displayed instead of "Close" icon at top-left
### **torchOnDrawable** (_Drawable_)
Icon to be displayed instead of "TorchOn" icon at top-right when torch is on
### **torchOffDrawable** (_Drawable_)
Icon to be displayed instead of "TorchOff" icon at top-right when torch is off
### **helpDrawable** (_Drawable_)
Icon to be displayed instead of "Help" icon at top-right
### **modeBtnActiveBackgroundColor** (_Integer_) (default: @color/ourcartPrimaryColor)
Color of background of active mode button that allows you to switch between regular and long receipt placed at bottom center on top of snap mode.
### **modeBtnActiveFontColor** (_Integer_) (default: #ffffff)
Color of font of active mode button that allows you to switch between regular and long receipt placed at bottom center on top of snap mode.
### **modeBtnInactiveFontColor** (_Integer_) (default: @color/ourcartTextColor)
Color of font of inactive mode button that allows you to switch between regular and long receipt placed at bottom center on top of snap mode.
### **modeBtnsBackgroundColor** (_Integer_) (default: #ffffff)
Background color of both mode button that allows you to switch between regular and long receipt placed at bottom center on top of snap mode.
### **modeBtnsFontFamily** (_Typeface_) (default: @font/ourcartFontFamily)
Font of both mode button that allows you to switch between regular and long receipt placed at bottom center on top of snap mode.
### **modeBtnsFontSize** (_Integer_) (default: 13sp)
Font size in "sp" of both mode button that allows you to switch between regular and long receipt placed at bottom center on top of snap mode.
### **snapBtnAutomaticCaptureModeColor** (_Integer_) (default: @ourcart/ourcartScannerAutomaticCaptureSnapBtnBackgroundColor)
Color of snap button located always at bottom center during automatic capture mode, the button itself in inactive in this state
### **snapBtnAutomaticCaptureModeRingColor** (_Integer_) (default: @ourcart/ourcartScannerAutomaticCaptureSnapBtnBackgroundColor)
Color of progress animation around snap button located always at bottom center during automatic capture mode, the button itself in inactive in this state
### **snapBtnManualCaptureModeColor** (_Integer_) (default: @ourcart/ourcartScannerManualCaptureSnapBtnBackgroundColor)
Color of snap button located always at bottom center during manual capture mode, the button is active, can be clicked, and will take picture
### **snapBtnManualCaptureModeRingColor** (_Integer_) (default: @ourcart/ourcartScannerManualCaptureSnapBtnBackgroundColor)
Color of ring around snap button located always at bottom center during manual capture mode, the button is active, can be clicked, and will take picture
### **snapBtnAutoCapturingColor** (_Integer_) (default: #ffffff)
Color of snap button located always at bottom center during automatic capture mode when picture is being captured (2s), the button itself in inactive in this state     
### **snapBtnAutoCapturingRingColor** (_Integer_) (default: @color/ourcartPrimaryColor)
Color of ring around, snap button located always at bottom center during automatic capture mode when picture is being captured (2s), the button itself in inactive in this state
### **automaticCaptureDrawable** (_Drawable_) 
Icon displayed on top of, snap button located always at bottom center during automatic capture mode, the button itself in inactive in this state
### **manualCaptureDrawable** (_Drawable_)
Icon displayed on top of, snap button located always at bottom center during manual capture mode, the button itself in active in this state, and will take picture
### **nextBtnBackgroundColor** (_Integer_) (default: @color/ourcartPrimaryColor)
Color of "Next" button that confirms snapping of all parts of receipt in long receipt mode Displayed at bottom right during long receipt mode when at least one picture has been snapped.
### **nextBtnFontColor** (_Integer_) (default: #ffffff)
Color of text for "Next" button that confirms snapping of all parts of receipt in long receipt mode Displayed at bottom right during long receipt mode when at least one picture has been snapped.
### **nextBtnFontFamily** (_Typeface_) (default: @font/ourcartFontFamily)
Font for text of "Next" button that confirms snapping of all parts of receipt in long receipt mode. Displayed at bottom right during long receipt mode when at least one picture has been snapped.
### **nextBtnFontSize** (_Integer_) (default: 16sp)
Text size of "Next" button that confirms snapping of all parts of receipt in long receipt mode. Displayed at bottom right during long receipt mode when at least one picture has been snapped.
### **imageCounterBackgroundColor** (_Integer_) (default: @color/ourcartPrimaryColor)
Color of background of image counter in long receipt mode. Displayed at bottom left during long receipt mode.
### **imageCounterFontColor** (_Integer_) (default: #ffffff)
Color of text of image counter in long receipt mode. Displayed at bottom left during long receipt mode.
### **imageCounterFontFamily** (_Typeface_) (default: @font/ourcartFontFamily)
Text font of image counter in long receipt mode. Displayed at bottom left during long receipt mode.
### **imageCounterFontSize** (_Integer_) (default: 14sp)
Text size of image counter in long receipt mode. Displayed at bottom left during long receipt mode.
### **feedbackBackgroundColor** (_Integer_) (default: @color/ourcartScannerFeedbackBackgroundColor)
Color of background for feedback messages displayed in type middle of screen in many situations.
### **feedbackFontColor** (_Integer_) (default: @color/ourcartPrimaryColor)
Text color for feedback messages displayed in type middle of screen in many situations.
### **feedbackFontColor** (_Typeface_) (default: @font/ourcartFontFamily)
Font text of feedback messages displayed in type middle of screen in many situations.
### **feedbackFontSize** (_Integer_) (default: 14sp)
Text size of feedback messages displayed in type middle of screen in many situations.
### **capturingProgressBorderColor** (_Integer_) (default: @color/ourcartPrimaryColor)
Color of progress border being drawn around the receipt during automatic capture mode when picture is being captured (2s),
### **receiptShadowColor** (_Integer_) (default: rgba(90, 255, 255, 255))
Color of transparent layer applied when receipt have been detected.
### **receiptShadowBorderColor** (_Integer_) (default: #ffffff)
Color of Border around transparent layer applied when receipt have been detected.

## ValidationResult documentation
Instance of of `ValidationResult` are returned by validators.
### **isBlurry** (_Boolean_)
Is receipt too blurry
### **hasPoorLighting** (_Boolean_)
Is receipt too dark
### **hasNoText** (_Boolean_)
Is has no text
### **noRetailer** (_Boolean_)
Is receipt has no retailer
### **noDate** (_Boolean_)
Is receipt has no date
### **noTotal** (_Boolean_)
Is receipt has no items / total

## EdgeData documentation
Instance of of `EdgeData` are returned by getEdgePointsData.
### **borderPoints** (_Map<Integer, PointF>_)
4 border points of receipt on the bitmap in order:

- 1: top-left
- 2: top-right
- 3: bottom-left
- 4: bottom-right

If no receipt was detected the points will be on the corners of bitmap.
### **bitmap** (_Bitmap_)
Reference to bitmap that was analyzed
### **croppedBitmap** (_Bitmap_)
Bitmap already cropped to the points, must be specified that you want those or it will be null


## Customization of colors:
You can customize colors eider by setting them in instance of `UISettings` and passing to `ReceiptScanner.startScanner`, or by overwriting 5 global colors. The latter approach is recommended since it requires much les configuration.
To do it go to file colors.xml and set new values:

Example:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="ourcartPrimaryColor">#ff0000</color>
    <color name="ourcartTextColor">#0000ff</color>
    <color name="ourcartScannerFeedbackBackgroundColor">#F1EBFA</color>
    <color name="ourcartScannerAutomaticCaptureSnapBtnBackgroundColor">#ffffff</color>
    <color name="ourcartScannerManualCaptureSnapBtnBackgroundColor">#F2B700</color>
</resources>
```

### Colors to customize:
  - **ourcartPrimaryColor** (default: `#56246F`) - primary color of most active components
  - **ourcartTextColor** (default: `#333333`) - color of inactive mode button
  - **ourcartScannerFeedbackBackgroundColor** (default: `#F1EBFA`) - Color of background for feedback messages displayed in type middle of screen in many situations.
  - **ourcartScannerAutomaticCaptureSnapBtnBackgroundColor** (default: `#ffffff`) - Color of snap button and the animation spinning around it located always at bottom center during automatic capture mode, the button itself in inactive in this state
  - **ourcartScannerManualCaptureSnapBtnBackgroundColor** (default: `#F2B700`) - Color of snap button and ring around it located always at bottom center during manual capture mode, the button is active, can be clicked, and will take picture

## Customization of font:
Same as color you can overwrite font globally by adding “`ourcartFontFamily`” to styles, default font is `Poppins`.
Make sure that font you picking is an xml describing `font-family` resource.
Or customize every font displayed by setting them in instance of `UISettings` and passing to `ReceiptScanner.startScanner`

Example:  
Add to your styles
```xml
<item name="ourcartFontFamily" type="font">@font/poppins</item>
```

![readme_font](https://s3.us-east-1.amazonaws.com/ourcart.platform.assets/images/font_files.png)

Example font file `@font/poppins` (`src/res/font/poppins.xml` use this file to overwrite font of sdk, of course name it differently appropriately to your font): 
```xml
<?xml version="1.0" encoding="utf-8"?>
<font-family xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto">

    <font
      android:font="@font/poppins_thin"
      android:fontStyle="normal"
      android:fontWeight="100"
      app:font="@font/poppins_thin"
      app:fontStyle="normal"
      app:fontWeight="100" />
    <font
      android:font="@font/poppins_extralight"
      android:fontStyle="normal"
      android:fontWeight="200"
      app:font="@font/poppins_extralight"
      app:fontStyle="normal"
      app:fontWeight="200" />
    <font
      android:font="@font/poppins_light"
      android:fontStyle="normal"
      android:fontWeight="300"
      app:font="@font/poppins_light"
      app:fontStyle="normal"
      app:fontWeight="300" />
    <font
      android:font="@font/poppins_regular"
      android:fontStyle="normal"
      android:fontWeight="400"
      app:font="@font/poppins_regular"
      app:fontStyle="normal"
      app:fontWeight="400" />
    <font
      android:font="@font/poppins_medium"
      android:fontStyle="normal"
      android:fontWeight="500"
      app:font="@font/poppins_medium"
      app:fontStyle="normal"
      app:fontWeight="500" />
    <font
      android:font="@font/poppins_semibold"
      android:fontStyle="normal"
      android:fontWeight="600"
      app:font="@font/poppins_semibold"
      app:fontStyle="normal"
      app:fontWeight="600" />
    <font
      android:font="@font/poppins_bold"
      android:fontStyle="normal"
      android:fontWeight="700"
      app:font="@font/poppins_bold"
      app:fontStyle="normal"
      app:fontWeight="700" />
    <font
      android:font="@font/poppins_extrabold"
      android:fontStyle="normal"
      android:fontWeight="800"
      app:font="@font/poppins_extrabold"
      app:fontStyle="normal"
      app:fontWeight="800" />
    <font
      android:font="@font/poppins_black"
      android:fontStyle="normal"
      android:fontWeight="900"
      app:font="@font/poppins_black"
      app:fontStyle="normal"
      app:fontWeight="900" />
</font-family>
```
Those should be actual font files (like `.ttf` and `.otf`) meant to be used for specific weight `android:fontWeight`:
```xml
android:font="@font/poppins_extrabold" <!-- file "src/res/font/poppins_extrabold.ttf" -->
app:font="@font/poppins_extrabold" <!-- file "src/res/font/poppins_extrabold.ttf" -->
```
if you want just the "Bold" and "Normal" assign 
- same file of normal weight for `android:fontWeight`: 100, 200, 300, 400, 500
- same file of bold weight for `android:fontWeight`: 600, 700, 800, 900

## Customization of text:
Similarly to colors receipt scanner allows you to overwrite texts. To do it go to file `strings.xml` and set new values.

Example:
Add to your `strings.xml`
```xml
<string name="OURCART_AUTO_CAPTURE_ON">Auto-capture is on from no on</string>
```

All texts with default values in xml format:
```xml
<string name="OURCART_adjust_angle">The angle is incorrect. Hold your camera directly above the receipt.</string>
<string name="OURCART_move_closer">Move closer so more of the receipt will be in the picture.</string>
<string name="OURCART_bad_lighting">Lighting is too low. Please move to a brighter area or turn on more lights.</string>
<string name="OURCART_shake_detected">Camera shake detected. Please try to hold your device steadier or use support to stabilize your camera.</string>
<string name="OURCART_looking_for_receipts">Looking for receipt...</string>
<string name="OURCART_HOLD_STEADY">Hold your camera steady,\n we are capturing...</string>
<string name="OURCART_AUTO_MANUAL_ON">No receipt found.\n Capture manually.</string>
<string name="OURCART_AUTO_CAPTURE_ON">Auto-capture is on</string>
<string name="OURCART_AUTO_CAPTURE_OFF">Auto-capture is off</string>
<string name="OURCART_REGULAR_RECEIPT_MODE">Regular receipt mode</string>
<string name="OURCART_LONG_RECEIPT_MODE">Long receipt mode</string>
<string name="OURCART_regular_receipt">Regular Receipt</string>
<string name="OURCART_long_receipt">Long Receipt</string>
<string name="OURCART_next">Next</string>
```

#### In the text there are tags similar to html ones:
- `<b>` - make the text bold, also add attribute to string `formatted="false"` may not work without it
- `<u>` - add underline to text
