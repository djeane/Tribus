package apptribus.com.tribus.pojo;

/**
 * Created by User on 7/15/2017.
 */

public class Video {
    private String description;
    private int size;
    private String duration;
    private boolean isUploaded;
    private boolean isDownloaded;
    private String downloadUri;

    public Video() {
    }

    public Video(String descriprion, int size, String duration, boolean isUploaded, boolean isDownloaded, String downloadUri) {
        this.description = descriprion;
        this.size = size;
        this.duration = duration;
        this.isUploaded = isUploaded;
        this.isDownloaded = isDownloaded;
        this.downloadUri = downloadUri;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
