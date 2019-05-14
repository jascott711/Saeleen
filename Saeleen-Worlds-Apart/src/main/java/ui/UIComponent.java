package ui;

import java.awt.Color;
import java.awt.Graphics;

/**
 * PlayerHUD
 */
public class UIComponent {
    
    public float posX = 0;
    public float posY = 0;

    protected Color hb100 = new Color(0,206,110); //healthbar 100%
	protected Color mb100 = new Color(0,163,225); //manabar 100%
	protected Color mb50 = new Color(127,0,110); //manabar 50%
	protected Color mb0 = new Color(190,0,82); //manabar 0%
    protected Color xb100 = new Color(255,244,104); //xpbar 100%
    
    public UIComponent(){

    }

    public UIComponent(float posX, float posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public float getPosX() {
        return this.posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return this.posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    
    public void update(float deltaTime) {
    }
    
    public void render(Graphics g) {
    }

}