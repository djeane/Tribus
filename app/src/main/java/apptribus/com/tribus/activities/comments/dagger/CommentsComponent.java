package apptribus.com.tribus.activities.comments.dagger;

import apptribus.com.tribus.activities.comments.CommentsActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 7/24/2017.
 */
@CommentsScope
@Component(modules = {CommentsModule.class}, dependencies = {AppComponent.class})
public interface CommentsComponent {

    void inject(CommentsActivity activity);
}
