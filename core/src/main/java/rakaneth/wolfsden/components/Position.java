package rakaneth.wolfsden.components;

import com.badlogic.ashley.core.Component;

import squidpony.squidmath.Coord;

public class Position implements Component {
	public Coord pos;
	public String mapID;
	
	public Position(Coord startPoint, String mapID)
	{
		pos = startPoint;
		this.mapID = mapID;
	}
}
