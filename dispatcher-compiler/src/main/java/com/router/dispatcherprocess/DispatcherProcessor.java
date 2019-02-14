package com.router.dispatcherprocess;

import com.google.auto.service.AutoService;
import com.router.annotation.Dispatcher;
import com.router.annotation.RouterLoader;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

@AutoService(Processor.class)
public class DispatcherProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private Filer filer;
    private Messager messager;
    private String modelName;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        Map<String, String> options = processingEnvironment.getOptions();
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("need config model argument");
        }
        modelName = options.get(Constant.MODEL_NAME);
        if (modelName == null || modelName.length() == 0) {
            throw new IllegalArgumentException("need config model name");
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Dispatcher.class);
        try {
            return parseElement(elements);
        } catch (IOException e) {
            return false;
        }
    }

    private boolean parseElement(Set<? extends Element> elements) throws IOException {
        if (elements == null || elements.size() == 0) {
            return false;
        }

        ParameterizedTypeName parameter = ParameterizedTypeName.get(ClassName.get(ArrayList.class),
                ClassName.get(Class.class));
        ParameterSpec parameterSpec = ParameterSpec.builder(parameter, "dispatchers").build();

        MethodSpec.Builder builder = MethodSpec.methodBuilder(Constant.GENERATOR_METHODNAME);
        builder.addAnnotation(Override.class);
        builder.addModifiers(Modifier.PUBLIC);
        builder.addParameter(parameterSpec);


        for (Element element : elements) {
            if (element.getKind() != ElementKind.CLASS) {
                throw new IllegalStateException("Dispatcher annotation only be used for class");
            }

            ClassName dispatchName = ClassName.get((TypeElement) element);
            builder.addStatement("dispatchers.add($T.class)", dispatchName);
        }

        TypeSpec typeSpec = TypeSpec.classBuilder(Constant.GENERATOR_CLASSNAME + "$" + modelName)
                .addMethod(builder.build())
                .addJavadoc("do not edit \n")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(RouterLoader.class))
                .build();

        JavaFile.builder(Constant.GENERATOR_PACKAGE, typeSpec).build().writeTo(filer);

        return true;
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> support = new LinkedHashSet<>();
        support.add(Dispatcher.class.getCanonicalName());
        return support;
    }
}
