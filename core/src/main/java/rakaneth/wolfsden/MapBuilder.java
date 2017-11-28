package rakaneth.wolfsden;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonWriter;

import squidpony.DataConverter;
import squidpony.squidgrid.mapping.SectionDungeonGenerator;
import squidpony.squidgrid.mapping.SerpentMapGenerator;

public final class MapBuilder
{
  private static String            fileName = "data/maps.js";
  private HashMap<String, MapBase> bases;
  public static final MapBuilder   instance = new MapBuilder();
  public HashMap<String, WolfMap>  maps;

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
    SerpentMapGenerator smg = new SerpentMapGenerator(base.width, base.height, WolfGame.rng, 0.2);
    SectionDungeonGenerator decorator = new SectionDungeonGenerator(base.width, base.height, WolfGame.rng);
    char[][] charMap, rawMap;
    smg.putBoxRoomCarvers(base.boxCarvers);
    smg.putRoundRoomCarvers(base.roundCarvers);
    smg.putCaveCarvers(base.caveCarvers);
    rawMap = smg.generate();
    decorator.addDoors(base.doors, base.doubleDoors);
    decorator.addLake(base.water);
    charMap = decorator.generate(rawMap, smg.getEnvironment());
    WolfMap raw = new WolfMap(charMap, id, base.dark, base.name);
    if (base.up != null)
      raw.makeUpStair(decorator.stairsUp);
    if (base.down != null)
      raw.makeDownStair(decorator.stairsDown);
    if (base.out != null)
      raw.makeOutStair(decorator.stairsUp);
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
    public int     width;
    public int     height;
    public boolean dark;
    public int     caveCarvers;
    public int     boxCarvers;
    public int     roundCarvers;
    public int     doors;
    public int     water;
    public boolean doubleDoors;
    public String  up;
    public String  down;
    public String  out;
    public String  name;
  }
}
