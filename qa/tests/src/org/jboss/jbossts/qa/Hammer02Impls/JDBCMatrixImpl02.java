/*
 * JBoss, Home of Professional Open Source
 * Copyright 2007, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 *
 * (C) 2005-2006,
 * @author JBoss Inc.
 */
//
// Copyright (C) 1998, 1999, 2000, 2001, 2002, 2003
//
// Arjuna Technologies Ltd.,
// Newcastle upon Tyne,
// Tyne and Wear,
// UK.
//
// $Id: JDBCMatrixImpl02.java,v 1.2 2003/06/26 11:44:01 rbegg Exp $
//

package org.jboss.jbossts.qa.Hammer02Impls;

/*
 * Copyright (C) 1999-2001 by HP Bluestone Software, Inc. All rights Reserved.
 *
 * HP Arjuna Labs,
 * Newcastle upon Tyne,
 * Tyne and Wear,
 * UK.
 *
 * $Id: JDBCMatrixImpl02.java,v 1.2 2003/06/26 11:44:01 rbegg Exp $
 */

/*
 * Try to get around the differences between Ansi CPP and
 * K&R cpp with concatenation.
 */

/*
 * Copyright (C) 1999-2001 by HP Bluestone Software, Inc. All rights Reserved.
 *
 * HP Arjuna Labs,
 * Newcastle upon Tyne,
 * Tyne and Wear,
 * UK.
 *
 * $Id: JDBCMatrixImpl02.java,v 1.2 2003/06/26 11:44:01 rbegg Exp $
 */


import org.jboss.jbossts.qa.Hammer02.*;
import org.omg.CORBA.IntHolder;
import org.omg.CosTransactions.Control;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class JDBCMatrixImpl02 implements MatrixOperations
{
	public JDBCMatrixImpl02(int width, int height, String databaseURL, String databaseUser, String databasePassword, String databaseDynamicClass)
			throws InvocationException
	{
		_width = width;
		_height = height;
		_databaseUser = databaseUser;

		try
		{
			if (databaseDynamicClass != null)
			{
				_databaseURL = databaseURL;

				_databaseProperties = new Properties();
				_databaseProperties.put(com.arjuna.ats.jdbc.TransactionalDriver.userName, databaseUser);
				_databaseProperties.put(com.arjuna.ats.jdbc.TransactionalDriver.password, databasePassword);
				_databaseProperties.put(com.arjuna.ats.jdbc.TransactionalDriver.dynamicClass, databaseDynamicClass);
			}
			else
			{
				_databaseURL = databaseURL;
				_databaseUser = databaseUser;
				_databasePassword = databasePassword;
				_databaseProperties = null;
			}
		}
		catch (Exception exception)
		{
			System.err.println("JDBCMatrixImpl02.JDBCMatrixImpl02: " + exception);
			throw new InvocationException();
		}
	}

	public int get_width()
			throws InvocationException
	{
		return _width;
	}

	public int get_height()
			throws InvocationException
	{
		return _height;
	}

	public void get_value(int x, int y, IntHolder value, Control ctrl)
			throws InvocationException
	{
		if ((x < 0) || (x >= _width) || (y < 0) || (y >= _height))
		{
			throw new InvocationException(Reason.ReasonUnknown);
		}

		try
		{
			com.arjuna.ats.jts.ExplicitInterposition interposition = new com.arjuna.ats.jts.ExplicitInterposition();

			interposition.registerTransaction(ctrl);

			try
			{
				Connection connection;
				if (_databaseProperties != null)
				{
					connection = DriverManager.getConnection(_databaseURL, _databaseProperties);
				}
				else
				{
					connection = DriverManager.getConnection(_databaseURL, _databaseUser, _databasePassword);
				}

				Statement statement = connection.createStatement();

				ResultSet resultSet = statement.executeQuery("SELECT Value FROM " + _databaseUser + "_Matrix WHERE X = \'" + x + "\' AND Y = \'" + y + "\'");
				resultSet.next();
				value.value = resultSet.getInt("Value");
				if (resultSet.next())
				{
					throw new Exception();
				}
				resultSet.close();

				statement.close();
				connection.close();
			}
			catch (Exception exception)
			{
				System.err.println("JDBCMatrixImpl02.get_value: " + exception);

				interposition.unregisterTransaction();

				throw new InvocationException(Reason.ReasonUnknown);
			}
			catch (Error error)
			{
				System.err.println("JDBCMatrixImpl02.get_value: " + error);

				interposition.unregisterTransaction();

				throw new InvocationException(Reason.ReasonUnknown);
			}

			interposition.unregisterTransaction();
		}
		catch (InvocationException invocationException)
		{
			throw invocationException;
		}
		catch (Exception exception)
		{
			System.err.println("JDBCMatrixImpl02.get_value: " + exception);
			throw new InvocationException(Reason.ReasonUnknown);
		}
	}

	public void set_value(int x, int y, int value, Control ctrl)
			throws InvocationException
	{
		if ((x < 0) || (x >= _width) || (y < 0) || (y >= _height))
		{
			throw new InvocationException(Reason.ReasonUnknown);
		}

		try
		{
			com.arjuna.ats.jts.ExplicitInterposition interposition = new com.arjuna.ats.jts.ExplicitInterposition();

			interposition.registerTransaction(ctrl);

			try
			{
				Connection connection;
				if (_databaseProperties != null)
				{
					connection = DriverManager.getConnection(_databaseURL, _databaseProperties);
				}
				else
				{
					connection = DriverManager.getConnection(_databaseURL, _databaseUser, _databasePassword);
				}

				Statement statement = connection.createStatement();

				statement.executeUpdate("UPDATE " + _databaseUser + "_Matrix SET Value = \'" + value + "\' WHERE X = \'" + x + "\' AND Y = \'" + y + "\'");

				statement.close();
				connection.close();
			}
			catch (Exception exception)
			{
				System.err.println("JDBCMatrixImpl02.set_value: " + exception);

				interposition.unregisterTransaction();

				throw new InvocationException(Reason.ReasonUnknown);
			}

			catch (Error error)
			{
				System.err.println("JDBCMatrixImpl02.set_value: " + error);

				interposition.unregisterTransaction();

				throw new InvocationException(Reason.ReasonUnknown);
			}

			interposition.unregisterTransaction();
		}
		catch (InvocationException invocationException)
		{
			throw invocationException;
		}
		catch (Exception exception)
		{
			System.err.println("JDBCMatrixImpl02.set_value: " + exception);
			throw new InvocationException(Reason.ReasonUnknown);
		}
	}

	private int _width;
	private int _height;

	private String _databaseURL;
	private String _databaseUser;
	private String _databasePassword;
	private Properties _databaseProperties;
}
