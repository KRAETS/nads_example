/*******************************************************************************
 * Copyright  2015 rzorzorzo@users.sf.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.rzo.netty.ahessian.heartbeat;

import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.rzo.netty.ahessian.utils.MyReentrantLock;

public class IntervalTimer
{
	private final Timer _timer;
	private final TimerTask _task;
	private volatile Timeout _timeout;
	private final long _interval;
	private volatile String _name = "?";
	private final Lock _lock = new MyReentrantLock();

	public IntervalTimer(final Timer timer, final TimerTask task,
			final long interval)
	{
		_timer = timer;
		_task = new TimerTask()
		{

			public void run(Timeout timeout) throws Exception
			{
				_lock.lock();
				try
				{
					if (!timeout.equals(getTimeout()))
					{
						// System.out.println("other timeout -> ignore");
						return;
					}
					// System.out.println(new Date()+" timer called " +
					// _name+"/"+_interval);
					try
					{
						task.run(timeout);
					}
					catch (Throwable ex)
					{
						ex.printStackTrace();
					}
					if (getTimeout() != null)
						setTimeout(_timer.newTimeout(this, interval,
								TimeUnit.MILLISECONDS));
				}
				finally
				{
					_lock.unlock();
				}

			}

		};
		_interval = interval;
	}

	synchronized public void start()
	{
		_lock.lock();
		try
		{
			if (_timeout != null)
				return;
			// System.out.println("starting timer "+_name);
			setTimeout(_timer.newTimeout(_task, _interval,
					TimeUnit.MILLISECONDS));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			_lock.unlock();
		}
	}

	synchronized public void stop()
	{
		_lock.lock();
		try
		{
			if (getTimeout() == null)
				return;
			// System.out.println("stopping timer "+_name);
			getTimeout().cancel();
			setTimeout(null);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			_lock.unlock();
		}
	}

	synchronized public boolean isActive()
	{
		return _timeout != null;
	}

	public long getInterval()
	{
		return _interval;
	}

	public void setName(String name)
	{
		_name = name;
	}

	private Timeout getTimeout()
	{
		return _timeout;
	}

	private void setTimeout(Timeout timeout)
	{
		_timeout = timeout;
	}

}
