/*--
$Id: DataType.java,v 1.6 2006-05-09 01:33:33 ddonn Exp $
$Date: 2006-05-09 01:33:33 $

Copyright 2006 Internet2, Stanford University

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
package edu.internet2.middleware.signet;

import java.util.Hashtable;
import java.util.NoSuchElementException;
import edu.internet2.middleware.signet.resource.ResLoaderUI;

/**
 * This is a typesafe enumeration that identifies the various types of 
 * Signet {@link Limit} data.
 */
public class DataType implements ITypeSafeEnum
{
	/////////////////////////////////
	// static
	/////////////////////////////////

	/** implements Serializable */
	private static final long serialVersionUID = 1L;

	/** Keeps track of all instances by name, for efficient lookup. Use Hashtable
	 * to disallow null values and also support synchronized access.
	*/
	private static final Hashtable instancesByName;

	/**The instance that represents a "string" limit-type. */
	public static final DataType TEXT;// = new DataType("text", "Any alphanumeric value");

	/** The instance that represents an inactive entity. */
	public static final DataType NUMERIC;// = new DataType("numeric", "Any integer or decimal value");

	/** The instance that represents a pending entity. */
	public static final DataType DATE;// = new DataType("date", "Any calendar date");

	static
	{
		instancesByName = new Hashtable(3, 1.0f);

		TEXT = new DataType("text", ResLoaderUI.getString("DataType.text.txt"));
		instancesByName.put(TEXT.name, TEXT);

		NUMERIC = new DataType("numeric", ResLoaderUI.getString("DataType.numeric.txt"));
		instancesByName.put(NUMERIC.name, NUMERIC);

		DATE = new DataType("date", ResLoaderUI.getString("DataType.date.txt"));
		instancesByName.put(DATE.name, DATE);
	}

	/* (non-Javadoc)
	 * @see edu.internet2.middleware.signet.ITypeSafeEnum#getInstanceByName(java.lang.String)
	 */
	public static Object getInstanceByName(String name) throws NoSuchElementException
	{
		DataType result = (DataType)instancesByName.get(name);
		if (null == result)
			throw new NoSuchElementException(name);
		return (result);
	}


	////////////////////////////////
	// instance
	////////////////////////////////

	/**
	 * Stores the external name of this instance, by which it can be retrieved.
	 */
	private final String	name;

	/**
	 * Stores the human-readable description of this instance, by which
	 * it is identified in the user interface.
	 */
	private final transient String	description;

	/**
	 * Constructor is private to prevent instantiation except during class loading.
	 * @param name The external name of the DataType value.
	 * @param description The human-readable description of the DataType value;
	 * 	presented in the user interface.
	 */
	private DataType(String name, String description)
	{
		this.name = name;
		this.description = description;
	}

	////////////////////////////
	// implements ITypeSafeEnum
	////////////////////////////

	/**
	 * Return the external name associated with this instance.
	 * @return the name by which this instance is identified in code.
	 */
	public String getName() { return name; }

	/**
	 * Return the description associated with this instance.
	 * @return the human-readable description by which this instance is identified in the user interface.
	 */
	public String getHelpText() { return description; }


	//////////////////////////
	// Serializable support
	//////////////////////////

	/** Insure that deserialization preserves the signleton property. */
	private Object readResolve() { return (getInstanceByName(name)); }


	////////////////////////////
	// overrides Object
	////////////////////////////

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	protected Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException
			("Instances of type-safe enumerations are singletons and cannot be cloned.");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() { return (name); }

}
