/**

Copyright 2021-2022 eHualu Corporation, All Rights Reserved.

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


/**  
* @Title: Header.java
* @Package com.intel.cosbench.driver.operator  
* @Description: Head object class
* @author sine  
* @date 2021-10-30
* @version V1.0  
*/

package com.intel.cosbench.driver.operator;

import java.util.Date;

import com.intel.cosbench.bench.*;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.driver.util.ObjectPicker;
import com.intel.cosbench.service.AbortedException;
import com.intel.cosbench.api.storage.StorageException;
import com.intel.cosbench.api.storage.StorageInterruptedException;


/**
 * This class represents primitive HEAD operation.
 *
 * @author sine
 *
 */
class Header extends AbstractOperator {

	public static final String OP_TYPE = "head";

	private ObjectPicker objPicker = new ObjectPicker();

	public Header() {
		/* empty */
	}

	@Override
	protected void init(String id, int ratio, String division, Config config) {
		super.init(id, ratio, division, config);
		objPicker.init(division, config);
	}

	@Override
	public String getOpType() {
		return OP_TYPE;
	}

	@Override
	protected void operate(int idx, int all, Session session) {
		String[] path = objPicker.pickObjPath(session.getRandom(), idx, all);
		Sample sample = doHead(path[0], path[1], config, session, this);
		session.getListener().onSampleCreated(sample);
		Date now = sample.getTimestamp();
		Result result = new Result(now, getId(), getOpType(), getSampleType(), getName(), sample.isSucc());
		session.getListener().onOperationCompleted(result);
	}

	public static Sample doHead(String conName, String objName, Config config, Session session, Operator op) {

		if (Thread.interrupted())
			throw new AbortedException();

		long start = System.nanoTime();

		try {
			session.getApi().headObject(conName, objName, config);
		} catch (StorageInterruptedException sie) {
			doLogErr(session.getLogger(), sie.getMessage(), sie);
			throw new AbortedException();
		} catch (StorageException se) {
			String msg = "Error head-object " + conName + "/" + objName + " " + se.getMessage();
			doLogWarn(session.getLogger(), msg);
			
			return new Sample(new Date(), op.getId(), op.getOpType(), op.getSampleType(), op.getName(), false);
		} catch (Exception e) {
			isUnauthorizedException(e, session);
			errorStatisticsHandle(e, session, conName + "/" + objName);

			return new Sample(new Date(), op.getId(), op.getOpType(), op.getSampleType(), op.getName(), false);
		}

		long end = System.nanoTime();

		return new Sample(new Date(), op.getId(), op.getOpType(), op.getSampleType(), op.getName(), true,
				(end - start) / 1000000, 0L, 0L);
	}
}
