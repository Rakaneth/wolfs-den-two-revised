package rakaneth.wolfsden;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonWriter;

import squidpony.DataConverter;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidgrid.mapping.styled.TilesetType;

public final class MapBuilder
{
	private static String						 fileName	= "data/maps.js";
	private HashMap<String, MapBase> maps;
	public static MapBuilder				 instance	= new MapBuilder();

	@SuppressWarnings("unchecked")
	private MapBuilder()
	{
		DataConverter converter = new DataConverter(JsonWriter.OutputType.javascript);
		maps = converter.fromJson(HashMap.class, MapBase.class, Gdx.files.internal(fileName));
	}

	public WolfMap buildMap(String id)
	{
		MapBase base = maps.get(id);
		DungeonGenerator dg = new DungeonGenerator(base.width, base.height, Game.rng);
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
		return new WolfMap(charMap, id, base.dark);
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
	}
}
