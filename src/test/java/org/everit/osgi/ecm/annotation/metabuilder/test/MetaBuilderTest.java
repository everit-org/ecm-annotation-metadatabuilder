package org.everit.osgi.ecm.annotation.metabuilder.test;

import org.everit.osgi.ecm.annotation.metabuilder.MetaBuilder;
import org.everit.osgi.ecm.meta.AttributeMetaHolder;
import org.everit.osgi.ecm.meta.ComponentMeta;
import org.junit.Assert;
import org.junit.Test;

public class MetaBuilderTest {

    @Test
    public void testAnnotatedClass() {
        ComponentMeta<AnnotatedClass> componentMeta = MetaBuilder.buildComponentMeta(new DummyBundleContext(),
                AnnotatedClass.class);

        AttributeMetaHolder<?>[] attributeHolders = componentMeta.getAttributeHolders();

        Assert.assertEquals(2, attributeHolders.length);

        AttributeMetaHolder<?> referenceOnClass = attributeHolders[0];
        Assert.assertEquals("1.target", referenceOnClass.getAttribute().getName());

        AttributeMetaHolder<?> referenceOnField = attributeHolders[1];
        Assert.assertEquals("referenceWithOnlyDefault.clause", referenceOnField.getAttribute().getName());
    }
}
