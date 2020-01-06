package de.moldiy.moldiyecs.utils;

import java.util.IdentityHashMap;

import org.junit.Test;

public class test {

	@Test
	public void test() {
		IdentityHashMap<Integer, Integer> lol = new IdentityHashMap<Integer, Integer>();
		
		
		System.out.println(lol.size());
		
		
		lol.put(1, null);
		
		System.out.println(lol.size());
		
		Integer test = lol.get(0);
		
		System.out.println(test);
		
		Bag<Integer> i = new Bag<Integer>();
		
		
	}
	
}
