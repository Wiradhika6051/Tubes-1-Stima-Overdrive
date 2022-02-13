package za.co.entelect.challenge.enums;

import com.google.gson.annotations.SerializedName;

public enum Speed {
    @SerializedName("MINIMUM_SPEED")
    MINIMUM_SPEED,
    @SerializedName("SPEED_STATE_1")
    SPEED_STATE_1,
    @SerializedName("INITIAL_SPEED")
    INITIAL_SPEED,
    @SerializedName("SPEED_STATE_2")
    SPEED_STATE_2,
    @SerializedName("SPEED_STATE_3")
    SPEED_STATE_3,
    @SerializedName("MAXIMUM_SPEED")
    MAXIMUM_SPEED,
    @SerializedName("BOOST_SPEED")
    BOOST_SPEED;

    public Speed getSpeedState(int dmg,Boolean isBoosting){
        if(isBoosting){
            return BOOST_SPEED;
        }
            switch (dmg) {
                case 0:
                    return MAXIMUM_SPEED;
                case 1:
                    return SPEED_STATE_3;
                default:
                    return INITIAL_SPEED;
            }
    }
}
