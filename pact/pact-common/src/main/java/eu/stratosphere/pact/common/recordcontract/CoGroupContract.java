/***********************************************************************************************************************
 *
 * Copyright (C) 2010 by the Stratosphere project (http://stratosphere.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 **********************************************************************************************************************/

package eu.stratosphere.pact.common.recordcontract;

import eu.stratosphere.pact.common.recordstubs.CoGroupStub;
import eu.stratosphere.pact.common.type.Key;
import eu.stratosphere.pact.common.util.ReflectionUtil;


/**
 * CrossContract represents a Match InputContract of the PACT Programming Model.
 * InputContracts are second-order functions. They have one or multiple input sets of records and a first-order
 * user function (stub implementation).
 * <p> 
 * CoGroup works on two inputs and calls the first-order user function of a {@link CoGroupStub} 
 * with the groups of records sharing the same key (one group per input) independently.
 * 
 * @see CoGroupStub
 */
public class CoGroupContract extends DualInputContract<CoGroupStub<?>>
{	
	private static String DEFAULT_NAME = "<Unnamed Matcher>";		// the default name for contracts
	
	private Class<? extends Key> keyClass;							// the class of the key
	
	private final int firstKeyFieldNumber;							// the key position in the first input's records
	
	private final int secondKeyFieldNumber;							// the key position in the second input's records
	
	// --------------------------------------------------------------------------------------------

	/**
	 * Creates a CoGroupContract with the provided {@link CoGroupStub} implementation
	 * and a default name.
	 * 
	 * @param c The {@link CoGroupStub} implementation for this CoGroup InputContract.
	 * @param firstKeyColumn The position of the key in the first input's records.
	 * @param secondKeyColumn The position of the key in the second input's records.
	 */
	public CoGroupContract(Class<? extends CoGroupStub<?>> c, int firstKeyColumn, int secondKeyColumn) {
		this(c, firstKeyColumn, secondKeyColumn, DEFAULT_NAME);
	}
	
	/**
	 * Creates a CoGroupContract with the provided {@link CoGroupStub} implementation 
	 * and the given name. 
	 * 
	 * @param c The {@link CoGroupStub} implementation for this CoGroup InputContract.
	 * @param firstKeyColumn The position of the key in the first input's records.
	 * @param secondKeyColumn The position of the key in the second input's records.
	 * @param name The name of PACT.
	 */
	public CoGroupContract(Class<? extends CoGroupStub<?>> c, int firstKeyColumn, int secondKeyColumn, String name) {
		super(c, name);
		this.firstKeyFieldNumber = firstKeyColumn;
		this.secondKeyFieldNumber = secondKeyColumn;
		this.keyClass = ReflectionUtil.getTemplateType(c, CoGroupStub.class, 0);
	}

	/**
	 * Creates a CoGroupContract with the provided {@link CoGroupStub} implementation the default name.
	 * It uses the given contract as its input.
	 * 
	 * @param c The {@link CoGroupStub} implementation for this CoGroup InputContract.
	 * @param firstKeyColumn The position of the key in the first input's records.
	 * @param secondKeyColumn The position of the key in the second input's records.
	 * @param input1 The contract to use as the first input.
	 * @param input2 The contract to use as the second input.
	 */
	public CoGroupContract(Class<? extends CoGroupStub<?>> c, int firstKeyColumn, int secondKeyColumn,
															Contract input1, Contract input2)
	{
		this(c, firstKeyColumn, secondKeyColumn, input1, input2, DEFAULT_NAME);
	}
	
	/**
	 * Creates a CoGroupContract with the provided {@link CoGroupStub} implementation and the given name.
	 * It uses the given contract as its input.
	 * 
	 * @param c The {@link CoGroupStub} implementation for this CoGroup InputContract.
	 * @param firstKeyColumn The position of the key in the first input's records.
	 * @param secondKeyColumn The position of the key in the second input's records.
	 * @param input1 The contract to use as the first input.
	 * @param input2 The contract to use as the second input.
	 * @param name The name of PACT.
	 */
	public CoGroupContract(Class<? extends CoGroupStub<?>> c, int firstKeyColumn, int secondKeyColumn,
													Contract input1, Contract input2, String name)
	{
		this(c, firstKeyColumn, secondKeyColumn, name);
		setFirstInput(input1);
		setSecondInput(input2);
	}
	
	/**	/**
	 * Gets the column number of the key in the first input's records.
	 *  
	 * @return The column number of the key field in the first input.
	 */
	public int getFirstKeyColumnNumber()
	{
		return this.firstKeyFieldNumber;
	}
	
	/**
	 * Gets the column number of the key in the second input's records.
	 *  
	 * @return The column number of the key field in the second input.
	 */
	public int getSecondKeyColumnNumber()
	{
		return this.secondKeyFieldNumber;
	}
	
	/**
	 * Gets the type of the key field on which this reduce contract groups.
	 * 
	 * @return The type of the key field.
	 */
	public Class<? extends Key> getKeyClass()
	{
		return this.keyClass;
	}
	
	/**
	 * Sets the type of the key field on which this reduce contract groups.
	 * 
	 * @param clazz The type of the key field.
	 */
	public void setKeyClass(Class<? extends Key> clazz)
	{
		this.keyClass = clazz;
	}
}