package complexability.puremotionmusic.Helper;

/**
 * Created by Sorawis on 4/12/2016.
 */
public class Mapper {
    private String effect;
    private int motion;
    private String mapName;
    private boolean selected;
    public Mapper(int MotionId, String effectName, String mapperName){
        effect = effectName;
        motion = MotionId;
        mapName = mapperName;
        selected = false;
    }
    public void setEffect(String effect) {
        this.effect = effect;
    }

    public void setMotion(int motion) {
        this.motion = motion;
    }

    public int getMotion() {
        return motion;
    }

    public String getEffect() {
        return effect;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getMapName() {
        return mapName;
    }

    public boolean isSelected(){
        return selected;
    }
    public void select(){
        selected = true;
    }
    public void deselect(){
        selected = false;
    }
}
