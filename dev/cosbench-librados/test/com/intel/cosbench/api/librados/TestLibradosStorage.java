/** 
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
package com.intel.cosbench.api.librados;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.ceph.rados.exceptions.RadosException;
import com.intel.cosbench.api.storage.StorageException;

/**
 * 
 * @author Niklas Goerke niklas974@github
 * 
 */

public class TestLibradosStorage {

    private static DummyConfig config;
    private DummyLogger logger;
    private static LibradosStorage storage;

    @Before
    public void setUp() throws Exception {
        config = new DummyConfig();
        logger = new DummyLogger();
        storage = new LibradosStorage();
        storage.init(config, logger);
    }

    @AfterClass
    public static void tearDown() {
        try {
            storage.deleteContainer("testcontainer", config);
        } catch (Exception e) {

        }
    }

    @Test
    public final void testInit() {
        // test init via setUp()
    }

    @Test(expected = StorageException.class)
    public final void testDispose() {
        storage.dispose();
        storage.createContainer(null, null);
    }

    @Test
    public final void testGetObject() throws IOException {
        storage.createContainer("testcontainer", config);
        String str = "asdfjklö";
        InputStream is = new ByteArrayInputStream(str.getBytes("UTF-8"));
        storage.createObject("testcontainer", "testobject", is, 12, config);
        InputStream os = storage.getObject("testcontainer", "testobject", config);
        byte[] ib = new byte[20];
        ib = str.getBytes();
        byte[] ob = new byte[9];
        os.read(ob, 0, 9);
        assertTrue(Arrays.equals(ib, ob));
        storage.deleteContainer("testcontainer", config);
    }

    @Test
    public final void testCreateDeleteContainer() {
        storage.createContainer("testcontainer", config);
        storage.deleteContainer("testcontainer", config);
    }

    @Test
    public final void testCreateObjectStringStringInputStreamLongConfig() throws UnsupportedEncodingException {
        storage.createContainer("testcontainer", config);
        String str = "asdfjklö";
        InputStream is = new ByteArrayInputStream(str.getBytes("UTF-8"));
        storage.createObject("testcontainer", "testobject", is, 8, config);
        storage.deleteContainer("testcontainer", config);
    }

    @Test
    public final void testMassCreateGetObjects() throws IOException {
        storage.createContainer("testcontainer", config);
        String str = "asdfjklö";
        for (int i = 0; i < 1000; i++) {
            InputStream is = new ByteArrayInputStream(str.getBytes("UTF-8"));
            storage.createObject("testcontainer", "testobject" + i, is, 9, config);
        }
        for (int i = 0; i < 1000; i++) {
            InputStream os = storage.getObject("testcontainer", "testobject" + i, config);
            byte[] ob = new byte[9];
            os.read(ob, 0, 9);
            byte[] ib = str.getBytes();
            assertTrue(Arrays.equals(ib, ob));
        }
        storage.deleteContainer("testcontainer", config);
    }

    @Test(expected = StorageException.class)
    public final void testDeleteObject() throws IOException, RadosException {
        try {
            storage.createContainer("testcontainer", config);
            String str = "asdfjklö";
            // create object
            InputStream is = new ByteArrayInputStream(str.getBytes("UTF-8"));
            storage.createObject("testcontainer", "testobject", is, 9, config);

            // get object to check if it is there
            InputStream os = storage.getObject("testcontainer", "testobject", config);
            byte[] ob = new byte[9];
            os.read(ob, 0, 9);
            byte[] ib = str.getBytes();
            assertTrue(Arrays.equals(ib, ob));

            // now delete object:
            storage.deleteObject("testcontainer", "testobject", config);
        } catch (Throwable e) {
            fail("There Should not be an exception, here");
        }
        // Expecting an Exception here:
        storage.getObject("testcontainer", "testobject", config);

        // This will probably never happen…
        storage.deleteContainer("testcontainer", config);
    }

}
