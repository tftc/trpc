package com.itiancai.trpc.springsupport.client.config;

import com.google.common.collect.Maps;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Map;

import lombok.Data;

@Data
@ConfigurationProperties("trpc")
public class TrpcChannelsProperties {

  @NestedConfigurationProperty
  private Map<String, TrpcChannelProperties> client = Maps.newHashMap();

  public TrpcChannelProperties getChannel(String name) {
    TrpcChannelProperties grpcChannelProperties = client.get(name);
    if (grpcChannelProperties == null) {
      grpcChannelProperties = TrpcChannelProperties.DEFAULT;
    }
    return grpcChannelProperties;
  }
}