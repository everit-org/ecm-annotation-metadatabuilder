package org.everit.osgi.ecm.annotation.metabuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.Reference;
import org.everit.osgi.ecm.annotation.attribute.ReferenceAttribute;
import org.everit.osgi.ecm.meta.AttributeMetaHolder;
import org.everit.osgi.ecm.meta.ComponentMeta;
import org.everit.osgi.ecm.meta.ComponentMeta.ComponentMetaBuilder;
import org.everit.osgi.ecm.meta.ReferenceAttributeMeta;
import org.everit.osgi.ecm.meta.ReferenceAttributeMeta.ReferenceAttributeMetaBuilder;
import org.everit.osgi.ecm.meta.ReferenceCardinality;
import org.everit.osgi.ecm.meta.ReferenceConfigurationType;
import org.everit.osgi.ecm.meta.ReferenceMeta;
import org.everit.osgi.ecm.meta.ReferenceMeta.ReferenceMetaBuilder;
import org.osgi.framework.BundleContext;

public class MetaBuilder<C> {

    public static <C> ComponentMeta<C> buildComponentMeta(BundleContext bundleContext, Class<C> clazz) {
        MetaBuilder<C> metaBuilder = new MetaBuilder<C>(clazz, bundleContext);
        return metaBuilder.build();
    }

    private final List<AttributeMetaHolder<?>> attributes = new ArrayList<AttributeMetaHolder<?>>();

    private BundleContext bundleContext;

    private Class<C> clazz;

    private MetaBuilder() {
    }

    private MetaBuilder(Class<C> clazz, BundleContext bundleContext) {
        this.clazz = clazz;
        this.bundleContext = bundleContext;
    }

    private ComponentMeta<C> build() {
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
                .withLabel(componentAnnotation.label())
                .withName(componentAnnotation.name())
                .withType(clazz);

        generateMetaForAttributes();

        componentMetaBuilder.withAttributeHolders(attributes.toArray(new AttributeMetaHolder<?>[0]));

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

    private ReferenceConfigurationType convertReferenceAttributeType(
            org.everit.osgi.ecm.annotation.attribute.ReferenceConfigurationType attributeType) {

        if (attributeType.equals(org.everit.osgi.ecm.annotation.attribute.ReferenceConfigurationType.CLAUSE)) {
            return ReferenceConfigurationType.CLAUSE;
        } else {
            return ReferenceConfigurationType.FILTER;
        }
    }

    private String deriveName(AnnotatedElement element, Annotation annotation) {
        String name = getValueOfAnnotation(annotation, "name");
        name = makeStringNullIfEmpty(name);
        if (name != null) {
            return name;
        }

        if (element instanceof Field) {
            return ((Field) element).getName();
        }

        return null;
    }

    private Class<?> deriveReferenceInterface(AnnotatedElement element, Reference annotation) {
        Class<?> referenceInterface = annotation.referenceInterface();
        if (referenceInterface != null) {
            return referenceInterface;
        }

        if (element instanceof Field) {
            return ((Field) element).getType();
        }

        return null;
    }

    private void generateAttributeMetaForAnnotatedElements(AnnotatedElement[] annotatedElements) {
        for (AnnotatedElement annotatedElement : annotatedElements) {
            Annotation[] annotations = annotatedElement.getAnnotations();
            for (Annotation annotation : annotations) {
                processAttributeHolderAnnotation(annotatedElement, annotation);
            }
        }
    }

    private void generateMetaForAttributes() {
        generateAttributeMetaForAnnotatedElements(new AnnotatedElement[] { clazz });
        generateAttributeMetaForAnnotatedElements(clazz.getDeclaredFields());
        generateAttributeMetaForAnnotatedElements(clazz.getDeclaredMethods());
    }

    private <R> R getValueOfAnnotation(Annotation annotation, String fieldName) {
        Class<? extends Annotation> annotationType = annotation.getClass();
        Method method;
        try {
            method = annotationType.getMethod(fieldName);

            @SuppressWarnings("unchecked")
            R result = (R) method.invoke(annotation);

            return result;
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }

    private String makeStringNullIfEmpty(String text) {
        if (text == null) {
            return null;
        }

        if (text.trim().equals("")) {
            return null;
        }
        return text;
    }

    private void processAttributeHolderAnnotation(AnnotatedElement element, Annotation annotation) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        if (annotationType.equals(Reference.class)) {
            processReferenceAnnotation(element, (Reference) annotation);
        } // TODO handle more annotations
    }

    private void processReferenceAnnotation(AnnotatedElement element, Reference annotation) {
        ReferenceAttribute attribute = annotation.attribute();
        ReferenceAttributeMeta referenceAttribute = new ReferenceAttributeMetaBuilder()
                .withDefaultValue(attribute.defaultValue())
                .withDescription(makeStringNullIfEmpty(attribute.description()))
                .withLabel(makeStringNullIfEmpty(attribute.label()))
                .withMetatype(attribute.metatype())
                .withName(makeStringNullIfEmpty(attribute.name()))
                .withReferenceConfigurationType(convertReferenceAttributeType(attribute.configurationType()))
                .build();

        ReferenceMeta referenceMeta = new ReferenceMetaBuilder()
                .withName(deriveName(element, annotation))
                .withReferenceInterface(deriveReferenceInterface(element, annotation))
                .withBind(makeStringNullIfEmpty(annotation.bind()))
                .withCardinality(convertAnnotationReferenceCardinality(annotation.cardinality()))
                .withDynamic(annotation.dynamic())
                .withUnbind(makeStringNullIfEmpty(annotation.unbind()))
                .withAttribute(referenceAttribute).build();

        attributes.add(referenceMeta);
    }
}
