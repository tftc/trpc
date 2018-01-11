package com.itiancai.trpc.plugin.generator;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.protostuff.compiler.model.ImmutableModuleConfiguration;

public class ProtostuffCompilerTest {

  @Test
  @Disabled
  public void compile() {
    ProtostuffCompiler compiler = new ProtostuffCompiler();
    compiler.compile(ImmutableModuleConfiguration.builder()
            .name("java")
//            .addProtoFiles("protostuff_unittest/comments.proto")
//            .addProtoFiles("protostuff_unittest/field_modifiers.proto")
            .addProtoFiles("protostuff_unittest/index.proto")
//            .addProtoFiles("protostuff_unittest/map.proto")
//            .addProtoFiles("protostuff_unittest/oneof.proto")
//            .addProtoFiles("protostuff_unittest/messages_sample.proto")
            .generator(CompilerModule.JAVA_COMPILER)
            .output("target/generated-test-sources")
            .build());
  }

}