package com.heavyplayer.util.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.heavyplayer.util.ElasticThreadPoolExecutor;

public class ElasticityTest {
	ElasticThreadPoolExecutor pool;
	static Object lock = new Object();
	volatile int threadCount;

	@Before
	public void setUp() throws Exception {
		pool = new ElasticThreadPoolExecutor(1, 3, 0, TimeUnit.MICROSECONDS, new ArrayBlockingQueue<Runnable>(20), new CountingThreadFactory());
		threadCount = 0;
	}

	@Test
	public void testBaseCorePool() {
		synchronized(lock) {
			assertEquals(1, pool.getCorePoolSize());
			assertEquals(0, threadCount);
			pool.execute(new Lock());
			waitForQueueToHaveAtMost(0);
			assertEquals(1, pool.getCorePoolSize());
			assertEquals(1, threadCount);
		}
	}

	@Test
	public void testIncrease() {
		synchronized(lock) {
			pool.execute(new Lock());
			waitForQueueToHaveAtMost(0);
			assertEquals(1, pool.getCorePoolSize());
			assertEquals(1, threadCount);
			pool.execute(new Lock());
			waitForQueueToHaveAtMost(0);
			assertEquals(2, pool.getCorePoolSize());
			assertEquals(2, threadCount);
			pool.execute(new Lock());
			waitForQueueToHaveAtMost(0);
			assertEquals(3, pool.getCorePoolSize());
			assertEquals(3, threadCount);
		}
	}

	@Test
	public void testMaximum() {
		synchronized(lock) {
			for(int i = 0; i < 20; i++)
				pool.execute(new Lock());
			waitForQueueToHaveAtMost(17);

			assertEquals(3, pool.getCorePoolSize());
			assertEquals(3, threadCount);
		}
	}

	@Test
	public void testDecrease() {
		for(int i = 0; i < 20; i++)
			pool.execute(new Lock());

		waitForQueueToHaveAtMost(17);

		while(pool.getActiveCount() > 0) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				fail("Failed to sleep");
			}
		}
		
		assertEquals(1, pool.getCorePoolSize());
	}

	public void waitForQueueToHaveAtMost(int n) {
		do {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				fail("Wait for queue to have at most N");
			}
		} while(pool.getQueue().size() > n);
	}

	class Lock implements Runnable {
		@Override
		public void run() {
			synchronized(ElasticityTest.lock) {
				return;
			}
		}
	}

	class CountingThreadFactory implements ThreadFactory {
		@Override
		public Thread newThread(Runnable runnable) {
			Thread thread = new Thread(runnable);
			threadCount++;
			return thread;
		}

	}
}
