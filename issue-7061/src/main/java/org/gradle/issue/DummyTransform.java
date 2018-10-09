package org.gradle.issue;

import org.gradle.api.artifacts.transform.ArtifactTransform;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class DummyTransform extends ArtifactTransform {
    @NotNull
    @Override
    public List<File> transform(@NotNull File input) {
        return Collections.singletonList(input);
    }
}
