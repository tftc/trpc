package com.itiancai.trpc.springsupport.client.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class TrpcChannelProperties {

  public static final String DEFAULT_HOST = "127.0.0.1";
  public static final Integer DEFAULT_PORT = 9090;

  public static final TrpcChannelProperties DEFAULT = new TrpcChannelProperties();

  private List<String> host = new ArrayList<String>() {
    private static final long serialVersionUID = -8367871342050560040L;

    {
      add(DEFAULT_HOST);
    }
  };
  private List<Integer> port = new ArrayList<Integer>() {
    private static final long serialVersionUID = 4705083089654936515L;

    {
      add(DEFAULT_PORT);
    }
  };

  private boolean plaintext = true;

  /**
   * Setting to enable keepalive. Default to {@code false}
   */
  private boolean enableKeepAlive = false;

  /**
   * Sets whether keepalive will be performed when there are no outstanding RPC on a connection.
   * Defaults to {@code false}.
   */
  private boolean keepAliveWithoutCalls = false;

  /**
   * The default delay in seconds before we send a keepalive. Defaults to {@code 180}
   */
  private long keepAliveTime = 180;

  /**
   * The default timeout in seconds for a keepalive ping request. Defaults to {@code 20}
   */
  private long keepAliveTimeout = 20;
}