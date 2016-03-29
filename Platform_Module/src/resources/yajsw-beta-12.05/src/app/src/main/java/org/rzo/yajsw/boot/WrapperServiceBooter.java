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
package org.rzo.yajsw.boot;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URLClassLoader;

public class WrapperServiceBooter
{
	public static void main(String[] args)
	{
		if (System.getProperty("java.io.tmpdir") != null)
		{
			File tmp = new File(System.getProperty("java.io.tmpdir"));
			if (!tmp.exists())
				tmp.mkdirs();
		}

		URLClassLoader cl = WrapperLoader.getWrapperClassLoader();
		Thread.currentThread().setContextClassLoader(cl);
		String osName = System.getProperty("os.name");
		String clazz = null;
		if (osName.toLowerCase().startsWith("windows"))
			clazz = "org.rzo.yajsw.app.WrapperMainServiceWin";
		else
			clazz = "org.rzo.yajsw.app.WrapperMainServiceUnix";
		try
		{
			Class cls = Class.forName(clazz, true, cl);
			Method mainMethod = cls.getDeclaredMethod("main",
					new Class[] { String[].class });
			mainMethod.invoke(null, new Object[] { args });
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
