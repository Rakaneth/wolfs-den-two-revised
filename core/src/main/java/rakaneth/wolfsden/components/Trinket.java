package rakaneth.wolfsden.components;

import rakaneth.wolfsden.RKDice;

public class Trinket extends Equipment
{
	public Trinket()
	{
		super();
	}
	
	public Trinket(String name, String desc, RKDice atk, int def, RKDice dmg, int mov, int delay, int prot)
	{
		super(name, desc, atk, def, dmg, mov, delay, prot);
	}
}
