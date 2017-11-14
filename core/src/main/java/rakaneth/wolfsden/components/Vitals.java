package rakaneth.wolfsden.components;

import com.badlogic.ashley.core.Component;

public class Vitals implements Component
{
	public boolean alive;
	public int vit;
	public int maxVit;
	public int end;
	public int maxEnd;
	public int XP;
	public int totXP;
	
	public Vitals()
	{
		alive = true;
	}
}
