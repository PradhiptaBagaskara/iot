package bon.ruvio.pakansapi.model;

public class RootModel {
    public boolean isAuto;
    public boolean isNyala;

    public RootModel() {
    }
    public RootModel(boolean isAuto, boolean isNyala, boolean mStatus) {

        this.isAuto = isAuto;
        this.isNyala = isNyala;
        this.mStatus = mStatus;
    }


    public boolean isAuto() {
        return isAuto;
    }

    public void setAuto(boolean auto) {
        isAuto = auto;
    }

    public boolean isNyala() {
        return isNyala;
    }

    public void setNyala(boolean nyala) {
        isNyala = nyala;
    }

    public boolean ismStatus() {
        return mStatus;
    }

    public void setmStatus(boolean mStatus) {
        this.mStatus = mStatus;
    }

    public boolean mStatus;
}
