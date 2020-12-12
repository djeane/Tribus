package apptribus.com.tribus.activities.change_admin.dagger;

import apptribus.com.tribus.activities.change_admin.ChangeAdminActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 7/12/2017.
 */
@ChangeAdminScope
@Component(modules = {ChangeAdminModule.class}, dependencies = {AppComponent.class})
public interface ChangeAdminComponent {

    void inject(ChangeAdminActivity activity);
}
