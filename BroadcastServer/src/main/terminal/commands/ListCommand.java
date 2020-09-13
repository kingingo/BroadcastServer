package main.terminal.commands;


import java.util.ArrayList;

import main.Main;
import main.client.Client;
import main.terminal.CommandExecutor;

public class ListCommand implements CommandExecutor{

	@Override
	public void onCommand(String[] args) {
		StringBuilder builder = new StringBuilder();
		ArrayList<Client> list = Main.server.getClients();
		builder.append("["+list.size()+"] ");
		
		int length = 0;
		String add;
		for(int i = 0; i < list.size(); i++) {
			String name = list.get(i).getName()+"("+list.get(i).getPingManager().getCurrentPing()+"ms)";
			if(i == list.size()-1){
				add = name;
			}else if(i == list.size()-2) {
				add = name+" & ";
			}else add = name+",";
			
			length+=add.length();
			builder.append(add);
			
			if(length > 25) {
				builder.append("\n");
			}
		}
		
		System.out.println(builder.toString());
	}

}
