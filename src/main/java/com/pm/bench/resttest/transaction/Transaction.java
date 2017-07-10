package com.pm.bench.resttest.transaction;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * This class represents a single financial transaction.
 */
public class Transaction
{
	@Nonnull
	private final LocalDate transactionDate;

	@Nonnull
	private final BigDecimal amount;

	@JsonCreator
	public Transaction(
		@JsonProperty( value = "Date", required = true ) @Nonnull final LocalDate transactionDate,
		@JsonProperty( value = "Amount", required = true ) @Nonnull final BigDecimal amount )
	{
		this.transactionDate = requireNonNull( transactionDate );
		this.amount = requireNonNull( amount );
	}

	@Nonnull
	@JsonProperty( "Date" )
	public LocalDate getTransactionDate()
	{
		return transactionDate;
	}

	@Nonnull
	@JsonProperty( "Amount" )
	public BigDecimal getAmount()
	{
		return amount;
	}
	
	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper( this )
						.add( "transactionDate", transactionDate )
						.add( "amount", amount )
						.toString();
	}
}