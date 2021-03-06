package com.bibek.engine2d;

import java.util.HashMap;

// Entity is a list of components
public class Entity {
    // Store the components in a hash-map where the class of component is the key
    private HashMap<Class, Component> mComponents = new HashMap<>();

    // Add a component
    public <T extends Component> void addComponent(T component) {
        mComponents.put(component.getClass(), component);
    }

    // Check if the entity contains a component of given class
    public <T extends Component> boolean hasComponent(Class<T> componentClass) {
        return mComponents.containsKey(componentClass);
    }

    // Get a component from a class
    public  <T extends Component> T getComponent(Class<T> componentClass) {
        return componentClass.cast(mComponents.get(componentClass));
    }
}
