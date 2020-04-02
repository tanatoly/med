package com.rafael.med.common;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * The Class Threads.
 */
public class Threads 
{
	private static final class StandradThreadFactory implements ThreadFactory
	{
		
		protected final AtomicInteger 	threadNumber = new AtomicInteger(1);
		protected final String 			namePrefix;
		protected final boolean 		isDaemon;
		protected final int 			priority;
		
		private StandradThreadFactory(String poolName, boolean isDaemon, int priority)
		{
			this.isDaemon 	= isDaemon;
			this.namePrefix = poolName + "-thread-";
			this.priority 	= priority;
		}

		@Override
		public Thread newThread(Runnable runnable)
		{
			Thread thread = new Thread(runnable);
			thread.setPriority(priority);
			thread.setName(namePrefix + threadNumber.getAndIncrement());
			thread.setDaemon(isDaemon);
			return thread;
		}
	}
		
	/**
	 * Pool executor.
	 *
	 * @param corePoolSize the core pool size
	 * @param maximumPoolSize the maximum pool size
	 * @param keepAliveTime the keep alive time
	 * @param unit the unit
	 * @param workQueue the work queue
	 * @param name the name
	 * @param isDaemon the is daemon
	 * @param priority the priority
	 * @return the thread pool executor
	 */
	private static ThreadPoolExecutor poolExecutor(int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue,
            String name,
            boolean isDaemon,
            int priority)
	{
		return new ThreadPoolExecutor(corePoolSize, maximumPoolSize,0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(),new StandradThreadFactory(name,isDaemon,priority));
	}
	
	
	/**
	 * Scheduled pool executor.
	 *
	 * @param corePoolSize the core pool size
	 * @param name the name
	 * @param isDaemon the is daemon
	 * @param priority the priority
	 * @return the scheduled thread pool executor
	 */
	private static ScheduledThreadPoolExecutor scheduledPoolExecutor(int corePoolSize, String name,boolean isDaemon,int priority)
	{
		return new ScheduledThreadPoolExecutor(corePoolSize, new StandradThreadFactory(name,isDaemon,priority));
	}
	
	
	
    /**
     * Creates a thread pool that reuses a fixed number of threads
     * operating off a shared unbounded queue, using the provided
     * ThreadFactory to create new threads when needed.  At any point,
     * at most {@code nThreads} threads will be active processing
     * tasks.  If additional tasks are submitted when all threads are
     * active, they will wait in the queue until a thread is
     * available.  If any thread terminates due to a failure during
     * execution prior to shutdown, a new one will take its place if
     * needed to execute subsequent tasks.  The threads in the pool will
     * exist until it is explicitly {@link ExecutorService#shutdown
     * shutdown}.
     *
     * @param nThreads the number of threads in the pool
     * @param name the name
     * @return the executor service
     * @throws IllegalArgumentException if {@code nThreads <= 0}
     */
	
	public static ExecutorService newFixedThreadPool(int nThreads, String name) 
	{
		return newFixedThreadPool(nThreads, name, false);
	}
	
	/**
	 * New fixed thread pool.
	 *
	 * @param nThreads the n threads
	 * @param name the name
	 * @param isDaemon the is daemon
	 * @return the executor service
	 */
	public static ExecutorService newFixedThreadPool(int nThreads, String name,boolean isDaemon) 
	{
		return newFixedThreadPool(nThreads, name, isDaemon, Thread.NORM_PRIORITY);
	}
	
    /**
     * New fixed thread pool.
     *
     * @param nThreads the n threads
     * @param name the name
     * @param isDaemon the is daemon
     * @param priority the priority
     * @return the executor service
     */
    public static ExecutorService newFixedThreadPool(int nThreads, String name,boolean isDaemon,int priority) 
    {
        return poolExecutor(nThreads, nThreads,0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(),name, isDaemon, priority);
    }

    /**
     * Creates an Executor that uses a single worker thread operating
     * off an unbounded queue, and uses the provided ThreadFactory to
     * create a new thread when needed. Unlike the otherwise
     * equivalent {@code newFixedThreadPool(1, threadFactory)} the
     * returned executor is guaranteed not to be reconfigurable to use
     * additional threads.
     *
     * @param name the name
     * @return the newly created single-threaded Executor
     * @throws NullPointerException if threadFactory is null
     */
    
    public static ExecutorService newSingleThreadExecutor(String name)
    {
    	return newSingleThreadExecutor(name, false);
    }
    
    /**
     * New single thread executor.
     *
     * @param name the name
     * @param isDaemon the is daemon
     * @return the executor service
     */
    public static ExecutorService newSingleThreadExecutor(String name,boolean isDaemon)
    {
    	return newSingleThreadExecutor(name,isDaemon,Thread.NORM_PRIORITY);
    }
   
