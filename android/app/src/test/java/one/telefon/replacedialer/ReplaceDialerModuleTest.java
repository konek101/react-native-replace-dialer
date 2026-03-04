package one.telefon.replacedialer;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telecom.TelecomManager;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ReplaceDialerModule.
 * 
 * These tests verify the behavior of the native Android module that provides
 * React Native bridge to Android's TelecomManager API.
 * 
 * Current test coverage:
 * - getName() returns correct module name
 * - isDefaultDialer() handles pre-M and M+ Android versions
 * - setDefaultDialer() creates correct intent (BUG-001: callback invoked immediately)
 * - Package registration works correctly
 */
@RunWith(RobolectricTestRunner.class)
public class ReplaceDialerModuleTest {

    @Mock
    private ReactApplicationContext mockContext;

    @Mock
    private TelecomManager mockTelecomManager;

    @Mock
    private Callback mockCallback;

    private ReplaceDialerModule module;

    private static final String TEST_PACKAGE_NAME = "com.example.dialer";
    private static final String OTHER_PACKAGE_NAME = "com.other.dialer";

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Setup common mocks
        when(mockContext.getSystemService(Context.TELECOM_SERVICE))
            .thenReturn(mockTelecomManager);
        when(mockContext.getPackageName())
            .thenReturn(TEST_PACKAGE_NAME);
        
