/*
 * National Technical University of Athens
 * Artificial Intelligence, Project I
 * 
 * Author: Michael Beyer
 * 
 */

public class Point
{
	private double longitude;
	private double latitude;

	public Point( double latitude, double longitude )
	{
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	@Override
	public boolean equals( Object obj )
	{
		if( this == obj )
			return true;
		if( obj == null )
			return false;
		if( this.getClass() != obj.getClass() )
			return false;
		
		Point other = (Point) obj;
		return ( this.latitude == other.getLatitude() )
				&& ( this.longitude == other.getLongitude() );
	}

	@Override
	public int hashCode()
	{
		double res = this.longitude;
		res = 31 * res * this.latitude;
		return Double.valueOf( res ).hashCode();
	}

	public double getLongitude() { return this.longitude; }
	public double getLatitude()  { return this.latitude; }
}
