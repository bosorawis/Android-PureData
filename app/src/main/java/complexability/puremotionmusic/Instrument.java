package complexability.puremotionmusic;

/**
 * Created by Sorawis on 3/20/2016.
 */
public class Instrument {
    private String effects[];
    private int effectIds[];
    private float val[];


    public String[] getEffects(){
        return effects;
    }
    public int[] getEffectIds(){
        return effectIds;
    }
    public int getEffectId(int id){
        return effectIds[id];
    }
    public String getEffect(int id){
        return effects[id];
    }

    public void setEffects(String items[]){
        System.arraycopy(items, 0, effects, 0, effects.length);
    }

    public void setEffectIds(int ids[]){
        System.arraycopy(ids, 0, effectIds, 0, effectIds.length);
    }
    public void setAllVal(float data[]){
        System.arraycopy(data, 0, val, 0, val.length);
    }
    public void setVal(float data, int id){
        val[id] = data;
    }

    public Instrument(String allEffects[], int allIds[], float defaultVal[]){
        setEffects(allEffects);
        setEffectIds(allIds);
        setAllVal(defaultVal);
    }

}
