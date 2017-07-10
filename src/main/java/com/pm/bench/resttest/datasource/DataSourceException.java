package com.pm.bench.resttest.datasource;

/**
 * Used to define exceptions relating to retrieval of transaction data from its source.
 */
@SuppressWarnings( "serial" )
public class DataSourceException extends RuntimeException
{
	public DataSourceException( final String errorMessage )
	{
		super( errorMessage );
	}
	
	public DataSourceException( final String errorMessage, final Throwable cause )
	{
		super( errorMessage, cause );
	}
}