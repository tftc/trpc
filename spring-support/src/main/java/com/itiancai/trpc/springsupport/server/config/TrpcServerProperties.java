package com.itiancai.trpc.springsupport.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties("trpc.server")
public class TrpcServerProperties {
  /**
   * Server port to listen on. Defaults to 9090.
   */
  private int port = 9090;

  /**
   * Bind address for the server. Defaults to 0.0.0.0.
   */
  private String address = "0.0.0.0";

  /**
   * Security options for transport security. Defaults to disabled.
   */
  private final Security security = new Security();

  @Data
  public static class Security {

    /**
     * Flag that controls whether transport security is used
     */
    private Boolean enabled = false;

    /**
     * Path to SSL certificate chain
     */
    private String certificateChainPath = "";

    /**
     * Path to SSL certificate
     */
    private String certificatePath = "";

  }

}