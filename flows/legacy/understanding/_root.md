# Understanding: Project Root

> Entry point for recursive understanding. Children are top-level logical domains.

## Phase: EXPLORING

## Project Overview

**react-native-replace-dialer** is a React Native library (v0.0.11) that provides Android-only functionality to:
1. Check if the app is set as the default dialer on Android devices
2. Request to become the default dialer app

The library consists of:
- **JavaScript layer**: `src/ReplaceDialer.js` - React Native module wrapper
- **Native Android layer**: Java code in `android/app/src/main/java/one/telefon/replacedialer/`
  - `ReplaceDialerModule.java` - Native module implementing TelecomManager APIs
  - `ReplaceDialerModulePackage.java` - React Native package registration

**Key Technical Details**:
- Targets Android API 21+ (minSdkVersion 21), with full functionality on Android M (API 23+)
- Uses Android TelecomManager API for dialer management
- Implements callback-based async pattern for native module communication
- Requires specific Android manifest configuration (intent filters for DIAL action, InCallService)

## Validated Understanding

After analyzing source code:

### JavaScript Layer (`src/ReplaceDialer.js`)
- Class-based API with constructor
- `checkNativeModule()` - Validates native module availability with detailed error messages
- `isDefaultDialer(cb)` - Async check via callback, logs result
- `setDefaultDialer(cb)` - Requests to set as default dialer via callback
- Uses `NativeModules.ReplaceDialerModule` bridge

### Native Android Layer (`ReplaceDialerModule.java`)
- Extends `ReactContextBaseJavaModule`
- Implements `isDefaultDialer(Callback)` - Uses `TelecomManager.getDefaultDialerPackage()`
- Implements `setDefaultDialer(Callback)` - Uses `TelecomManager.ACTION_CHANGE_DEFAULT_DIALER` intent
- Handles pre-M Android versions (returns true for API < 23)
- Uses logging for debugging (`Log.w()`)

### Package Registration (`ReplaceDialerModulePackage.java`)
- Implements `ReactPackage` interface
- Creates native module instance in `createNativeModules()`
- Returns empty list for `createViewManagers()` (no UI components)

## Identified Domains

> Logical domains discovered. Each becomes a child directory for deeper exploration.

| Domain | Hypothesis | Priority | Status |
|--------|------------|----------|--------|
| native-android-module | Native Android implementation using TelecomManager APIs | HIGH | PENDING |
| javascript-bridge | React Native bridge layer connecting JS to native module | HIGH | PENDING |
| android-integration | Android manifest configuration and intent filters | MEDIUM | PENDING |

## Source Mapping

> Which source paths map to which logical domains

| Source Path | -> Domain |
|-------------|----------|
| `android/app/src/main/java/one/telefon/replacedialer/ReplaceDialerModule.java` | native-android-module |
| `android/app/src/main/java/one/telefon/replacedialer/ReplaceDialerModulePackage.java` | native-android-module |
| `src/ReplaceDialer.js` | javascript-bridge |
| `README.md`, `docs/installation_android.md` | android-integration |

## Cross-Cutting Concerns

> Things that span multiple domains (may become ADRs)

- **Android TelecomManager API usage**: Uses `ACTION_CHANGE_DEFAULT_DIALER` intent and `getDefaultDialerPackage()` method
- **Callback-based async pattern**: Both JS and native layers use callbacks for async operations
- **Android version handling**: Checks for Android M (API 23+) for dialer checks
- **Error handling strategy**: Detailed error messages with troubleshooting steps

## Children Spawned

```
Completed:
- native-android-module: Native Android implementation with TelecomManager API (SDD recommended, critical bug identified)

Skipped (adequately covered):
- javascript-bridge: Simple wrapper, documented in sdd-native-android-module specs
- android-integration: Manifest config documented in README and specs

```

## Synthesis

> Updated after all children complete

### Project Summary

**react-native-replace-dialer** v0.0.11 is an Android-only React Native library that provides default dialer management functionality via the Android TelecomManager API.

### Architecture

- **JavaScript Layer**: Simple wrapper with native module validation
- **Native Layer**: React Native native module pattern using callbacks
- **System Integration**: Android TelecomManager for dialer operations

### Key Findings

1. **CRITICAL BUG**: `setDefaultDialer()` invokes callback before receiving activity result
   - Always reports success (`true`)
   - Cannot detect user cancellation or failure
   - Fix requires implementing `ActivityEventListener` interface
   - See: `flows/adr-001-activity-result/context.md`

2. **Technical Debt**:
   - Static callback field not thread-safe
   - Commented-out code indicates known but unfixed issues
   - Unused Gson dependency

3. **Compatibility**:
   - minSdkVersion 21, targetSdkVersion 34
   - Full functionality on Android M (API 23+)
   - Pre-M returns true (dialer concept not applicable)

### Generated Documentation

1. **flows/sdd-native-android-module/**
   - `01-requirements.md` - Stakeholder requirements
   - `02-specifications.md` - Technical specifications
   
2. **flows/adr-001-activity-result/**
   - `context.md` - Critical bug documentation and fix

### Flow Recommendations

| Module | Flow Type | Status | Priority |
|--------|-----------|--------|----------|
| Native Android Module | SDD | DRAFT | HIGH |
| Activity Result Bug Fix | ADR | DRAFT | CRITICAL |
| JavaScript Bridge | (covered in SDD) | - | - |
| Android Integration | (documented in README) | - | - |

## Bubble Up

> Summary to pass to parent during EXITING

- Analysis complete: React Native Android dialer library
- Critical bug identified in setDefaultDialer() callback handling
- Generated SDD and ADR flows (DRAFT status)
- Immediate action required: Fix activity result handling
- See flows/legacy/log.md for full session summary

---

*Created by /legacy ENTERING phase, validated in EXPLORING phase, synthesized in EXITING phase*
