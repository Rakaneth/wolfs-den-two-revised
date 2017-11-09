package rakaneth.wolfsden;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.graphics.Color;

import squidpony.squidgrid.gui.gdx.MapUtility;
import squidpony.squidgrid.mapping.DungeonUtility;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;

public class WolfMap
{
	public char[][]											baseMap;
	public char[][]											displayMap;
	public double[][]										resistanceMap;
	public String												id;
	public Color[][]										fgs;
	public Color[][]										bgs;
	private boolean											dark;
	public float[][]										bgFloats;
	public float[][]										fgFloats;
	public Map<Coord, Connection>				connections;
	public Coord												stairsDown;
	public Coord												stairsUp;
	public Coord												stairsOut;
	private static final Set<Character>	walkables	= new HashSet<Character>(
			Arrays.asList(new Character[] { '>', '<', '.', '\\', ',' }));

	public WolfMap(char[][] baseMap, String id, boolean dark)
	{
		this.baseMap = baseMap;
		this.id = id;
		int width = baseMap.length;
		int height = baseMap[0].length;
		displayMap = new char[width][height];
		bgs = MapUtility.generateDefaultBGColors(this.baseMap);
		fgs = MapUtility.generateDefaultColors(this.baseMap);
		bgFloats = new float[width][height];
		fgFloats = new float[width][height];
		char displayGlyph;
		for (int x = 0; x < baseMap.length; x++)
		{
			for (int y = 0; y < baseMap[x].length; y++)
			{
				switch (this.baseMap[x][y]) {
				case '#':
					displayGlyph = Swatch.CHAR_WALL;
					break;
				case '+':
					displayGlyph = Swatch.CHAR_CLOSED;
					break;
				case '\\':
					displayGlyph = Swatch.CHAR_OPEN;
					break;
				case '>':
					displayGlyph = Swatch.CHAR_DOWN;
					break;
				case '<':
					displayGlyph = Swatch.CHAR_UP;
					break;
				default:
					displayGlyph = Swatch.CHAR_FLOOR;
				}
				displayMap[x][y] = displayGlyph;
				bgFloats[x][y] = bgs[x][y].toFloatBits();
				fgFloats[x][y] = fgs[x][y].toFloatBits();
			}
		}
		resistanceMap = DungeonUtility.generateResistances(this.baseMap);
		this.dark = dark;
		connections = new HashMap<>();
	}

	public int getWidth()
	{
		return baseMap.length;
	}

	public int getHeight()
	{
		return baseMap[0].length;
	}

	public boolean isDark()
	{
		return dark;
	}

	public boolean isOOB(int x, int y)
	{
		return x < 0 || y < 0 || x >= getWidth() || y >= getHeight();
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
		return gr.singleRandom(WolfGame.rng);
	}

	public Coord getEmptyNear(Coord c, int radius)
	{
		// TODO: finish
		GreasedRegion gr = new GreasedRegion();
		return gr.singleRandom(WolfGame.rng);
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
		stairsUp = c;
	}

	public void makeDownStair(Coord c)
	{
		baseMap[c.x][c.y] = '>';
		displayMap[c.x][c.y] = Swatch.CHAR_DOWN;
		resistanceMap = DungeonUtility.generateResistances(this.baseMap);
		stairsDown = c;
	}

	public void makeOutStair(Coord c)
	{
		baseMap[c.x][c.y] = 'o';
		displayMap[c.x][c.y] = Swatch.CHAR_OUT;
		resistanceMap = DungeonUtility.generateResistances(this.baseMap);
		stairsOut = c;
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

	public void connect(Coord from, Coord to, WolfMap toMap)
	{
		connections.put(from, new Connection(to, toMap.id));
	}

	public Connection getConnection(Coord from)
	{
		return connections.get(from);
	}

	@Override
	public String toString()
	{
		return id;
	}

	public enum Stairs
	{
		DOWN, UP, OUT, NONE;

		public Stairs opp()
		{
			switch (this) {
			case DOWN:
				return UP;
			case UP:
				return DOWN;
			case OUT:
				return OUT;
			default:
				return NONE;
			}
		}
	}

	public class Connection
	{
		public Coord	toC;
		public String	mapID;

		public Connection(Coord to, String mapID)
		{
			toC = to;
			this.mapID = mapID;
		}
		
		public WolfMap getMap()
		{
			return MapBuilder.instance.maps.get(mapID);
		}
	}

}
