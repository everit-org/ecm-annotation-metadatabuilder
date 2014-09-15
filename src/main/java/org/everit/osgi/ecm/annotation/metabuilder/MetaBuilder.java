package org.everit.osgi.ecm.annotation.metabuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.Reference;
import org.everit.osgi.ecm.annotation.attribute.ReferenceAttribute;
import org.everit.osgi.ecm.meta.AttributeMetaHolder;
import org.everit.osgi.ecm.meta.ComponentMeta;
import org.everit.osgi.ecm.meta.ComponentMeta.ComponentMetaBuilder;
import org.everit.osgi.ecm.meta.InstanceSupplier;
import org.everit.osgi.ecm.meta.ReferenceAttributeMeta;
import org.everit.osgi.ecm.meta.ReferenceAttributeMeta.ReferenceAttributeMetaBuilder;
import org.everit.osgi.ecm.meta.ReferenceAttributeType;
import org.everit.osgi.ecm.meta.ReferenceCardinality;
import org.everit.osgi.ecm.meta.ReferenceMeta;
import org.everit.osgi.ecm.meta.ReferenceMeta.ReferenceMetaBuilder;
import org.osgi.framework.BundleContext;

public class MetaBuilder {

    public static <C> ComponentMeta<C> buildComponentMeta(BundleContext bundleContext, Class<C> clazz,
            InstanceSupplier<C> instanceSupplier) {
        MetaBuilder metaBuilder = new MetaBuilder();
        return metaBuilder.build(clazz, bundleContext, instanceSupplier);
    }

    private final List<AttributeMetaHolder<?>> attributes = new ArrayList<AttributeMetaHolder<?>>();

    private MetaBuilder() {
    }

    private <C> ComponentMeta<C> build(Class<C> clazz, BundleContext bundleContext,
            InstanceSupplier<C> instanceSupplier) {
        Component componentAnnotation = clazz.getAnnotation(Component.class);
        if (componentAnnotation == null) {
            throw new ComponentAnnotationMissingException("Component annotation is missing on type " + clazz.toString());
        }

        ComponentMetaBuilder<C> componentMetaBuilder = new ComponentMetaBuilder<C>()
                .withBundleContext(bundleContext)
                .withConfigurationFactory(componentAnnotation.configurationFactory())
                .withConfigurationPid(componentAnnotation.configurationPid())
                .withConfigurationRequired(componentAnnotation.configurationRequired())
                .withDescription(componentAnnotation.description())
                .withIcon(componentAnnotation.icon())
                .withInstanceSupplier(instanceSupplier)
                .withLabel(componentAnnotation.label())
                .withName(componentAnnotation.name())
                .withType(clazz);

        componentMetaBuilder.withAttributeHolders(generateMetaForAttributes(clazz));

        return componentMetaBuilder.build();
    }

    private ReferenceCardinality convertAnnotationReferenceCardinality(
            org.everit.osgi.ecm.annotation.ReferenceCardinality annotationCardinality) {

        switch (annotationCardinality) {
        case AT_LEAST_ONE:
            return ReferenceCardinality.AT_LEAST_ONE;
        case OPTIONAL:
            return ReferenceCardinality.OPTIONAL;
        case MULTIPLE:
            return ReferenceCardinality.MULTIPLE;
        case MANDATORY:
            return ReferenceCardinality.MANDATORY;
        }
        return null;
    }

    private ReferenceAttributeType convertReferenceAttributeType(
            org.everit.osgi.ecm.annotation.attribute.ReferenceAttributeType attributeType) {

        if (attributeType.equals(org.everit.osgi.ecm.annotation.attribute.ReferenceAttributeType.CLAUSE)) {
            return ReferenceAttributeType.CLAUSE;
        } else {
            return ReferenceAttributeType.FILTER;
        }
    }

    private String deriveName(AnnotatedElement element, Reference annotation) {
        // TODO Auto-generated method stub
        return null;
    }

    private Class<?> deriveReferenceInterface(Class<?> referenceInterface) {
        // TODO Auto-generated method stub
        return null;
    }

    private AttributeMetaHolder<?>[] generateMetaForAttributes(Class<?> clazz) {
        Annotation[] classAnnotations = clazz.getAnnotations();
        for (Annotation annotation : classAnnotations) {
            processAttributeHolderAnnotation(clazz, annotation);
        }
        return null;
    }

    private void processAttributeHolderAnnotation(AnnotatedElement element, Annotation annotation) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        if (annotationType.equals(Reference.class)) {
            processReferenceAnnotation(element, (Reference) annotation);
        }
    }

    private void processReferenceAnnotation(AnnotatedElement element, Reference annotation) {
        ReferenceAttribute attribute = annotation.attribute();
        ReferenceAttributeMeta referenceAttribute = new ReferenceAttributeMetaBuilder()
                .withDefaultValue(attribute.defaultValue())
                .withDescription(attribute.description())
                .withLabel(attribute.label())
                .withMetatype(attribute.metatype())
                .withName(attribute.name())
                .withReferenceAttributeType(convertReferenceAttributeType(attribute.attributeType()))
                .build();

        ReferenceMeta referenceMeta = new ReferenceMetaBuilder()
                .withName(deriveName(element, annotation))
                .withReferenceInterface(deriveReferenceInterface(annotation.referenceInterface()))
                .withBind(annotation.bind())
                .withCardinality(convertAnnotationReferenceCardinality(annotation.cardinality()))
                .withDynamic(annotation.dynamic())
                .withUnbind(annotation.unbind())
                .withAttribute(referenceAttribute).build();

        attributes.add(referenceMeta);
    }
}
