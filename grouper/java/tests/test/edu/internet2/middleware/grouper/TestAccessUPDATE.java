/*
 * Copyright (C) 2004-2005 University Corporation for Advanced Internet Development, Inc.
 * Copyright (C) 2004-2005 The University Of Chicago
 * All Rights Reserved. 
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *  * Neither the name of the University of Chicago nor the names
 *    of its contributors nor the University Corporation for Advanced
 *   Internet Development, Inc. may be used to endorse or promote
 *   products derived from this software without explicit prior
 *   written permission.
 *
 * You are under no obligation whatsoever to provide any enhancements
 * to the University of Chicago, its contributors, or the University
 * Corporation for Advanced Internet Development, Inc.  If you choose
 * to provide your enhancements, or if you choose to otherwise publish
 * or distribute your enhancements, in source code form without
 * contemporaneously requiring end users to enter into a separate
 * written license agreement for such enhancements, then you thereby
 * grant the University of Chicago, its contributors, and the University
 * Corporation for Advanced Internet Development, Inc. a non-exclusive,
 * royalty-free, perpetual license to install, use, modify, prepare
 * derivative works, incorporate into the software or other computer
 * software, distribute, and sublicense your enhancements or derivative
 * works thereof, in binary and source code form.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND WITH ALL FAULTS.  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT ARE DISCLAIMED AND the
 * entire risk of satisfactory quality, performance, accuracy, and effort
 * is with LICENSEE. IN NO EVENT SHALL THE COPYRIGHT OWNER, CONTRIBUTORS,
 * OR THE UNIVERSITY CORPORATION FOR ADVANCED INTERNET DEVELOPMENT, INC.
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OR DISTRIBUTION OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package test.edu.internet2.middleware.grouper;

import  edu.internet2.middleware.grouper.*;
import  edu.internet2.middleware.subject.*;

import  java.util.*;
import  junit.framework.*;


public class TestAccessUPDATE extends TestCase {

  private GrouperSession  s, nrs0, nrs1;
  private GrouperQuery    q;

  public TestAccessUPDATE(String name) {
    super(name);
  }

  protected void setUp () {
    DB db = new DB();
    db.emptyTables();
    db.stop();
    s = Constants.createSession();
    Assert.assertNotNull("s", s);
    nrs0 = Constants.createSession(Constants.mem0I, Constants.mem0T);
    Assert.assertNotNull("nrs0", nrs0);
    nrs1 = Constants.createSession(Constants.mem1I, Constants.mem1T);
    Assert.assertNotNull("nrs1", nrs1);
    Constants.createGroups(s);
    Constants.createMembers(s);
    q = new GrouperQuery(s);
    Assert.assertNotNull("q", q);
  }

  protected void tearDown () {
    s.stop();
    nrs0.stop();
    nrs1.stop();
  }


  /*
   * TESTS
   */

  public void testAddAndDelMembersAsRoot() {
    try {
      Constants.g0.listAddVal(Constants.m0);
      Assert.assertTrue("g0 add", true);
      Assert.assertTrue(
        "g0 size after add", Constants.g0.listVals().size() == 1
      );
      try {
        Constants.g0.listDelVal(Constants.m0);
        Assert.assertTrue("g0 del", true);
        Assert.assertTrue(
          "g0 size after del", Constants.g0.listVals().size() == 0
        );
      } catch (RuntimeException e) {
        Assert.fail("g0 del " + e.getMessage());
      }
    } catch (RuntimeException e) {
      Assert.fail("g0 add " + e.getMessage());
    }
  } 

  public void testAddAndDelMembersAsNonRootAndNoUPDATE() {
    GrouperGroup g = Constants.loadGroup(
      nrs0, Constants.g0s, Constants.g0e
    );
    try {
      g.listAddVal(Constants.m0);
      Assert.fail("g0 add");
    } catch (RuntimeException e) {
      Assert.assertTrue("g0 add", true);
      Assert.assertTrue(
        "g0 size after add", g.listVals().size() == 0
      );
    }
    try {
      g.listDelVal(Constants.m0);
      Assert.fail("g0 del");
    } catch (RuntimeException e) {
      Assert.assertTrue("g0 del", true);
      Assert.assertTrue(
        "g0 size after del", g.listVals().size() == 0
      );
    }
  } 

  public void testAddAndDelMembersAsNonRootAndUPDATE() {
    Constants.grantAccessPriv(
      s, Constants.g0, Constants.m0, Grouper.PRIV_UPDATE
    );
    GrouperGroup g = Constants.loadGroup(
      nrs0, Constants.g0s, Constants.g0e
    );
    try {
      g.listAddVal(Constants.m0);
      Assert.assertTrue("g0 add", true);
      Assert.assertTrue(
        "g0 size after add", g.listVals().size() == 1
      );
      try {
        g.listDelVal(Constants.m0);
        Assert.assertTrue("g0 del", true);
        Assert.assertTrue(
          "g0 size after del", g.listVals().size() == 0
        );
      } catch (RuntimeException e) {
        Assert.fail("g0 del " + e.getMessage());
      }
    } catch (RuntimeException e) {
      Assert.fail("g0 add " + e.getMessage());
    }
  } 

  // --- //

  public void testGrantAndRevokeOptinAsRoot() {
    Assert.assertTrue(
      "g0 grant optin",
      s.access().grant(s, Constants.g0, Constants.m0, Grouper.PRIV_OPTIN)
    );
    Assert.assertTrue(
      "g0 revoke optin",
      s.access().revoke(s, Constants.g0, Constants.m0, Grouper.PRIV_OPTIN)
    );
  }

  public void testGrantAndRevokeOptinAsNonRootAndNoUPDATE() {
    GrouperGroup g = Constants.loadGroup(
      nrs0, Constants.g0s, Constants.g0e
    );
    Assert.assertFalse(
      "g0 grant optin",
      nrs0.access().grant(nrs0, g, Constants.m0, Grouper.PRIV_OPTIN)
    );
    Assert.assertFalse(
      "g0 revoke optin",
      nrs0.access().revoke(nrs0, g, Constants.m0, Grouper.PRIV_OPTIN)
    );
  }

  public void testGrantAndRevokeOptinAsNonRootAndUPDATE() {
    Constants.grantAccessPriv(
      s, Constants.g0, Constants.m0, Grouper.PRIV_UPDATE
    );
    GrouperGroup g = Constants.loadGroup(
      nrs0, Constants.g0s, Constants.g0e
    );
    Assert.assertTrue(
      "g0 grant optin",
      nrs0.access().grant(nrs0, g, Constants.m0, Grouper.PRIV_OPTIN)
    );
    Assert.assertTrue(
      "g0 revoke optin",
      nrs0.access().revoke(nrs0, g, Constants.m0, Grouper.PRIV_OPTIN)
    );
  }

  // --- //

  public void testGrantAndRevokeOptoutAsRoot() {
    Assert.assertTrue(
      "g0 grant optout",
      s.access().grant(s, Constants.g0, Constants.m0, Grouper.PRIV_OPTOUT)
    );
    Assert.assertTrue(
      "g0 revoke optout",
      s.access().revoke(s, Constants.g0, Constants.m0, Grouper.PRIV_OPTOUT)
    );
  }

  public void testGrantAndRevokeOptoutAsNonRootAndNoUPDATE() {
    GrouperGroup g = Constants.loadGroup(
      nrs0, Constants.g0s, Constants.g0e
    );
    Assert.assertFalse(
      "g0 grant optout",
      nrs0.access().grant(nrs0, g, Constants.m0, Grouper.PRIV_OPTOUT)
    );
    Assert.assertFalse(
      "g0 revoke optout",
      nrs0.access().revoke(nrs0, g, Constants.m0, Grouper.PRIV_OPTOUT)
    );
  }

  public void testGrantAndRevokeOptoutAsNonRootAndUPDATE() {
    Constants.grantAccessPriv(
      s, Constants.g0, Constants.m0, Grouper.PRIV_UPDATE
    );
    GrouperGroup g = Constants.loadGroup(
      nrs0, Constants.g0s, Constants.g0e
    );
    Assert.assertTrue(
      "g0 grant optout",
      nrs0.access().grant(nrs0, g, Constants.m0, Grouper.PRIV_OPTOUT)
    );
    Assert.assertTrue(
      "g0 revoke optout",
      nrs0.access().revoke(nrs0, g, Constants.m0, Grouper.PRIV_OPTOUT)
    );
  }

}

