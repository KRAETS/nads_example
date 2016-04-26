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

package org.rzo.yajsw.os;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Interface Process.
 */
public interface Process
{

	/**
	 * Sets the command.
	 * 
	 * @param cmd
	 *            the new command
	 */
	public void setCommand(String cmd);

	public void setCommand(String[] cmd);

	/**
	 * Sets the working dir.
	 * 
	 * @param dir
	 *            the new working dir
	 */
	public void setWorkingDir(String dir); //

	public boolean changeWorkingDir(String dir); //

	/**
	 * Sets the priority.
	 * 
	 * @param priority
	 *            the new priority
	 */
	public void setPriority(int priority); //

	/**
	 * Sets the cpu affinity.
	 * 
	 * @param cpuAffinity
	 *            the new cpu affinity
	 */
	public void setCpuAffinity(int cpuAffinity);

	/**
	 * Sets the visible.
	 * 
	 * @param visible
	 *            the new visible
	 */
	public void setVisible(boolean visible);

	/**
	 * Sets the title.
	 * 
	 * @param title
	 *            the new title
	 */
	public void setTitle(String title);

	/**
	 * Start.
	 * 
	 * @return true, if successful
	 */
	public boolean start();

	/**
	 * Wait for.
	 */
	public void waitFor();

	/**
	 * Wait for.
	 * 
	 * @param timeout
	 *            the timeout
	 */
	public void waitFor(long timeout);

	/**
	 * Kill.
	 * 
	 * @param code
	 *            the code
	 * 
	 * @return true, if successful
	 */
	public boolean kill(int code);

	/**
	 * Kill tree.
	 * 
	 * @param code
	 *            the code
	 * 
	 * @return true, if successful
	 */
	public boolean killTree(int code);

	/**
	 * Checks if is running.
	 * 
	 * @return true, if is running
	 */
	public boolean isRunning();

	/**
	 * Gets the pid.
	 * 
	 * @return the pid
	 */
	public int getPid();

	/**
	 * Gets the exit code.
	 * 
	 * @return the exit code
	 */
	public int getExitCode();

	/**
	 * Gets the command.
	 * 
	 * @return the command
	 */
	public String getCommand();

	/**
	 * Gets the working dir.
	 * 
	 * @return the working dir
	 */
	public String getWorkingDir();

	/**
	 * Gets the priority.
	 * 
	 * @return the priority
	 */
	public int getPriority();

	/**
	 * Checks if is visible.
	 * 
	 * @return true, if is visible
	 */
	public boolean isVisible();

	/**
	 * Gets the children.
	 * 
	 * @return the children
	 */
	public Collection getChildren();

	/**
	 * Sets the pipe streams.
	 * 
	 * @param pipeStreams
	 *            the pipe streams
	 * @param redirectErrorStream
	 *            the redirect error stream
	 */
	public void setPipeStreams(boolean pipeStreams, boolean redirectErrorStream);

	/**
	 * Gets the input stream.
	 * 
	 * @return the input stream
	 */
	public InputStream getInputStream();

	/**
	 * Gets the error stream.
	 * 
	 * @return the error stream
	 */
	public InputStream getErrorStream();

	/**
	 * Gets the output stream.
	 * 
	 * @return the output stream
	 */
	public OutputStream getOutputStream();

	/**
	 * Gets the current cpu.
	 * 
	 * @return the current cpu
	 */
	public int getCurrentCpu();

	/**
	 * Gets the current physical memory.
	 * 
	 * @return the current physical memory
	 */
	public long getCurrentPhysicalMemory();

	/**
	 * Gets the current virtual memory.
	 * 
	 * @return the current virtual memory
	 */
	public long getCurrentVirtualMemory();

	/**
	 * Gets the current page faults.
	 * 
	 * @return the current page faults
	 */
	public int getCurrentPageFaults();

	/**
	 * Destroy.
	 */
	public void destroy();

	/**
	 * Stop.
	 * 
	 * @param timeout
	 *            the timeout
	 * @param code
	 *            the code
	 * 
	 * @return true, if successful
	 */
	public boolean stop(int timeout, int code);

	/** The Constant PRIORITY_NORMAL. */
	public static final int PRIORITY_NORMAL = 0;

	/** The Constant PRIORITY_BELOW_NORMAL. */
	public static final int PRIORITY_BELOW_NORMAL = -1;

	/** The Constant PRIORITY_LOW. */
	public static final int PRIORITY_LOW = -2;

	/** The Constant PRIORITY_ABOVE_NORMAL. */
	public static final int PRIORITY_ABOVE_NORMAL = 1;

	/** The Constant PRIORITY_HIGH. */
	public static final int PRIORITY_HIGH = 2;

	/** The Constant PRIORITY_UNDEFINED. */
	public static final int PRIORITY_UNDEFINED = -99;

	/** The Constant AFFINITY_UNDEFINED. */
	public static final int AFFINITY_UNDEFINED = -99;

	public void setTmpPath(String tmpPath);

	public boolean reconnectStreams();

	public void setTeeName(String teeName);

	public String getTitle();

	public void setUser(String name);

	public String getUser();

	public void setPassword(String password);

	public int getCurrentThreads();

	public int getCurrentHandles();

	public boolean isTerminated();

	public void setLogger(Logger logger);

	public List<String[]> getEnvironment();

	public Map<String, String> getEnvironmentAsMap();

	public void setEnvironment(List<String[]> env);

	public void setDebug(boolean debug);

	public void setMinimized(boolean b);

	public boolean isLogonActiveSession();

	public void setLogonActiveSession(boolean logonActiveSession);

	public void setDesktop(String desktop);

	public void setUmask(int umask);

	public void setUseSpawn(boolean useSpawn);

	public void setLinuxUseVfork(boolean linuxUseVfork);

}
