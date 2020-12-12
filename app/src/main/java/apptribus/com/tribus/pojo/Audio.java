package apptribus.com.tribus.pojo;

/**
 * Created by User on 8/10/2017.
 */

public class Audio {

    //private int size;
    //private String duration;
    private boolean isUploaded;
    private boolean isDownloaded;
    private String downloadUri;
    private String duration;

    public Audio() {
    }

    public Audio(boolean isUploaded, boolean isDownloaded, String downloadUri) {
        this.isUploaded = isUploaded;
        this.isDownloaded = isDownloaded;
        this.downloadUri = downloadUri;
    }

    public Audio(boolean isUploaded, boolean isDownloaded, String downloadUri, String duration) {
        this.isUploaded = isUploaded;
        this.isDownloaded = isDownloaded;
        this.downloadUri = downloadUri;
        this.duration = duration;
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
