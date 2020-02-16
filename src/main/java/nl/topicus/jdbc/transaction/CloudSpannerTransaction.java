package nl.topicus.jdbc.transaction;

import java.sql.SQLException;
import java.util.List;

import com.google.cloud.Timestamp;
import com.google.cloud.spanner.BatchClient;
import com.google.cloud.spanner.BatchReadOnlyTransaction;
import com.google.cloud.spanner.BatchTransactionId;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.Key;
import com.google.cloud.spanner.KeySet;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.Options.QueryOption;
import com.google.cloud.spanner.Options.ReadOption;
import com.google.cloud.spanner.Partition;
import com.google.cloud.spanner.PartitionOptions;
import com.google.cloud.spanner.ReadOnlyTransaction;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.SpannerException;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.Struct;
import com.google.cloud.spanner.TimestampBound;
import com.google.cloud.spanner.TransactionContext;
import com.google.rpc.Code;

import nl.topicus.jdbc.CloudSpannerConnection;
import nl.topicus.jdbc.exception.CloudSpannerSQLException;

/**
 * An abstraction of transactions on Google Cloud Spanner JDBC connections.
 * 
 * @author loite
 *
 */
public class CloudSpannerTransaction implements TransactionContext, BatchReadOnlyTransaction
{
	public static class TransactionException extends RuntimeException
	{
		private static final long serialVersionUID = 1L;

		private TransactionException(String message, SQLException cause)
		{
			super(message, cause);
		}
	}

	private TransactionThread transactionThread;

	private ReadOnlyTransaction readOnlyTransaction;

	private BatchReadOnlyTransaction batchReadOnlyTransaction;

	private DatabaseClient dbClient;

	private BatchClient batchClient;

	private CloudSpannerConnection connection;

	public CloudSpannerTransaction(DatabaseClient dbClient, BatchClient batchClient, CloudSpannerConnection connection)
	{
		this.dbClient = dbClient;
		this.batchClient = batchClient;
		this.connection = connection;
	}

	public boolean isRunning()
	{
		return batchReadOnlyTransaction != null || readOnlyTransaction != null || transactionThread != null;
	}

	public boolean hasBufferedMutations()
	{
		return transactionThread != null && transactionThread.hasBufferedMutations();
	}

	public void begin() throws SQLException
	{
		if (connection.isBatchReadOnly())
		{
			if (batchReadOnlyTransaction == null)
			{
				batchReadOnlyTransaction = batchClient.batchReadOnlyTransaction(TimestampBound.strong());
			}
		}
		else if (connection.isReadOnly())
		{
			if (readOnlyTransaction == null)
			{
				readOnlyTransaction = dbClient.readOnlyTransaction();
			}
		}
		else
		{
			if (transactionThread == null)
			{
				transactionThread = new TransactionThread(dbClient);
				transactionThread.start();
			}
		}
	}

	public Timestamp commit() throws SQLException
	{
		Timestamp res = null;
		try
		{
			if (connection.isBatchReadOnly())
			{
				if (batchReadOnlyTransaction != null)
				{
					batchReadOnlyTransaction.close();
				}
			}
			else if (connection.isReadOnly())
			{
				if (readOnlyTransaction != null)
				{
					readOnlyTransaction.close();
				}
			}
			else
			{
				if (transactionThread != null)
				{
					res = transactionThread.commit();
				}
			}
		}
		finally
		{
			transactionThread = null;
			readOnlyTransaction = null;
			batchReadOnlyTransaction = null;
		}
		return res;
	}

	public void rollback() throws SQLException
	{
		try
		{
			if (connection.isBatchReadOnly())
			{
				if (batchReadOnlyTransaction != null)
				{
					batchReadOnlyTransaction.close();
				}
			}
			else if (connection.isReadOnly())
			{
				if (readOnlyTransaction != null)
				{
					readOnlyTransaction.close();
				}
			}
			else
			{
				if (transactionThread != null)
				{
					transactionThread.rollback();
				}
			}
		}
		finally
		{
			transactionThread = null;
			readOnlyTransaction = null;
			batchReadOnlyTransaction = null;
		}
	}

	@FunctionalInterface
	private static interface TransactionAction
	{
		public void apply(String xid) throws SQLException;
	}

	public void prepareTransaction(String xid) throws SQLException
	{
		checkTransaction();
		preparedTransactionAction(xid, transactionThread::prepareTransaction);
	}

	public void commitPreparedTransaction(String xid) throws SQLException
	{
		checkTransaction();
		preparedTransactionAction(xid, transactionThread::commitPreparedTransaction);
	}

