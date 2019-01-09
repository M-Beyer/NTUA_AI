/*
 * National Technical University of Athens
 * Artificial Intelligence, Project I
 *
 * Author: Michael Beyer
 *
 */

public class Node
{
    static private int        counter  = 0;

    private String id;
    private Point  point;
    private String roadId;
    private String optionalName;

    public Node( Point point, String roadId )
    {
        this.point = point;
        this.roadId = roadId;
        this.id = "n" + counter;
        Node.counter++;
    }

    @Override
    public boolean equals( Object obj )
    {
        boolean res = false;

        if ( obj instanceof Node )
        {
            Node temp = ( Node ) obj;

            res = temp.id.equals( this.id );
        }

        return res;
    }

    public String getId()                        { return this.id; }
    public Point  getPoint()                     { return this.point; }
    public String getRoadId()                    { return this.roadId; }
    public String getOptionalName()              { return this.optionalName; }
    public void   setOptionalName( String name ) { this.optionalName = name; }

}
