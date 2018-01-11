package com.itiancai.trpc.plugin.generator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Protobuf {

  private String fieldType;
  private boolean required;
  private int order;

}
