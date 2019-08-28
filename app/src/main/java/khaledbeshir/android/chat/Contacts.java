package khaledbeshir.android.chat;

/**
 * Created by khaled on 7/5/2019.
 */

public class Contacts {

    private String name ;
    private String status;
    private String image ;

    public Contacts(){

    }


    public Contacts(String name, String status, String image) {
        this.name = name;
        this.status = status;
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getImage() {
        return image;
    }
}
