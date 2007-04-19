/*
  Copyright (C) 2004-2007 University Corporation for Advanced Internet Development, Inc.
  Copyright (C) 2004-2007 The University Of Chicago

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package edu.internet2.middleware.grouper.internal.dao.hibernate;
import  edu.internet2.middleware.grouper.ErrorLog;
import  edu.internet2.middleware.grouper.GrouperDAOFactory;
import  edu.internet2.middleware.grouper.MemberOf;
import  edu.internet2.middleware.grouper.Stem;
import  edu.internet2.middleware.grouper.StemNotFoundException;
import edu.internet2.middleware.grouper.internal.dao.GroupDAO;
import  edu.internet2.middleware.grouper.internal.dao.GrouperDAOException;
import  edu.internet2.middleware.grouper.internal.dao.StemDAO;
import  edu.internet2.middleware.grouper.internal.dto.GroupDTO;
import  edu.internet2.middleware.grouper.internal.dto.GroupTypeDTO;
import  edu.internet2.middleware.grouper.internal.dto.MemberDTO;
import  edu.internet2.middleware.grouper.internal.dto.StemDTO;
import  edu.internet2.middleware.grouper.internal.util.Rosetta;
import  java.util.Date;
import  java.util.Iterator;
import  java.util.LinkedHashSet;
import  java.util.Set;
import  net.sf.hibernate.*;

/**
 * Basic Hibernate <code>Stem</code> DAO interface.
 * <p><b>WARNING: THIS IS AN ALPHA INTERFACE THAT MAY CHANGE AT ANY TIME.</b></p>
 * @author  blair christensen.
 * @version $Id: HibernateStemDAO.java,v 1.7 2007-04-19 14:31:20 blair Exp $
 * @since   1.2.0
 */
public class HibernateStemDAO extends HibernateDAO implements StemDAO {

  // PRIVATE CLASS CONSTANTS //
  private static final String KLASS = HibernateStemDAO.class.getName();


  // PRIVATE INSTANCE VARIABLES //
  private String  createSource;
  private long    createTime;
  private String  creatorUUID;
  private String  description;
  private String  displayExtension;
  private String  displayName;
  private String  extension;
  private String  id;
  private String  name;
  private String  modifierUUID;
  private String  modifySource;
  private long    modifyTime;
  private String  parentUUID;
  private String  uuid;


  // PUBLIC INSTANCE METHODS //

  /**
   * @since   1.2.0
   */
  public String createChildGroup(StemDTO _parent, GroupDTO _child, MemberDTO _m)
    throws  GrouperDAOException
  {
    try {
      Session       hs  = HibernateDAO.getSession();
      Transaction   tx  = hs.beginTransaction();
      HibernateDAO  dao = (HibernateDAO) Rosetta.getDAO(_child);
      try {
        hs.save(dao);
        // add group-type tuples
        HibernateGroupTypeTupleDAO  tuple = new HibernateGroupTypeTupleDAO();
        Iterator                    it    = _child.getTypes().iterator();
        while (it.hasNext()) {
          tuple.setGroupUuid( _child.getUuid() );
          tuple.setTypeUuid( ( (GroupTypeDTO) it.next() ).getUuid() );
          hs.save(tuple); // new group-type tuple
        }
        hs.update( Rosetta.getDAO(_parent) );
        if ( !GrouperDAOFactory.getFactory().getMember().exists( _m.getUuid() ) ) {
          hs.save( Rosetta.getDAO(_m) );
        }
        tx.commit();
      }
      catch (HibernateException eH) {
        tx.rollback();
        throw eH;
      }
      finally {
        hs.close();
      }
      return dao.getId();
    }
    catch (HibernateException eH) {
      throw new GrouperDAOException( eH.getMessage(), eH );
    }
  } 

  /**
   * @since   1.2.0
   */
  public String createChildStem(StemDTO _parent, StemDTO _child)
    throws  GrouperDAOException
  {
    try {
      Session       hs  = HibernateDAO.getSession();
      Transaction   tx  = hs.beginTransaction();
      HibernateDAO  dao = (HibernateDAO) Rosetta.getDAO(_child);
      try {
        hs.save(dao);
        hs.update( Rosetta.getDAO(_parent) );
        tx.commit();
      }
      catch (HibernateException eH) {
        tx.rollback();
        throw eH;
      }
      finally {
        hs.close();
      }
      return dao.getId();
    }
    catch (HibernateException eH) {
      throw new GrouperDAOException( eH.getMessage(), eH );
    }
  } 

