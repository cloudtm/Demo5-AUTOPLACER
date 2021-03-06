/*
 * INESC-ID, Instituto de Engenharia de Sistemas e Computadores Investigação e Desevolvimento em Lisboa
 * Copyright 2013 INESC-ID and/or its affiliates and other
 * contributors as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.infinispan.dataplacement.c50.keyfeature;

import java.util.Map;

/**
 * Manages the key features for all the keys generated by the application. This is application dependent, so
 * the programmer should implement this interface and pass that implementation to Infinispan
 *
 * @author Pedro Ruivo
 * @since 5.2
 */
public interface KeyFeatureManager {

   /**
    * returns an array with all the features that a key can have
    *
    * Note: this information should be static!
    *
    * @return  an array with all the features that a key can have
    */
   Feature[] getAllKeyFeatures();

   /**
    * returns the features of this particular key.
    *
    * Note: a key don't need to have all the features.
    * Note2: if you put a feature that was no returned by the method above, it will be ignored
    *
    * @param key  the key
    * @return     the features of this particular key.
    */
   Map<Feature, FeatureValue> getFeatures(Object key);

}
