'use strict';

var path = require('path');
var conf = require('./gulp/conf');

var pathSrcHtml = [
  path.join(conf.paths.src, '/**/*.html')
];

module.exports = function (config) {

  var configuration = {
    files: [
      'node_modules/jquery/dist/jquery.js',
      'node_modules/angular/angular.js',
      'node_modules/angular-mocks/angular-mocks.js',
      'node_modules/babel-polyfill/dist/polyfill.js',
      conf.paths.tmp + '/serve/app/index.module.js',
    ].concat(pathSrcHtml),

    singleRun: true,

    autoWatch: false,

    ngHtml2JsPreprocessor: {
      stripPrefix: conf.paths.src + '/',
      moduleName: 'userDashboard'
    },

    logLevel: 'WARN',

    frameworks: ['jasmine'],

    browsers: ['PhantomJS'], // can be replaced by Chrome

    plugins: [
      'karma-chrome-launcher',
      'karma-phantomjs-launcher',
      'karma-coverage',
      'karma-jasmine',
      'karma-ng-html2js-preprocessor'
    ],

    coverageReporter: {
      type: 'html',
      dir: 'coverage/'
    },

    // to avoid DISCONNECTED messages on slow/busy machines
    browserDisconnectTimeout: 10000, // default 2000
    browserDisconnectTolerance: 1, // default 0
    browserNoActivityTimeout: 60000, //default 10000

    reporters: ['progress']
  };

  // This is the default preprocessors configuration for a usage with Karma cli
  // The coverage preprocessor in added in gulp/unit-test.js only for single tests
  // It was not possible to do it there because karma doesn't let us now if we are
  // running a single test or not
  configuration.preprocessors = {};
  pathSrcHtml.forEach(function (path) {
    configuration.preprocessors[path] = ['ng-html2js'];
  });


  config.set(configuration);
};
