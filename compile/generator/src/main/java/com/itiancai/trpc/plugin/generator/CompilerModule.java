package com.itiancai.trpc.plugin.generator;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.MapBinder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import io.protostuff.generator.CompilerRegistry;
import io.protostuff.generator.CompilerUtils;
import io.protostuff.generator.ExtensibleStCompiler;
import io.protostuff.generator.ExtensibleStCompilerFactory;
import io.protostuff.generator.ExtensionProvider;
import io.protostuff.generator.GeneratorException;
import io.protostuff.generator.OutputStreamFactory;
import io.protostuff.generator.ProtoCompiler;
import io.protostuff.generator.StCompiler;
import io.protostuff.generator.StCompilerFactory;
import io.protostuff.generator.html.markdown.MarkdownProcessor;
import io.protostuff.generator.html.markdown.PegDownMarkdownProcessor;

import static com.google.inject.multibindings.MapBinder.newMapBinder;

public class CompilerModule extends AbstractModule {

  public static final String JAVA_COMPILER_TEMPLATE = "generator/template/main.stg";

  public static final String JAVA_COMPILER = "java";

  @Override
  protected void configure() {
    bind(CompilerRegistry.class);
    bind(CompilerUtils.class);
    bind(MarkdownProcessor.class).to(PegDownMarkdownProcessor.class).in(Scopes.SINGLETON);
    install(new FactoryModuleBuilder()
            .implement(ProtoCompiler.class, StCompiler.class)
            .build(StCompilerFactory.class));
    install(new FactoryModuleBuilder()
            .implement(ProtoCompiler.class, ExtensibleStCompiler.class)
            .build(ExtensibleStCompilerFactory.class));
    MapBinder<String, ProtoCompiler> compilers = newMapBinder(binder(), String.class, ProtoCompiler.class);
    compilers.addBinding(JAVA_COMPILER).toProvider(CompilerModule.JavaCompilerProviderPri.class);
  }

  @Provides
  OutputStreamFactory outputStreamFactory() {
    return location -> {
      try {
        Path path = Paths.get(location);
        Path dir = path.getParent();
        Files.createDirectories(dir);
        return new FileOutputStream(location);
      } catch (IOException e) {
        throw new GeneratorException("Could not create file: %s", e, location);
      }
    };
  }

  public static class JavaCompilerProviderPri implements Provider<ProtoCompiler> {

    private final ExtensibleStCompilerFactory factory;

    @Inject
    public JavaCompilerProviderPri(ExtensibleStCompilerFactory factory) {
      this.factory = factory;
    }

    @Override
    public ProtoCompiler get() {
      ExtensionProvider extensionProvider = new CustomExtensionProvider();
      return factory.create(Collections.singletonList(JAVA_COMPILER_TEMPLATE), extensionProvider);
    }
  }
}