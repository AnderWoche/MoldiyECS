package de.moldiy.moldiyecs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.moldiy.moldiyecs.componentmanager.Component;
import de.moldiy.moldiyecs.componentmanager.ComponentIDFactory;
import de.moldiy.moldiyecs.subscription.Aspect;
import de.moldiy.moldiyecs.utils.BitVector;

public class aspectTest {
	
	public static class com1 implements Component {
		public void reset() {
		}
	}
	
	public static class com2 implements Component {
		@Override
		public void reset() {
		}
	}
	
	@Test
	public void test() {
		ComponentIDFactory facktory = new ComponentIDFactory();
		
		System.out.println(facktory.getComponentIDFor(com1.class));
		assertEquals(facktory.getComponentIDFor(com1.class), 0);
		
		System.out.println(facktory.getComponentIDFor(com2.class));
		assertEquals(facktory.getComponentIDFor(com2.class), 1);
				
				
		
		Aspect a = new Aspect();
		
		a.getAllSet().set(3);
		a.getAllSet().set(2);
		
		a.getOneSet().set(1);
		a.getOneSet().set(2);
		
		
		BitVector vec = new BitVector();
		vec.set(2);
		vec.set(3);
//		vec.set(1);
		vec.set(7);
		
		
		assertTrue(a.isInterested(vec));
	}

}
