# receipt-scanner-android-demo

## Concept
The Ourcart SDK provides a complete toolkit for seamless receipt capture and processing. It helps users easily upload high-quality receipts to the Ourcart API through three core components:
1. Camera Component (ReceiptScanner) - A visual interface with features to guide users in capturing quality receipt photos:
 - Edge detection with visual feedback
 - Real-time angle verification
 - Auto-capture with manual override option
 - Multi-snapshot support for long receipts (manual switching)
 - Customizable UI elements
 - Automatic toggle to manual scanning after a configurable delay
2. Cropping Component (EdgeData) - A non-visual module for intelligent image processing:
 - Automatic receipt detection in snapshots
 - Corner point identification for precise cropping
 - Support for long receipts where corners touch image edges
 - Receipt straightening with adjusted ratio output
3. Receipt API Component (sendReceipt) - a non-visual module handling the backend communication:
 - Image optimization (resizing and compression)
 - Secure upload handling
 - Integration with Ourcart's backend processing

Clip showing the capabilities of the camera component:

https://github.com/user-attachments/assets/9fa77060-1a6a-497a-992e-61d4ec5dc64b



## Installation

Add to your `build.gradle.kts` dependencies:
```agsl
implementation("com.ourcart:receiptscanner:1.9.1")
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

 ### Supported Android versions
- #### The recommended version of Android is 8 or higher (API level 26).

## Quickstart
🔧 This is quick example of how to run scanner and send receipts to ourcart.
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
      Log.e("TAG", "Files sent");
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
`ReceiptScanner` have static methods that can allow you to run scanner, validate, get corner points, and send receipt to Ourcart backend API:

- ### ReceiptScanner.startScanner:
  Starts scanner activity
  - #### Input:
    - **context** (_Context_) - current context
    - **uiSettings** (_UISettings_)(Optional) - instance with changes to Ui, color, fonts, text sizes, and icons for every place
    - **scannerConfig** (_ScannerConfig_) - instance with methods for handling user interactions and default mode
  - #### Output:
    - void

## 📤 ReceiptScanner sendReceipts
- ### ReceiptScanner.sendReceipt - bitmaps
  Send **bitmaps** to Ourcart
  - #### Input:
    - **bitmaps** (_List&lt;Bitmap>_) - max 6 bitmaps
    - **apiConfig** (_ApiConfig_) - config specific for a client
  - #### Output:
    - **CompletableFuture &lt;Boolean>** - if the `CompletableFuture` is completed `exceptionally` the Exception is passed, it will contains message from server in case of HTTP request error.

- ### ReceiptScanner.sendReceipt - pdf
  Send **pdf** to ourcart.
  - #### Input:
    - **context** (_Context_) - current context
    - **pdfFileUri** (_Uri_) - uri of pdf
    - **apiConfig** (_ApiConfig_) - config specific for a client
  - #### Output:
    - **CompletableFuture &lt;Boolean>** - if the `CompletableFuture` is completed `exceptionally` the Exception is passed, it will contains message from server in case of HTTP request error.
  - #### Throws:
    - `FileTypeException` - thrown if file is not a pdf
    - `FileSizeException` - thrown if file is over 12 MB
    - `IOException` - thrown if file cannot be read

## ✂️Edge Detection & Cropping
- ### ReceiptScanner.getEdgePointsData
  Takes list of bitmaps and returns list of `EdgeData` instances with bitmap and points that are corners of the receipt in order: 
  
  top-left. top-right, bottom-left, bottom-right.

  If no receipt was detected the points will be on the corners of bitmap.
  - #### Input:
    - **bitmaps** (_List&lt;Bitmap>_)
    - **withCroppedBitmaps** (_List&lt;boolean>_)(_Optional_) - if set to true `EdgeData` will contain an already cropped bitmap in `croppedBitmap`
  - #### Output:
    - CompletableFuture<List<`ImageEdgeDetector.EdgeData`>>

- ### ReceiptScanner.cropBitmap
    Crops bitmap to specific points.
    - #### Input:
      - **bitmap** (_Bitmap_)
      - **points** (_Map<Integer, PointF>_) - edges of the receipt in order edges of the receipt in order (like from `EdgeData`): 
        - 1: top-left
        - 2: top-right
        - 3: bottom-left
        - 4: bottom-right
  - #### Output:
   - Bitmap

## 🟢 Validation of receipts
It requires ML model that needs to be downloaded. First run `preValidationInit` early so it can check and download the model, then check if the model is present with `getPreValidationStatus` and if it is, run the validation with `validateReceipt`.
- ### ReceiptScanner.preValidationInit
  Method that will update/download ML model used in validation, it will not block ability to validate on a old version of ML model, it is suggested to run this method early during startup of application.
  - #### Input:
    - **context** (_Context_)
    - **config** (_PreInitValidationConfig_) - config for pre-init, it have 3 settings `apiKey`, `isProduction` and  `requireWifi`
    - **onModelAvailable** (_Consumer&lt;Boolean>_)(_Optional_) - Callback executed when updated is performed successfully or if it is not needed. It gets a boolean value that indicates has model been updated or not.
    - **onError** (_Consumer&lt;Exception>_)(_Optional_) - Callback executed when error occurred. 
  - #### Output:
    - void

Example:
```java
  ImageValidator.PreInitValidationConfig validationConfig = new ImageValidator.PreInitValidationConfig();
  validationConfig.isProduction = false;
  validationConfig.apiKey = Config.API_KEY);
  validationConfig.requireWifi = true;

  ReceiptScanner.preValidationInit(
      getContext(),
      validationConfig,
      (hasUpdatePreformed) -> {
          Toast.makeText(
                getContext(),
                hasUpdatePreformed ? "New model downloaded, and updated" : "No update needed, newest ML model version already present",
                Toast.LENGTH_LONG
          ).show();
      },
      (e) -> {
          if (e instanceof ImageValidator.WifiDisabledException) {
              Toast.makeText(getContext(), "Wifi needs to be enabled", Toast.LENGTH_LONG).show();
          } else {
              Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
          }
  });