    /**
     * New single thread executor.
     *
     * @param name the name
     * @param isDaemon the is daemon
     * @param priority the priority
     * @return the executor service
     */
    public static ExecutorService newSingleThreadExecutor(String name,boolean isDaemon,int priority)
    {
        return new FinalizableDelegatedExecutorService
        	(poolExecutor(1, 1,0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(),name, isDaemon, priority));
    }

    /**
     * Creates a thread pool that creates new threads as needed, but
     * will reuse previously constructed threads when they are
     * available, and uses the provided
     * ThreadFactory to create new threads when needed.
     *
     * @param name the name
     * @return the newly created thread pool
     * @throws NullPointerException if threadFactory is null
     */
    
    public static ExecutorService newCachedThreadPool(String name)
    {
    	return newCachedThreadPool(name, false);
    }
    
    /**
     * New cached thread pool.
     *
     * @param name the name
     * @param isDaemon the is daemon
     * @return the executor service
     */
    public static ExecutorService newCachedThreadPool(String name,boolean isDaemon)
    {
    	return newCachedThreadPool(name,isDaemon,Thread.NORM_PRIORITY);
    }
    
    /**
     * New cached thread pool.
     *
     * @param name the name
     * @param isDaemon the is daemon
     * @param priority the priority
     * @return the executor service
     */
    public static ExecutorService newCachedThreadPool(String name,boolean isDaemon,int priority)
    {
        return poolExecutor(0, Integer.MAX_VALUE,60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),name, isDaemon, priority);
    }


    /**
     * Creates a single-threaded executor that can schedule commands
     * to run after a given delay, or to execute periodically.  (Note
     * however that if this single thread terminates due to a failure
     * during execution prior to shutdown, a new one will take its
     * place if needed to execute subsequent tasks.)  Tasks are
     * guaranteed to execute sequentially, and no more than one task
     * will be active at any given time. Unlike the otherwise
     * equivalent {@code newScheduledThreadPool(1, threadFactory)}
     * the returned executor is guaranteed not to be reconfigurable to
     * use additional threads.
     *
     * @param name the name
     * @return a newly created scheduled executor
     * @throws NullPointerException if threadFactory is null
     */
    
    public static ScheduledExecutorService newSingleThreadScheduledExecutor(String name)
    {
    	return newSingleThreadScheduledExecutor(name, false);
    }
    
    /**
     * New single thread scheduled executor.
     *
     * @param name the name
     * @param isDaemon the is daemon
     * @return the scheduled executor service
     */
    public static ScheduledExecutorService newSingleThreadScheduledExecutor(String name,boolean isDaemon)
    {
    	return newSingleThreadScheduledExecutor(name,isDaemon,Thread.NORM_PRIORITY);
    }
    
    /**
     * New single thread scheduled executor.
     *
     * @param name the name
     * @param isDaemon the is daemon
     * @param priority the priority
     * @return the scheduled executor service
     */
    public static ScheduledExecutorService newSingleThreadScheduledExecutor(String name,boolean isDaemon,int priority)
    {
        return new DelegatedScheduledExecutorService(scheduledPoolExecutor(1, name, isDaemon, priority));
    }

    /**
     * Creates a thread pool that can schedule commands to run after a
     * given delay, or to execute periodically.
     *
     * @param corePoolSize the number of threads to keep in the pool,
     * even if they are idle
     * @param name the name
     * @return a newly created scheduled thread pool
     * @throws IllegalArgumentException if {@code corePoolSize < 0}
     * @throws NullPointerException if threadFactory is null
     */
    
    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize,String name)
    {
    	return newScheduledThreadPool(corePoolSize,name, false);
    }
    
    /**
     * New scheduled thread pool.
     *
     * @param corePoolSize the core pool size
     * @param name the name
     * @param isDaemon the is daemon
     * @return the scheduled executor service
     */
    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize,String name,boolean isDaemon)
    {
    	return newScheduledThreadPool(corePoolSize,name,isDaemon,Thread.NORM_PRIORITY);
    }
    
    /**
     * New scheduled thread pool.
     *
     * @param corePoolSize the core pool size
     * @param name the name
     * @param isDaemon the is daemon
     * @param priority the priority
     * @return the scheduled executor service
     */
    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize, String name,boolean isDaemon,int priority)
    {
        return scheduledPoolExecutor(corePoolSize, name, isDaemon, priority);
    }

    /**
     * A wrapper class that exposes only the ExecutorService methods
     * of an ExecutorService implementation.
     */
    static class DelegatedExecutorService extends AbstractExecutorService {
        
        /** The e. */
        private final ExecutorService e;
        
        /**
         * Instantiates a new delegated executor service.
         *
         * @param executor the executor
         */
        DelegatedExecutorService(ExecutorService executor) { e = executor; }
        
        /* (non-Javadoc)
         * @see java.util.concurrent.Executor#execute(java.lang.Runnable)
         */
        public void execute(Runnable command) { e.execute(command); }
        
        /* (non-Javadoc)
         * @see java.util.concurrent.ExecutorService#shutdown()
         */
        public void shutdown() { e.shutdown(); }
        
        /* (non-Javadoc)
         * @see java.util.concurrent.ExecutorService#shutdownNow()
         */
        public List<Runnable> shutdownNow() { return e.shutdownNow(); }
        
        /* (non-Javadoc)
         * @see java.util.concurrent.ExecutorService#isShutdown()
         */
        public boolean isShutdown() { return e.isShutdown(); }
        
        /* (non-Javadoc)
         * @see java.util.concurrent.ExecutorService#isTerminated()
         */
        public boolean isTerminated() { return e.isTerminated(); }
        
        /* (non-Javadoc)
         * @see java.util.concurrent.ExecutorService#awaitTermination(long, java.util.concurrent.TimeUnit)
         */
        public boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException {
            return e.awaitTermination(timeout, unit);
        }
        
        /* (non-Javadoc)
         * @see java.util.concurrent.AbstractExecutorService#submit(java.lang.Runnable)
         */
        public Future<?> submit(Runnable task) {
            return e.submit(task);
        }
        
        /* (non-Javadoc)
         * @see java.util.concurrent.AbstractExecutorService#submit(java.util.concurrent.Callable)
         */
        public <T> Future<T> submit(Callable<T> task) {
            return e.submit(task);
        }
        
        /* (non-Javadoc)
         * @see java.util.concurrent.AbstractExecutorService#submit(java.lang.Runnable, java.lang.Object)
         */
        public <T> Future<T> submit(Runnable task, T result) {
            return e.submit(task, result);
        }
        
        /* (non-Javadoc)
         * @see java.util.concurrent.AbstractExecutorService#invokeAll(java.util.Collection)
         */
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
            throws InterruptedException {
            return e.invokeAll(tasks);
        }
        
        /* (non-Javadoc)
         * @see java.util.concurrent.AbstractExecutorService#invokeAll(java.util.Collection, long, java.util.concurrent.TimeUnit)
         */
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
                                             long timeout, TimeUnit unit)
            throws InterruptedException {
            return e.invokeAll(tasks, timeout, unit);
        }
        
        /* (non-Javadoc)
         * @see java.util.concurrent.AbstractExecutorService#invokeAny(java.util.Collection)
         */
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
            return e.invokeAny(tasks);
        }
        
        /* (non-Javadoc)
         * @see java.util.concurrent.AbstractExecutorService#invokeAny(java.util.Collection, long, java.util.concurrent.TimeUnit)
         */
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks,
                               long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
            return e.invokeAny(tasks, timeout, unit);
        }
    }

    /**
     * The Class FinalizableDelegatedExecutorService.
     */
    static class FinalizableDelegatedExecutorService
        extends DelegatedExecutorService {
        
        /**
         * Instantiates a new finalizable delegated executor service.
         *
         * @param executor the executor
         */
        FinalizableDelegatedExecutorService(ExecutorService executor) {
            super(executor);
        }
        
        /* (non-Javadoc)
         * @see java.lang.Object#finalize()
         */
        protected void finalize() {
            super.shutdown();
        }
    }

    /**
     * A wrapper class that exposes only the ScheduledExecutorService
     * methods of a ScheduledExecutorService implementation.
     */
    static class DelegatedScheduledExecutorService
            extends DelegatedExecutorService
            implements ScheduledExecutorService {
        
        /** The e. */
        private final ScheduledExecutorService e;
        
        /**
         * Instantiates a new delegated scheduled executor service.
         *
         * @param executor the executor
         */
        DelegatedScheduledExecutorService(ScheduledExecutorService executor) {
            super(executor);
            e = executor;
        }
        
        /* (non-Javadoc)
         * @see java.util.concurrent.ScheduledExecutorService#schedule(java.lang.Runnable, long, java.util.concurrent.TimeUnit)
         */
        public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
            return e.schedule(command, delay, unit);
        }
        
        /* (non-Javadoc)
         * @see java.util.concurrent.ScheduledExecutorService#schedule(java.util.concurrent.Callable, long, java.util.concurrent.TimeUnit)
         */
        public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
            return e.schedule(callable, delay, unit);
        }
        
        /* (non-Javadoc)
         * @see java.util.concurrent.ScheduledExecutorService#scheduleAtFixedRate(java.lang.Runnable, long, long, java.util.concurrent.TimeUnit)
         */
        public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
            return e.scheduleAtFixedRate(command, initialDelay, period, unit);
        }
        
        /* (non-Javadoc)
         * @see java.util.concurrent.ScheduledExecutorService#scheduleWithFixedDelay(java.lang.Runnable, long, long, java.util.concurrent.TimeUnit)
         */
        public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
            return e.scheduleWithFixedDelay(command, initialDelay, delay, unit);
        }
    }

    /** Cannot instantiate. */
    private Threads() {}
}
