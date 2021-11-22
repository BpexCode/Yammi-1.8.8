package ru.yammi.module.misc;

import net.minecraft.entity.Entity;
import net.xtrafrancyz.covered.ObfValue;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.module.option.Option;
import ru.yammi.utils.Reflection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Hitboxes extends Module {

    private List<EntityData> entityDataList = new CopyOnWriteArrayList<>();

    public Hitboxes() {
        super("Hitboxes", Category.Misc, "Gavno");
        this.getOptions().add(new Option("Width", 0F, 10F));
        this.getOptions().add(new Option("Height", 0F, 10F));
    }

    @EventTarget
    public void onUpdate(TickEvent tickEvent) {
        float width = this.getOption("Width").getFloatValue();
        float height = this.getOption("Height").getFloatValue();

        if(width != 0.0F && height != 0.0F) {
            for (Entity entity : this.mc.theWorld.loadedEntityList) {
                if (entity != null && entity != this.mc.thePlayer) {
                    entity.width = width;
                    entity.height = height;
                    packConsts(entity);
                }
            }
        }
    }

    @Override
    public void onEnable() {
        for (Entity entity : this.mc.theWorld.loadedEntityList) {
            if (entity != null && entity != this.mc.thePlayer) {
                entityDataList.add(new EntityData(entity));
            }
        }
    }

    @Override
    public void onDisable() {
        for(EntityData entityData : entityDataList) {
            entityData.entity.width = entityData.width;
            entityData.entity.height = entityData.height;
            packConsts(entityData.entity);
        }

        entityDataList.clear();
    }

    private void packConsts(Entity entity){
        try {
            Field field = Reflection.getField(Entity.class, "OBFVAL_0");
            if(field != null) {
                ObfValue.OFloat oFloat = (ObfValue.OFloat) field.get(entity);
                oFloat.set(entity.width);

            }
            Field field1 = Reflection.getField(Entity.class, "OBFVAL_1");
            if(field1 != null) {
                ObfValue.OFloat oFloat = (ObfValue.OFloat) field1.get(entity);
                oFloat.set(entity.height);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class EntityData {
        private Entity entity;
        private float width;
        private float height;

        public EntityData(Entity entity) {
            this.entity = entity;
            this.width = entity.width;
            this.height = entity.height;
        }
    }
}
