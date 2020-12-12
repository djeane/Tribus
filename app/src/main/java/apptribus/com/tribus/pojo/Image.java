package apptribus.com.tribus.pojo;

/**
 * Created by User on 7/20/2017.
 */

public class Image {

    private String description;
    private int size;
    private boolean isUploaded;
    private boolean isDownloaded;
    private String downloadUri;

    public Image() {
    }

    public Image(String description, int size, boolean isUploaded, boolean isDownloaded, String downloadUri) {
        this.description = description;
        this.size = size;
        this.isUploaded = isUploaded;
        this.isDownloaded = isDownloaded;
        this.downloadUri = downloadUri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public String getDownloadUri() {
        return downloadUri;
    }

    public void setDownloadUri(String downloadUri) {
        this.downloadUri = downloadUri;
    }
}
