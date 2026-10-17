package com.winlator.renderer.effects;

import com.winlator.renderer.material.ScreenMaterial;

public abstract class Effect {
    private ScreenMaterial material;

    protected ScreenMaterial createMaterial() {
        return null;
    }

    public ScreenMaterial getMaterial() {
        if (material == null) material = createMaterial();
        return material;
    }

    /** The owning EGL context was replaced; never delete its names in the new context. */
    public void forgetMaterial() { material = null; }

    public void destroyMaterial() {
        if (material != null) material.destroy();
        material = null;
    }
}
