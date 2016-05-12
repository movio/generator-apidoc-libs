import co.movio.jenkins.build.sbt.SbtBuild
import co.movio.jenkins.job.impl.sbt.SbtJob
import co.movio.jenkins.job.impl.DockerJob
import co.movio.jenkins.job.impl.SeedJob
import co.movio.jenkins.step.impl.git.GitTagStep
import co.movio.jenkins.step.impl.sbt.SbtCommandStep
import co.movio.jenkins.view.View

def sbtBuild = new SbtBuild
  (
    name: "<%= props.appName %>",
    gitRepo: "<%= props.gitCloneUrl %>",
    testReportRegex: "**/target/test-reports/*.xml",
    requiresMysql: <%= props.testsRequireMysql %>,
  )

def sbtCiJob = new SbtJob
  (
    script: this,
    build: sbtBuild,
  )

def sbtReleaseJob = new SbtJob
  (
    script: this,
    build: sbtBuild,
    isRelease: true,
  )


def seedJob = new SeedJob
  (
    script: this,
    build: sbtBuild,
    jenkinsLibVersion: '0.9.0-SNAPSHOT'
  )

def view = new View
  (
    script: this,
    viewName: sbtBuild.name
  )
