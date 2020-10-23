/**

Copyright 2013 Intel Corporation, All Rights Reserved.
Copyright 2019 OpenIO Corporation, All Rights Reserved.

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
package com.intel.cosbench.client.swauth.utils;

import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;

import com.intel.cosbench.api.auth.AuthException;
import com.intel.cosbench.api.auth.AuthInterruptedException;
import com.intel.cosbench.api.auth.AuthTimeoutException;
import com.intel.cosbench.client.swauth.SwiftAuthClient;
import com.intel.cosbench.client.swauth.SwiftAuthClientException;

public class SwiftTokenCacheImpl {

    private static SwiftTokenCache latestTokenCache = new SwiftTokenCache();


    public static synchronized void loadSwiftTokenCache(SwiftAuthClient client, SwiftTokenCache currentTokenCache) {
        // Actual operation to load token cache from server
    if(currentTokenCache.getVersion() == latestTokenCache.getVersion())
    {
        try {
                client.login();
                latestTokenCache.setToken(client.getAuthToken());
                latestTokenCache.setStorageURL(client.getStorageURL());
                latestTokenCache.incrementVersion();
        } catch (SocketTimeoutException ste) {
            throw new AuthTimeoutException(ste);
        } catch (ConnectTimeoutException cte) {
            throw new AuthTimeoutException(cte);
        } catch (InterruptedIOException ie) {
            throw new AuthInterruptedException(ie);
        } catch (SwiftAuthClientException se) {
            throw new AuthException(se.getMessage(), se);
        } catch (Exception e) {
            throw new AuthException(e);
        }
        }
    }

    public static SwiftTokenCache getSwiftTokenCache(SwiftAuthClient client) {
        if(latestTokenCache.getVersion()==0 || ! client.check())
            loadSwiftTokenCache(client,latestTokenCache);
                return latestTokenCache;
    }
}
