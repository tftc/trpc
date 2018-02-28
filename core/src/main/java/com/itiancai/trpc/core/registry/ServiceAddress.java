package com.itiancai.trpc.core.registry;

public class ServiceAddress {

  private String host;

  private int port;

  public ServiceAddress(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ServiceAddress that = (ServiceAddress) o;

    if (port != that.port) return false;
    return host != null ? host.equals(that.host) : that.host == null;
  }

  @Override
  public int hashCode() {
    int result = host != null ? host.hashCode() : 0;
    result = 31 * result + port;
    return result;
  }
}
