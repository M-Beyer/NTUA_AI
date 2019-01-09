/*
 * National Technical University of Athens
 * Artificial Intelligence, Project I
 *
 * Author: Michael Beyer
 *
 */

import java.util.List;
import java.util.ArrayList;


public class Road
{
    private String id;
    private String name;
    private List<Node> nodes = new ArrayList<Node>();

    static private List<Road> allRoads = new ArrayList<Road>();
    static private int        counter  = 0;

    public Road( String name )
    {
        this.name = name;
        this.id = "r" + counter;
        Road.counter++;
        Road.allRoads.add( this );
    }

    public void appendNode( Node node )
    {
        nodes.add( node );
    }

    static public double getCostBetweenNodes( Node a, Node b )
    {
        Point pa = a.getPoint();
        Point pb = b.getPoint();
        double dLongitude = pa.getLongitude() - pb.getLongitude();
        double dLatitude = pa.getLatitude() - pb.getLatitude();

        return Math.sqrt( Math.pow( dLongitude, 2.0 ) + Math.pow( dLatitude, 2.0 ) );
    }

    public String     getId()    { return this.id; }
    public String     getName()  { return this.name; }
    public List<Node> getNodes() { return this.nodes; }
}
