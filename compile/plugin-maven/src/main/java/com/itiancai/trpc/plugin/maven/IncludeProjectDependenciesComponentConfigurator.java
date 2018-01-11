package com.itiancai.trpc.plugin.maven;

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.configurator.AbstractComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.composite.ObjectWithFieldsConverter;
import org.codehaus.plexus.component.configurator.converters.special.ClassRealmConverter;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component(role = ComponentConfigurator.class, hint = "include-project-dependencies")
public class IncludeProjectDependenciesComponentConfigurator extends AbstractComponentConfigurator {


  @Override
  public void configureComponent(Object component, PlexusConfiguration configuration,
                                 ExpressionEvaluator evaluator, ClassRealm realm, ConfigurationListener listener) throws ComponentConfigurationException {
    addProjectDependenciesToClassRealm(evaluator, realm);
    converterLookup.registerConverter(new ClassRealmConverter(realm));
    ObjectWithFieldsConverter converter = new ObjectWithFieldsConverter();
    converter.processConfiguration(converterLookup, component, realm.getParentClassLoader(), configuration,
            evaluator, listener);
  }

  private void addProjectDependenciesToClassRealm(ExpressionEvaluator expressionEvaluator, ClassRealm realm)
          throws ComponentConfigurationException {
    List<String> runtimeClasspathElements;
    try {
      // noinspection unchecked
      runtimeClasspathElements = (List<String>) expressionEvaluator
              .evaluate("${project.runtimeClasspathElements}");
    } catch (ExpressionEvaluationException e) {
      throw new ComponentConfigurationException(
              "There was a problem evaluating: ${project.runtimeClasspathElements}", e);
    }

    // Add the project dependencies to the ClassRealm
    final URL[] urls = buildUrls(runtimeClasspathElements);
    for (URL url : urls) {
      realm.addURL(url);
    }
  }

  private URL[] buildUrls(List<String> runtimeClasspathElements) throws ComponentConfigurationException {
    // Add the projects classes and dependencies
    List<URL> urls = new ArrayList<>(runtimeClasspathElements.size());
    for (String element : runtimeClasspathElements) {
      try {
        final URL url = new File(element).toURI().toURL();
        urls.add(url);
      } catch (MalformedURLException e) {
        throw new ComponentConfigurationException("Unable to access project dependency: " + element, e);
      }
    }

    // Add the plugin's dependencies (so Trove stuff works if Trove isn't on
    return urls.toArray(new URL[urls.size()]);
  }

}