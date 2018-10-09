package org.gradle.issue;

import org.gradle.api.attributes.Attribute;

public interface BuildTypeAttr extends org.gradle.api.Named {

    Attribute<BuildTypeAttr> ATTRIBUTE = Attribute.of(BuildTypeAttr.class);
}
