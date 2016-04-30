package complexability.puremotionmusic.Helper;

/**
 * Created by turbo on 4/30/2016.
 */
public class Kalman {
    private float Q_angle = 0.001f;
    private float Q_bias = 0.003f;
    private float R_measure = 0.03f;

    private float angle; // Reset the angle
    private float bias; // Reset bias
    private float[][] P = new float[2][2]; // Error covariance matrix - This is a 2x2 matrix




    public float getAngle(float newAngle, float newRate, float dt){

    }

    public void setAngle(float angle){
        // Used to set angle, this should be set as the starting angle


    }
    public float getRate() {
        // Return the unbiased rate
    }
    /* These are used to tune the Kalman filter */
    public void setQangle(float Q_angle){

    }
    public void setQbias(float Q_bias){

    }
    public void setRmeasure(float R_measure){

    }

    public float getQangle(){

    }
    public float getQbias(){

    }
    public float getRmeasure(){

    }


}
