## Build Tools
Android Studio Quail 3 | 2026.1.3

### How to Run
1. Grant execution permissions: Run this command in your terminal from the project root:
```Shell Script
chmod +x gradlew
```
2. Run the command with ./: Always use the dot-slash prefix:
```Shell Script
./gradlew <task>
```
3. Build
```Shell Script
./gradlew assembleDebug
```
4. Install APK
```Shell Script
./gradlew installDebug
```
5. Run Unit Tests
```Shell Script
./gradlew unitTestSuite
```
6. Run Android Tests / Instrumentation Tests
```Shell Script
./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.akundu.kkplayer.TestSuite
```

| Task                  | Command                                              |
|-----------------------|------------------------------------------------------|
| Unit Test Report      | ./gradlew :app:koverHtmlReport                       |
| Android Test Report   | ./gradlew :app:connectedDebugAndroidTest             |
| Verify Coverage Limits| ./gradlew :app:koverVerify (if thresholds are set)   |
| Clean & Report        | ./gradlew clean koverHtmlReport                      |

> [!TIP]
> - To see the Unit Test Report in MAC, Run: `open app/build/reports/tests/testDebugUnitTest/index.html`
> - To see the Unit Test Report in Windows, Run: `start app/build/reports/tests/testDebugUnitTest/index.html`
> - To see the Unit Android Test Report in MAC, Run: `open app/build/reports/androidTests/connected/debug/index.html`
> - To see the Unit Android Test Report in Windows, Run: `start app/build/reports/androidTests/connected/debug/index.html`
> - To see the Test Coverage Report in MAC, Run: `open app/build/reports/kover/html/index.html`
> - To see the Test Coverage Report in Windows, Run: `start app/build/reports/kover/html/index.html`