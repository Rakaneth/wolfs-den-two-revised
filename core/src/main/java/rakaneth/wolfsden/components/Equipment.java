package rakaneth.wolfsden.components;

import com.badlogic.ashley.core.Component;

import rakaneth.wolfsden.RKDice;

public abstract class Equipment implements Component
{
	public int atk;
	public int def;
	public RKDice dmg;
	public int mov;
	public int delay;
	
	public Equipment()
	{
		atk = 0;
		def = 0;
		dmg = new RKDice("0k0");
		mov = 0;
		delay = 0;
	}
	
	public Equipment(int atk, int def, RKDice dmg, int mov, int delay)
	{
		this.atk = atk;
		this.def = def;
		this.dmg = dmg;
		this.mov = mov;
		this.delay = delay;
	}
}
