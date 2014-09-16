package org.everit.osgi.ecm.annotation.metabuilder.test;

import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.Reference;
import org.everit.osgi.ecm.annotation.References;
import org.everit.osgi.ecm.annotation.attribute.ReferenceAttribute;
import org.everit.osgi.ecm.annotation.attribute.ReferenceConfigurationType;

@Component
@References({ @Reference(name = "0") })
public class AnnotatedClass {

    @Reference(attribute = @ReferenceAttribute(configurationType = ReferenceConfigurationType.CLAUSE))
    private Runnable referenceWithOnlyDefault;
}
