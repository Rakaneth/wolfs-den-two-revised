package rakaneth.wolfsden.components;

import com.badlogic.ashley.core.Component;

import rakaneth.wolfsden.CommandTypes;

public class Player implements Component
{
	public CommandTypes cmdType;
	public Object cmdParam;
	public boolean tookTurn;
}
