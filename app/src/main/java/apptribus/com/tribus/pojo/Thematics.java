package apptribus.com.tribus.pojo;

/**
 * Created by User on 4/25/2018.
 */

public class Thematics {

    private String keyTribu;
    private String tribuUniqueName;

    public Thematics(){

    }

    public Thematics(String keyTribu, String tribuUniqueName) {
        this.keyTribu = keyTribu;
        this.tribuUniqueName = tribuUniqueName;
    }


    public String getKeyTribu() {
        return keyTribu;
    }

    public void setKeyTribu(String keyTribu) {
        this.keyTribu = keyTribu;
    }

    public String getTribuUniqueName() {
        return tribuUniqueName;
    }

    public void setTribuUniqueName(String tribuUniqueName) {
        this.tribuUniqueName = tribuUniqueName;
    }
}
