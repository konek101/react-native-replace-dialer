/**
 * @format
 * @jest-environment jsdom
 */

import { NativeModules } from 'react-native';
import ReplaceDialer from '../../src/ReplaceDialer';

// Mock console.log
const originalConsoleLog = console.log;

describe('ReplaceDialer', () => {
  beforeEach(() => {
    // Reset all mocks before each test
    jest.resetAllMocks();
    console.log = jest.fn();
    
    // Setup default mock for native module
    NativeModules.ReplaceDialerModule = {
      isDefaultDialer: jest.fn((callback) => callback(true)),
      setDefaultDialer: jest.fn((callback) => callback(true)),
    };
  });

  afterEach(() => {
    console.log = originalConsoleLog;
  });

  describe('constructor', () => {
    it('should create instance without errors', () => {
      // Act
      const dialer = new ReplaceDialer();
      
      // Assert
      expect(dialer).toBeDefined();
      expect(typeof dialer.isDefaultDialer).toBe('function');
      expect(typeof dialer.setDefaultDialer).toBe('function');
      expect(typeof dialer.checkNativeModule).toBe('function');
    });
  });

  describe('checkNativeModule', () => {
    it('should throw error when native module is null', () => {
      // Arrange
      NativeModules.ReplaceDialerModule = null;
      const dialer = new ReplaceDialer();
      
      // Act & Assert
      expect(() => dialer.checkNativeModule()).toThrow(
        expect.stringContaining('NativeModule.ReplaceDialerModule is null')
      );
    });

    it('should include troubleshooting steps in error message', () => {
      // Arrange
      NativeModules.ReplaceDialerModule = null;
      const dialer = new ReplaceDialer();
      
      // Act & Assert
      try {
        dialer.checkNativeModule();
        fail('Expected error to be thrown');
      } catch (error) {
        expect(error.message).toContain('Rebuild and re-run the app');
        expect(error.message).toContain('pod install');
        expect(error.message).toContain('manual installation instructions');
        expect(error.message).toContain('mock the native module');
        expect(error.message).toContain('Github repository');
      }
    });

    it('should not throw error when native module is available', () => {
      // Arrange
      NativeModules.ReplaceDialerModule = {
        isDefaultDialer: jest.fn(),
        setDefaultDialer: jest.fn(),
      };
      const dialer = new ReplaceDialer();
      
      // Act & Assert
      expect(() => dialer.checkNativeModule()).not.toThrow();
    });
  });

  describe('isDefaultDialer', () => {
    beforeEach(() => {
      NativeModules.ReplaceDialerModule = {
        isDefaultDialer: jest.fn((callback) => callback(true)),
      };
    });

    it('should invoke callback with true when app is default dialer', () => {
      // Arrange
      const dialer = new ReplaceDialer();
      const mockCallback = jest.fn();
      
      // Act
      dialer.isDefaultDialer(mockCallback);
      
      // Assert
      expect(mockCallback).toHaveBeenCalledTimes(1);
      expect(mockCallback).toHaveBeenCalledWith(true);
    });

    it('should invoke callback with false when app is not default dialer', () => {
      // Arrange
      NativeModules.ReplaceDialerModule = {
        isDefaultDialer: jest.fn((callback) => callback(false)),
      };
      const dialer = new ReplaceDialer();
      const mockCallback = jest.fn();
      
      // Act
      dialer.isDefaultDialer(mockCallback);
      
      // Assert
      expect(mockCallback).toHaveBeenCalledTimes(1);
      expect(mockCallback).toHaveBeenCalledWith(false);
    });

    it('should log the result', () => {
      // Arrange
      const dialer = new ReplaceDialer();
      const mockCallback = jest.fn();
      
      // Act
      dialer.isDefaultDialer(mockCallback);
      
      // Assert
      expect(console.log).toHaveBeenCalledWith('isDefaultDialer()', true);
    });

    it('should call native module method', () => {
      // Arrange
      const dialer = new ReplaceDialer();
      const mockCallback = jest.fn();
      
      // Act
      dialer.isDefaultDialer(mockCallback);
      
      // Assert
      expect(NativeModules.ReplaceDialerModule.isDefaultDialer)
        .toHaveBeenCalledTimes(1);
    });

    it('should throw error if native module is not available', () => {
      // Arrange
      NativeModules.ReplaceDialerModule = null;
      const dialer = new ReplaceDialer();
      const mockCallback = jest.fn();
      
      // Act & Assert
      expect(() => dialer.isDefaultDialer(mockCallback)).toThrow();
      expect(mockCallback).not.toHaveBeenCalled();
    });
  });

  describe('setDefaultDialer', () => {
    beforeEach(() => {
      NativeModules.ReplaceDialerModule = {
        setDefaultDialer: jest.fn((callback) => callback(true)),
      };
    });

    it('should invoke callback after native call', () => {
      // Arrange
      const dialer = new ReplaceDialer();
      const mockCallback = jest.fn();
      
      // Act
      dialer.setDefaultDialer(mockCallback);
      
      // Assert
      expect(mockCallback).toHaveBeenCalledTimes(1);
    });

    it('should invoke callback with result from native module', () => {
      // Arrange
      const dialer = new ReplaceDialer();
      const mockCallback = jest.fn();
      
      // Act
      dialer.setDefaultDialer(mockCallback);
      
      // Assert
      expect(mockCallback).toHaveBeenCalledWith(true);
    });

    it('should invoke callback with false when native module returns false', () => {
      // Arrange
      NativeModules.ReplaceDialerModule = {
        setDefaultDialer: jest.fn((callback) => callback(false)),
      };
      const dialer = new ReplaceDialer();
      const mockCallback = jest.fn();
      
      // Act
      dialer.setDefaultDialer(mockCallback);
      
      // Assert
      expect(mockCallback).toHaveBeenCalledWith(false);
    });

    it('should log the result', () => {
      // Arrange
      const dialer = new ReplaceDialer();
      const mockCallback = jest.fn();
      
      // Act
      dialer.setDefaultDialer(mockCallback);
      
      // Assert
      expect(console.log).toHaveBeenCalledWith('setDefaultDialer', true);
    });

    it('should call native module method', () => {
      // Arrange
      const dialer = new ReplaceDialer();
      const mockCallback = jest.fn();
      
      // Act
      dialer.setDefaultDialer(mockCallback);
      
      // Assert
      expect(NativeModules.ReplaceDialerModule.setDefaultDialer)
        .toHaveBeenCalledTimes(1);
    });

    it('should throw error if native module is not available', () => {
      // Arrange
      NativeModules.ReplaceDialerModule = null;
      const dialer = new ReplaceDialer();
      const mockCallback = jest.fn();
      
      // Act & Assert
      expect(() => dialer.setDefaultDialer(mockCallback)).toThrow();
      expect(mockCallback).not.toHaveBeenCalled();
    });
  });

  describe('Integration: Full dialer check flow', () => {
    it('should handle complete flow: check status then request default', () => {
      // Arrange - simulate app is NOT default dialer initially
      NativeModules.ReplaceDialerModule = {
        isDefaultDialer: jest.fn((callback) => callback(false)),
        setDefaultDialer: jest.fn((callback) => callback(true)),
      };
      
      const dialer = new ReplaceDialer();
      const statusCallback = jest.fn();
      const setCallback = jest.fn();
      
      // Act - check status
      dialer.isDefaultDialer(statusCallback);
      
      // Assert status check
      expect(statusCallback).toHaveBeenCalledWith(false);
      
      // Act - request to become default
      dialer.setDefaultDialer(setCallback);
      
      // Assert set request
      expect(setCallback).toHaveBeenCalledWith(true);
    });

    it('should skip set request if already default dialer', () => {
      // Arrange - simulate app IS default dialer
      NativeModules.ReplaceDialerModule = {
        isDefaultDialer: jest.fn((callback) => callback(true)),
        setDefaultDialer: jest.fn((callback) => callback(true)),
      };
      
      const dialer = new ReplaceDialer();
      const statusCallback = jest.fn();
      
      // Act - check status
      dialer.isDefaultDialer(statusCallback);
      
      // Assert status check
      expect(statusCallback).toHaveBeenCalledWith(true);
      
      // Assert setDefaultDialer was NOT called
      expect(NativeModules.ReplaceDialerModule.setDefaultDialer)
        .not.toHaveBeenCalled();
    });
  });
});
