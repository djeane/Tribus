package apptribus.com.tribus.activities.comments.mvp;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import apptribus.com.tribus.activities.comments.repository.CommentsAPI;
import apptribus.com.tribus.activities.comments.view_pager.CommentsViewHolder;
import apptribus.com.tribus.pojo.Comment;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import rx.Observable;

public class CommentsModel {

    public final AppCompatActivity activity;

    public CommentsModel(AppCompatActivity activity) {
        this.activity = activity;
    }

    //SET ADAPTER TO SHOW LIST OF TRIBUS
    public FirebaseRecyclerAdapter<Comment, CommentsViewHolder> setAdapter(Tribu tribu){
        return CommentsAPI.getComments(tribu, activity);
    }

    //OBSERVABLES
    //Get the current mTribu
    public Observable<Tribu> getTribu(String uniqueName) {
        return CommentsAPI.getTribu(uniqueName);
    }

    //Get the current user
    public Observable<User> getUser() {
        return CommentsAPI.getUser();
    }

    //Method that verify the last position and scroll until there
    public void verifyLastPosition(RecyclerView recyclerView, LinearLayoutManager linearLayoutManager) {
        CommentsAPI.verifyLastPosition(recyclerView, linearLayoutManager);
    }

    //MESSAGES
    //Method to send message to Firebase
    public void sendCommentToFirebase(Comment comment) {
        CommentsAPI.sendCommentToFirebase(comment);
    }

}
