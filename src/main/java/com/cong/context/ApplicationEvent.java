package com.cong.context;

import java.time.Clock;
import java.time.Instant;

/**
 * basic event of application
 */
public class ApplicationEvent {
  private final Instant timestamp;
  private final Object source;

  /**
   * create a new application event
   */
  public ApplicationEvent(Object source) {
    this(source, Clock.systemDefaultZone());
  }

  /**
   * create a new application event with curtain clock
   */
  protected ApplicationEvent(Object source, Clock clock) {
    if (source == null) {
      throw new IllegalArgumentException("Event source cannot be null");
    }
    this.source = source;
    this.timestamp = clock.instant();
  }

  /**
   * get timeStamp
   */
  public final Instant getTimestamp() {
    return this.timestamp;
  }

  /**
   * get source
   */
  public final Object getSource() {
    return this.source;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[timestamp=" + getTimestamp() + ", source=" + getSource() + "]";
  }
}
