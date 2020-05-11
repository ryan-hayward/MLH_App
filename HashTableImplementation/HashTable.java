/////////////////// ALL ASSIGNMENTS INCLUDE THIS SECTION /////////////////////
//
// Title: P3 Project (HashTable)
// Files: HashTable.java, HashTableTest.java, DataStructureADT.java,
//        HashTableADT.java, MyProfiler.java
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

import java.util.ArrayList;

/**
 * Class implements a hash table using linear probing as the collision
 * resolution mechanism. Class implements required methods from DataStructureADT
 * as well as HashTableADT. Includes several private helper methods.
 * @author rchayward
 *
 * @param <K> comparable type K for keys
 * @param <V> comparable type V for values
 */
@SuppressWarnings("unchecked")
public class HashTable<K extends Comparable<K>, V> implements HashTableADT<K, V> {
	
	/**
	 * Nested protected class for the creation of an object to hold key,
	 * value pairs in the form of HashNodes.
	 * @author rchayward
	 * 
	 */
	protected class HashNode {
		//self explanatory data members
		private K key;
		private V value;
		private int hashCode;
		private int hashIndex;
		private boolean isEmpty;
		
		/**
		 * Empty general constructor
		 */
		private HashNode() {
			this.key = null;
			this.value = null;
			this.isEmpty = true;
		}
		
		/**
		 * Constructor of basic key, value HashNode
		 * @param key - key to assign to node
		 * @param value - value to assign to node
		 */
		private HashNode(K key, V value) {
			this.key = key;
			this.value = value;
			this.isEmpty = false;
		}
	}
	
	private int capacity; //table size
	private double loadFactorThreshold; //load factor (<1)
	protected int size; //number of nodes in table
	protected ArrayList<HashNode> table; //table to store nodes
	private int collisionResolution = 1; //linear probing
		
	/**
	 * Default no-arg constructor (picked a prime table size)
	 */
	public HashTable() {
		capacity = 11;
		loadFactorThreshold = 0.9;
		table = new ArrayList<HashNode>();
		size = 0;
	}
	
	/**
	 * HashTable constructor for general size and load factor threshold usage.
	 * @param initialCapacity - capacity passed to the hash table initially.
	 * @param loadFactorThreshold - fraction (0 < x < 1), fill percentage of
	 * table that will trigger a resize + rehash.
	 */
	public HashTable(int initialCapacity, double loadFactorThreshold) {
		this.capacity = initialCapacity;
		this.loadFactorThreshold = loadFactorThreshold;
		HashNode empty = new HashNode();
		table = new ArrayList<HashNode>(); //object array of size capacity
		this.size = 0; 
		for (int i = 0; i < capacity; i++) { //init empty array
			table.add(empty);
		}
	}
	
	/**
	 * Adds the key,value pair to the data structure and increase the number of keys.
	 * If key is already in data structure, replace value with new value.
	 * @param key - key of K type to add to the hash table
	 * @param value - value of V type to add to the hash table
	 * @throws IllegalNullKeyException if key is null
	 */
	@Override
	public void insert(K key, V value) throws IllegalNullKeyException {
		if(key == null) { //if key param is null
			throw new IllegalNullKeyException("Key is null.");
		}
		//check load threshold
		if(((double) size)/capacity >= loadFactorThreshold) { 
			resize(); //call helper method to resize hashtable
		}
		int index = Math.abs(key.hashCode()) % capacity; //index of key to insert/replace
		while(!table.get(index).isEmpty) { //while insert spot is filled 
			if(table.get(index).key.equals(key)) { //if duplicate is found
				table.get(index).value = value; //replace
			}
			if(index < capacity - 1) {
				index++; //search to right if possible
			} else {
				index = 0; //else wrap around
			}
		}
		HashNode node = new HashNode(key, value); //new node to insert
		node.hashCode = key.hashCode(); //set new hash code and index
		node.hashIndex = Math.abs(node.hashCode) % capacity;
		table.set(index, node); //add node
		size++; 
	}
	
