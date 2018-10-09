package org.gradle.issue

import org.gradle.testkit.runner.TaskOutcome
import org.gradle.testkit.runner.internal.DefaultGradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.junit.Assert.assertEquals

class IssueSpec extends Specification {
    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
    }

    def "artifacts transform should choose only one candidate"() {
        given:
        buildFile << """
plugins {
   id 'java-library'
   id 'org.gradle.issue-7061'
}

repositories {
    google()
    mavenLocal()
}

dependencies {
    implementation 'org.gradle:library-xbundle:1.0.0'
}



gradle.buildFinished { BuildResult buildResult ->
    def failure = buildResult.getFailure()
    if (failure instanceof org.gradle.internal.exceptions.LocationAwareException) {
        if (failure.getReportableCauses().any {
            it.getMessage().contains("Could not find org.gradle:library-xbundle")
        }) {
            logger.quiet("--------------------------------------------------------------------------------------------")
            logger.quiet(" Please execute ':library-xbundle:install' to produce the library first.")
            logger.quiet("--------------------------------------------------------------------------------------------")
        }
    }
}
        """

        when:
        def result = new DefaultGradleRunner().withPluginClasspath()
                .withProjectDir(testProjectDir.root)
                .withArguments("issueTask", "--debug", "--rerun-tasks")
                .forwardOutput()
                .build()

        then: "this build should not fail"

        assertEquals("issue task should be success", TaskOutcome.SUCCESS, result.task('issueTask').outcome)
    }
}