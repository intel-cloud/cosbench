/** 
 
Copyright 2013 Intel Corporation, All Rights Reserved.

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

package com.intel.cosbench.driver.operator;

import static com.intel.cosbench.driver.operator.Deleter.doDelete;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.intel.cosbench.api.storage.StorageException;
import com.intel.cosbench.api.storage.StorageInterruptedException;
import com.intel.cosbench.bench.*;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.driver.agent.AgentException;
import com.intel.cosbench.driver.util.ObjectScanner;
import com.intel.cosbench.service.AbortedException;

/**
 * This class encapsulates operations to delete objects, essentially, it maps to
 * primitive DELETE operation.
 * 
 * @author ywang19, qzheng7
 * 
 */
class Cleaner extends AbstractOperator {

    public static final String OP_TYPE = "cleanup";

    private boolean deleteContainer;
    private ObjectScanner objScanner = new ObjectScanner();

    public Cleaner() {
        /* empty */
    }

    @Override
    protected void init(String id, int ratio, String division, Config config) {
        super.init(id, ratio, division, config);
        objScanner.init(division, config);
        deleteContainer = config.getBoolean("deleteContainer", true);
    }

    @Override
    public String getOpType() {
        return OP_TYPE;
    }

    @Override
    public String getSampleType() {
        return Deleter.OP_TYPE;
    }

    @Override
    protected void operate(int idx, int all, Session session) {
        String[] path = null;
        String opType = getOpType();
        String lastContainer = null;

        while ((path = objScanner.nextObjPath(path, idx, all)) != null) {
            if (deleteContainer && !StringUtils.equals(lastContainer, path[0])) {
                if (lastContainer != null)
                    doDispose(lastContainer, config, session);
                lastContainer = path[0];
            }
            if (path[1] == null)
                continue;
            Sample sample = doDelete(path[0], path[1], config, session, this);
            sample.setOpType(opType);
            session.getListener().onSampleCreated(sample);
        }

        if (deleteContainer && lastContainer != null)
            doDispose(lastContainer, config, session);

        Date now = new Date();
		Result result = new Result(now, getId(), getOpType(), getSampleType(),
				getName(), true);
        session.getListener().onOperationCompleted(result);
    }

    public  void doDispose(String conName, Config config, Session session) {
        if (Thread.interrupted())
            throw new AbortedException();
        
        boolean isEmpty = true;
        boolean tryAgain = false;
        do {
        	if(isEmpty) {
        		 try {
        	            session.getApi().deleteContainer(conName, config);
        	            isEmpty = true;
        	            tryAgain = false;
        	        } catch (StorageInterruptedException sie) {
        	            throw new AbortedException();
        	        } catch (StorageException se) {
        	            String msg = "Error deleting container " +  conName; 
        	            doLogErr(session.getLogger(), msg,se);
        	            if(isConflictException(session,se)) {
        	            	isEmpty = false;
        	            	tryAgain = true;
        	            }else{
        	            	tryAgain = false;
        	            }
        	        } catch (Exception e) {
        	            doLogErr(session.getLogger(), "fail to perform clean operation " + conName, e);
        	            throw new AgentException(); // mark error
        	        }
        	}else {
        		try{
        			for(String objName: getObjectsList(conName, config, session)) {
        				doDelete(conName, objName, config, session, this);
        			}
        		  }catch(StorageException se) {
        			  doLogErr(session.getLogger(), "fail to get : "+conName+" objects list ",se);
        			  tryAgain = false;
        		  }catch (IOException ioe) {
        			  doLogErr(session.getLogger(), "fail to convert objects stream to string",ioe);
        			  tryAgain = false;
        		  }catch (Exception e) {
        			  doLogErr(session.getLogger(), "unexpected error",e);
        			  tryAgain = false;
        		  }
        		isEmpty = true;
        		tryAgain = true;
        	}
        }while(tryAgain == true);
      
    }
    
    private static String[] getObjectsList(String conName, Config config, Session session) throws IOException {
    	String[] objects = {};
    	StringWriter stringWriter = new StringWriter();
    	try {
			IOUtils.copy(session.getApi().getList(conName, "", config), stringWriter);
		}catch(StorageException se) {
			throw se;
		}catch (IOException e) {
			throw e;
		}
    	String objectString = stringWriter.toString();
    	objects = objectString.split("\n");
    	return objects;
    }
    
    private static boolean isConflictException(Session session, Exception e) {
    	if(e != null && e.getMessage() != null)
    		try{
    			if(409 == Integer.valueOf(e.getMessage().substring(9, 12))){
    				doLogDebug(session.getLogger(),"catch 409 error, will clean up the unempty container and try again");
    				return true;
    			}
    		}catch(NumberFormatException ne) {
    			ne.printStackTrace(); // mask ignore
    			return false;
    		}
    	return false;
    }

}
