# Understanding: Native Android Module

> Native Android implementation using React Native bridge and Android TelecomManager APIs.

## Phase: EXPLORING

## Hypothesis

This module implements the core native functionality for managing Android default dialer status:
- Uses Android TelecomManager system service
- Implements two main operations: check status and request default
- Follows React Native native module pattern (extends ReactContextBaseJavaModule)
- Uses callback-based async communication with JavaScript layer
- Handles Android version differences (pre-M vs M+)

## Sources

- `android/app/src/main/java/one/telefon/replacedialer/ReplaceDialerModule.java` - Main native module implementation
- `android/app/src/main/java/one/telefon/replacedialer/ReplaceDialerModulePackage.java` - React Native package registration
- `android/app/build.gradle` - Build configuration and dependencies

## Validated Understanding

After detailed code analysis:

### ReplaceDialerModule.java

**Class Structure**:
- Extends `ReactContextBaseJavaModule`
- Holds `ReactApplicationContext mContext` reference
- Static callback `setCallback` for setDefaultDialer operation (potential issue: not cleared)
- Logging tag: `LOG = "one.telefon.replacedialer.ReplaceDialerModule"`

**Methods**:

1. **`isDefaultDialer(Callback myCallback)`**
   - Returns `true` for Android versions < API 23 (pre-Marshmallow)
   - Uses `TelecomManager.getDefaultDialerPackage()` to check current default
   - Compares with app's package name: `mContext.getPackageName()`
   - Invokes callback with boolean result
   - Logs operation with `Log.w()`

2. **`setDefaultDialer(Callback myCallback)`**
   - Stores callback in static `setCallback` field
   - Creates intent: `TelecomManager.ACTION_CHANGE_DEFAULT_DIALER`
   - Sets package name extra: `EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME`
   - Starts activity for result with request code `RC_DEFAULT_PHONE = 3289`
   - **Issue**: Immediately invokes callback with `true` before result is known
   - Commented-out code shows attempted `ActivityEventListener` implementation

**Constants**:
- `RC_DEFAULT_PHONE = 3289` - Request code for default dialer request
- `RC_PERMISSION = 3810` - Unused request code for permissions
- `REQUEST_CODE_SET_DEFAULT_DIALER = 123` - Unused constant

**Commented-out Code**:
- `ActivityEventListener` interface implementation
- `onActivityResult()` method to handle callback invocation
- `onNewIntent()` method
- This indicates incomplete activity result handling

### ReplaceDialerModulePackage.java

**Standard React Native Package**:
- Implements `ReactPackage` interface
- `createNativeModules()`: Creates and returns list with `ReplaceDialerModule` instance
- `createViewManagers()`: Returns empty list (no custom views)
- No constructor logic

### Build Configuration (build.gradle)

**Android Configuration**:
- `compileSdkVersion 34`
- `minSdkVersion 21` (Android 5.0 Lollipop)
- `targetSdkVersion 34`
- Java 17 compatibility
- ABI filters: `armeabi-v7a`, `x86`

**Dependencies**:
- `com.facebook.react:react-native:+` (React Native)
- `com.google.code.gson:gson:2.10.1` (JSON parsing, unused in current code)
- `androidx.appcompat:appcompat:1.6.1`
- `androidx.annotation:annotation:1.7.1`

## Children Identified

> Deeper concepts spawned during SPAWNING phase

| Child | Hypothesis | Status |
|-------|------------|--------|
| telecom-manager-integration | Android TelecomManager API usage patterns | PENDING |
| react-native-bridge | Native module registration and method export | PENDING |
| activity-result-handling | Incomplete activity result callback handling | PENDING |

## Dependencies

- **Uses**: 
  - Android TelecomManager system service
  - React Native bridge (ReactContextBaseJavaModule, Callback)
  - Android Intents for dialer request
- **Used by**: JavaScript bridge layer (src/ReplaceDialer.js)

## Key Insights

1. **Incomplete Activity Result Handling**: The module starts an activity for result but doesn't properly handle the callback. The `setCallback` is invoked immediately with `true` instead of waiting for user's dialer selection.

2. **Static Callback Risk**: Using static `setCallback` field could lead to memory leaks or incorrect callback invocation if multiple requests are made.

3. **Android Version Handling**: Gracefully handles pre-M Android by returning `true` (assumes dialer concept doesn't apply).

4. **Logging Strategy**: Uses `Log.w()` (warning level) for all logging, including successful operations.

5. **Unused Dependencies**: Gson dependency is included but not used in the current implementation.

## ADR Candidates

- **ADR: Callback-based async pattern** - Chose callbacks over Promises (React Native supports both)
- **ADR: TelecomManager API** - Uses official Android TelecomManager for dialer management
- **ADR: Activity result handling** - Current implementation is incomplete; needs proper ActivityEventListener
- **ADR: Android API levels** - minSdkVersion 21, full functionality on API 23+

## Flow Recommendation

- **Type**: SDD (Spec-Driven Development)
- **Confidence**: high
- **Rationale**: Internal service logic, implementation details are critical, no direct stakeholder-facing features. However, the incomplete activity result handling is a critical bug that needs addressing.

## Critical Issues Identified

1. **BUG**: `setDefaultDialer()` invokes callback before receiving activity result
2. **BUG**: Commented-out `ActivityEventListener` suggests known but unfixed issue
3. **TECH DEBT**: Static callback field is not thread-safe and doesn't handle concurrent calls
4. **UNUSED**: `isDefaultDialer` callback parameter name differs from usage (`data` vs boolean)

## Synthesis

> Updated during SYNTHESIZING phase after children complete

### From Children
Child concepts (telecom-manager-integration, react-native-bridge, activity-result-handling) are implementation details already covered in the validated understanding. No separate deep dive needed.

### Combined Understanding

The native Android module provides React Native bridge to Android's TelecomManager API:

**Architecture**:
- Standard React Native native module pattern
- Extends `ReactContextBaseJavaModule`, registered via `ReactPackage`
- Two exported methods: `isDefaultDialer()` and `setDefaultDialer()`

**Implementation Details**:
- Uses `TelecomManager.getDefaultDialerPackage()` for status checks
- Uses `ACTION_CHANGE_DEFAULT_DIALER` intent for requesting default status
- Handles pre-M Android (API < 23) by returning true
- Callback-based async pattern throughout

**Critical Issues**:
1. `setDefaultDialer()` invokes callback immediately with `true` before receiving activity result
2. Commented-out `ActivityEventListener` implementation indicates known but unfixed bug
3. Static callback field is not thread-safe
4. Gson dependency unused

**Build Configuration**:
- minSdkVersion 21, targetSdkVersion 34
- Java 17 compatibility
- ABI: armeabi-v7a, x86

## Bubble Up

> Summary to pass to parent during EXITING

- Implements Android default dialer management via TelecomManager API
- Two main operations: check status (`isDefaultDialer`) and request default (`setDefaultDialer`)
- **Critical bug**: Activity result not properly handled, callback invoked prematurely
- Requires Android manifest configuration for InCallService and DIAL intents
- Callback-based async pattern throughout
- SDD flow recommended for documenting this internal service logic

---

*Phase: SYNTHESIZING | Depth: 1 | Parent: root*
