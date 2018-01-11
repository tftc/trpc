package com.itiancai.trpc.plugin.generator;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import io.protostuff.compiler.ParserModule;
import io.protostuff.compiler.model.ImmutableModule;
import io.protostuff.compiler.model.ModuleConfiguration;
import io.protostuff.compiler.model.Proto;
import io.protostuff.compiler.model.UsageIndex;
import io.protostuff.compiler.parser.FileReader;
import io.protostuff.compiler.parser.FileReaderFactory;
import io.protostuff.compiler.parser.Importer;
import io.protostuff.compiler.parser.ProtoContext;
import io.protostuff.generator.CompilerRegistry;
import io.protostuff.generator.GeneratorException;
import io.protostuff.generator.ProtoCompiler;

public class ProtostuffCompiler {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProtostuffCompiler.class);

  protected final Injector injector;

  /**
   * Create new compiler instance.
   */
  public ProtostuffCompiler() {
    injector = Guice.createInjector(new ParserModule(), new CompilerModule());
  }

  /**
   * Compile module - parse source files and generate code using specified outputs.
   */
  public void compile(ModuleConfiguration configuration) {
    LOGGER.debug("Compiling module {}", configuration);
    FileReaderFactory fileReaderFactory = injector.getInstance(FileReaderFactory.class);
    Importer importer = injector.getInstance(Importer.class);
    CompilerRegistry registry = injector.getInstance(CompilerRegistry.class);
    ProtoCompiler compiler = registry.findCompiler(configuration.getGenerator());
    if (compiler == null) {
      throw new GeneratorException("Unknown template: %s | %s", configuration.getGenerator(), registry.availableCompilers());
    }
    FileReader fileReader = fileReaderFactory.create(configuration.getIncludePaths());
    Map<String, Proto> importedFiles = new HashMap<>();
    for (String path : configuration.getProtoFiles()) {
      LOGGER.info("Parse {}", path);
      ProtoContext context = importer.importFile(fileReader, path);
      Proto proto = context.getProto();
      importedFiles.put(path, proto);
    }
    ImmutableModule.Builder builder = ImmutableModule.builder();
    builder.name(configuration.getName());
    builder.output(configuration.getOutput());
    builder.options(configuration.getOptions());
    for (Proto proto : importedFiles.values()) {
      builder.addProtos(proto);
    }
    UsageIndex index = UsageIndex.build(importedFiles.values());
    builder.usageIndex(index);
    ImmutableModule module = builder.build();
    for (Proto proto : importedFiles.values()) {
      proto.setModule(module);
    }
    compiler.compile(module);
  }
}