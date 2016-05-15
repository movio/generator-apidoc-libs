'use strict';
var yeoman = require('yeoman-generator');
var chalk = require('chalk');
var yosay = require('yosay');
var _ = require('lodash-fp');

module.exports = yeoman.generators.Base.extend({

  prompting: function () {
    var done = this.async();

    // Have Yeoman greet the user.
    this.log(yosay(
      'Welcome to the magnificent ' + chalk.red('generator-apidoc-libs') + ' generator!'
    ));

    var prompts = [{
      type: 'input',
      name: 'organization',
      message: 'Organization',
      default: 'movio.cinema',
      store: true
    }, {
      type: 'input',
      name: 'projectName',
      message: 'Project name',
      default: 'members core'
    }, {
      type: 'input',
      name: 'appName',
      message: 'Application name (eg in github)',
      default: function (answers) {
        var abbrev = answers.organization.match(/\b(\w)/g).join('');
        return abbrev + '-' + _.kebabCase(answers.projectName) + '-lib';
      }
    }, {
        type: 'input',
        name: 'gitRepoUrl',
        message: 'Git Repo URL',
        default: function(answers) {
            return 'http://github.com/movio/' + answers.appName;
        }
    }, {
      type: 'confirm',
      name: 'useApidoc',
      message: 'Use apidoc',
      default: true
    }, {
      type: 'input',
      name: 'apidocOrg',
      message: 'apidoc organization',
      when: function (answers) {
        return answers.useApidoc;
      },
      default: function (answers) {
        return answers.organization;
      },
      store: true
    }, {
      type: 'input',
      name: 'apidocApp',
      message: 'apidoc application name',
      when: function (answers) {
        return answers.useApidoc;
      },
      default: function (answers) {
        return _.kebabCase(answers.projectName);
      }
    }, {
      type: 'input',
      name: 'apidocVersion',
      message: 'apidoc application version',
      when: function (answers) {
        return answers.useApidoc;
      },
      default: '0.1.0-SNAPSHOT'
    }];

    this.prompt(prompts, function (props) {
      // To access props later use this.props.someOption;
      this.props = props;

      props.orgAbbreviation = props.organization.match(/\b(\w)/g).join('');
      props.appNameSpace = props.projectName.toLowerCase().split(' ').join('.');

      props.gitCloneUrl = props.gitRepoUrl.replace(/https:\/\//g, "git@");

      done();
    }.bind(this));
  },

  writing: function () {
    // Add all files in the shared directory
    this.directory('shared', '.');
  },

  install: function () {
    this.installDependencies();
  }
});
