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