package com.ribay.server.db;

import com.basho.riak.client.api.RiakCommand;
import com.basho.riak.client.core.FutureOperation;
import com.basho.riak.client.core.RiakFuture;

import java.util.concurrent.ExecutionException;

/**
 * Created by CD on 09.07.2016.
 */
public interface MyRiakClient {

    public <T, S> T execute(RiakCommand<T, S> command)
            throws ExecutionException, InterruptedException;

    public <V, S> RiakFuture<V, S> execute(FutureOperation<V, ?, S> operation) throws ExecutionException, InterruptedException;

    public <T, S> RiakFuture<T, S> executeAsync(RiakCommand<T, S> command);

}
