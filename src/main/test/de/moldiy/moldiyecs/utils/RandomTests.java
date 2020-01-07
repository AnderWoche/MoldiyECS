package de.moldiy.moldiyecs.utils;

import java.util.IdentityHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;

import de.moldiy.moldiyecs.Entity;

public class RandomTests {

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

	@Test
	public void ThreadTest() {
		class SyncronizedClass {
			private final Lock lock = new ReentrantLock();
			public Entity entity = new Entity(10);

			public void A() {
//				lock.lock();
				System.out.println("Call A from thread: " + Thread.currentThread());
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
//				lock.unlock();
			}

			public void B() {
//				lock.lock();
				System.out.println("Call B from thread: " + Thread.currentThread());
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
//				lock.unlock();
			}
		}
		final SyncronizedClass testClass = new SyncronizedClass();
		Thread t = new Thread(new Runnable() {
			public void run() {
//				for (int i = 0; i < 100; i++) {
				testClass.A();
				testClass.B();
//				}
			}
		});
		t.setName("Thread A");
		t.start();
		Thread t2 = new Thread(new Runnable() {
			public void run() {
//				for (int i = 0; i < 100; i++) {
				System.out.println("Entity ID = " + testClass.entity.getID());
				testClass.B();
				testClass.A();
//				}
			}
		});
		t2.setName("Thread B");
		t2.start();

		try {
			t.join();
			t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
