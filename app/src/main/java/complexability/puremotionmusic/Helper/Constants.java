package complexability.puremotionmusic.Helper;

/**
 * Created by Sorawis on 4/3/2016.
 */
public class Constants {

    public static final int REQUEST_CODE_BUTTONS = 1;
    public static final int REQUEST_CODE_3_BUTTONS = 2;
    public static final int REQUEST_CODE_ITEMS = 3;
    public static final int REQUEST_CODE_ICON_ITEMS = 4;
    public static final int REQUEST_CODE_SINGLE_CHOICE_LIST = 5;
    public static final int REQUEST_CODE_ADAPTER = 6;
    public static final int REQUEST_CODE_VIEW = 7;
    public static final int REQUEST_CODE_EDIT_TEXT = 8;


    public static final int VOLUME      = 0;
    public static final int NOTE        = 1;
    public static final int REVERB      = 2;
    public static final int TREMOLO     = 3;

    Effects volume = new Effects(VOLUME, "Volume", (float) 0.5);
    Effects note   = new Effects(NOTE, "Note", (float) 50);
    Effects reverb = new Effects(REVERB, "Reverb", (float) 0.5);

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
