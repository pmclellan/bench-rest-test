package com.pm.bench.resttest.datasource;

/**
 * Used to define exceptions caused by malformed transaction data.
 */
@SuppressWarnings( "serial" )
public class MalformedDataException extends DataSourceException
{
	public MalformedDataException( final String errorMessage )
	{
		super( errorMessage );
	}
	
	public MalformedDataException( final String errorMessage, final Throwable cause )
	{
		super( errorMessage, cause );
	}
}