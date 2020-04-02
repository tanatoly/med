package com.rafael.med.common;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.EventTranslatorThreeArg;
import com.lmax.disruptor.EventTranslatorVararg;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.LiteBlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;


public abstract class ByteBufferRing
{
	private static final Logger log = LogManager.getLogger();

	public enum WaitStrategyType
	{
		/**
		 * Sleeping strategy that initially spins, then uses a Thread.yield(), and eventually for the minimum number of nanos
		 * the OS and JVM will allow while the {@link com.lmax.disruptor.EventProcessor}s are waiting on a barrier.
		 * This strategy is a good compromise between performance and CPU resource. Latency spikes can occur after quiet periods.
		 */
		SLEEPING,

		/**
		 * Blocking strategy that uses a lock and condition variable for {@link EventProcessor}s waiting on a barrier.
		 * This strategy can be used when throughput and low-latency are not as important as CPU resource.
		 */
		BLOCKING,

		/**
		 * Busy Spin strategy that uses a busy spin loop for {@link com.lmax.disruptor.EventProcessor}s waiting on a barrier.
		 * This strategy will use CPU resource to avoid syscalls which can introduce latency jitter.  It is best
		 * used when threads can be bound to specific CPU cores.
		 */
		BUSY_SPIN,


		/**
		 * Variation of the {@link BlockingWaitStrategy} that attempts to elide conditional wake-ups when
		 * the lock is uncontended.  Shows performance improvements on microbenchmarks.  However this
		 * wait strategy should be considered experimental as I have not full proved the correctness of
		 * the lock elision code.
		 */
		LITE_BLOCKING,


		/**
		 * Yielding strategy that uses a Thread.yield() for {@link com.lmax.disruptor.EventProcessor}s waiting on a barrier
		 * after an initially spinning.
		 * This strategy is a good compromise between performance and CPU resource without incurring significant latency spikes.
		 */
		YIELDING;

		public static WaitStrategy waitStrategy(WaitStrategyType waitStrategyType)
		{

			switch (waitStrategyType) {
			case SLEEPING:
				return new SleepingWaitStrategy();
			case BLOCKING:
				return new BlockingWaitStrategy();
			case BUSY_SPIN:
				return new BusySpinWaitStrategy();
			case LITE_BLOCKING:
				return new LiteBlockingWaitStrategy();
			case YIELDING:
				return new YieldingWaitStrategy();

			default:
				throw new IllegalArgumentException("not found type = " + waitStrategyType);
			}
		}
	}




	public final static class ByteBufferCell
	{

		public ByteBuffer buffer;
		public Object arg1;
		public Object arg2;
		public long debugInsertTime;

		public ByteBufferCell(int capacity, ByteOrder byteOrder, boolean isBufferDirect)
		{
			this.buffer = isBufferDirect ? ByteBuffer.allocateDirect(capacity).order(byteOrder) : ByteBuffer.allocate(capacity).order(byteOrder);
		}
	}


	protected RingBuffer<ByteBufferCell> ring;
	protected Disruptor<ByteBufferCell> disruptor;
	protected int ringSize;


	public ByteBufferRing(int ringSize,final int bufferCapacity, final ByteOrder bufferByteOrder,final boolean isBufferDirect, Executor executor,boolean isSinglePublisher, WaitStrategyType waitStrategyType,EventHandler<ByteBufferCell> handler)
	{
		this.ringSize = ringSize;
		EventFactory<ByteBufferCell> eventFactory = new EventFactory<ByteBufferCell>()
		{
			@Override
			public ByteBufferCell newInstance()
			{
				return new ByteBufferCell(bufferCapacity, bufferByteOrder,isBufferDirect);
			}
		};

		ExceptionHandler<Object> exceptionHandler = new ExceptionHandler<Object>()
		{
			@Override
			public void handleOnStartException(Throwable ex)
			{
				throw new IllegalStateException("FAILED START RING", ex);

			}

			@Override
			public void handleOnShutdownException(Throwable ex)
			{
				log.warn("FAILED SHUTDOWN RING",  ex);
			}

			@Override
			public void handleEventException(Throwable ex, long sequence, Object event)
			{
				log.error("RING PROCESS ERROR - sequence = {} , event = {} - ",sequence,event,ex);
			}
		};


		disruptor = new Disruptor<ByteBufferCell>(eventFactory, ringSize, executor, isSinglePublisher ? ProducerType.SINGLE : ProducerType.MULTI, WaitStrategyType.waitStrategy(waitStrategyType));
		disruptor.setDefaultExceptionHandler(exceptionHandler);
		disruptor.handleEventsWith(handler);
		ring = disruptor.start();
	}



