package com.example.fbuteamproject.wrappers;


import com.example.fbuteamproject.utils.Config;

public class EntityWrapper {
    private Config.Entity entity;
    private EntityChangeListener listener;


    public Config.Entity getEntity(){
        return entity;
    }

    public void setEntity(Config.Entity entity){
        this.entity = entity;
        if (listener != null) listener.onEntityChanged();
    }

    public EntityChangeListener getListener(){
        return listener;
    }

    public void setListener(EntityChangeListener listener){
        this.listener = listener;
    }

    public interface EntityChangeListener {
        void onEntityChanged();
    }


}
