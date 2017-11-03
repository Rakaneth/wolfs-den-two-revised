package rakaneth.wolfsden.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

import rakaneth.wolfsden.Swatch;
import squidpony.squidgrid.gui.gdx.MapUtility;
import squidpony.squidgrid.mapping.DungeonUtility;

public class WolfMap implements Component
{
	public char[][]		baseMap;
	public char[][]		displayMap;
	public double[][]	resistanceMap;
	public Color[][]	bgColors;
	public Color[][]	fgColors;
	public String id;

	public WolfMap(char[][] baseMap, String id)
	{
		this.baseMap = baseMap;
		this.id = id;
		displayMap = new char[baseMap.length][baseMap[0].length];
		for (int x = 0; x < baseMap.length; x++)
		{
			for (int y = 0; y < baseMap[x].length; y++)
			{
				if (this.baseMap[x][y] == '#')
					displayMap[x][y] = Swatch.CHAR_WALL;
				else
					displayMap[x][y] = Swatch.CHAR_FLOOR;
			}
		}
		resistanceMap = DungeonUtility.generateResistances(this.baseMap);
		bgColors = MapUtility.generateDefaultBGColors(this.baseMap);
		fgColors = MapUtility.generateDefaultColors(this.baseMap);
	}

	public int getWidth()
	{
		return baseMap.length;
	}

	public int getHeight()
	{
		return baseMap[0].length;
	}

}
