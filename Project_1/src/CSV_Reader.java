/*
 * National Technical University of Athens
 * Artificial Intelligence, Project I
 *
 * Author: Michael Beyer
 *
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSV_Reader
{
    public CSV_Reader()
    { }

    public List<String[]> read( String path, String separator )
    {
        try ( BufferedReader br = new BufferedReader( new FileReader( path ) ) )
        {
            String         line   = "";
            List<String[]> result = new ArrayList<String[]>();

            while ( ( line = br.readLine() ) != null )
            {

                String data[] = line.split( separator );
                result.add( data );
            }
            return result;
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }

        return null;
    }
}
