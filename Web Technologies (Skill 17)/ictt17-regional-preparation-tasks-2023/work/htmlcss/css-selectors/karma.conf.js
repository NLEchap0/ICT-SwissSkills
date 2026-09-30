const config = require('@skills17/karma-helpers');

module.exports = config({
  frameworks: ['mocha', 'chai'],
  plugins: ['karma-mocha', 'karma-chai', 'karma-chrome-launcher'],
  files: [
    { pattern: 'tests/**/*.test.js', included: true },
    { pattern: 'src/**/*', included: false },
  ],
  browsers: ['ChromeHeadlessNoSandbox'],
  customLaunchers: {
    ChromeHeadlessNoSandbox: {
      base: 'ChromeHeadless',
      flags: ['--no-sandbox'],
    },
  },
});
