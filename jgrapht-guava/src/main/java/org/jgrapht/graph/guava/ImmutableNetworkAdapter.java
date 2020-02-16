/*
 * (C) Copyright 2018-2018, by Dimitrios Michail and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package org.jgrapht.graph.guava;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.util.TypeUtil;

import com.google.common.graph.Graphs;
import com.google.common.graph.ImmutableNetwork;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;

/**
 * A graph adapter class using Guava's {@link ImmutableNetwork}.
 * 
 * <p>
 * Since the underlying network is immutable, the resulting graph is unmodifiable.
 * 
 * <p>Example usage: <blockquote>
 * 
 * <pre>
 * MutableNetwork&lt;String, DefaultEdge&gt; mutableNetwork =
 *     NetworkBuilder.directed().allowsParallelEdges(true).allowsSelfLoops(true).build();
 *     
 * mutableNetwork.addNode("v1");
 * 
 * ImmutableNetworkGraph&lt;String, DefaultEdge&gt; immutableNetwork = ImmutableNetwork.copyOf(mutableNetwork);
 * 
 * Graph&lt;String, DefaultEdge&gt; graph =
 *     new ImmutableNetworkAdapter&lt;&gt;(immutableNetwork, DefaultEdge.class);
 * </pre>
 * 
 * </blockquote>
 * 
 * @author Dimitrios Michail
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 */
public class ImmutableNetworkAdapter<V, E>
    extends BaseNetworkAdapter<V, E, ImmutableNetwork<V, E>>
    implements Graph<V, E>, Cloneable, Serializable
{
    private static final long serialVersionUID = 8776276294297681092L;

    protected static final String GRAPH_IS_IMMUTABLE = "Graph is immutable";

    /**
     * Create a new network adapter.
     * 
     * @param network the immutable network
     * @param ef the edge factory of the new graph
     */
    public ImmutableNetworkAdapter(ImmutableNetwork<V, E> network, EdgeFactory<V, E> ef)
    {
        super(network, ef);
    }

    /**
     * Create a new network adapter.
     * 
     * @param network the immutable network
     * @param edgeClass class on which to base factory for edges
     */
    public ImmutableNetworkAdapter(ImmutableNetwork<V, E> network, Class<? extends E> edgeClass)
    {
        this(network, new ClassBasedEdgeFactory<>(edgeClass));
    }

    @Override
    public E addEdge(V sourceVertex, V targetVertex)
    {
        throw new UnsupportedOperationException(GRAPH_IS_IMMUTABLE);
    }

    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E e)
    {
        throw new UnsupportedOperationException(GRAPH_IS_IMMUTABLE);
    }

    @Override
    public boolean addVertex(V v)
    {
        throw new UnsupportedOperationException(GRAPH_IS_IMMUTABLE);
    }

    @Override
    public E removeEdge(V sourceVertex, V targetVertex)
    {
        throw new UnsupportedOperationException(GRAPH_IS_IMMUTABLE);
    }

    @Override
    public boolean removeEdge(E e)
    {
        throw new UnsupportedOperationException(GRAPH_IS_IMMUTABLE);
    }

    @Override
    public boolean removeVertex(V v)
    {
        throw new UnsupportedOperationException(GRAPH_IS_IMMUTABLE);
    }

    @Override
    public double getEdgeWeight(E e)
    {
        return Graph.DEFAULT_EDGE_WEIGHT;
    }

    @Override
    public void setEdgeWeight(E e, double weight)
    {
        throw new UnsupportedOperationException("Graph is unweighted");
    }

    @Override
    public GraphType getType()
    {
        return super.getType().asUnmodifiable();
    }

    /**
     * Returns a shallow copy of this graph instance. Neither edges nor vertices are cloned.
     *
     * @return a shallow copy of this graph.
     *
     * @throws RuntimeException in case the clone is not supported
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone()
    {
        try {
            ImmutableNetworkAdapter<V, E> newGraph = TypeUtil.uncheckedCast(super.clone());

            newGraph.edgeFactory = this.edgeFactory;
            newGraph.unmodifiableVertexSet = null;
            newGraph.unmodifiableEdgeSet = null;
            newGraph.network = ImmutableNetwork.copyOf(Graphs.copyOf(this.network));

            return newGraph;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private void writeObject(ObjectOutputStream oos)
        throws IOException
    {
        oos.defaultWriteObject();

        // write type
        oos.writeObject(getType());

        // write vertices
        int n = vertexSet().size();
        oos.writeInt(n);
        for (V v : vertexSet()) {
            oos.writeObject(v);
        }

        // write edges
        int m = edgeSet().size();
        oos.writeInt(m);
        for (E e : edgeSet()) {
            oos.writeObject(getEdgeSource(e));
            oos.writeObject(getEdgeTarget(e));
            oos.writeObject(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream ois)
        throws ClassNotFoundException, IOException
    {
        ois.defaultReadObject();

        GraphType type = (GraphType) ois.readObject();
        if (type.isMixed()) {
            throw new IOException("Graph type not supported");
        }

        MutableNetwork<V, E> mutableNetwork =
            (type.isDirected() ? NetworkBuilder.directed() : NetworkBuilder.undirected())
                .allowsParallelEdges(type.isAllowingMultipleEdges())
                .allowsSelfLoops(type.isAllowingSelfLoops()).build();

        // read vertices
        int n = ois.readInt();
        for (int i = 0; i < n; i++) {
            V v = (V) ois.readObject();
            mutableNetwork.addNode(v);
        }

        // read edges
        int m = ois.readInt();
        for (int i = 0; i < m; i++) {
            V s = (V) ois.readObject();
            V t = (V) ois.readObject();
            E e = (E) ois.readObject();
            mutableNetwork.addEdge(s, t, e);
        }

        // setup the immutable copy
        this.network = ImmutableNetwork.copyOf(mutableNetwork);
    }

}
