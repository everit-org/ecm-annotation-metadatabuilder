/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.osgi.ecm.annotation.metadatabuilder;

/**
 * Thrown when annotations are defined in the way that they the behavior of the component cannot be
 * determined. E.g.: Activate annotation is attached to multiple functions.
 */
public class InconsistentAnnotationException extends RuntimeException {

  private static final long serialVersionUID = 8835062566167923386L;

  public InconsistentAnnotationException(final String message) {
    super(message);
  }

}
