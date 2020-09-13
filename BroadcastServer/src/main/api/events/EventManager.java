package main.api.events;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;

public class EventManager {
	@Getter
	public static HashMap<EventListener,HashMap<Integer,ArrayList<Method>>> handlers = new HashMap<EventListener, HashMap<Integer,ArrayList<Method>>>();
	
	//unregestiert die Class
	public static boolean unregister(Class c){
		System.out.println("UNREGISTER "+c.getSimpleName());
		EventListener listener;
		for(int i = 0; i<handlers.size(); i++) {
			listener = ((EventListener) handlers.keySet().toArray()[i]);
			if( listener.getClass().equals(c)){
				handlers.remove(listener);
				return true;
			}
		}
		return false;
	}
	
	public static boolean unregister(EventListener listener) {
		System.out.println("UNREGISTER1 "+listener.getClass().getSimpleName());
		if(handlers.containsKey(listener)) {
			handlers.remove(listener);
			return true;
		}
		return false;
	}

	//Regestriert die Class mit den Events
	public static void register(EventListener listener){
		if(handlers.containsKey(listener))return;
    	if(!handlers.containsKey(listener))handlers.put(listener, new HashMap<Integer,ArrayList<Method>>());
		//Sucht alle Methoden in dem Listener raus
		Method[] methods = listener.getClass().getDeclaredMethods();
        for (int i = 0; i < methods.length; ++i) {
        	//Filtert alle EventHandler raus!
            EventHandler eventHandler = methods[i].getAnnotation(EventHandler.class);
            
            if (eventHandler != null) {
            	//Fügt ihn zur Liste hinzu
            	if(!handlers.get(listener).containsKey(eventHandler.priority().getPriority()))handlers.get(listener).put(eventHandler.priority().getPriority(), new ArrayList<Method>());
            	handlers.get(listener).get(eventHandler.priority().getPriority()).add(methods[i]);
            }
        }
	}
	
	//Feuert das Event ab
	public static void callEvent(final Event event) {
		EventListener listener;
        for (int a = 0; a < handlers.size(); a++) {
        	listener = (EventListener) handlers.keySet().toArray()[a];
            for (int i = 0; i<EventPriority.values().length; i++) {
            	
	            	if(getHandlers().get(listener) == null) {
	            		System.out.println(listener.getClass().getSimpleName()+" doesn't have an array?!");
	            		break;
	            	}
            	
            		if(getHandlers().get(listener).containsKey(i) && !getHandlers().get(listener).get(i).isEmpty()){
            			for(Method method : getHandlers().get(listener).get(i)){
            				if (!event.getClass().getSimpleName().equals(method.getParameterTypes()[0].getSimpleName())) continue;
                            try {
                                method.invoke(listener, new Object[]{event});
                            } catch (Exception e) {
                            	System.out.println("Method: "+method.getName()+"\nEvent: "+event.getClass().getSimpleName()+"\nListener: "+listener.getClass().getSimpleName()+"\n");
                                e.printStackTrace();
                            }
            			}
            		}
                }
            }
    }
}
