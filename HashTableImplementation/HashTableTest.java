T/////////////////// ALL ASSIGNMENTS INCLUDE THIS SECTION /////////////////////
//
// Title: P3A Project (HashTable)
// Files: HashTable.java, HashTableTest.jave, DataStructureADT.java,
//        HashTableADT.java
// Course: CS400 Spring 2020
//
// Author: Ryan Hayward
// Email: rchayward@wisc.edu
// Lecturer's Name: Deb Deppeler
//
////////////////////////////////// Description ////////////////////////////////
//
// This programs contains an implementation of a hash table structure and 
// several tests for it. It uses linear probing for collision resolution
//
///////////////////////////// CREDIT OUTSIDE HELP /////////////////////////////
//
// Students who get help from sources other than their partner must fully
// acknowledge and credit those sources of help here. Instructors and TAs do
// not need to be credited here, but tutors, friends, relatives, room mates,
// strangers, and others do. If you received no outside help from either type
// of source, then please explicitly indicate NONE.
//
// Persons: NONE
// Online Sources: NONE
//
/////////////////////////////// 80 COLUMNS WIDE ///////////////////////////////

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Random;



/**
 * This class includes test for the HashTable class found in
 * HashTable.java. This class uses JUnit 5 test styles to
 * test the veracity of my code.
 * @author rchayward
 *
 */
public class HashTableTest{

    	private HashTable ht; //initialize a hash table named ht
    
    /**
     * Code that runs befor each test method. Default test ht
     * @throws Exception just in case
     */
    @Before
    public void setUp() throws Exception {
    	ht = new HashTable(5, 0.7);
    }

    /**
     * Code that runs after each test method. Set ht to null
     * @throws Exception just in case
     */
    @After
    public void tearDown() throws Exception {
    	ht = null;
    }
    
    /** 
     * Tests that a HashTable returns an integer code
     * indicating which collision resolution strategy 
     * is used.
     * REFER TO HashTableADT for valid collision scheme codes.
     */
    @Test
    public void test000_collision_scheme() {
        HashTableADT htIntegerKey = new HashTable<Integer,String>();
        int scheme = htIntegerKey.getCollisionResolution();
        if (scheme < 1 || scheme > 9) 
            fail("collision resolution must be indicated with 1-9");
    }
        
    /** IMPLEMENTED AS EXAMPLE FOR YOU
     * Tests that insert(null,null) throws IllegalNullKeyException
     */
    @Test
    public void test001_IllegalNullKey() {
        try {
            HashTableADT htIntegerKey = new HashTable<Integer,String>();
            htIntegerKey.insert(null, null);
            fail("should not be able to insert null key");
        } 
        catch (IllegalNullKeyException e) { /* expected */ } 
        catch (Exception e) {
            fail("insert null key should not throw exception "+e.getClass().getName());
        }
    }
    
    /**
     * Test basic insert function with a few key value pairs
     */
    @Test
    public void test002_TestBasicInsert() {
    	try {
    		ht.insert("14", 14);
    		ht.insert("7", 7);
    		ht.insert("32", 5);
    	} catch(IllegalNullKeyException e) { System.out.println(e);
		} if(ht.numKeys() != 3) {
			fail("Keys not inserting.");
		}
    }
    
    /**
     * Test basic remove function with a few key value pairs
     */
    @Test
    public void test003_TestBasicRemove() {
    	try {
    		ht.insert("14", 14);
    		ht.insert("22", 22);
    		ht.insert("15", 15);
    		ht.remove("22");
    	} catch(IllegalNullKeyException e) { System.out.println(e);
    	} if(ht.size != 2) {
    		fail("Size is wrong.");
    	}
    }
    
    /**
     * Test a more complex version of remove.
     */
    @Test
    public void test004_TestComplexRemove() {
    	try {
    		ht.insert(7, 7);
        	ht.insert(14, 14);
        	ht.insert(21, 21);
        	ht.remove(14);
        	if(!ht.get(7).equals(7)) {
        		fail("Get is not working.");
        	}
    	} catch(IllegalNullKeyException e) { System.out.println(e);
    	} catch (KeyNotFoundException f) { System.out.println(f);
		} 
    }
    
    /**
     * Test the program's resize capability
     */
    @Test
    public void test005_TestResize() {
    	try {
    		ht.insert(7, 7);
    		ht.insert(13, 13);
    		ht.insert(20, 20);
    		ht.insert(12, 12);
    		ht.insert(1, 1);
    		ht.insert(2, 2);
    		System.out.println(ht.get(13));
    		System.out.println(ht.get(2));
    		System.out.println(ht.get(12));
    		System.out.println(ht.get(1));
    		System.out.println(ht.get(7));
    		System.out.println(ht.get(20));
    		System.out.println(ht.getCapacity());
    	} catch(IllegalNullKeyException e) { System.out.println(e);
    	} catch (KeyNotFoundException f) { System.out.println(f);
		} if(ht.getCapacity() != 11) {
    		fail("Resize is not working properly.");
    	}
    }
    
    /**
     * Tests the function of inserting a duplicate key. Should
     * simply replace the value.
     */
    @Test
    public void test006_TestInsertDuplicate() {
    	try {
    		ht.insert(7, 7);
    		ht.insert(13, 13);
    		ht.insert(20, 20);
    		ht.insert(12, 12);
    		ht.insert(7, 0);
    		if(ht.get(7).equals(7)) {
    			fail("Replace function is not working.");
    		} //now remove
    		ht.remove(7);
    		ht.remove(13);
    		ht.remove(20);
    		ht.remove(12);
    		if(ht.numKeys() != 0) {
    			fail("Replace is not working for removal");
    		}
    	} catch(IllegalNullKeyException e) { System.out.println(e);
    	} catch (KeyNotFoundException f) { System.out.println(f);
		} 
    }
    
    @SuppressWarnings("unchecked")
	@Test
    public void test007_TestInsertMany() {
    	try {
    		for(int i = 0; i < 10000; i++) {
    			int key = (int) (Math.random() * 10000);
    			ht.insert(key, key);
    		}
    	} catch(IllegalNullKeyException e) { System.out.println(e);
    	}
    }
}
