// Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.execution;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

public interface ConfigurationWithClasspathShortener {
  /**
   * @return null if option was not selected explicitly, legacy user-local options to be used
   */
  @Nullable
  ShortenClasspath getShortenClasspath();

  /**
   * Called from UI, when user explicitly selects method to be used to shorten the command line or from the deserialization
   */
  void setShortenClasspath(ShortenClasspath mode);

  Project getProject();
}