	public void rollbackPreparedTransaction(String xid) throws SQLException
	{
		checkTransaction();
		preparedTransactionAction(xid, transactionThread::rollbackPreparedTransaction);
	}

	private void preparedTransactionAction(String xid, TransactionAction action) throws SQLException
	{
		try
		{
			if (connection.isReadOnly())
			{
				throw new CloudSpannerSQLException(
						"Connection is in read-only mode and cannot be used for prepared transactions",
						Code.FAILED_PRECONDITION);
			}
			else
			{
				action.apply(xid);
			}
		}
		finally
		{
			transactionThread = null;
			readOnlyTransaction = null;
			batchReadOnlyTransaction = null;
		}
	}

	private void checkTransaction()
	{
		if (transactionThread == null && readOnlyTransaction == null && batchReadOnlyTransaction == null)
		{
			try
			{
				begin();
			}
			catch (SQLException e)
			{
				throw new TransactionException("Failed to start new transaction", e);
			}
		}
	}

	@Override
	public void buffer(Mutation mutation)
	{
		checkTransaction();
		if (transactionThread == null)
			throw new IllegalStateException("Mutations are not allowed in read-only mode");
		transactionThread.buffer(mutation);
	}

	@Override
	public void buffer(Iterable<Mutation> mutations)
	{
		checkTransaction();
		if (transactionThread == null)
			throw new IllegalStateException("Mutations are not allowed in read-only mode");
		transactionThread.buffer(mutations);
	}

	@Override
	public ResultSet executeQuery(Statement statement, QueryOption... options)
	{
		checkTransaction();
		if (batchReadOnlyTransaction != null)
			return batchReadOnlyTransaction.executeQuery(statement, options);
		else if (readOnlyTransaction != null)
			return readOnlyTransaction.executeQuery(statement, options);
		else if (transactionThread != null)
			return transactionThread.executeQuery(statement);

		throw new IllegalStateException("No transaction found (this should not happen)");
	}

	@Override
	public ResultSet read(String table, KeySet keys, Iterable<String> columns, ReadOption... options)
	{
		return null;
	}

	@Override
	public ResultSet readUsingIndex(String table, String index, KeySet keys, Iterable<String> columns,
			ReadOption... options)
	{
		return null;
	}

	@Override
	public Struct readRow(String table, Key key, Iterable<String> columns)
	{
		return null;
	}

	@Override
	public Struct readRowUsingIndex(String table, String index, Key key, Iterable<String> columns)
	{
		return null;
	}

	@Override
	public ResultSet analyzeQuery(Statement statement, QueryAnalyzeMode queryMode)
	{
		return null;
	}

	/**
	 * Close method is needed for the interface, but does not do anything
	 */
	@Override
	public void close()
	{
		// no-op as there is nothing to close or throw away
	}

	@Override
	public Timestamp getReadTimestamp()
	{
		if (batchReadOnlyTransaction != null)
			return batchReadOnlyTransaction.getReadTimestamp();
		if (readOnlyTransaction != null)
			return readOnlyTransaction.getReadTimestamp();
		return null;
	}

	@Override
	public List<Partition> partitionRead(PartitionOptions partitionOptions, String table, KeySet keys,
			Iterable<String> columns, ReadOption... options) throws SpannerException
	{
		return null;
	}

	@Override
	public List<Partition> partitionReadUsingIndex(PartitionOptions partitionOptions, String table, String index,
			KeySet keys, Iterable<String> columns, ReadOption... options) throws SpannerException
	{
		return null;
	}

	@Override
	public List<Partition> partitionQuery(PartitionOptions partitionOptions, Statement statement,
			QueryOption... options) throws SpannerException
	{
		checkTransaction();
		if (batchReadOnlyTransaction != null)
		{
			return batchReadOnlyTransaction.partitionQuery(partitionOptions, statement, options);
		}
		return null;
	}

	@Override
	public ResultSet execute(Partition partition) throws SpannerException
	{
		checkTransaction();
		if (batchReadOnlyTransaction != null)
		{
			return batchReadOnlyTransaction.execute(partition);
		}
		return null;
	}

	@Override
	public BatchTransactionId getBatchTransactionId()
	{
		checkTransaction();
		if (batchReadOnlyTransaction != null)
		{
			return batchReadOnlyTransaction.getBatchTransactionId();
		}
		return null;
	}

	public BatchReadOnlyTransaction getBatchReadOnlyTransaction()
	{
		return batchReadOnlyTransaction;
	}

}
