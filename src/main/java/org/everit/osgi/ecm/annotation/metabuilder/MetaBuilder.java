package org.everit.osgi.ecm.annotation.metabuilder;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.meta.AttributeMeta;
import org.everit.osgi.ecm.meta.ComponentMeta;
import org.everit.osgi.ecm.meta.ComponentMeta.ComponentMetaBuilder;
import org.everit.osgi.ecm.meta.InstanceSupplier;
import org.osgi.framework.BundleContext;

public class MetaBuilder {

    public static <C> ComponentMeta<C> buildComponentMeta(BundleContext bundleContext, Class<C> clazz,
            InstanceSupplier<C> instanceSupplier) {
        MetaBuilder metaBuilder = new MetaBuilder();
        return metaBuilder.build(clazz, bundleContext, instanceSupplier);
    }

    private final List<AttributeMeta<?>> attributes = new ArrayList<AttributeMeta<?>>();

    private MetaBuilder() {
    }

    private <C> ComponentMeta<C> build(Class<C> clazz, BundleContext bundleContext,
            InstanceSupplier<C> instanceSupplier) {
        Component componentAnnotation = clazz.getAnnotation(Component.class);
        if (componentAnnotation == null) {
            throw new ComponentAnnotationMissingException("Component annotation is missing on type " + clazz.toString());
        }

        ComponentMetaBuilder<C> componentMetaBuilder = new ComponentMetaBuilder<C>();
        componentMetaBuilder.withBundleContext(bundleContext);
        componentMetaBuilder.withConfigurationFactory(componentAnnotation.configurationFactory());
        componentMetaBuilder.withConfigurationPid(componentAnnotation.configurationPid());
        componentMetaBuilder.withConfigurationRequired(componentAnnotation.configurationRequired());
        componentMetaBuilder.withDescription(componentAnnotation.description());
        componentMetaBuilder.withIcon(componentAnnotation.icon());
        componentMetaBuilder.withInstanceSupplier(instanceSupplier);
        componentMetaBuilder.withLabel(componentAnnotation.label());
        componentMetaBuilder.withName(componentAnnotation.name());
        componentMetaBuilder.withType(clazz);

        componentMetaBuilder.withAttributes(generateMetaForAttributes(clazz));

        return componentMetaBuilder.build();
    }

    private AttributeMeta<?>[] generateMetaForAttributes(Class<?> clazz) {
        Annotation[] classAnnotations = clazz.getAnnotations();
        for (Annotation annotation : classAnnotations) {
            processAnnotation(annotation);
        }
        return null;
    }

    private void processAnnotation(Annotation annotation) {
        // TODO Auto-generated method stub

    }

}
