package org.gradle.issue;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ArtifactCollection;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.model.ObjectFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static org.gradle.api.internal.artifacts.ArtifactAttributes.ARTIFACT_FORMAT;

@SuppressWarnings("UnstableApiUsage")
public class IssuePlugin implements Plugin<Project> {
    private final String[] buildTypes = new String[]{"debug", "release"};

    @Override
    public void apply(@NotNull Project project) {

        final DependencyHandler dependencies = project.getDependencies();


        final ObjectFactory objects = project.getObjects();
        final BuildTypeAttr defaultBuildType = objects.named(BuildTypeAttr.class, "default");

        // mark all artifacts as "buildType=default" whose artifactType='xbundle'
        dependencies.getArtifactTypes().register("xbundle", artifactTypeDefinition ->
                artifactTypeDefinition.getAttributes().attribute(BuildTypeAttr.ATTRIBUTE, defaultBuildType));


        // register:
        // 1. xbundle, default -> aar, debug
        // 2. xbundle, default -> aar, release
        Arrays.stream(buildTypes).forEach(buildType -> dependencies.registerTransform(reg -> {
            reg.getFrom().attribute(ARTIFACT_FORMAT, "xbundle");
            reg.getTo().attribute(ARTIFACT_FORMAT, "aar");

            reg.getFrom().attribute(BuildTypeAttr.ATTRIBUTE, defaultBuildType);
            reg.getTo().attribute(BuildTypeAttr.ATTRIBUTE, objects.named(BuildTypeAttr.class, buildType));

            reg.artifactTransform(DummyTransform.class);
        }));

        // register: aar -> classes
        dependencies.registerTransform(reg -> {
            reg.getFrom().attribute(ARTIFACT_FORMAT, "aar");
            reg.getTo().attribute(ARTIFACT_FORMAT, "classes");

            reg.artifactTransform(DummyTransform.class);
        });

        // create a task to test
        project.getTasks().register("issueTask", t -> t.doFirst(task -> {
            final Configuration implementation = project.getConfigurations().getByName("implementation");
            final Configuration test = project.getConfigurations().create("test");
            test.setCanBeResolved(true);
            test.extendsFrom(implementation);

            // retrieve artifacts that is 'classes' and 'release'
            // This line will fail!
            final ArtifactCollection artifacts = test.getIncoming().artifactView(config -> {
                config.attributes(container -> container.attribute(ARTIFACT_FORMAT, "classes"));
                config.attributes(container -> container.attribute(BuildTypeAttr.ATTRIBUTE, objects.named(BuildTypeAttr.class, "release")));
            }).getArtifacts();

            project.getLogger().lifecycle("these are: " + artifacts.getArtifactFiles().getFiles());
        }));
    }
}
