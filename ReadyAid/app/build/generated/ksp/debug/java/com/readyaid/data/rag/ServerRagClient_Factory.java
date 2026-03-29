package com.readyaid.data.rag;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class ServerRagClient_Factory implements Factory<ServerRagClient> {
  @Override
  public ServerRagClient get() {
    return newInstance();
  }

  public static ServerRagClient_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ServerRagClient newInstance() {
    return new ServerRagClient();
  }

  private static final class InstanceHolder {
    private static final ServerRagClient_Factory INSTANCE = new ServerRagClient_Factory();
  }
}