	public void shutdown() throws Exception
	{
		disruptor.shutdown(1000,TimeUnit.MILLISECONDS);
	}

	public static WaitStrategyType waitStrategyByName(String waitStrategyName)
	{
		return WaitStrategyType.valueOf(waitStrategyName.toUpperCase());
	}


	public static final class IN extends ByteBufferRing
	{
		private EventTranslatorThreeArg<ByteBufferCell, ByteBuffer, Object, Object> eventTranslator;

		public IN(int ringSize, int bufferCapacity, ByteOrder bufferByteOrder, boolean isBufferDirect,Executor executor, boolean isSinglePublisher, WaitStrategyType waitStrategyType,EventHandler<ByteBufferCell> handler)
		{
			super(ringSize, bufferCapacity, bufferByteOrder, isBufferDirect, executor, isSinglePublisher, waitStrategyType, handler);
			this.eventTranslator = new EventTranslatorThreeArg<ByteBufferCell, ByteBuffer, Object, Object>()
			{
				@Override
				public void translateTo(ByteBufferCell byteBufferCell, long sequence, ByteBuffer buffer, Object arg1, Object arg2)
				{
					byteBufferCell.buffer.clear();
					if(buffer != null)
					{
						byteBufferCell.buffer.put(buffer);
						byteBufferCell.buffer.flip();
					}
					byteBufferCell.arg1 = arg1;
					byteBufferCell.arg2 = arg2;
					byteBufferCell.debugInsertTime = System.nanoTime();
				}
			};
		}


		public void push(ByteBuffer buffer,Object arg1ByReference,Object  arg2ByReference)
		{
			boolean isPublished = ring.tryPublishEvent(eventTranslator, buffer, arg1ByReference,arg2ByReference);
			if(!isPublished)
			{
				log.warn("IN RING  BUFFER SIZE = {} IS FULL - UNEXPECTED BUT CONTINUE WORKING",ringSize);
			}
		}
	}

	public static final class OUT extends ByteBufferRing
	{
		private EventTranslatorVararg<ByteBufferCell> eventTranslator;

		public OUT(int ringSize, int bufferCapacity, ByteOrder bufferByteOrder, boolean isBufferDirect,Executor executor, boolean isSinglePublisher, WaitStrategyType waitStrategyType,EventHandler<ByteBufferCell> handler)
		{
			super(ringSize, bufferCapacity, bufferByteOrder, isBufferDirect, executor, isSinglePublisher, waitStrategyType, handler);
			this.eventTranslator = new EventTranslatorVararg<ByteBufferCell>()
			{
				@Override
				public void translateTo(ByteBufferCell event, long sequence, Object... args)
				{
					MessageBuilder messageBuilder 	= (MessageBuilder) args[0];
					Object target					= args[1];
					Object [] params				= (Object[]) args[2];
					event.buffer.clear();
					messageBuilder.buildMessage(event.buffer, params);
					event.arg1 	= target;
					event.arg2	= messageBuilder.getOpcodeName();
				}
			};
		}

		public void push(MessageBuilder messageBuilder,Object target,Object... params)
		{
			boolean isPublished = ring.tryPublishEvent(eventTranslator, messageBuilder,target,params);
			if(!isPublished)
			{
				log.warn("OUT RING  BUFFER SIZE = {} IS FULL - UNEXPECTED BUT CONTINUE WORKING",ringSize);
			}
		}
	}
}
