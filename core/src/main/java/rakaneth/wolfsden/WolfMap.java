package rakaneth.wolfsden;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import squidpony.squidgrid.mapping.DungeonUtility;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;

public class WolfMap
{
	public char[][]											baseMap;
	public char[][]											displayMap;
	public double[][]										resistanceMap;
	public String												id;
	public int[][]											fgIndices;
	public int[][]											bgIndices;
	private static final Set<Character>	walkables	= new HashSet<Character>(
			Arrays.asList(new Character[] { '>', '<', '.', '\\', ',' }));

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
		bgIndices = DungeonUtility.generateBGPaletteIndices(this.baseMap);
		fgIndices = DungeonUtility.generatePaletteIndices(this.baseMap);
	}

	public int getWidth()
	{
		return baseMap.length;
	}

	public int getHeight()
	{
		return baseMap[0].length;
	}

	public boolean isOOB(Coord c)
	{
		return c.x < 0 || c.y < 0 || c.x >= getWidth() || c.y >= getHeight();
	}

	public boolean isPassable(Coord c)
	{
		char t = baseMap[c.x][c.y];
		return !isOOB(c) && walkables.contains(t);
	}

	public void carve(Coord c)
	{
		baseMap[c.x][c.y] = '.';
		displayMap[c.x][c.y] = Swatch.CHAR_FLOOR;
		resistanceMap = DungeonUtility.generateResistances(this.baseMap);
	}

	public void wall(Coord c)
	{
		baseMap[c.x][c.y] = '#';
		displayMap[c.x][c.y] = Swatch.CHAR_WALL;
		resistanceMap = DungeonUtility.generateResistances(this.baseMap);
	}

	public Coord getEmpty()
	{
		GreasedRegion gr = new GreasedRegion(baseMap, '.');
		return gr.singleRandom(Game.rng);
	}

	public Coord getEmptyNear(Coord c, int radius)
	{
		//TODO: finish
		GreasedRegion gr = new GreasedRegion();
		return gr.singleRandom(Game.rng);
	}

	public Coord getEmptyNear(Coord c)
	{
		return getEmptyNear(c, 1);
	}

	public void makeUpStair(Coord c)
	{
		baseMap[c.x][c.y] = '<';
		displayMap[c.x][c.y] = Swatch.CHAR_UP;
		resistanceMap = DungeonUtility.generateResistances(this.baseMap);
	}

	public void makeDownStair(Coord c)
	{
		baseMap[c.x][c.y] = '>';
		displayMap[c.x][c.y] = Swatch.CHAR_DOWN;
		resistanceMap = DungeonUtility.generateResistances(this.baseMap);
	}
	
	public void makeOutStair(Coord c)
	{
		baseMap[c.x][c.y] = 'o';
		displayMap[c.x][c.y] = Swatch.CHAR_OUT;
		resistanceMap = DungeonUtility.generateResistances(this.baseMap);
	}

	public Stairs getStair(Coord c)
	{
		char t = baseMap[c.x][c.y];
		switch (t) {
		case '>':
			return Stairs.DOWN;
		case '<':
			return Stairs.UP;
		case 'o':
			return Stairs.OUT;
		default:
			return Stairs.NONE;
		}
	}
	
	public enum Stairs
	{
		DOWN,
		UP,
		OUT,
		NONE;
	}

}
