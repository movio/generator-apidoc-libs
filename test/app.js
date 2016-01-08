'use strict';
var path = require('path');
var assert = require('yeoman-assert');
var helpers = require('yeoman-generator').test;

describe('generator-apidoc-libs:app', function () {
  before(function (done) {
    helpers.run(path.join(__dirname, '../generators/app'))
      .withPrompts({
        organization: 'movio.cinema',
        projectName: 'test project',
        appName: 'test-project',
        useApidoc: false
      })
      .on('end', done);
  });

  it('creates files', function () {
    assert.file([
      'build.sbt',
      'project/build.properties'
    ]);
  });
});
