/*
 * National Technical University of Athens
 * Artificial Intelligence, Project I
 *
 * Author: Michael Beyer
 *
 */

import java.nio.file.Paths;
import java.util.List;


public class Main
{
    static protected String currentWorkingDirectory;

    public static void main( String[] args )
    {
        currentWorkingDirectory = Paths.get( "" ).toAbsolutePath().toString();

        if ( args.length == 4 )
        {
            runApp( args );
        }
        else
        {
            printUsage();
        }

    }

    private static void printUsage()
    {
        System.out.println( "Usage:\n" +
                                    "\tjava -jar AI_TaxiService.jar <arg1> <arg2> <arg3> <arg4>\n\n" +
                                    "\tArg1: Relative path to client csv file\n" +
                                    "\tArg2: Relative path to taxis csv file\n" +
                                    "\tArg3: Relative path to map nodes csv file\n" +
                                    "\tArg4: Output file name (without ending)\n\n" );
    }

    private static void runApp( String[] args )
    {
        CSV_Reader reader = new CSV_Reader();

        List<String[]> client = reader.read( currentWorkingDirectory + "/" + args[0], "," );
        List<String[]> taxi   = reader.read( currentWorkingDirectory + "/" + args[1], "," );
        List<String[]> nodes  = reader.read( currentWorkingDirectory + "/" + args[2], "," );

        client.remove( 0 );    // remove first row -> non data elements
        taxi.remove( 0 );
        nodes.remove( 0 );

        Map map = new Map( client.get( 0 ), taxi, nodes );

        AStar aStar = new AStar( AStar.HeuristicType.EuclideanDistance );

        List<AStar.AStarNode> result = aStar.findPaths( map.getStart(), map.getGoals(), map.getGraph(), AStar.SearchType.strict );

        if ( !result.isEmpty() )
        {
            KMLGenerator kml = new KMLGenerator();
            kml.generatePathKML( args[3], "Routes", result, map.getStart(), map.getGoals() );
            System.out.println( "Done. Result in /Routes/ folder." );
        }
        else
        {
            System.out.println( "Error. No path found!" );
        }
    }

}
