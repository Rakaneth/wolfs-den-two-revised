package rakaneth.wolfsden.components;

import java.util.Stack;

import com.badlogic.ashley.core.Component;

public class ActionStack implements Component
{
	public Stack<Object> cmds	= new Stack<>();
	public boolean			 tookTurn;
}
