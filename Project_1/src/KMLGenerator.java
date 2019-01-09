/*
 * National Technical University of Athens
 * Artificial Intelligence, Project I
 *
 * Author: Michael Beyer
 *
 */

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileWriter;
import java.util.List;


public class KMLGenerator
{
    public KMLGenerator() {}

    public void generatePathKML( String fileName, String layerName,
                                 List<AStar.AStarNode> pathList,
                                 Node start, List<Node> target )
    {
        // create root node
        Element   kml = new Element( "kml");
        Namespace ns  = Namespace.getNamespace( "http://www.opengis.net/kml/2.2" );
        kml.setNamespace( ns );
        Document doc = new Document( kml);

        // document node
        Element subRoot = new Element("Document");
        Element nameTag = new Element("name");
        nameTag.addContent( layerName );
        subRoot.addContent( nameTag );

        // add line styles
        subRoot.addContent( Style.Blue.createLineStyleElement() );
        subRoot.addContent( Style.LightBlue.createLineStyleElement() );
        subRoot.addContent( Style.Green.createLineStyleElement() );
        subRoot.addContent( Style.DarkGray.createLineStyleElement() );

        // add pin style
        subRoot.addContent( Style.Orange.createPinStyleElement() );
        subRoot.addContent( Style.Blue.createPinStyleElement() );

        // create Routes
        // iterate backwards -> otherwise first route (e.g. fastest route) will
        // be layered behind the existing routes
        for( int i = pathList.size() - 1; i >= 0; i-- )
        {
            Element placemark = new Element("Placemark");

            Element name = new Element("name");
            name.setText( "Route " + String.valueOf(i + 1) );
            placemark.addContent( name );

            if( i == 0 )
                placemark.addContent( Style.Green.createLineStyleUrlElement() );
            else
                placemark.addContent( Style.DarkGray.createLineStyleUrlElement() );

            Element lineString = new Element("LineString");
            Element altitudeMode = new Element("altitudeMode");
            altitudeMode.setText( "relative" );

            lineString.addContent( altitudeMode );

            // coordinates (format: longitude latitude)
            Element coordinates = new Element("coordinates");

            for( Node n: pathList.get(i).getPathToNode() )
            {
                Point p = n.getPoint();
                coordinates.addContent( p.getLongitude() + ", " + p.getLatitude() + "\n" );
            }

            lineString.addContent( coordinates );

            placemark.addContent( lineString );
            subRoot.addContent( placemark );
        }

        // add goal markers
        for( Node n : target )
        {
            subRoot.addContent( createPin( n, Style.Blue ) );
        }

        // add start marker
        subRoot.addContent( createPin( start, Style.Orange ) );

        doc.getRootElement().addContent( subRoot );

        try
        {
            XMLOutputter xmlOutput = new XMLOutputter();

            xmlOutput.setFormat( Format.getPrettyFormat() );

            File diectory = new File( Main.currentWorkingDirectory + "/Routes" );
            if( !diectory.exists() )
                diectory.mkdir();

            xmlOutput.output( doc, new FileWriter(Main.currentWorkingDirectory + "/Routes/" + fileName + ".kml") );
        }
        catch( Exception e )
        {
            System.out.println( e );
        }
    }

    private Element createPin( Node n, Style style )
    {
        Element placemark = new Element( "Placemark" );
        Element name = new Element( "name" );
        name.setText( n.getOptionalName() );
        placemark.addContent( name );

        placemark.addContent( style.createPinStyleUrlElement() );

        Element point = new Element( "Point" );
        Element coord = new Element( "coordinates" );
        coord.addContent( n.getPoint().getLongitude() + ", " + n.getPoint().getLatitude() + "\n" );

        point.addContent( coord );
        placemark.addContent( point );

        return placemark;
    }

    private enum Style
    {
        Red( "red", "ff0000ff", "https://www.google.com/intl/en_us/mapfiles/ms/icons/red-dot.png" ),
        Blue( "blue", "50F00014", "https://www.google.com/intl/en_us/mapfiles/ms/icons/blue-dot.png"),
        Green( "green", "5014F000", "https://www.google.com/intl/en_us/mapfiles/ms/icons/green-dot.png" ),
        Orange( "orange", "501478FF", "https://www.google.com/intl/en_us/mapfiles/ms/icons/orange-dot.png" ),
        DarkGray( "darkGray", "50464646","" ),
        LightBlue( "lightBlue", "50FF7800", "" );

        private String name;
        private String colorId;
        private String pinUrl;

        Style(String name, String colorId, String pinUrl )
        {
            this.name = name;
            this.colorId = colorId;
            this.pinUrl = pinUrl;
        }

        private Element createLineStyleElement()
        {
            Element style = new Element("Style");
            style.setAttribute( "id", "line" + this.name );

            Element lineStyle = new Element("LineStyle");
            Element color = new Element("color");
            color.setText( this.colorId );
            Element width = new Element("width");
            width.setText( "4" );

            lineStyle.addContent( color );
            lineStyle.addContent( width );
            style.addContent( lineStyle );
            return style;
        }

        private Element createLineStyleUrlElement()
        {
            Element styleUrl = new Element("styleUrl");
            styleUrl.setText( "#line" + this.name );
            return styleUrl;
        }

        private Element createPinStyleElement()
        {
            Element style = new Element("Style" );
            style.setAttribute( "id", "pin" + this.name );

            Element pinStyle = new Element( "IconStyle" );
            Element icon = new Element( "Icon" );
            Element href = new Element( "href" );
            href.setText( this.pinUrl );

            icon.addContent( href );

            pinStyle.addContent( icon );
            style.addContent( pinStyle );
            return style;
        }

        private Element createPinStyleUrlElement()
        {
            Element styleUrl = new Element("styleUrl");
            styleUrl.setText( "#pin" + this.name );
            return styleUrl;
        }
    }

}
