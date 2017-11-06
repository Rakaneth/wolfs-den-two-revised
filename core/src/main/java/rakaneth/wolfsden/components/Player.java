package rakaneth.wolfsden.components;

import java.util.Stack;

import com.badlogic.ashley.core.Component;

import rakaneth.wolfsden.CommandTypes;
import squidpony.squidmath.OrderedMap;

public class Player implements Component
{
	public Stack<Object> cmds	= new Stack<>();
	public boolean			 tookTurn;
}
