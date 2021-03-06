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
package org.infinispan.interceptors.gmu;

import org.infinispan.commands.tx.PrepareCommand;
import org.infinispan.context.impl.TxInvocationContext;
import org.infinispan.remoting.responses.Response;
import org.infinispan.remoting.transport.Address;
import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;

import java.util.Collection;

import static org.infinispan.interceptors.totalorder.TotalOrderHelper.*;
import static org.infinispan.transaction.gmu.GMUHelper.joinAndSetTransactionVersion;
import static org.infinispan.util.Util.getAffectedKeys;

/**
 * // TODO: Document this
 *
 * @author Pedro Ruivo
 * @since 4.0
 */
public class TotalOrderGMUDistributionInterceptor extends GMUDistributionInterceptor implements TotalOrderRpcInterceptor {

   private final Log log = LogFactory.getLog(TotalOrderGMUDistributionInterceptor.class);

   @Override
   public Object visitPrepareCommand(TxInvocationContext ctx, PrepareCommand command) throws Throwable {
      return prepare(ctx, command, this);
   }

   @Override
   public Object visitPrepare(TxInvocationContext context, PrepareCommand command) throws Throwable {
      return super.visitPrepareCommand(context, command);
   }

   @Override
   protected void prepareOnAffectedNodes(TxInvocationContext ctx, PrepareCommand command, Collection<Address> recipients, boolean sync) {
      if(log.isTraceEnabled()) {
         log.tracef("Total Order Anycast transaction %s with Total Order", command.getGlobalTransaction().prettyPrint());
      }
      Collection<Response> responses = totalOrderBroadcastPrepare(command, recipients, getAffectedKeys(command, null),
                                                                  rpcManager, false, configuration.isSyncCommitPhase(),
                                                                  configuration.getSyncReplTimeout());
      joinAndSetTransactionVersion(responses, ctx, versionGenerator);
   }
}
