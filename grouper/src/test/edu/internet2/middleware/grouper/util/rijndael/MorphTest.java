/*
 * @author mchyzer
 * $Id: MorphTest.java,v 1.3 2008-09-14 04:54:00 mchyzer Exp $
 */
package edu.internet2.middleware.grouper.util.rijndael;

import junit.textui.TestRunner;

import org.apache.commons.lang.StringUtils;

import edu.internet2.middleware.grouper.GrouperTest;
import edu.internet2.middleware.grouper.cfg.ApiConfig;
import edu.internet2.middleware.morphString.Morph;



/**
 * test morphing
 */
public class MorphTest extends GrouperTest {

  /**
   * @param name
   */
  public MorphTest(String name) {
    super(name);
  }

  /**
   * 
   */
  public MorphTest() {
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    TestRunner.run(MorphTest.class);
  }

  /**
   * 
   */
  public void testMorph() {
    ApiConfig.testConfig.put("grouper.encrypt.key", "ert234mN54");
    String morphed = Morph.encrypt("whatever");
    assertFalse(StringUtils.equals(morphed, "whatever"));
    String unmorphed = Morph.decrypt(morphed);
    assertEquals(unmorphed, "whatever");
  }
  
}