```

- ### ReceiptScanner.getPreValidationStatus
  Method that checks the Possibility of performing validation check. Only `ValidationStatus.NOT_AVAILABLE` indicates that it is not possible.

  - #### Output:
    - `ValidationStatus` - Enum that have 3 possible values:
      - `AVAILABLE` - newest version of the model is present, validation is possible
      - `NOT_AVAILABLE` - no ML model is present, validation is **NOT** possible
      - `AVAILABLE_UPDATING` - a version of ML model is present, update is in progress, validation is possible


- ### ReceiptScanner.validateReceipt - BITMAPS
  - #### Input:
    - **context** (_Context_)
    - **bitmaps** (_List&lt;Bitmap>_)
  - #### Output:
    - CompletableFuture<ImageValidator.ValidationResult> - ValidationResult contains fields `noRetailer`, `noDate`, `noTime` and `noTotal`
  - #### Throws:
    - `ModelUnavailableException` - thrown if ML model is not available

Example:
```java
if (ReceiptScanner.getPreValidationStatus(getContext()) != ValidationStatus.NOT_AVAILABLE) {
    try {
        v.setEnabled(false);
        ReceiptScanner.validateReceipt(getContext(), bitmaps)
                .thenAccept((results) -> {
                     StringBuilder sb = new StringBuilder();
                     sb.append("noDate: " + validationResult.noDate + "\n");
                     sb.append("noTime: " + validationResult.noTime + "\n");
                     sb.append("noRetailer: " + validationResult.noRetailer + "\n");
                     sb.append("noTotal: " + validationResult.noTotal + "\n");

                     Log.i(TAG, sb.toString());
                });
    } catch (ImageValidator.ModelUnavailableException e) {
        // first line ("if") makes this error impossible
        Toast.makeText(getContext(), "No model available", Toast.LENGTH_LONG).show();
    }
}
```

- ### ReceiptScanner.validateReceipt - PDF
  - #### Input:
    - **context** (_Context_)
    - **pdfFileUri** (_Uri_)
  - #### Output:
    - CompletableFuture<ImageValidator.ValidationResult> - ValidationResult contains fields `hasNoText`, `noRetailer`, `noDate`, `noTime` and `noTotal`
  - #### Throws:
    - `ModelUnavailableException` - thrown if ML model is not available
    - `FileTypeException` - thrown if file is not a pdf
    - `FileSizeException` - thrown if file is over 12 MB
    - `IOException` - thrown if file cannot be read

Example:
```java
if (ReceiptScanner.getPreValidationStatus(getContext()) != ValidationStatus.NOT_AVAILABLE) {
    try {
        v.setEnabled(false);
        ReceiptScanner.validateReceipt(getContext(), pdfUri)
                .thenAccept((validationResult) -> {
                     StringBuilder sb = new StringBuilder();
                     sb.append("hasNoText: " + validationResult.hasNoText + "\n");
                     sb.append("noDate: " + validationResult.noDate + "\n");
                     sb.append("noTime: " + validationResult.noTime + "\n");
                     sb.append("noRetailer: " + validationResult.noRetailer + "\n");
                     sb.append("noTotal: " + validationResult.noTotal + "\n");

                     Log.i(TAG, sb.toString());
                });
    } catch (FileService.FileTypeException e) {
        Toast.makeText(getContext(), "Wrong file type", Toast.LENGTH_LONG).show();
    } catch (FileService.FileSizeException e) {
        Toast.makeText(getContext(), "File too large", Toast.LENGTH_LONG).show();
    } catch (ImageValidator.ModelUnavailableException e) {
        // first line ("if") makes this error impossible
        Toast.makeText(getContext(), "No model available", Toast.LENGTH_LONG).show();
    } catch (IOException e) {
        Toast.makeText(getContext(), "Read file error", Toast.LENGTH_LONG).show();
    }
}
```
## PreInitValidationConfig documentation
📌 `PreInitValidationConfig` instance of this class must me provided for `ReceiptScanner.preValidationInit`

Example:
```java
ImageValidator.PreInitValidationConfig validationConfig = new ImageValidator.PreInitValidationConfig();
validationConfig.isProduction = false;
validationConfig.apiKey = "<api key>";
validationConfig.requireWifi = true;
```
- ### **isProduction** (_boolean_)(_default: false_)
  determine should be send to production or staging environment.
- ### **apiKey** (_String_)
  String required for client in order to access Ourcart api.
- ### **requireWifi** (_string_)(_default: false_)
  If `true` the update/download will only be performed when WIFI on enabled, and on Error will be executed with `WifiDisabledException`

## ApiConfig documentation
📌 `ApiConfig` instance of this class must me provided to send files to Ourcart, all fields must me set:

Example:
```java
ReceiptScanner.ApiConfig apiConfig = new ReceiptScanner.ApiConfig();
apiConfig.isProd = false;
apiConfig.apiKey = Config.API_KEY;
apiConfig.clientCountry = Config.COUNTRY_CODE;
apiConfig.clientCode = Config.CLIENT_CODE;
apiConfig.clientUserId = Config.CLIENT_USER_ID;
```
- ### **isProd** (_boolean_) 
  determine should be send to production or staging environment.
- ### **apiKey** (_string_)
  string for Ourcart requests, must match country, environment, and clientCode, provided to you by Ourcart
- ### **clientCountry** (_string_)
  country code for Ourcart requests, provided to you by Ourcart
- ### **clientCode** (_string_)
  client code for Ourcart requests, provided to you by Ourcart
- ### **clientUserId** (_string_)
  id of client to be sent and associated with receipts, it can be any string but have it be a real string associated with currently logged in user, it will help us block fraudulent users and will provide consistent data.

## ScannerConfig documentation
⚙️ `ScannerConfig` instance of this class must me provided to handle user interactions and output from scanner Activity.
- ### **isRetakeMode** (_boolean_)
  Set to `true` if you want scanner to be in "Retake mode", it is for retaking one picture without automatic capturing and ability to change the mode to "long receipt".
After taking one picture activity will be finished and `onReceiptSnapped` will be executed.
- ### **isLongMode** (_boolean_)
  Should scanner be in "`Long receipt mode`" by default.

- ### **checkLongReceiptOnSnapAutoMode** (_boolean_) (_default: false_)
  Should check is the entire receipt is visible and not too lengthy during auto snap `Regular receipt mode`
- ### **imageTooLengthyRatio** (_float_) (_default: 6_)
  Ratio of height to width if more then that it will be considered too long for 1 picture. In `Regular receipt mode` it will display message and switch to `Long receipt mode` for if `checkLongReceiptOnSnapAutoMode` is set to true.

- ### **capturingDuration** (_int_) (_default: 2000_)
  Time in milliseconds before image will be captured when valid receipt have been detected during automatic mode (time to get camera focus)
- ### **switchToManualTimeout** (_int_) (_default: 10000_)
  Time in milliseconds before app will switch to "manual capturing" if no receipt will be detected in automatic mode for regular receipt
- ### **enableAutomaticMode** (_boolean_)
  Should Automatic Mode be enabled for `Regular receipt mode`
- ### **validateAngle** (_boolean_)
  Should angle be checked.

🔧 Callback methods
- ### **onHelpClick** (_(Context ctx) -> void_)
  Callback method executed by clicking on "help" icon in top right, gives access current context.
- ### **onCloseClicked** (_(Context ctx) -> void_)
  Callback method executed by clicking on "Close" icon in top left, gives access current context.
- ### **onReceiptSnapped** (_(List<Bitmap> bitmaps, Context ctx) -> void_)
  Callback method executed when Clicking on "`Next`" button in "`Long receipt mode`", or taking a single picture in "`Regular mode`" either by `auto` or `manual` capture.
- ### **onEvent** (_(ScannerEvent event) -> void_)
  Callback executed on user events gets the enum of events:
```java
package com.ourcart.receiptscanner.enums;

