package rakaneth.wolfsden;

import java.util.HashMap;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.utils.JsonWriter;

import rakaneth.wolfsden.components.Armor;
import rakaneth.wolfsden.components.Drawing;
import rakaneth.wolfsden.components.EquipDoll;
import rakaneth.wolfsden.components.Identity;
import rakaneth.wolfsden.components.Mainhand;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Offhand;
import rakaneth.wolfsden.components.Position;
import rakaneth.wolfsden.components.Trinket;
import rakaneth.wolfsden.screens.PlayScreen;
import squidpony.DataConverter;

public class ItemBuilder
{
	private static final String itemFileName = "data/items.js";
	private static final String equipFileName = "data/equipment.js";
	private HashMap<String, ItemBase> consumables;
	private HashMap<String, EquipBase> equipment;
	private static int itemCounter = 1;
	private static int equipCounter = 1;
	
	@SuppressWarnings("unchecked")
	public ItemBuilder()
	{
		DataConverter converter = new DataConverter(JsonWriter.OutputType.javascript);
		consumables = converter.fromJson(HashMap.class, ItemBase.class, Gdx.files.internal(itemFileName));
		equipment = converter.fromJson(HashMap.class, EquipBase.class, Gdx.files.internal(equipFileName));
	}
	
	public Entity forge(String id, WolfMap map)
	{
		Engine engine = PlayScreen.instance.engine;
		EquipBase base = equipment.get(id);
		if (base == null)
			return null;
		
		String IDid = String.format("%s-%d", id, equipCounter++);
		Entity mold = engine.createEntity();
		mold.add(new Identity(base.name, IDid, base.desc));
		RKDice atk = base.atk == null ? new RKDice() : new RKDice(base.atk);
		RKDice dmg = base.dmg == null ? new RKDice() : new RKDice(base.dmg);
		switch(base.slot) {
		case ARMOR:
			mold.add(new Armor(atk, base.def, dmg, base.mov, base.delay, base.prot));
			break;
		case TRINKET:
			mold.add(new Trinket(atk, base.def, dmg, base.mov, base.delay, base.prot));
			break;
		case MH:
			mold.add(new Mainhand(atk, base.def, dmg, base.mov, base.delay, base.prot));
			break;
		case OH:
			mold.add(new Offhand(atk, base.def, dmg, base.mov, base.delay, base.prot));
			break;
		}
		if (map != null) 
		{
			mold.add(new Drawing(base.glyph, Colors.get(base.color)));
			mold.add(new Position(map.getEmpty(), map));
		}
		engine.addEntity(mold);
		return mold;
	}
	
	public Entity forge(String id)
	{
		return forge(id, null);
	}
	
	
	private static class ItemBase
	{
		public String id;
		public String name;
		public String desc;
		public int stack;
		public ItemType iType;
		public float value;
		public char glyph;
		public String color;
		
		public enum ItemType
		{
			REPAIR, FOOD, BUFF;
		}
	}
	
	public void equip(Entity wielder, String itemID)
	{
		EquipDoll eqd = Mapper.equipets.get(wielder);
		if (eqd == null)
			return;
		
		EquipBase base = equipment.get(itemID);
		if (base == null)
			return;
		
		Entity item = forge(itemID);
		switch (base.slot) {
		case MH:
			eqd.mh = item;
			break;
		case OH:
			eqd.oh = item;
			break;
		case TRINKET:
			eqd.trinket = item;
			break;
		case ARMOR:
			eqd.armor = item;
			break;
		}
	}
	
	private static class EquipBase
	{
		public String id;
		public String name;
		public String desc;
		public String atk;
		public String dmg;
		public int def;
		public int prot;
		public int mov;
		public int delay;
		public Slot slot;
		public boolean dig;
		public int rarity;
		public char glyph;
		public String color;
		
		
		public enum Slot
		{
			ARMOR, TRINKET, MH, OH;
		}
	}
}


