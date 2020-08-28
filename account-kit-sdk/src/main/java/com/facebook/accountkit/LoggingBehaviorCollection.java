package com.facebook.accountkit;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class LoggingBehaviorCollection {
  private final HashSet<LoggingBehavior> loggingBehaviors;

  public LoggingBehaviorCollection() {
    this.loggingBehaviors = new HashSet(Collections.singleton(LoggingBehavior.DEVELOPER_ERRORS));
  }

  public void add(LoggingBehavior behavior) {
    synchronized (this.loggingBehaviors) {
      this.loggingBehaviors.add(behavior);
    }
  }

  public void clear() {
    synchronized (this.loggingBehaviors) {
      this.loggingBehaviors.clear();
    }
  }

  public Set<LoggingBehavior> get() {
    synchronized (this.loggingBehaviors) {
      return Collections.unmodifiableSet(new HashSet(this.loggingBehaviors));
    }
  }

  public boolean isEnabled(LoggingBehavior behavior) {
    synchronized (this.loggingBehaviors) {
      return this.loggingBehaviors.contains(behavior);
    }
  }

  public void remove(LoggingBehavior behavior) {
    synchronized (this.loggingBehaviors) {
      this.loggingBehaviors.remove(behavior);
    }
  }
}