  /**
   * @since   1.2.0
   */
  public String createRootStem(StemDTO _root)
    throws  GrouperDAOException
  {
    try {
      Session           hs  = HibernateDAO.getSession();
      Transaction       tx  = hs.beginTransaction();
      HibernateStemDAO  dao = (HibernateStemDAO) _root.getDAO();
      try {
        hs.save(dao);
        tx.commit();
      }
      catch (HibernateException eH) {
        tx.rollback();
        throw eH;
      }
      finally {
        hs.close();
      }
      return dao.getId();
    }
    catch (HibernateException eH) {
      throw new GrouperDAOException( eH.getMessage(), eH );
    }
  } 

  /**
   * @since   1.2.0
   */
  public void delete(StemDTO _ns)
    throws  GrouperDAOException 
  {
    try {
      Session     hs  = HibernateDAO.getSession();
      Transaction tx  = hs.beginTransaction();
      try {
        hs.delete( _ns.getDAO() );
        tx.commit();
      }
      catch (HibernateException eH) {
        tx.rollback();
        throw eH;
      }
      finally {
        hs.close();
      } 
    }
    catch (HibernateException eH) {
      throw new GrouperDAOException( eH.getMessage(), eH );
    }
  } 

  /**
   * @since   1.2.0
   */
  public boolean exists(String uuid) 
    throws  GrouperDAOException
  {
    // TODO 20070316 cache?
    try {
      Session hs  = HibernateDAO.getSession();
      Query   qry = hs.createQuery("select ns.id from HibernateStemDAO ns where ns.uuid = :uuid");
      qry.setString("uuid", uuid);
      boolean rv  = false;
      if ( qry.uniqueResult() != null ) {
        rv = true; 
      }
      hs.close();
      return rv;
    }
    catch (HibernateException eH) {
      ErrorLog.fatal( HibernateStemDAO.class, eH.getMessage() );
      throw new GrouperDAOException( eH.getMessage(), eH );
    }
  } 
  
  /**
   * @since   1.2.0
   */
  public Set findAllByApproximateDisplayExtension(String val) 
    throws  GrouperDAOException
  {
    Set stems = new LinkedHashSet();
    try {
      Session hs  = HibernateDAO.getSession();
      Query   qry = hs.createQuery("from HibernateStemDAO as ns where lower(ns.displayExtension) like lower(:value)");
      qry.setCacheable(false);
      qry.setCacheRegion(KLASS + ".FindByApproximateDisplayExtension");
      qry.setString(  "value" , "%" + val.toLowerCase() + "%" );
      Iterator it = qry.list().iterator();
      while (it.hasNext()) {
        stems.add( StemDTO.getDTO( (StemDAO) it.next() ) );
      }
      hs.close();
    }
    catch (HibernateException eH) {
      throw new GrouperDAOException( eH.getMessage(), eH );
    }
    return stems;
  } 

  /**
   * @since   1.2.0
   */
  public Set findAllByApproximateDisplayName(String val) 
    throws  GrouperDAOException
  {
    Set stems = new LinkedHashSet();
    try {
      Session hs  = HibernateDAO.getSession();
      Query   qry = hs.createQuery("from HibernateStemDAO as ns where lower(ns.displayName) like lower(:value)");
      qry.setCacheable(false);
      qry.setCacheRegion(KLASS + ".FindByApproximateDisplayName");
      qry.setString(  "value" , "%" + val.toLowerCase() + "%" );
      Iterator it = qry.list().iterator();
      while (it.hasNext()) {
        stems.add( StemDTO.getDTO( (StemDAO) it.next() ) );
      }
      hs.close();
    }
    catch (HibernateException eH) {
      throw new GrouperDAOException( eH.getMessage(), eH );
    }
    return stems;
  } 

  /**
   * @since   1.2.0
   */
  public Set findAllByApproximateExtension(String val) 
    throws  GrouperDAOException
  {
    Set stems = new LinkedHashSet();
    try {
      Session hs  = HibernateDAO.getSession();
      Query   qry = hs.createQuery("from HibernateStemDAO as ns where lower(ns.extension) like lower(:value)");
      qry.setCacheable(false);
      qry.setCacheRegion(KLASS + ".FindByApproximateExtension");
      qry.setString(  "value" , "%" + val.toLowerCase() + "%" );
      Iterator it = qry.list().iterator();
      while (it.hasNext()) {
        stems.add( StemDTO.getDTO( (StemDAO) it.next() ) );
      }
      hs.close();
    }
    catch (HibernateException eH) {
      throw new GrouperDAOException( eH.getMessage(), eH );
    }
    return stems;
  } 

