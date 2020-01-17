package bon.ruvio.pakansapi.model;

public class SettingModel {
    public int lamaNyala;

    public SettingModel(int lamaNyala) {
        this.lamaNyala = lamaNyala;
    }

    public SettingModel() {
    }

    public int getLamaNyala() {
        return lamaNyala;
    }

    public void setLamaNyala(int lamaNyala) {
        this.lamaNyala = lamaNyala;
    }
}
