package uk.co.carelesslabs.ui;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import uk.co.carelesslabs.Media;
import uk.co.carelesslabs.OnlineEvoGame;

public class SquareMenu extends Menu {
    public BuildMenu build;
    
    public SquareMenu(final ApplicationAdapter game){
        super(0, 0, 2, Media.squareMenu);
        
        int scale = 2;
        addButtons(3, 2, 2, Media.pinkButton, Media.selector, scale);
        
        Button btn = buttons.get(0);
        btn.setOnClickListener(
                new OnClickListener(){
                    @Override
                    public void onClick(Button b) {
                        
                    }
                });
        
        btn = buttons.get(1);
        btn.icon = Media.iconSettings;
        btn.setOnClickListener(
                new OnClickListener(){
                    @Override
                    public void onClick(Button b) {
                        System.out.println("Settings.");
                    }
                });
        
        btn = buttons.get(2);
        btn.icon = Media.iconResources;
        btn.setOnClickListener(
                new OnClickListener(){
                    @Override
                    public void onClick(Button b) {
                    	((OnlineEvoGame) game).getInput().setInventory(true);
                    }
                });
        
        btn = buttons.get(3);
        btn.icon = Media.iconBuild;
        buttons.get(3).setOnClickListener(
                new OnClickListener(){
                    @Override
                    public void onClick(Button b) {
                        build.toggleActive();
                        System.out.println("click inventory!");
                    }
                });
        
        // BUILDING
        build = new BuildMenu(pos.x + width, 0, 2, Media.mainBack);  
    }
    
    @Override
    public void draw(SpriteBatch batch){
        super.draw(batch);
        build.draw(batch);
    }
    
    @Override
    public void checkHover(Vector2 pos) {
        super.checkHover(pos);
        build.  checkHover(pos);
    }
    
}