	/**
	 * Private helper method aids in resizing the hash table when the 
	 * load factor threshold is exceeded.
	 * @throws IllegalNullKeyException - if key is null
	 */
	private void resize() throws IllegalNullKeyException {
		//double the size of the table and add 1
		ArrayList<HashNode> oldTable = table; //copy into a temporary list
		int oldCap = capacity; //remember old capacity
		capacity = (capacity * 2) + 1;  //new capacity (per specs)
		table = new ArrayList<HashNode>();  //new array list
		size = 0;  //reset numItems to zero
		HashNode empty = new HashNode(); //empty node
		//Initialize an new empty array
		for(int j = 0; j < capacity; j++) {
			table.add(empty);
		} //Rehash the nodes
		for(int i = 0; i < oldCap; i++) {
			if(oldTable.get(i).isEmpty) {
				continue; //don't rehash null nodes
			} else {
				rehash(oldTable.get(i), oldCap);
			}
		}
	}
	
	/**
	 * Private helper method aids in rehashing the nodes of the
	 * hash table when the load factor threshold is exceeded. 
	 * @param obj - object from the object array (cast to node)
	 * @param oldCapacity - capacity before resizing
	 */
	private void rehash(HashNode node, int oldCapacity) {
		try { //attempt a cast
			node.hashIndex = Math.abs(node.hashCode) % capacity; //rehash by new size
			insert(node.key, node.value); //insert new node with new hashCode
		} catch (IllegalNullKeyException f) { //catch exception from insert
			System.out.println(f);
		}
	}
	
	/**
	 * If key is found, removes the (K,V) pair from the data structure and
	 * decrease the number of keys.
	 * @param key - key to remove from the hash table
	 * @return true if key is found, false if not found
	 * @throws IllegalNullKeyException if key param is null
	 */
	@Override
	public boolean remove(K key) throws IllegalNullKeyException {
		if(key == null) { //if key param is null
			throw new IllegalNullKeyException("Key is null. (R)");
		}
		int index = Math.abs(key.hashCode()) % capacity;
		HashNode empty = new HashNode(); //empty node
		for(int i = 0; i < capacity; i++) {
			if(!table.get(index).isEmpty) { //if location is not empty
				if(compareKeys(key, table.get(index))) { //and key matches
					size--; //decrease size
					table.set(index, empty); //set node to empty
					return true; //key found
				} else { //iterate to next index
					if(index < capacity - 1) {
						index++; 
					} else {
						index = 0; 
					}
				}
			} else { //if empty, iterate to next index
				if(index < capacity - 1) {
					index++;
				} else {
					index = 0;
				} 
			}
		}
		return false; //return false if key is not found
	}
	
	/**
	 * Private helper method that compares HashNode keys. This
	 * method aids the get() and remove() methods.
	 * @param searchKey - key parameter from remove()
	 * @param node - hashNode in hash table
	 * @return - true if the keys match, false if they don't
	 */
	private boolean compareKeys(K searchKey, HashNode node) {
		if(searchKey.equals(node.key)) { //return true if equal
			return true;
		} else { return false; }
	}

	/**
	 * Method returns the value associated with the specified key.
	 * Does not remove keys or decrease the number of keys
	 * @param key - key to search for in the hash table
	 * @throws IllegalNullKeyException if key is null
	 * @throws KeyNotFoundException if key is not found
	 */
	@Override
	public V get(K key) throws IllegalNullKeyException, KeyNotFoundException {
		if(key == null) {  //if param is null
			throw new IllegalNullKeyException("Key is null.");
		}
		int index = Math.abs(key.hashCode()) % capacity; //find hash index
		for(int i = 0; i < capacity; i++) { 
			if(!table.get(index).isEmpty) { //if location is not empty
				if(compareKeys(key, table.get(index))) { //and key matches
					return table.get(index).value;
				} else { //iterate to next index
					if(index < capacity - 1) {
						index++;
					} else {
						index = 0;
					}
				}
			} else { //if empty, iterate to next index
				if(index < capacity - 1) {
					index++;
				} else {
					index = 0;
				} 
			}
		} //throw exception if key is not found
		throw new KeyNotFoundException("Key not found in table.");
	}
	
	/**
	 * @return number of keys in hash table.
	 */
	@Override
	public int numKeys() {
		return size;
	}
	
	/**
	 * @return the load factor threshold.
	 */
	@Override
	public double getLoadFactorThreshold() {
		return loadFactorThreshold;
	}
	
	/**
	 * @return the load factor (size/capacity)
	 */
	@Override
	public double getLoadFactor() {
		return ((double) size) / capacity;
	}
	
	/**
	 * @return the capacity of the hash table
	 */
	@Override
	public int getCapacity() {
		return capacity;
	}
	
	/**
	 * @return the collision resolution method of choice
	 */
	@Override
	public int getCollisionResolution() {
		return collisionResolution;
	}
		
}
