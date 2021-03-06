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
package org.infinispan.util.concurrent.locks.containers.readwrite;

import org.infinispan.util.concurrent.locks.OwnableReentrantReadWriteLock;
import org.infinispan.util.concurrent.locks.containers.AbstractPerEntryLockContainer;

import java.util.concurrent.TimeUnit;

/**
 * // TODO: Document this
 *
 * @author Pedro Ruivo
 * @since 5.2
 */
public class OwnableReentrantPerEntryReadWriteLockContainer extends AbstractPerEntryLockContainer<OwnableReentrantReadWriteLock> {


   public OwnableReentrantPerEntryReadWriteLockContainer(int concurrencyLevel) {
      super(concurrencyLevel);
   }

   @Override
   protected OwnableReentrantReadWriteLock newLock() {
      return new OwnableReentrantReadWriteLock();
   }

   @Override
   protected void unlockExclusive(OwnableReentrantReadWriteLock toRelease, Object owner) {
      toRelease.unlock(owner);
   }

   @Override
   protected void unlockShare(OwnableReentrantReadWriteLock toRelease, Object owner) {
      toRelease.unlockShare(owner);
   }

   @Override
   protected boolean tryExclusiveLock(OwnableReentrantReadWriteLock lock, long timeout, TimeUnit unit, Object lockOwner) throws InterruptedException {
      return lock.tryLock(lockOwner, timeout, unit);
   }

   @Override
   protected boolean tryShareLock(OwnableReentrantReadWriteLock lock, long timeout, TimeUnit unit, Object lockOwner) throws InterruptedException {
      return lock.tryShareLock(lockOwner, timeout, unit);
   }

   @Override
   public boolean ownsExclusiveLock(Object key, Object owner) {
      OwnableReentrantReadWriteLock l = getLockFromMap(key, false);
      return l != null && owner.equals(l.getOwner());
   }

   @Override
   public boolean ownsShareLock(Object key, Object owner) {
      OwnableReentrantReadWriteLock l = getLockFromMap(key, false);
      return l.ownsShareLock(owner);
   }

   @Override
   public boolean isExclusiveLocked(Object key) {
      OwnableReentrantReadWriteLock l = getLockFromMap(key, false);
      return l != null && l.isLocked();
   }

   @Override
   public boolean isSharedLocked(Object key) {
      OwnableReentrantReadWriteLock l = getLockFromMap(key, false);
      return l != null && l.isShareLocked();
   }

   @Override
   public OwnableReentrantReadWriteLock getExclusiveLock(Object key) {
      return getLockFromMap(key, true);
   }

   @Override
   public OwnableReentrantReadWriteLock getShareLock(Object key) {
      return getLockFromMap(key, true);
   }
}
