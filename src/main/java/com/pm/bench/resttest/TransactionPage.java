package com.pm.bench.resttest;

import java.util.ArrayList;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

public class TransactionPage
{
	private final int totalCount;
	private final int pageNumber;

	@NonNull
	private final List<Transaction> transactions;

	@JsonCreator
	public TransactionPage(
		@JsonProperty( "totalCount" ) int totalCount,
		@JsonProperty( "page" ) int pageNumber,
		@JsonProperty( "transactions" ) @Nullable List<Transaction> transactions )
	{
		this.totalCount = totalCount;
		this.pageNumber = pageNumber;
		this.transactions =
				transactions == null ? ImmutableList.of() : ImmutableList.copyOf( transactions );
	}

	@JsonProperty( "totalCount" )
	public int getTotalCount()
	{
		return totalCount;
	}

	@JsonProperty( "page" )
	public int getPageNumber()
	{
		return pageNumber;
	}

	@JsonProperty( "transactions" )
	public List<Transaction> getTransactions()
	{
		return transactions;
	}
	
	public static void main(String[] args ) throws Exception
	{
		ObjectMapper mapper = new ObjectMapper();
		System.out.println( mapper.writeValueAsString( new TransactionPage( 5, 7, new ArrayList<>() ) ) );
	}
}