  /**
   * @since   1.2.0
   */
  public Set findAllByApproximateName(String val) 
    throws  GrouperDAOException
  {
    Set stems = new LinkedHashSet();
    try {
      Session hs  = HibernateDAO.getSession();
      Query   qry = hs.createQuery("from HibernateStemDAO as ns where lower(ns.name) like lower(:value)");
      qry.setCacheable(false);
      qry.setCacheRegion(KLASS + ".FindByApproximateName");
      qry.setString(  "value" , "%" + val.toLowerCase() + "%" );
      Iterator it = qry.list().iterator();
      while (it.hasNext()) {
        stems.add( StemDTO.getDTO( (StemDAO) it.next() ) );
      }
      hs.close();
    }
    catch (HibernateException eH) {
      throw new GrouperDAOException( eH.getMessage(), eH );
    }
    return stems;
  } 

  /**
   * @since   1.2.0
   */
  public Set findAllByApproximateNameAny(String name) 
    throws  GrouperDAOException
  {
    Set stems = new LinkedHashSet();
    try {
      Session hs  = HibernateDAO.getSession();
      Query   qry = hs.createQuery(
        "from HibernateStemDAO as ns where "
        + "   lower(ns.name)              like :name "
        + "or lower(ns.displayName)       like :name "
        + "or lower(ns.extension)         like :name "
        + "or lower(ns.displayExtension)  like :name" 
      );
      qry.setCacheable(false);
      qry.setCacheRegion(KLASS + ".FindAllByApproximateNameAny");
      qry.setString("name", "%" + name.toLowerCase() + "%");
      Iterator it = qry.list().iterator();
      while (it.hasNext()) {
        stems.add( StemDTO.getDTO( (StemDAO) it.next() ) );
      }
      hs.close();
    }
    catch (HibernateException eH) {
      throw new GrouperDAOException( eH.getMessage(), eH );
    }
    return stems;
  } 

  /**
   * @since   1.2.0
   */
  public Set findAllByCreatedAfter(Date d) 
    throws  GrouperDAOException
  {
    Set stems = new LinkedHashSet();
    try {
      Session hs  = HibernateDAO.getSession();
      Query   qry = hs.createQuery("from HibernateStemDAO as ns where ns.createTime > :time");
      qry.setCacheable(false);
      qry.setCacheRegion(KLASS + ".FindAllByCreatedAfter");
      qry.setLong( "time", d.getTime() );
      Iterator it = qry.list().iterator();
      while (it.hasNext()) {
        stems.add( StemDTO.getDTO( (StemDAO) it.next() ) );
      }
      hs.close();
    }
    catch (HibernateException eH) {
      throw new GrouperDAOException( eH.getMessage(), eH );
    }
    return stems;
  } 

  /**
   * @since   1.2.0
   */
  public Set findAllByCreatedBefore(Date d) 
    throws  GrouperDAOException
  {
    Set stems = new LinkedHashSet();
    try {
      Session hs  = HibernateDAO.getSession();
      Query   qry = hs.createQuery("from HibernateStemDAO as ns where ns.createTime < :time");
      qry.setCacheable(false);
      qry.setCacheRegion(KLASS + ".FindAllByCreatedBefore");
      qry.setLong( "time", d.getTime() );
      Iterator it = qry.list().iterator();
      while (it.hasNext()) {
        stems.add( StemDTO.getDTO( (StemDAO) it.next() ) );
      }
      hs.close();
    }
    catch (HibernateException eH) {
      throw new GrouperDAOException( eH.getMessage(), eH );
    }
    return stems;
  } 
  
  /**
   * @since   1.2.0
   */
  public Set findAllChildGroups(Stem ns)
    throws  GrouperDAOException
  {
    Set groups = new LinkedHashSet();
    try {
      Session hs  = HibernateDAO.getSession();
      Query   qry = hs.createQuery("from HibernateGroupDAO as g where g.parentUuid = :parent");
      qry.setCacheable(false);
      qry.setCacheRegion(KLASS + ".FindChildGroups");
      qry.setString( "parent", ns.getUuid() );
      Iterator it = qry.list().iterator();
      while (it.hasNext()) {
        groups.add( GroupDTO.getDTO( (GroupDAO) it.next() ) );
      }
      hs.close();
    }
    catch (HibernateException eH) {
      throw new GrouperDAOException( eH.getMessage(), eH );
    }
    return groups;
  }
  
