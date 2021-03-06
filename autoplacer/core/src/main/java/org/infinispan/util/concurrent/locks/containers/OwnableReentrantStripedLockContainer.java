/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009 Red Hat Inc. and/or its affiliates and other
 * contributors as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
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
package org.infinispan.util.concurrent.locks.containers;

import net.jcip.annotations.ThreadSafe;
import org.infinispan.util.concurrent.locks.OwnableReentrantLock;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * A LockContainer that holds {@link org.infinispan.util.concurrent.locks.OwnableReentrantLock}s.
 *
 * @author Manik Surtani (<a href="mailto:manik@jboss.org">manik@jboss.org</a>)
 * @see ReentrantStripedLockContainer
 * @see org.infinispan.util.concurrent.locks.OwnableReentrantLock
 * @since 4.0
 */
@ThreadSafe
public class OwnableReentrantStripedLockContainer extends AbstractStripedLockContainer<OwnableReentrantLock> {
   OwnableReentrantLock[] sharedLocks;

   /**
    * Creates a new LockContainer which uses a certain number of shared locks across all elements that need to be
    * locked.
    *
    * @param concurrencyLevel concurrency level for number of stripes to create.  Stripes are created in powers of two,
    *                         with a minimum of concurrencyLevel created.
    */
   public OwnableReentrantStripedLockContainer(int concurrencyLevel) {
      initLocks(calculateNumberOfSegments(concurrencyLevel));
   }

   @Override
   protected void initLocks(int numLocks) {
      sharedLocks = new OwnableReentrantLock[numLocks];
      for (int i = 0; i < numLocks; i++) sharedLocks[i] = new OwnableReentrantLock();
   }

   @Override
   public final OwnableReentrantLock getExclusiveLock(Object object) {
      return sharedLocks[hashToIndex(object)];
   }

   @Override
   public final boolean ownsExclusiveLock(Object object, Object owner) {
      OwnableReentrantLock lock = getExclusiveLock(object);
      return owner.equals(lock.getOwner());
   }

   @Override
   public final boolean isExclusiveLocked(Object object) {
      OwnableReentrantLock lock = getExclusiveLock(object);
      return lock.isLocked();
   }

   @Override
   public final int getNumLocksHeld() {
      int i = 0;
      for (OwnableReentrantLock l : sharedLocks) if (l.isLocked()) i++;
      return i;
   }

   public String toString() {
      return "OwnableReentrantStripedReadWriteLockContainer{" +
            "sharedLocks=" + (sharedLocks == null ? null : Arrays.asList(sharedLocks)) +
            '}';
   }

   @Override
   public int size() {
      return sharedLocks.length;
   }

   @Override
   protected boolean tryExclusiveLock(OwnableReentrantLock lock, long timeout, TimeUnit unit, Object lockOwner) throws InterruptedException {
      return lock.tryLock(lockOwner, timeout, unit);
   }

   @Override
   protected void unlockExclusive(OwnableReentrantLock l, Object owner) {
      l.unlock(owner);
   }

   @Override
   protected void unlockShare(OwnableReentrantLock toRelease, Object owner) {
      //no-op
   }

   @Override
   protected boolean tryShareLock(OwnableReentrantLock lock, long timeout, TimeUnit unit, Object lockOwner) throws InterruptedException {
      return false;
   }
}
