module.exports = {
  dependency: {
    platforms: {
      android: {
        sourceDir: './android/app',
        packageImportPath: 'import one.telefon.replacedialer.ReplaceDialerModulePackage;',
        packageInstance: 'new ReplaceDialerModulePackage()',
      },
    },
  },
};
