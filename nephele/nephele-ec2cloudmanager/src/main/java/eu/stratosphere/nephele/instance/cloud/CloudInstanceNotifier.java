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

package eu.stratosphere.nephele.instance.cloud;

import eu.stratosphere.nephele.instance.AbstractInstance;
import eu.stratosphere.nephele.instance.InstanceListener;
import eu.stratosphere.nephele.jobgraph.JobID;

/**
 * This class is an auxiliary class to send the notification
 * about the availability of an {@link AbstractInstance} to the given {@link InstanceListener} object. The notification
 * must be sent from
 * a separate thread, otherwise the atomic operation of requesting an instance
 * for a vertex and switching to the state ASSINING could not be guaranteed.
 * This class is thread-safe.
 * 
 * @author warneke
 */
public class CloudInstanceNotifier extends Thread {

	/**
	 * The {@link InstanceListener} object to send the notification to.
	 */
	private final InstanceListener instanceListener;

	private final CloudInstance instance;

	private final JobID id;

	/**
	 * Constructs a new instance notifier object.
	 * 
	 * @param instanceListener
	 *        the listener to send the notification to
	 * @param allocatedSlice
	 *        the slice with has been allocated for the job
	 */
	public CloudInstanceNotifier(InstanceListener instanceListener, JobID id, CloudInstance instance) {
		this.instanceListener = instanceListener;
		this.instance = instance;
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		this.instanceListener.resourceAllocated(id, instance.asAllocatedResource());
	}
}
