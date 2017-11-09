package rakaneth.wolfsden;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonWriter;

import squidpony.DataConverter;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidgrid.mapping.styled.TilesetType;

public final class MapBuilder
{
	private static String						 fileName	= "data/maps.js";
	private HashMap<String, MapBase> bases;
	public static final MapBuilder	 instance	= new MapBuilder();
	public HashMap<String, WolfMap>	 maps;

	@SuppressWarnings("unchecked")
	private MapBuilder()
	{
		DataConverter converter = new DataConverter(JsonWriter.OutputType.javascript);
		bases = converter.fromJson(HashMap.class, MapBase.class, Gdx.files.internal(fileName));
		maps = new HashMap<String, WolfMap>();
	}

	public WolfMap buildMap(String id)
	{
		MapBase base = bases.get(id);
		DungeonGenerator dg = new DungeonGenerator(base.width, base.height, WolfGame.rng);
		TilesetType tt;
		char[][] charMap;
		switch (base.type) {
		case "closedDungeon":
			tt = TilesetType.LIMITED_CONNECTIVITY;
			break;
		case "openDungeon":
			tt = TilesetType.DEFAULT_DUNGEON;
			break;
		case "cave":
			tt = TilesetType.REFERENCE_CAVES;
			break;
		default:
			tt = TilesetType.DEFAULT_DUNGEON;
			break;
		}
		charMap = dg.addDoors(base.doors, base.doubleDoors)
								.addWater(base.water)
								.generate(tt);
		WolfMap raw = new WolfMap(charMap, id, base.dark);
		if (base.up != null)
			raw.makeUpStair(dg.stairsUp);
		if (base.down != null)
			raw.makeDownStair(dg.stairsDown);
		if (base.out != null)
			raw.makeOutStair(dg.stairsUp);
		maps.put(id, raw);
		return raw;
	}

	public void buildAll()
	{
		for (Map.Entry<String, MapBase> entry : bases.entrySet())
		{
			String id = entry.getKey();
			MapBase info = entry.getValue();
			WolfMap curMap, upMap, downMap, outMap;
			curMap = maps.get(id);
			if (curMap == null)
				curMap = buildMap(id);
			
			if (info.up != null) 
			{
				upMap = maps.get(info.up);
				if (upMap == null)
					upMap = buildMap(info.up);
				curMap.connect(curMap.stairsUp, upMap.stairsDown, upMap);
			}
			
			if (info.down != null) 
			{
				downMap = maps.get(info.down);
				if (downMap == null)
					downMap = buildMap(info.down);
				curMap.connect(curMap.stairsDown, downMap.stairsUp, downMap);
			}
			
			if (info.out != null) 
			{
				outMap = maps.get(info.out);
				if (outMap == null)
					outMap = buildMap(info.out);
				curMap.connect(curMap.stairsOut, outMap.getEmpty(), outMap);
			}
		}
	}

	private static class MapBase
	{
		public int		 width;
		public int		 height;
		public boolean dark;
		public String	 type;
		public int		 doors;
		public int		 water;
		public boolean doubleDoors;
		public String	 up;
		public String	 down;
		public String	 out;
	}
}
