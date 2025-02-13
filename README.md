# receipt-scanner-android-demo

## Installation

Add to your `build.gradle.kts` dependencies:
```agsl
implementation("com.ourcart:receiptscanner:1.1.18")
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

[Full flow scanner documentation for entire flow and full ui](FULL_FLOW_README.md)

[Custom scanner documentation for separate methods and only receipt scanner ui](CUSTOM_SCANNER_README.md)