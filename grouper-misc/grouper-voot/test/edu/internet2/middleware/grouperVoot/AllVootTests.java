/*******************************************************************************
 * Copyright 2012 Internet2
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package edu.internet2.middleware.grouperVoot;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import edu.internet2.middleware.grouper.helper.GrouperTest;

/**
 * Class to test all class in the VOOT connector for Grouper.
 * This class will test all the main functionalities implemented to support VOOT protocol.
 * 
 * @author Andrea Biancini <andrea.biancini@gmail.com>
 */
public class AllVootTests extends GrouperTest {
  
  /**
   * Main method to execute all tests.
   * @param args parameters passed to main (ignored).
   */
  public static void main(String[] args) {
    TestRunner.run(AllVootTests.suite());
  }
  
  /**
   * Test suite containing all tests for the VOOT connector for Grouper.
   * 
   * @return suite the test suite containing all tests
   */
  public static Test suite() {
    TestSuite suite = new TestSuite("Test for edu.internet2.middleware.grouperVoot");

    suite.addTestSuite(VootServiceLogicTest.class);
    suite.addTestSuite(VootParamsLogicTest.class);
    suite.addTestSuite(VootErrorsTest.class);
    
    return suite;
  }

}
