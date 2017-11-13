package rakaneth.wolfsden.components;

import java.util.Arrays;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import rakaneth.wolfsden.RKDice;

public class EquipDoll implements Component
{
	public Entity armor;
	public Entity mh;
	public Entity trinket;
	public Entity oh;
	
	public int totalProt()
	{
		int ap = Mapper.armors.get(armor).prot;
		int tp = Mapper.trinkets.get(trinket).prot;
		int mp = Mapper.mainhands.get(mh).prot;
		int op = Mapper.offhands.get(oh).prot;
		return ap + tp + mp + op;
	}
	
	public RKDice totalAtk()
	{
		RKDice acc = new RKDice();
		acc = acc.add(Mapper.armors.get(armor).atk);
		acc = acc.add(Mapper.trinkets.get(trinket).atk);
		acc = acc.add(Mapper.mainhands.get(mh).atk);
		acc = acc.add(Mapper.offhands.get(oh).atk);
		return acc;
	}
	
	public RKDice totalDmg()
	{
		RKDice acc = new RKDice();
		acc = acc.add(Mapper.armors.get(armor).dmg);
		acc = acc.add(Mapper.trinkets.get(trinket).dmg);
		acc = acc.add(Mapper.mainhands.get(mh).dmg);
		acc = acc.add(Mapper.offhands.get(oh).dmg);
		return acc;
	}
	
	public int totalDef()
	{
		int ad = Mapper.armors.get(armor).def;
		int td = Mapper.trinkets.get(trinket).def;
		int md = Mapper.mainhands.get(mh).def;
		int od = Mapper.offhands.get(oh).def;
		return ad + td + md + od;
	}
	
	public int totalMov()
	{
		int am = Mapper.armors.get(armor).mov;
		int tm = Mapper.trinkets.get(trinket).mov;
		int mm = Mapper.mainhands.get(mh).mov;
		int om = Mapper.offhands.get(oh).mov;
		int[] movs = new int[] {am, tm, mm, om};
		Arrays.sort(movs);
		return movs[movs.length-1];
	}
	
	public int totalDelay()
	{
		int am = Mapper.armors.get(armor).delay;
		int tm = Mapper.trinkets.get(trinket).delay;
		int mm = Mapper.mainhands.get(mh).delay;
		int om = Mapper.offhands.get(oh).delay;
		int[] dlys = new int[] {am, tm, mm, om};
		Arrays.sort(dlys);
		return dlys[dlys.length-1];
	}
}
