package ru.yammi.utils;

import com.google.common.base.Throwables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.Sys;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class ShaderUtils {

    private static Minecraft mc = Minecraft.getMinecraft();

    private static Field listShadersField;
    private static Method loadShaderMethod;

    private int ticks = 0;
    private float progress = 0.0F;

    public ShaderUtils(){ }

    public void onRender(){
        if(ticks != 41) {
            ticks++;
            progress += 0.05F;
        }

        ShaderGroup sg = this.mc.entityRenderer.getShaderGroup();
        try {
            @SuppressWarnings("unchecked")
            List<Shader> shaders = ShaderUtils.getShaders(sg);
            if(shaders!= null) {
                for (Shader s : shaders) {
                    ShaderUniform su = s.getShaderManager().getShaderUniform("Progress");

                    if (su != null) {
                        su.set(progress / 5.0F);
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            Throwables.propagate(e);
        }
    }

    public void disable(){
        ticks = 0;
        progress = 0.0F;
        if(mc.entityRenderer.isShaderActive())
            mc.entityRenderer.switchUseShader();
    }

    public void enable(){
        EntityRenderer er = mc.entityRenderer;
        if (!er.isShaderActive()) {
            ShaderUtils.loadShader(new ResourceLocation("blurShader"));
        } else if (er.isShaderActive()) {
            er.switchUseShader();
        }
    }

    public static void loadShader(ResourceLocation resourcePath) {
       try {
           loadShaderMethod.invoke(Minecraft.getMinecraft().entityRenderer, new Object[] {resourcePath});
       } catch (Exception e) {
           e.printStackTrace();
       }
    }

    public static List<Shader> getShaders(ShaderGroup shaderGroup){
        try {
            return (List<Shader>)listShadersField.get(shaderGroup);
        } catch (Exception e) {}
        return null;
    }

    static {
        try {
            listShadersField = Reflection.getField(ShaderGroup.class, "listShaders", "d");
            listShadersField.setAccessible(true);

            loadShaderMethod = Reflection.getMethod(EntityRenderer.class, new Class[] {ResourceLocation.class}, "loadShader", "a");
            loadShaderMethod.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
