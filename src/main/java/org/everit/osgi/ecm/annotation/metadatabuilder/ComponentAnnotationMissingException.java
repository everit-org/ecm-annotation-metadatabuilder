/**
 * This file is part of Everit - Component Annotations Metadata Builder.
 *
 * Everit - Component Annotations Metadata Builder is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Everit - Component Annotations Metadata Builder is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Everit - Component Annotations Metadata Builder.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.everit.osgi.ecm.annotation.metadatabuilder;

public class ComponentAnnotationMissingException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -1780793818219338114L;

    public ComponentAnnotationMissingException(String message) {
        super(message);
    }

}
