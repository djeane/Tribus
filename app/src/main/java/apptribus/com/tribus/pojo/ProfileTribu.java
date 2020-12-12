package apptribus.com.tribus.pojo;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

/**
 * Created by User on 6/3/2017.
 */
@IgnoreExtraProperties
public class ProfileTribu {

    private String nameTribu;
    private String uniqueName;
    private String description;
    private String thematic;
    private String imageUrl;
    private String smallUrl;
    private Date creationDate;
    private long numRecommendations;
    private long numFollowers;
    private long numComments;
    private boolean isPublic;
    private String thumb;

    public ProfileTribu() {
    }

    public ProfileTribu(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public ProfileTribu(String nameTribu, String uniqueName, String description, String thematic, String imageUrl,
                        Date creationDate, int numRecommendations, int numFollowers, int numComments, boolean isPublic) {
        this.nameTribu = nameTribu;
        this.uniqueName = uniqueName;
        this.description = description;
        this.thematic = thematic;
        this.imageUrl = imageUrl;
        this.creationDate = creationDate;
        this.numRecommendations = numRecommendations;
        this.numFollowers = numFollowers;
        this.numComments = numComments;
        this.isPublic = isPublic;
    }

    public String getNameTribu() {
        return nameTribu;
    }

    public void setNameTribu(String nameTribu) {
        this.nameTribu = nameTribu;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThematic() {
        return thematic;
    }

    public void setThematic(String thematic) {
        this.thematic = thematic;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public long getNumRecommendations() {
        return numRecommendations;
    }

    public void setNumRecommendations(long numRecommendations) {
        this.numRecommendations = numRecommendations;
    }

    public long getNumFollowers() {
        return numFollowers;
    }

    public void setNumFollowers(long numFollowers) {
        this.numFollowers = numFollowers;
    }

    public long getNumComments() {
        return numComments;
    }

    public void setNumComments(long numComments) {
        this.numComments = numComments;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}