  /**
   * @since   1.2.0
   */
  public Set findAllChildStems(Stem ns)
    throws  GrouperDAOException
  {
    Set stems = new LinkedHashSet();
    try {
      Session hs  = HibernateDAO.getSession();
      Query   qry = hs.createQuery("from HibernateStemDAO as ns where ns.parentUuid = :parent");
      qry.setCacheable(false);
      qry.setCacheRegion(KLASS + ".FindChildStems");
      qry.setString( "parent", ns.getUuid() );
      Iterator it = qry.list().iterator();
      while (it.hasNext()) {
        stems.add( StemDTO.getDTO( (StemDAO) it.next() ) );
      }
      hs.close();
    }
    catch (HibernateException eH) {
      throw new GrouperDAOException( eH.getMessage(), eH );
    }
    return stems;
  } 

  /**
   * @since   1.2.0
   */
  public StemDTO findByName(String name) 
    throws  GrouperDAOException,
            StemNotFoundException
  {
    try {
      Session hs  = HibernateDAO.getSession();
      Query   qry = hs.createQuery("from HibernateStemDAO as ns where ns.name = :name");
      qry.setCacheable(false);
      qry.setCacheRegion(KLASS + ".FindByName");
      qry.setString("name", name);
      HibernateStemDAO dao = (HibernateStemDAO) qry.uniqueResult();
      hs.close();
      if (dao == null) {
        throw new StemNotFoundException();
      }
      return StemDTO.getDTO(dao);
    }
    catch (HibernateException eH) {
      throw new GrouperDAOException( eH.getMessage(), eH );
    }
  } 

  /**
   * @since   1.2.0
   */
  public StemDTO findByUuid(String uuid)
    throws  GrouperDAOException,
            StemNotFoundException
  {
    try {
      Session hs  = HibernateDAO.getSession();
      Query   qry = hs.createQuery("from HibernateStemDAO as ns where ns.uuid = :uuid");
      qry.setCacheable(false);
      qry.setCacheRegion(KLASS + ".FindByUuid");
      qry.setString("uuid", uuid);
      HibernateStemDAO dao = (HibernateStemDAO) qry.uniqueResult();
      hs.close();
      if (dao == null) {
        throw new StemNotFoundException();
      }
      return StemDTO.getDTO(dao);
    }
    catch (HibernateException eH) {
      throw new GrouperDAOException( eH.getMessage(), eH );
    }
  } 

  /**
   * @since   1.2.0
   */
  public String getCreateSource() {
    return this.createSource;
  }

  /**
   * @since   1.2.0
   */
  public long getCreateTime() {
    return this.createTime;
  }

  /**
   * @since   1.2.0
   */
  public String getCreatorUuid() {
    return this.creatorUUID;
  }

  /**
   * @since   1.2.0
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * @since   1.2.0
   */
  public String getDisplayExtension() {
    return this.displayExtension;
  }

  /**
   * @since   1.2.0
   */
  public String getDisplayName() {
    return this.displayName;
  }

  /**
   * @since   1.2.0
   */
  public String getExtension() {
    return this.extension;
  }

  /** 
   * @since   1.2.0
   */
  public String getId() {
    return this.id;
  }

  /**
   * @since   1.2.0
   */
  public String getModifierUuid() {
    return this.modifierUUID;
  }
  
  /**
   * @since   1.2.0
   */
  public String getModifySource() {
    return this.modifySource;
  }

  /**
   * @since   1.2.0
   */
  public long getModifyTime() {
    return this.modifyTime;
  }

  /**
   * @since   1.2.0
   */
  public String getName() {
    return this.name;
  }

  /**
   * @since   1.2.0
   */
  public String getParentUuid() {
    return this.parentUUID;
  }

  /**
   * @since   1.2.0
   */
  public String getUuid() {
    return this.uuid;
  }

  /** 
   * @since   1.2.0
   */
  public void revokePriv(StemDTO _ns, MemberOf mof)
    throws  GrouperDAOException
  {
    try {
      Session     hs  = HibernateDAO.getSession();
      Transaction tx  = hs.beginTransaction();
      try {
        Iterator it = mof.internal_getDeletes().iterator();
        while (it.hasNext()) {
          hs.delete( Rosetta.getDAO( it.next() ) );
        }
        it = mof.internal_getSaves().iterator();
        while (it.hasNext()) {
          hs.saveOrUpdate( it.next() );
        }
        hs.update( _ns.getDAO() );
        tx.commit();
      }
      catch (HibernateException eH) {
        tx.rollback();
        throw eH;
      }
      finally {
        hs.close(); 
      }
    }
    catch (HibernateException eH) {
      throw new GrouperDAOException( eH.getMessage(), eH );
    }
  } 

