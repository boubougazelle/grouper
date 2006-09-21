/*
  Copyright 2004-2006 University Corporation for Advanced Internet Development, Inc.
  Copyright 2004-2006 The University Of Chicago

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

package edu.internet2.middleware.grouper;
import  java.util.regex.*;

/** 
 * @author  blair christensen.
 * @version $Id: AttributeValidator.java,v 1.7 2006-09-21 16:10:23 blair Exp $
 */
class AttributeValidator {

  // PRIVATE CLASS CONSTANTS //
  private static final Pattern  RE_COLON  = Pattern.compile(":");
  private static final Pattern  RE_WS     = Pattern.compile("^\\s*$");


  // PROTECTED CLASS METHODS //

  // @since   1.1.0
  protected static boolean isPermittedName(String name) {
    return Validator.isNotNullOrBlank(name);
  } // protected static boolean isPermittedName(name)
  
  // @since   1.1.0
  protected static boolean isPermittedValue(String value) {
    return Validator.isNotNullOrBlank(value);
  } // protected static boolean isPermittedValue(value)
  
  protected static void namingValue(String value)
    throws  ModelException
  {
    Validator.notNullPerModel(value, E.ATTR_NULL);
    _noColon(value);
    _notJustWhiteSpace(value);
  } // protected static void namingValue(value)
   

  // PRIVATE CLASS METHODS // 

  // @since 1.0
  private static void _noColon(String value) 
    throws  ModelException
  {
    Matcher m = RE_COLON.matcher(value);
    if (m.find()) {
      throw new ModelException(E.ATTR_COLON);
    }
  } // private static void _noColon(value)
 
  // @since 1.0
  private static void _notJustWhiteSpace(String value) 
    throws  ModelException
  {
    Matcher m = RE_WS.matcher(value);
    if (m.find()) {
      throw new ModelException(E.ATTR_NULL);
    }
  } // private static void _notJustWhiteSpace(value)

} // class AttributeValidator