public enum ScannerEvent {
  TORCH_ON, // event on enabling torch by user
  TORCH_OFF,// event off enabling torch by user
  LONG_RECEIPT_MODE, // event on clicking on "Long receipt" button by user
  REGULAR_RECEIPT_MODE, // event on clicking on "Regular receipt" button by user
  LONG_RECEIPT_PHOTO, // event on clicking on taking photo in "Long receipt" mode by user
  MANUAL_MODE, // event on automatic switching to manual capture in regular mode if no image was detected
}
```

## UISettings documentation
🎨 `UISettings` instance of this class must me provided to set parameters of every part of scanner Activity.

- ### **showHelpIcon** (_boolean_) (default: true)
  Should help icon be displayed
- ### **showTargetBorder** (_boolean_) (default: true)
  Should aiming help (suggested place the receipt should be in) be displayed
- ### **targetBorderColor** (_Integer_) (default: #ffffff)
  Color of aiming help (suggested place the receipt should be in) be displayed
- ### **closeDrawable** (_Drawable_)
  Icon to be displayed instead of "Close" icon at top-left
- ### **torchOnDrawable** (_Drawable_)
  Icon to be displayed instead of "TorchOn" icon at top-right when torch is on
- ### **torchOffDrawable** (_Drawable_)
  Icon to be displayed instead of "TorchOff" icon at top-right when torch is off
- ### **helpDrawable** (_Drawable_)
  Icon to be displayed instead of "Help" icon at top-right

### 📌 Switch Icons (Mode Buttons)
- ### **modeBtnActiveBackgroundColor** (_Integer_) (default: @color/ourcartPrimaryColor)
  Color of background of active mode button that allows you to switch between regular and long receipt placed at bottom center on top of snap mode.
- ### **modeBtnActiveFontColor** (_Integer_) (default: #ffffff)
  Color of font of active mode button that allows you to switch between regular and long receipt placed at bottom center on top of snap mode.
- ### **modeBtnInactiveFontColor** (_Integer_) (default: @color/ourcartTextColor)
  Color of font of inactive mode button that allows you to switch between regular and long receipt placed at bottom center on top of snap mode.
- ### **modeBtnsBackgroundColor** (_Integer_) (default: #ffffff)
  Background color of both mode button that allows you to switch between regular and long receipt placed at bottom center on top of snap mode.
- ### **modeBtnsFontFamily** (_Typeface_) (default: @font/ourcartFontFamily)
  Font of both mode button that allows you to switch between regular and long receipt placed at bottom center on top of snap mode.
- ### **modeBtnsFontSize** (_Integer_) (default: 13sp)
  Font size in "sp" of both mode button that allows you to switch between regular and long receipt placed at bottom center on top of snap mode.

### 🎯 Shutter Button
- ### **snapBtnAutomaticCaptureModeColor** (_Integer_) (default: @ourcart/ourcartScannerAutomaticCaptureSnapBtnBackgroundColor)
  Color of the snap button located always at bottom center during automatic capture mode, the button itself is inactive in this state
- ### **snapBtnAutomaticCaptureModeRingColor** (_Integer_) (default: @ourcart/ourcartScannerAutomaticCaptureSnapBtnBackgroundColor)
  Color of the progress animation around snap button located always at bottom center during automatic capture mode, the button itself is inactive in this state
- ### **snapBtnManualCaptureModeColor** (_Integer_) (default: @ourcart/ourcartScannerManualCaptureSnapBtnBackgroundColor)
  Color of the snap button located always at bottom center during manual capture mode, the button is active, can be clicked, and will take picture
- ### **snapBtnManualCaptureModeRingColor** (_Integer_) (default: @ourcart/ourcartScannerManualCaptureSnapBtnBackgroundColor)
  Color of the ring around snap button located always at bottom center during manual capture mode, the button is active, can be clicked, and will take picture
- ### **snapBtnAutoCapturingColor** (_Integer_) (default: #ffffff)
  Color of the snap button located always at bottom center during automatic capture mode when picture is being captured (2s), the button itself is inactive in this state     
- ### **snapBtnAutoCapturingRingColor** (_Integer_) (default: @color/ourcartPrimaryColor)
  Color of the ring around the snap button located always at bottom center during automatic capture mode when picture is being captured (2s), the button itself is inactive in this state
- ### **automaticCaptureDrawable** (_Drawable_) 
  Icon displayed on top of the snap button located always at bottom center during automatic capture mode, the button itself is inactive in this state
- ### **manualCaptureDrawable** (_Drawable_)
  Icon displayed on top of the snap button located always at bottom center during manual capture mode, the button itself is active in this state, and will take picture

### ⏭️ Next Button
- ### **nextBtnBackgroundColor** (_Integer_) (default: @color/ourcartPrimaryColor)
  Color of the "Next" button that confirms snapping of all parts of receipt in long receipt mode Displayed at bottom right during long receipt mode when at least one picture has been snapped.
- ### **nextBtnFontColor** (_Integer_) (default: #ffffff)
  Color of the text for "Next" button that confirms snapping of all parts of receipt in long receipt mode Displayed at bottom right during long receipt mode when at least one picture has been snapped.
- ### **nextBtnFontFamily** (_Typeface_) (default: @font/ourcartFontFamily)
  Font for the text of "Next" button that confirms snapping of all parts of receipt in long receipt mode. Displayed at bottom right during long receipt mode when at least one picture has been snapped.
- ### **nextBtnFontSize** (_Integer_) (default: 16sp)
  Text size of the "Next" button that confirms snapping of all parts of receipt in long receipt mode. Displayed at bottom right during long receipt mode when at least one picture has been snapped.

### 📸 Image Counter
- ### **imageCounterBackgroundColor** (_Integer_) (default: @color/ourcartPrimaryColor)
  Color of the background of the image counter in long receipt mode. Displayed at bottom left during long receipt mode.
- ### **imageCounterFontColor** (_Integer_) (default: #ffffff)
  Color of the text of image counter in long receipt mode. Displayed at bottom left during long receipt mode.
- ### **imageCounterFontFamily** (_Typeface_) (default: @font/ourcartFontFamily)
  Text font of the image counter in long receipt mode. Displayed at bottom left during long receipt mode.
- ### **imageCounterFontSize** (_Integer_) (default: 14sp)
  Text size of the image counter in long receipt mode. Displayed at bottom left during long receipt mode.

### 💬 Message Container
- ### **feedbackBackgroundColor** (_Integer_) (default: @color/ourcartScannerFeedbackBackgroundColor)
  Color of the background for feedback messages displayed in type middle of screen in many situations.
- ### **feedbackFontColor** (_Integer_) (default: @color/ourcartPrimaryColor)
  Text color of the feedback messages displayed in type middle of screen in many situations.
- ### **feedbackFontSize** (_Integer_) (default: 14sp)
  Text size of the feedback messages displayed in type middle of screen in many situations.
- ### **receiptShadowColor** (_Integer_) (default: rgba(90, 255, 255, 255))
  Color of the transparent layer applied when receipt have been detected.
- ### **receiptShadowBorderColor** (_Integer_) (default: #ffffff)
  Color of the torder around transparent layer applied when receipt have been detected.

## EdgeData documentation
⚙️ Instance of `EdgeData` is returned by getEdgePointsData.
- ### **borderPoints** (_Map<Integer, PointF>_)
  4 border points of the receipt on the bitmap in order:
  - 1: top-left
  - 2: top-right
  - 3: bottom-left
  - 4: bottom-right

  If no receipt was detected the points will be on the corners of bitmap.
- ### **bitmap** (_Bitmap_)
  Reference to bitmap that was analyzed
- ### **croppedBitmap** (_Bitmap_)
  Bitmap already cropped to the points, must be specified that you want those or it will be null


## Customization of colors:
⚙️ You can customize colors either by setting them in instance of `UISettings` and passing to `ReceiptScanner.startScanner`, or by overwriting 5 global colors.
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

## Customization of the font:
Same as the color you can overwrite font globally by adding “`ourcartFontFamily`” to styles, default font is `Poppins`.
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
<string name="OURCART_long_receipt_detected">Long receipt detected,\n please take all parts of receipt separately it in up to 6 parts.</string>
<string name="OURCART_adjust_angle">The angle is incorrect. Hold your camera directly above the receipt.</string>
<string name="OURCART_move_closer">Move closer so more of the receipt will be in the picture.</string>
<string name="OURCART_bad_lighting">Lighting is too low. Please move to a brighter area or turn on more lights.</string>
<string name="OURCART_shake_detected">Camera shake detected. Please try to hold your device steadier or use support to stabilize your camera.</string>
<string name="OURCART_looking_for_receipts">Looking for receipt...</string>
<string name="OURCART_HOLD_STEADY">Hold your camera steady,\n we are capturing...</string>
<string name="OURCART_AUTO_MANUAL_ON">No receipt found.\n Capture manually.</string>
<string name="OURCART_REGULAR_RECEIPT_MODE">Regular receipt mode</string>
<string name="OURCART_LONG_RECEIPT_MODE">Long receipt mode</string>
<string name="OURCART_regular_receipt_btn_automatic_mode">Regular Receipt</string>
<string name="OURCART_long_receipt_btn_automatic_mode">Long Receipt</string>
<string name="OURCART_regular_receipt_btn_regular_mode">Regular Receipt</string>
<string name="OURCART_long_receipt_btn_regular_mode">Long Receipt</string>
<string name="OURCART_next">Next</string>
```

#### In the text there are tags similar to html ones:
- `<b>` - make the text bold, also add attribute to string `formatted="false"` may not work without it
- `<u>` - add underline to text
