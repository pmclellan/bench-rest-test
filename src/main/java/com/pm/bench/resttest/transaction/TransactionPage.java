package com.pm.bench.resttest.transaction;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

public class TransactionPage
{
	private final int totalCount;
	private final int pageNumber;

	@NonNull
	private final List<Transaction> transactions;

	@JsonCreator
	public TransactionPage(
		@JsonProperty( "totalCount" ) final int totalCount,
		@JsonProperty( "page" ) final int pageNumber,
		@JsonProperty( "transactions" ) @Nullable final List<Transaction> transactions )
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
	
	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper( this )
						.add( "totalCount", totalCount )
						.add( "pageNumber", pageNumber )
						.add( "transactions", transactions )
						.toString();
	}
}