  /**
   * @since   1.2.0
   */
  public void renameStemAndChildren(StemDTO _ns, Set children)
    throws  GrouperDAOException
  {
    try {
      Session     hs  = HibernateDAO.getSession();
      Transaction tx  = hs.beginTransaction();
      try {
        Iterator it = children.iterator();
        while (it.hasNext()) {
          hs.update( Rosetta.getDAO( it.next() ) );
        }
        hs.update( Rosetta.getDAO(_ns) );
        tx.commit();
      }
      catch (HibernateException eH) {
        tx.rollback();  
        throw eH;
      }
      finally {
        hs.close();
      }
    }
    catch (HibernateException eH) {
      throw new GrouperDAOException( eH.getMessage(), eH );
    }
  } 
  
  /**
   * @since   1.2.0
   */
  public void revokePriv(StemDTO _ns, Set toDelete)
    throws  GrouperDAOException
  {
    try {
      Session     hs  = HibernateDAO.getSession();
      Transaction tx  = hs.beginTransaction();
      try {
        Iterator it = toDelete.iterator();
        while (it.hasNext()) {
          hs.delete( Rosetta.getDAO( it.next() ) );
        }
        hs.update( Rosetta.getDAO(_ns) );
        tx.commit();
      }
      catch (HibernateException eH) {
        tx.rollback();
        throw eH;
      }
      finally {
        hs.close(); 
      }
    }
    catch (HibernateException eH) {
      throw new GrouperDAOException( eH.getMessage(), eH );
    }
  } 

  /**
   * @since   1.2.0
   */
  public StemDAO setCreateSource(String createSource) {
    this.createSource = createSource;
    return this;
  }

  /**
   * @since   1.2.0
   */
  public StemDAO setCreateTime(long createTime) {
    this.createTime = createTime;
    return this;
  }
  
  /**
   * @since   1.2.0
   */
  public StemDAO setCreatorUuid(String creatorUUID) {
    this.creatorUUID = creatorUUID;
    return this;
  }

  /**
   * @since   1.2.0
   */
  public StemDAO setDescription(String description) {
    this.description = description;
    return this;
  }

  /**
   * @since   1.2.0
   */
  public StemDAO setDisplayExtension(String displayExtension) {
    this.displayExtension = displayExtension;
    return this;
  }

  /**
   * @since   1.2.0
   */
  public StemDAO setDisplayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  /**
   * @since   1.2.0
   */
  public StemDAO setExtension(String extension) {
    this.extension = extension;
    return this;
  }

  /**
   * @since   1.2.0
   */
  public StemDAO setId(String id) {
    this.id = id;
    return this;
  }

  /**
   * @since   1.2.0
   */
  public StemDAO setModifierUuid(String modifierUUID) {
    this.modifierUUID = modifierUUID;
    return this;
  }

  /**
   * @since   1.2.0
   */
  public StemDAO setModifySource(String modifySource) {
    this.modifySource = modifySource;
    return this;
  }

  /**
   * @since   1.2.0
   */
  public StemDAO setModifyTime(long modifyTime) {
    this.modifyTime = modifyTime;
    return this;
  }

  /**
   * @since   1.2.0
   */
  public StemDAO setName(String name) {
    this.name = name;
    return this;
  }

  /**
   * @since   1.2.0
   */
  public StemDAO setParentUuid(String parentUUID) {
    this.parentUUID = parentUUID;
    return this;
  }

  /**
   * @since   1.2.0
   */
  public StemDAO setUuid(String uuid) {
    this.uuid = uuid;
    return this;
  }

  /**
   * @since   1.2.0
   */
  public void update(StemDTO _ns)
    throws  GrouperDAOException 
  {
    try {
      Session     hs  = HibernateDAO.getSession();
      Transaction tx  = hs.beginTransaction();
      try {
        hs.update( _ns.getDAO() );
        tx.commit();
      }
      catch (HibernateException eH) {
        tx.rollback();
        throw eH;
      }
      finally {
        hs.close();
      } 
    }
    catch (HibernateException eH) {
      throw new GrouperDAOException( eH.getMessage(), eH );
    }
  } 


  // PROTECTED CLASS METHODS //

  // @since   1.2.0
  protected static void reset(Session hs) 
    throws  HibernateException
  {
    // To appease Oracle the root stem is named ":" internally.
    hs.delete("from HibernateStemDAO as ns where ns.name not like '" + Stem.DELIM + "'");
  } 

} 

