/*
 * National Technical University of Athens
 * Artificial Intelligence, Project I
 *
 * Author: Michael Beyer
 *
 */

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.graph.MutableValueGraph;

public class AStar
{
    private Comparator<AStarNode> compHeuristic = new ASNHeuristicComparator();
    private Comparator<AStarNode> compCost      = new ASNCostComparator();

    private HeuristicType heuristic;

    public enum HeuristicType
    {
        EuclideanDistance, Manhattan
    }

    public enum SearchType
    {
        strict,        // resulting path(s) will have same cost
        open           // returns up to 5 paths, higher cost is allowed
    }

    public AStar( HeuristicType heuristic )
    {
        this.heuristic = heuristic;
    }

    public List<AStarNode> findPaths( Node start, List<Node> target, MutableValueGraph<Node, Double> g, SearchType searchType )
    {
        List<AStarNode> searchFrontier = new ArrayList<AStarNode>();
        searchFrontier.add( new AStarNode( start, java.util.Collections.emptyList(), 0, 0, target ) );
        return search( searchFrontier, target, g, searchType );
    }

    private List<AStarNode> search( List<AStarNode> frontier, List<Node> target,
                                    MutableValueGraph<Node, Double> g, SearchType searchType )
    {
        SearchSets sets = new SearchSets( frontier, g.nodes().size() / 2, 10 );

        List<AStarNode> result = new ArrayList<AStarNode>();

        AStarNode current;

        boolean pathsFound = false;

        while ( !pathsFound && !sets.openSet.frontier.isEmpty() )
        {
            current = sets.openSet.frontier.remove( 0 );

            if ( !isGoal( current.getNodeVal(), target ) )
            {
                sets.closedSet.add( current );

                Set<Node> adjacent = g.adjacentNodes( current.getNodeVal() );

                for ( Node n : adjacent )
                {
                    double    costToNode = g.edgeValue( n, current.getNodeVal() ).get();
                    AStarNode newASNode  = new AStarNode( n, current.getPathToNode(), current.getCost(), costToNode, target );

                    sets.openSet.update( newASNode );
                }
            }
            else
            {
                result.add( current );
                result.sort( compCost );

                if ( searchType == SearchType.strict
                        && !sets.openSet.frontier.isEmpty()
                        && sets.openSet.frontier.get( 0 ).getHeuristicCost() > result.get( 0 ).getCost() )
                {
                    pathsFound = true;
                }
                else
                {
                    if ( result.size() > 5 )
                        pathsFound = true;
                }
            }
        }
        return result;
    }

    private boolean isGoal( Node n, List<Node> target )
    {
        for ( Node t : target )
        {
            if ( n.equals( t ) )
            {
                return true;
            }
            if ( n.getPoint().equals( t.getPoint() ) )
            {
                return true;
            }
        }
        return false;
    }


    private class SearchSets
    {
        OpenSet   openSet;
        ClosedSet closedSet;

        public SearchSets( List<AStarNode> frontier, int expectedEntriesForClosedSet, int valuesPerEntry )
        {
            this.openSet = new OpenSet( frontier );
            this.closedSet = new ClosedSet( expectedEntriesForClosedSet, valuesPerEntry );
        }

        private class OpenSet
        {
            List<AStarNode> frontier;

            public OpenSet( List<AStarNode> frontier )
            {
                this.frontier = frontier;
            }

            public void update( AStarNode node )
            {
                if ( SearchSets.this.closedSet.has( node ) )
                    return;

                Iterator<AStarNode> iter = frontier.iterator();

                boolean add = true;

                while ( iter.hasNext() )
                {
                    AStarNode current = iter.next();

                    boolean equal = current.equals( node );

                    if ( equal && current.getHeuristicCost() > node.getHeuristicCost() )
                    {
                        iter.remove();
                    }
                    if ( equal && current.getHeuristicCost() < node.getHeuristicCost() )
                        add = false;
                }

                if ( add )
                    this.frontier.add( node );

                this.frontier.sort( compHeuristic );
            }
        }

        private class ClosedSet
        {
            private ListMultimap<Point, AStarNode> closedSet;

            public ClosedSet( int expectedEntries, int valuesPerEntry )
            {
                this.closedSet = ArrayListMultimap.create( expectedEntries, valuesPerEntry );
            }

            public boolean has( AStarNode node )
            {
                return this.closedSet.containsEntry( node.getNodeVal().getPoint(), node );
            }

            public void add( AStarNode node )
            {
                this.closedSet.put( node.getNodeVal().getPoint(), node );
            }
        }
    }


    public class AStarNode
    {
        private Node   nodeVal;
        private double cost;
        private double heuristicCost;

        private List<Node> pathToNode = new ArrayList<Node>();

        public AStarNode( Node nodeVal, List<Node> pathToParent, double costToParent, double costToThisNode, List<Node> target )
        {
            this.nodeVal = nodeVal;
            this.pathToNode.addAll( pathToParent );
            this.pathToNode.add( nodeVal );
            this.cost = costToParent + costToThisNode;
            this.heuristicCost = cost + computeHeuristicCost( nodeVal, target );
        }

        public double computeHeuristicCost( Node current, List<Node> target )
        {
            switch ( AStar.this.heuristic )
            {
                case EuclideanDistance:
                    return computeEuclideanCost( current, target );

                case Manhattan:
                    return computeManhattanCost( current, target );

                default:
                    return -1.0;
            }
        }

        private double computeEuclideanCost( Node current, List<Node> target )
        {
            List<Double> cost = new ArrayList<Double>();
            for ( Node t : target )
            {
                double dLongitude = t.getPoint().getLongitude() - current.getPoint().getLongitude();
                double dLatitude  = t.getPoint().getLatitude() - current.getPoint().getLatitude();

                cost.add( Math.sqrt( Math.pow( dLongitude, 2.0 ) + Math.pow( dLatitude, 2.0 ) ) );
            }
            cost.sort( ( a, b ) -> Double.compare( a, b ) );
            return cost.get( 0 );
        }

        private double computeManhattanCost( Node current, List<Node> target )
        {
            List<Double> cost = new ArrayList<Double>();
            for ( Node t : target )
            {
                double dLongitude = Math.abs( t.getPoint().getLongitude() - current.getPoint().getLongitude() );
                double dLatitude  = Math.abs( t.getPoint().getLatitude() - current.getPoint().getLatitude() );

                cost.add( dLongitude + dLatitude );
            }
            cost.sort( ( a, b ) -> Double.compare( a, b ) );
            return cost.get( 0 );
        }

        @Override
        public boolean equals( Object obj )
        {
            boolean res = false;

            if ( obj instanceof AStarNode )
            {
                AStarNode temp = ( AStarNode ) obj;

                res = temp.getNodeVal().equals( this.getNodeVal() );
            }

            return res;
        }

        public Node       getNodeVal()        { return this.nodeVal; }
        public double     getCost()           { return this.cost; }
        public double     getHeuristicCost()  { return this.heuristicCost; }
        public List<Node> getPathToNode()     { return this.pathToNode; }
    }

    private class ASNHeuristicComparator implements Comparator<AStarNode>
    {
        @Override
        public int compare( AStarNode n1, AStarNode n2 )
        {
            return ( int ) ( n1.getHeuristicCost() - n2.getHeuristicCost() );
        }
    }

    private class ASNCostComparator implements Comparator<AStarNode>
    {
        @Override
        public int compare( AStarNode n1, AStarNode n2 )
        {
            return ( int ) ( n1.getCost() - n2.getCost() );
        }
    }

}
