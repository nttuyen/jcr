/*
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.exoplatform.services.jcr.impl.storage.jdbc.optimisation.db;

import org.exoplatform.services.jcr.datamodel.NodeData;
import org.exoplatform.services.jcr.datamodel.PropertyData;
import org.exoplatform.services.jcr.impl.util.io.FileCleaner;
import org.exoplatform.services.jcr.storage.value.ValueStoragePluginProvider;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by The eXo Platform SAS
 * 
 * 20.03.2007
 * 
 * @author <a href="mailto:peter.nedonosko@exoplatform.com.ua">Peter Nedonosko</a>
 * @version $Id$
 */
public class MySQLSingleDbJDBCConnection extends SingleDbJDBCConnection
{

   /**
    * MySQL Singledatabase JDBC Connection constructor.
    * 
    * @param dbConnection
    *          JDBC connection, shoudl be opened before
    * @param readOnly
    *          boolean if true the dbConnection was marked as READ-ONLY.
    * @param containerName
    *          Workspace Storage Container name (see configuration)
    * @param valueStorageProvider
    *          External Value Storages provider
    * @param maxBufferSize
    *          Maximum buffer size (see configuration)
    * @param swapDirectory
    *          Swap directory File (see configuration)
    * @param swapCleaner
    *          Swap cleaner (internal FileCleaner).
    * @throws SQLException
    * 
    * @see org.exoplatform.services.jcr.impl.util.io.FileCleaner
    */
   public MySQLSingleDbJDBCConnection(Connection dbConnection, boolean readOnly, String containerName,
      ValueStoragePluginProvider valueStorageProvider, int maxBufferSize, File swapDirectory, FileCleaner swapCleaner)
      throws SQLException
   {

      super(dbConnection, readOnly, containerName, valueStorageProvider, maxBufferSize, swapDirectory, swapCleaner);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void prepareQueries() throws SQLException
   {
      super.prepareQueries();
      FIND_NODES_AND_PROPERTIES =
         "select J.*, P.ID AS P_ID, P.NAME AS P_NAME, P.VERSION AS P_VERSION, P.P_TYPE, P.P_MULTIVALUED,"
            + " V.DATA, V.ORDER_NUM, V.STORAGE_DESC from JCR_SVALUE V, JCR_SITEM P use index(JCR_IDX_SITEM_PARENT_ID)"
            + " join (select I.ID, I.PARENT_ID, I.NAME, I.VERSION, I.I_INDEX, I.N_ORDER_NUM from JCR_SITEM I"
            + " where I.CONTAINER_NAME=? AND I.I_CLASS=1 order by I.ID LIMIT ? OFFSET ?) J on P.PARENT_ID = J.ID"
            + " where P.I_CLASS=2 and P.CONTAINER_NAME=? and V.PROPERTY_ID=P.ID order by J.ID";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected int addNodeRecord(NodeData data) throws SQLException
   {
      // check if parent exists
      if (data.getParentIdentifier() != null)
      {
         ResultSet item = findItemByIdentifier(getInternalId(data.getParentIdentifier()));
         try
         {
            if (!item.next())
               throw new SQLException("Parent is not found. Behaviour of " + JCR_FK_ITEM_PARENT);
         }
         finally
         {
            try
            {
               item.close();
            }
            catch (SQLException e)
            {
               LOG.error("Can't close the ResultSet: " + e);
            }
         }
      }
      return super.addNodeRecord(data);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected int addPropertyRecord(PropertyData data) throws SQLException
   {
      // check if parent exists
      if (data.getParentIdentifier() != null)
      {
         ResultSet item = findItemByIdentifier(getInternalId(data.getParentIdentifier()));
         try
         {
            if (!item.next())
               throw new SQLException("Parent is not found. Behaviour of " + JCR_FK_ITEM_PARENT);
         }
         finally
         {
            try
            {
               item.close();
            }
            catch (SQLException e)
            {
               LOG.error("Can't close the ResultSet: " + e);
            }
         }
      }
      return super.addPropertyRecord(data);
   }

}
