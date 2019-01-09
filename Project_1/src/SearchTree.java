/*
 * National Technical University of Athens
 * Artificial Intelligence, Project I
 *
 * Author: Michael Beyer
 *
 */

import java.util.ArrayList;
import java.util.List;

// Simon D.Levy's KDTree implementation
// https://home.wlu.edu/~levys/software/kd/
import edu.wlu.cs.levy.CG.KDTree;

public class SearchTree
{
    KDTree<String> tree = new KDTree<String>( 2 );

    SearchTree()
    { }

    public void createSearchTree( List<Node> nodes )
    {
        for ( Node n : nodes )
        {
            try
            {
                Point  p     = n.getPoint();
                double key[] = { p.getLatitude(), p.getLongitude() };

                tree.insert( key, n.getId() );
            }
            catch ( Exception e )
            {
                // KDTree wont allow double entries. In this scenario
                // this is ok, thus the exception is dropped
            }
        }
    }

    public List<Node> search( Node node, int n, List<Node> allNodes )
    {
        List<Node> result      = new ArrayList<Node>();
        double     searchKey[] = { node.getPoint().getLatitude(), node.getPoint().getLongitude() };

        try
        {
            List<String> set = tree.nearest( searchKey, n );

            allNodes.forEach( x ->
                              {
                                  if ( x.getId().equals( set.get( 0 ) ) )
                                      result.add( x );
                              }
            );
        }
        catch ( Exception e )
        {
            System.out.println( e );
        }

        return result;
    }

}
