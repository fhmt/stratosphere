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

package eu.stratosphere.pact.contextcheck;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.stratosphere.pact.common.contract.Contract;
import eu.stratosphere.pact.common.contract.DualInputContract;
import eu.stratosphere.pact.common.contract.FileDataSink;
import eu.stratosphere.pact.common.contract.FileDataSource;
import eu.stratosphere.pact.common.contract.GenericDataSink;
import eu.stratosphere.pact.common.contract.SingleInputContract;
import eu.stratosphere.pact.common.plan.Plan;
import eu.stratosphere.pact.common.plan.Visitor;

/**
 * Traverses a plan and checks whether all Contracts are correctly connected to
 * their input contracts.
 * 
 * @author Max Heimel
 * @author Fabian Hueske
 */
public class ContextChecker implements Visitor<Contract> {

	/**
	 * A set of all already visited nodes during DAG traversal. Is used
	 * to avoid processing one node multiple times.
	 */
	public Set<Contract> visitedNodes = new HashSet<Contract>();
	
	/**
	 * Used for checking whether a contract does not have any children.
	 */
	//private Map<Contract, Set<Contract>> contractChildren =
	//		new HashMap<Contract, Set<Contract>>();

	/**
	 * Default constructor
	 */
	public ContextChecker() {
	}

	/**
	 * Checks whether the given plan is valid. In particular it is checked that
	 * all contracts have the correct number of inputs and all inputs are of the
	 * expected type. In case of an invalid plan an extended RuntimeException is
	 * thrown.
	 * 
	 * @param plan
	 *        The PACT plan to check.
	 */
	public void check(Plan plan) {
		this.visitedNodes.clear();
		plan.accept(this);
	}

	/**
	 * Checks whether the node is correctly connected to its input.
	 */
	@Override
	public boolean preVisit(Contract node) {

		// check if node was already visited
		if (this.visitedNodes.contains(node)) {
			return false;
		}
		
		//contractChildren.put(node, new HashSet<Contract>());

		// apply the appropriate check method
		if (node instanceof FileDataSink) {
			checkFileDataSink((FileDataSink) node);
		} else if (node instanceof FileDataSource) {
			checkFileDataSource((FileDataSource) node);
		} else if (node instanceof GenericDataSink) {
			checkDataSink((GenericDataSink) node);
		} else if (node instanceof SingleInputContract<?>) {
			checkSingleInputContract((SingleInputContract<?>) node);
		} else if (node instanceof DualInputContract<?>) {
			checkDualInputContract((DualInputContract<?>) node);
		}
		// Data sources must not be checked, since correctness of input type is
		// checked.

		// mark node as visited
		this.visitedNodes.add(node);

		return true;
	}

	@Override
	public void postVisit(Contract node) {
/*		if (contractChildren.get(node).size() == 0) {
			// we did not visit any node that had this node
			// as input
			throw new MissingChildException("Node " +
					node.getName() + " does not have any childern.");
		}*/
	}

	/**
	 * Checks if DataSinkContract is correctly connected. In case that the
	 * contract is incorrectly connected a RuntimeException is thrown.
	 * 
	 * @param dataSinkContract
	 *        DataSinkContract that is checked.
	 */
	private void checkDataSink(GenericDataSink dataSinkContract) {
		Contract input = dataSinkContract.getInputs().get(0);

		// check if input exists
		if (input == null) {
			throw new MissingChildException();
		}
		
		//contractChildren.get(input).add(dataSinkContract);
	}
	
	/**
	 * Checks if FileDataSink is correctly connected. In case that the
	 * contract is incorrectly connected a RuntimeException is thrown.
	 * 
	 * @param fileSink
	 *        FileDataSink that is checked.
	 */
	private void checkFileDataSink(FileDataSink fileSink) {
		String path = fileSink.getFilePath();
		if (path == null) {
			throw new PlanException("File path of FileDataSink is null.");
		}
		if (path.equals("")) {
			throw new PlanException("File path of FileDataSink is empty string.");
		}
		if (!(path.startsWith("file://") || path.startsWith("hdfs://"))) {
			throw new PlanException("File path \"" + path +
					"\" of FileDataSink is not a valid file URL.");
		}
		checkDataSink(fileSink);
	}
	
	/**
	 * Checks if FileDataSource is correctly connected. In case that the
	 * contract is incorrectly connected a RuntimeException is thrown.
	 * 
	 * @param fileSource
	 *        FileDataSource that is checked.
	 */
	private void checkFileDataSource(FileDataSource fileSource) {
		String path = fileSource.getFilePath();
		if (path == null) {
			throw new PlanException("File path of FileDataSource is null.");
		}
		if (path.equals("")) {
			throw new PlanException("File path of FileDataSource is empty string.");
		}
		if (!(path.startsWith("file://") || path.startsWith("hdfs://"))) {
			throw new PlanException("File path \"" + path +
					"\" of FileDataSource is not a valid file uri.");
		}
	}

	/**
	 * Checks whether a SingleInputContract is correctly connected. In case that
	 * the contract is incorrectly connected a RuntimeException is thrown.
	 * 
	 * @param singleInputContract
	 *        SingleInputContract that is checked.
	 */
	private void checkSingleInputContract(SingleInputContract<?> singleInputContract) {

		List<Contract> input = singleInputContract.getInputs();

		// check if input exists
		if (input.size() == 0) {
			throw new MissingChildException();
		}
/*		for (Contract in : input) {
			contractChildren.get(in).add(singleInputContract);
		}*/
	}

	/**
	 * Checks whether a DualInputContract is correctly connected. In case that
	 * the contract is incorrectly connected a RuntimeException is thrown.
	 * 
	 * @param dualInputContract
	 *        DualInputContract that is checked.
	 */
	private void checkDualInputContract(DualInputContract<?> dualInputContract) {
		List<Contract> input1 = dualInputContract.getFirstInputs();
		List<Contract> input2 = dualInputContract.getSecondInputs();

		// check if input exists
		if (input1.size() == 0 || input2.size() == 0) {
			throw new MissingChildException();
		}
/*		for (Contract in : input1) {
			contractChildren.get(in).add(dualInputContract);
		}
		for (Contract in : input2) {
			contractChildren.get(in).add(dualInputContract);
		}*/
	}

}
