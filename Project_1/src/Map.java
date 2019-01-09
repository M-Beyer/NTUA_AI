/*
 * National Technical University of Athens
 * Artificial Intelligence, Project I
 *
 * Author: Michael Beyer
 *
 */

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;

import java.util.ArrayList;
import java.util.List;

public class Map
{
    private List<Road> roads;
    private List<Node> allNodes;
    private List<Node> goals;
    private Node       start;

    private MutableValueGraph<Node, Double> graph;

    private SearchTree searchTree;

    public Map( String[] start, List<String[]> goals, List<String[]> dataPoints )
    {
        Tuple<List<Node>, List<Road>> result = createMapNodes( dataPoints );
        this.allNodes = result.getFirst();
        this.roads = result.getSecond();

        this.searchTree = new SearchTree();
        searchTree.createSearchTree( this.allNodes );    // populate search tree with map nodes

        this.start = findStartNode( start, searchTree );
        this.goals = findGoalNodes( goals, searchTree );

        this.graph = createMap( this.allNodes, this.roads );
    }

    /**
     * Finds nearest node on map to provided start position.
     *
     * @param start:     String [latitude, longitude]
     * @param searchTree
     * @return start node on map
     */
    private Node findStartNode( String[] start, SearchTree searchTree )
    {
        Node node   = new Node( new Point( Double.valueOf( start[1] ), Double.valueOf( start[0] ) ), "" );
        Node result = searchTree.search( node, 1, this.allNodes ).get( 0 );
        result.setOptionalName( "Client" );
        return result;
    }

    /**
     * Finds nearest node on map for the provided goal positions
     *
     * @param goals:     String [latitude, longitude, roadName]
     * @param searchTree
     * @return List of goal nodes on the map
     */
    private List<Node> findGoalNodes( List<String[]> goals, SearchTree searchTree )
    {
        List<Node> result = new ArrayList<Node>();
        for ( String[] s : goals )
        {
            Node node  = new Node( new Point( Double.valueOf( s[1] ), Double.valueOf( s[0] ) ), s[2] );
            Node match = searchTree.search( node, 1, this.allNodes ).get( 0 );
            match.setOptionalName( node.getRoadId() );              // add taxi number
            result.add( match );
        }
        return result;
    }

    /**
     * Creates Nodes for all data points and adds them to
     * their corresponding roads.
     *
     * @param dataPoints String [latitude, longitude, roadName]
     * @return Tuple [allNodes, allRoads]
     */
    private Tuple<List<Node>, List<Road>> createMapNodes( List<String[]> dataPoints )
    {
        List<Node> allNodes = new ArrayList<Node>();
        List<Road> roads    = new ArrayList<Road>();

        Road currentRoad = new Road( dataPoints.get( 0 )[2] );

        for ( String[] item : dataPoints )
        {
            Point newPoint = new Point( Double.parseDouble( item[1] ), Double.parseDouble( item[0] ) );
            Node  newNode  = new Node( newPoint, currentRoad.getId() );

            allNodes.add( newNode );

            // item[2] is never empty -> skip in first iteration
            if ( item[2].equals( currentRoad.getName() ) )
            {

                currentRoad.appendNode( newNode );
                //Road.getAllRoads().get( Road.getAllRoads().size() - 1 ).appendNode( newNode );
            }
            else
            {
                roads.add( currentRoad );

                // create next road
                currentRoad = new Road( item[2] );
                currentRoad.appendNode( newNode );
            }
        }
        return new Tuple<List<Node>, List<Road>>( allNodes, roads );
    }

    /**
     * Creates a map with all roads connected
     *
     * @param allNodes
     * @param roads
     * @return undirected graph
     */
    private MutableValueGraph<Node, Double> createMap( List<Node> allNodes, List<Road> roads )
    {
        MutableValueGraph<Node, Double> g = ValueGraphBuilder.undirected().build();

        for ( Road r : roads )
        {
            // add nodes of current road to graph
            for ( int i = 0; i < r.getNodes().size() - 1; i++ )
            {
                Node   a    = r.getNodes().get( i );
                Node   b    = r.getNodes().get( i + 1 );
                double cost = Road.getCostBetweenNodes( a, b );

                g.putEdgeValue( a, b, cost );
            }
        }
        // add crossings
        ListMultimap<Point, Node> map = ArrayListMultimap.create( this.allNodes.size(), 10 );
        for ( Node n : allNodes )
        {
            map.put( n.getPoint(), n );
        }
        for ( Point p : map.keySet() )
        {
            List<Node> crossing = map.get( p );

            if ( crossing.size() > 1 )
            {
                Node n = crossing.get( 0 );

                for ( int i = 1; i < crossing.size(); i++ )
                {
                    Node   nextNode = crossing.get( i );
                    double cost     = Road.getCostBetweenNodes( n, nextNode );
                    g.putEdgeValue( n, nextNode, cost );
                }


                // testing
                /*
                for( Node a : crossing )
                {
                    for( Node b : crossing )
                    {
                        if( !a.equals( b ) )
                        {
                            double cost = Road.getCostBetweenNodes( a, b );
                            g.putEdgeValue( a, b, cost );
                        }
                    }
                }
                */
            }

        }

        return g;
    }

    private void findCrossings()
    {
        ListMultimap<Point, Node> map = ArrayListMultimap.create( this.allNodes.size(), 10 );

        for ( Node n : this.allNodes )
        {
            map.put( n.getPoint(), n );
        }

        for ( Point p : map.keySet() )
        {
            List<Node> crossing = map.get( p );

            int inner = crossing.size() - 1;
            int outer = inner - 1;

            for ( int i = 0; i < outer; i++ )
            {
                for ( int j = i + 1; j < inner; j++ )
                {
                    crossing.get( i ).getRoadId().substring( 1 );
                }
            }
        }
    }

    /**
     * Updates start and goal nodes for different search
     *
     * @param start: String [latitude, longitude]
     * @param goals: String [latitude, longitude, roadName]
     */
    public void changeStartAndGoal( String[] start, List<String[]> goals )
    {
        this.start = findStartNode( start, this.searchTree );
        this.goals = findGoalNodes( goals, this.searchTree );
    }

    public List<Road> getRoads()                      { return this.roads; }
    public List<Node> getAllNodes()                   { return this.allNodes; }
    public List<Node> getGoals()                      { return this.goals; }
    public Node       getStart()                      { return this.start; }
    public MutableValueGraph<Node, Double> getGraph() { return this.graph; }
}