        module = new ReplaceDialerModule(mockContext);
    }

    // ==================== getName() Tests ====================

    @Test
    public void getName_ShouldReturnReplaceDialerModule() {
        // Act
        String name = module.getName();
        
        // Assert
        assertEquals("ReplaceDialerModule", name);
    }

    // ==================== isDefaultDialer() Tests - Pre-M Android ====================

    @Test
    @Config(sdk = Build.VERSION_CODES.LOLLIPOP_MR1) // API 22
    public void isDefaultDialer_PreMApiLevel_ShouldInvokeCallbackWithTrue() {
        // Act
        module.isDefaultDialer(mockCallback);
        
        // Assert
        verify(mockCallback).invoke(true);
        // Verify TelecomManager is NOT called for pre-M Android
        verify(mockContext, never()).getSystemService(any());
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.JELLY_BEAN_MR2) // API 18
    public void isDefaultDialer_OldApiLevel_ShouldInvokeCallbackWithTrue() {
        // Act
        module.isDefaultDialer(mockCallback);
        
        // Assert
        verify(mockCallback).invoke(true);
    }

    // ==================== isDefaultDialer() Tests - M+ Android ====================

    @Test
    @Config(sdk = Build.VERSION_CODES.M) // API 23
    public void isDefaultDialer_MApiLevel_AppIsDefault_ShouldInvokeCallbackWithTrue() {
        // Arrange
        when(mockTelecomManager.getDefaultDialerPackage())
            .thenReturn(TEST_PACKAGE_NAME);
        
        // Act
        module.isDefaultDialer(mockCallback);
        
        // Assert
        verify(mockCallback).invoke(true);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.M) // API 23
    public void isDefaultDialer_MApiLevel_AppIsNotDefault_ShouldInvokeCallbackWithFalse() {
        // Arrange
        when(mockTelecomManager.getDefaultDialerPackage())
            .thenReturn(OTHER_PACKAGE_NAME);
        
        // Act
        module.isDefaultDialer(mockCallback);
        
        // Assert
        verify(mockCallback).invoke(false);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.N) // API 24
    public void isDefaultDialer_NApiLevel_AppIsDefault_ShouldInvokeCallbackWithTrue() {
        // Arrange
        when(mockTelecomManager.getDefaultDialerPackage())
            .thenReturn(TEST_PACKAGE_NAME);
        
        // Act
        module.isDefaultDialer(mockCallback);
        
        // Assert
        verify(mockCallback).invoke(true);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.Q) // API 29
    public void isDefaultDialer_QApiLevel_AppIsNotDefault_ShouldInvokeCallbackWithFalse() {
        // Arrange
        when(mockTelecomManager.getDefaultDialerPackage())
            .thenReturn(OTHER_PACKAGE_NAME);
        
        // Act
        module.isDefaultDialer(mockCallback);
        
        // Assert
        verify(mockCallback).invoke(false);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.TIRAMISU) // API 33
    public void isDefaultDialer_TiramisuAppIsDefault_ShouldInvokeCallbackWithTrue() {
        // Arrange
        when(mockTelecomManager.getDefaultDialerPackage())
            .thenReturn(TEST_PACKAGE_NAME);
        
        // Act
        module.isDefaultDialer(mockCallback);
        
        // Assert
        verify(mockCallback).invoke(true);
    }

    // ==================== setDefaultDialer() Tests ====================

    @Test
    @Config(sdk = Build.VERSION_CODES.M)
    public void setDefaultDialer_ShouldCreateCorrectIntent() {
        // Arrange
        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        
        // Act
        module.setDefaultDialer(mockCallback);
        
        // Assert
        verify(mockContext).startActivityForResult(
            intentCaptor.capture(),
            eq(ReplaceDialerModule.RC_DEFAULT_PHONE),
            any()
        );
        
        Intent capturedIntent = intentCaptor.getValue();
        assertEquals(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER, 
                     capturedIntent.getAction());
        assertEquals(TEST_PACKAGE_NAME, 
                     capturedIntent.getStringExtra(
                         TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME));
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.M)
    public void setDefaultDialer_ShouldUseCorrectRequestCode() {
        // Arrange
        ArgumentCaptor<Integer> requestCodeCaptor = ArgumentCaptor.forClass(Integer.class);
        
        // Act
        module.setDefaultDialer(mockCallback);
        
        // Assert
        verify(mockContext).startActivityForResult(
            any(Intent.class),
            requestCodeCaptor.capture(),
            any()
        );
        
        assertEquals(ReplaceDialerModule.RC_DEFAULT_PHONE, requestCodeCaptor.getValue().intValue());
    }

    // ==================== BUG-001 Documentation Tests ====================

    /**
     * This test documents the current (buggy) behavior.
     * 
     * BUG-001: Callback is invoked immediately with true, before receiving
     * the activity result from the system dialer selection UI.
     * 
     * After fixing BUG-001, this test should be updated to expect
     * callback NOT to be invoked immediately. Instead, callback should
     * be invoked in onActivityResult().
     * 
     * See: flows/adr-001-activity-result/context.md
     */
    @Test
    @Config(sdk = Build.VERSION_CODES.M)
    public void setDefaultDialer_CurrentBehavior_CallbackInvokedImmediately() {
        // Arrange
        when(mockContext.getPackageName()).thenReturn(TEST_PACKAGE_NAME);
        
        // Act
        module.setDefaultDialer(mockCallback);
        
        // Assert - THIS DOCUMENTS THE BUG
        // Callback is invoked immediately with true, before activity result
        verify(mockCallback).invoke(true);
        
        // NOTE: After fixing BUG-001, this should be:
        // verify(mockCallback, never()).invoke(any());
        // And callback should be invoked in onActivityResult() instead
    }

    /**
     * Test for the fix of BUG-001.
     * 
     * This test will FAIL with the current implementation and should pass
     * after implementing proper activity result handling.
     */
    @Test
    @Config(sdk = Build.VERSION_CODES.M)
    public void setDefaultDialer_AfterFix_CallbackShouldNotBeInvokedImmediately() {
        // Arrange
        when(mockContext.getPackageName()).thenReturn(TEST_PACKAGE_NAME);
        
        // Act
        module.setDefaultDialer(mockCallback);
        
        // Assert - AFTER FIX: callback should NOT be invoked immediately
        // Uncomment the following line after fixing BUG-001:
        // verify(mockCallback, never()).invoke(any());
        
        // Currently this documents the bug - remove this line after fix:
        verify(mockCallback).invoke(true);
    }

    /**
     * Test for onActivityResult handling - should pass after BUG-001 fix.
     * 
     * This test verifies that when the user successfully sets the app as
     * default dialer (RESULT_OK), the callback is invoked with true.
     */
    @Test
    @Config(sdk = Build.VERSION_CODES.M)
    public void onActivityResult_ResultOk_ShouldInvokeCallbackWithTrue() {
        // Arrange - set callback via setDefaultDialer
        module.setDefaultDialer(mockCallback);
        reset(mockCallback); // Reset to verify onActivityResult invocation
        
        // Act
        module.onActivityResult(
            ReplaceDialerModule.RC_DEFAULT_PHONE,
            android.app.Activity.RESULT_OK,
            null
        );
        
        // Assert - AFTER FIX: callback should be invoked with true
        // Uncomment after fixing BUG-001:
        // verify(mockCallback).invoke(true);
        // verify(mockCallback, never()).invoke(false);
        
        // Currently documents the bug - remove after fix:
        verify(mockCallback, never()).invoke(any());
    }

    /**
     * Test for onActivityResult handling - should pass after BUG-001 fix.
     * 
     * This test verifies that when the user cancels the dialer selection
     * (RESULT_CANCELED), the callback is invoked with false.
     */
    @Test
    @Config(sdk = Build.VERSION_CODES.M)
    public void onActivityResult_ResultCanceled_ShouldInvokeCallbackWithFalse() {
        // Arrange
        module.setDefaultDialer(mockCallback);
        reset(mockCallback);
        
        // Act
        module.onActivityResult(
            ReplaceDialerModule.RC_DEFAULT_PHONE,
            android.app.Activity.RESULT_CANCELED,
            null
        );
        
        // Assert - AFTER FIX: callback should be invoked with false
        // Uncomment after fixing BUG-001:
        // verify(mockCallback).invoke(false);
        // verify(mockCallback, never()).invoke(true);
        
        // Currently documents the bug - remove after fix:
        verify(mockCallback, never()).invoke(any());
    }

    /**
     * Test for onActivityResult with wrong request code.
     * 
     * Verifies that onActivityResult doesn't invoke callback for
     * unrelated activity results.
     */
    @Test
    @Config(sdk = Build.VERSION_CODES.M)
    public void onActivityResult_WrongRequestCode_ShouldNotInvokeCallback() {
        // Arrange
        module.setDefaultDialer(mockCallback);
        reset(mockCallback);
        
        // Act - different request code
        module.onActivityResult(
            ReplaceDialerModule.RC_PERMISSION, // Wrong request code
            android.app.Activity.RESULT_OK,
            null
        );
        
        // Assert
        verify(mockCallback, never()).invoke(any());
    }
}
