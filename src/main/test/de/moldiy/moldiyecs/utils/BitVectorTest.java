package de.moldiy.moldiyecs.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BitVectorTest {
	
	@Test
	public void BitVectorTest() {
		BitVector vec = new BitVector();
		vec.unsafeSet(0);
		vec.unsafeSet(1);
		vec.unsafeSet(2);
		vec.unsafeSet(60);
		int hash1 = vec.hashCode();
		
		
		BitVector vec2 = new BitVector();
		vec2.unsafeSet(0);
		vec2.unsafeSet(1);
		vec2.unsafeSet(2);
		vec2.unsafeSet(60);
		int hash2 = vec2.hashCode();
		
		
		assertEquals(hash1, hash2);
		
	}

}
