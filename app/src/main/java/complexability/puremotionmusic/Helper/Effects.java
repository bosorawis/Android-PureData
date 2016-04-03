package complexability.puremotionmusic.Helper;

/**
 * Created by Sorawis on 4/3/2016.
 */
public class Effects {
        private int id;
        private float defaultVal;
        private String name;

        public Effects(int effectId, String effectName, float defaultValue){
            id = effectId;
            name = effectName;
            defaultVal = defaultValue;
        }
        public String getName(){
            return name;
        }
        public int getId(){
            return id;
        }
        public float getDefaultVal(){
            return defaultVal;
        }

}
