/*
 * ============================================================================
 * GNU Lesser General Public License
 * ============================================================================
 *
 * JasperReports - Free Java report-generating library.
 * Copyright (C) 2001-2009 JasperSoft Corporation http://www.jaspersoft.com
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307, USA.
 * 
 * JasperSoft Corporation
 * 539 Bryant Street, Suite 100
 * San Francisco, CA 94107
 * http://www.jaspersoft.com
 */
package net.sf.jasperreports.olap;

import java.util.Map;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.query.JREmptyQueryExecuter;
import net.sf.jasperreports.engine.query.JRQueryExecuter;
import net.sf.jasperreports.engine.query.JRQueryExecuterFactory;
import net.sf.jasperreports.olap.xmla.JRXmlaQueryExecuter;
import net.sf.jasperreports.olap.xmla.JRXmlaQueryExecuterFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JRMdxQueryExecuterFactory implements JRQueryExecuterFactory
{
	
	private static final Log log = LogFactory.getLog(JRMdxQueryExecuterFactory.class);
	
	private final static Object[] MDX_BUILTIN_PARAMETERS;
	
	static
	{
		Object[] mondrianParams = new JRMondrianQueryExecuterFactory().getBuiltinParameters();
		Object[] xmlaParams = new JRXmlaQueryExecuterFactory().getBuiltinParameters();
		
		MDX_BUILTIN_PARAMETERS = new Object[mondrianParams.length + xmlaParams.length];
		System.arraycopy(mondrianParams, 0, MDX_BUILTIN_PARAMETERS, 0, mondrianParams.length);
		System.arraycopy(xmlaParams, 0, MDX_BUILTIN_PARAMETERS, mondrianParams.length, xmlaParams.length);
	}
	
	public Object[] getBuiltinParameters()
	{
		return MDX_BUILTIN_PARAMETERS;
	}

	public JRQueryExecuter createQueryExecuter(JRDataset dataset, Map parameters) throws JRException
	{
		JRQueryExecuter queryExecuter;
		if (getParameterValue(parameters, JRMondrianQueryExecuterFactory.PARAMETER_MONDRIAN_CONNECTION) != null)
		{
			queryExecuter = new JRMondrianQueryExecuter(dataset, parameters);
		}
		else if (getParameterValue(parameters, JRXmlaQueryExecuterFactory.PARAMETER_XMLA_URL) != null)
		{
			queryExecuter = new JRXmlaQueryExecuter(dataset, parameters);
		}
		else
		{
			log.warn("No Mondrian connection or XMLA URL set for MDX query");
			queryExecuter = new JREmptyQueryExecuter();
		}
		return queryExecuter;
	}

	protected final Object getParameterValue(Map valueParams, String name)
	{
		JRValueParameter valueParam = (JRValueParameter) valueParams.get(name);
		return valueParam == null ? null : valueParam.getValue();
	}
	
	public boolean supportsQueryParameterType(String className)
	{
		return true;
	}

}
