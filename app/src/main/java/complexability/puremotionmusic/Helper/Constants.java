package complexability.puremotionmusic.Helper;

/**
 * Created by Sorawis on 4/3/2016.
 */
public class Constants {
    public static final int VOLUME      = 0;
    public static final int NOTE        = 1;
    public static final int REVERB      = 2;
    public static final int TREMOLO     = 3;

    Effects volume = new Effects(VOLUME, "volume", (float) 0.5);
    Effects note   = new Effects(NOTE, "note", (float) 50);
    Effects reverb = new Effects(REVERB, "reverb", (float) 0.5);

    public Effects getEffect(int id){
        switch (id) {
            case VOLUME:
                return volume;
            case NOTE:
                return note;
            case REVERB:
                return reverb;
            default:
               return null;
        }
    }

}
