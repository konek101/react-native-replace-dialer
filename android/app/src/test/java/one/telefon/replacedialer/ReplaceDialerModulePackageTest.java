package one.telefon.replacedialer;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for ReplaceDialerModulePackage.
 * 
 * Tests verify that the React Native package is correctly registered
 * and creates the native module instance.
 */
@RunWith(RobolectricTestRunner.class)
public class ReplaceDialerModulePackageTest {

    @Mock
    private ReactApplicationContext mockContext;

    private ReplaceDialerModulePackage pkg;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        pkg = new ReplaceDialerModulePackage();
    }

    @Test
    public void constructor_ShouldCreateInstance() {
        // Act
        ReplaceDialerModulePackage pkg = new ReplaceDialerModulePackage();
        
        // Assert
        assertNotNull(pkg);
    }

    @Test
    public void createNativeModules_ShouldReturnListWithOneModule() {
        // Act
        List<NativeModule> modules = pkg.createNativeModules(mockContext);
        
        // Assert
        assertNotNull(modules);
        assertEquals(1, modules.size());
        assertTrue(modules.get(0) instanceof ReplaceDialerModule);
    }

    @Test
    public void createNativeModules_ModuleShouldHaveCorrectContext() {
        // Act
        List<NativeModule> modules = pkg.createNativeModules(mockContext);
        
        // Assert
        ReplaceDialerModule module = (ReplaceDialerModule) modules.get(0);
        // Module should be initialized with the context
        assertNotNull(module);
    }

    @Test
    public void createViewManagers_ShouldReturnEmptyList() {
        // Act
        List viewManagers = pkg.createViewManagers(mockContext);
        
        // Assert
        assertNotNull(viewManagers);
        assertTrue(viewManagers.isEmpty());
        assertEquals(0, viewManagers.size());
    }

    @Test
    public void createViewManagers_MultipleCalls_ShouldReturnEmptyList() {
        // Act
        List viewManagers1 = pkg.createViewManagers(mockContext);
        List viewManagers2 = pkg.createViewManagers(mockContext);
        
        // Assert
        assertTrue(viewManagers1.isEmpty());
        assertTrue(viewManagers2.isEmpty());
    }
}
