package apptribus.com.tribus.activities.register_user.mvp;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import apptribus.com.tribus.R;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.INDIVIDUAL_USERS;

/**
 * Created by User on 5/20/2017.
 */

@SuppressWarnings("Convert2MethodRef")
public class UserRegisterPresenter {

    private final UserRegisterView view;
    private final UserRegisterModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    private boolean response;
    public static boolean isOpen;
    private DatePickerDialog.OnDateSetListener mDateSetListenter;
    private int mYear, mMonth, mDay;


    //REFERENCES - FIREBASE (KEEP SYNCED)
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
    private DatabaseReference mReferenceIndvidualUser = mDatabase.getReference().child(INDIVIDUAL_USERS);


    public UserRegisterPresenter(UserRegisterView view, UserRegisterModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart(){

        //KEEP SYNCED
        mReferenceUser.keepSynced(true);
        mReferenceIndvidualUser.keepSynced(true);

        PresenceSystemAndLastSeen.presenceSystem();

        subscription.add(observerBtnCadastrar());
        subscription.add(observerImage());
        //subscription.add(observerTvAge());
        //subscription.add(observeBtnArrowBack());


        isOpen = true;
    }

    public void onResume() {
        PresenceSystemAndLastSeen.presenceSystem();
        mDateSetListenter = (view1, year, month, dayOfMonth) -> {

            mYear = year;
            mMonth = month;
            mDay = dayOfMonth;

            getAge(year, month, dayOfMonth);
        };


    }

    public void onPause(){
        PresenceSystemAndLastSeen.lastSeen();
    }

    public void onStop(){
        isOpen = false;
    }

    //SUBSCRIPTIONS
    private Subscription observerBtnCadastrar(){
        return view.observeBtnCadastrar()
                .doOnNext(__ -> view.mBtnCadastrar.setEnabled(false))
                .filter(__ -> {
                    if(!ShowSnackBarInfoInternet.checkConnectionAnother()){
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                        view.mBtnCadastrar.setEnabled(true);
                        return false;
                    }
                    else {
                        ShowSnackBarInfoInternet.showSnack(true, view);
                        return true;
                    }
                })
                .filter(__ -> {if(!this.validateFields()) {
                    view.observeBtnCadastrar().retry();
                    response = false;
                }
                else {
                    response = true;
                }
                return response;
                })
                .doOnNext(__ -> view.showLoading(true))
                .map(__ -> view.getUser())
                .observeOn(Schedulers.io())
                .filter(user -> {

                    //SET AGE INSIDE USER
                    /*user.setYear(mYear);
                    user.setMonth(mMonth);
                    user.setDay(mDay);*/

                    if(!model.createUser(user)){
                    view.observeBtnCadastrar().retry();
                    view.showLoading(false);
                    view.showMessage("Houve um erro ao cadastrar o usuário");
                    response = false;
                }
                else {
                    response = true;
                }
                return response;
                })
                .filter(__ -> true)
                .debounce(10000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(__ -> view.showLoading(false))
                .retry()
                .doOnNext(__ -> view.showMessage("Usuário cadastrado com sucesso!"))
                .subscribe(response -> {
                    model.startMainActivity();
                },
                        Throwable::printStackTrace
            );
    }

    private Subscription observerImage(){
        return view.observeImage()
                .filter(__ -> {
                    response = !model.requirePermission();
                    return response;
                })
                .doOnNext(__ -> model.getImage())
                .subscribe();
    }

    /*private Subscription observerTvAge(){
        return view.observeBtnAge()
                .subscribe(__ ->
                        showCalendar(),
                        Throwable::printStackTrace
                );
    }*/

    private void showCalendar() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                view.mContext,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListenter,
                year, month, day
        );

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    private void getAge(int year, int month, int dayOfMonth) {

        int age;

        final Calendar calenderToday = Calendar.getInstance();
        int currentYear = calenderToday.get(Calendar.YEAR);
        int currentMonth = 1 + calenderToday.get(Calendar.MONTH);
        int todayDay = calenderToday.get(Calendar.DAY_OF_MONTH);

        age = currentYear - year;

        if (month > currentMonth) {
            --age;
        } else if (month == currentMonth) {
            if (dayOfMonth > todayDay) {
                --age;
            }
        }

        String appendAge = "Tenho " + age + " anos";
        //view.mTvAge.setHint("");
        //view.mTvAge.setText(appendAge);
    }

    public void getImage(){
        model.getImage();
    }

    /*private boolean validateFields() {
        boolean isEmpty = true;
        if (view.mTvAge.getText().toString().trim().equals("")) {
            view.showMessage("O campo de idade não pode ficar vazio.");
            isEmpty = false;

        }  else if (view.mSpGender.getSelectedItem().toString().trim().contains("Selecione")) {
            view.showMessage("Selecione o gênero.");
            isEmpty = false;
        }
        Log.d("Valor: ", "validateFields - isEmpty: " + isEmpty);
        return isEmpty;
    }*/

    private boolean validateFields(){
        boolean isEmpty = true;
        if (view.mEtTelefone.getText().toString().trim().equals("")) {
            view.showMessage("O campo de telefone não pode ficar vazio.");
            view.mBtnCadastrar.setEnabled(true);
            isEmpty = false;
        }
        else if(view.mEtEmail.getText().toString().trim().equals("")){
            view.showMessage("O campo de e-mail não pode ficar vazio.");
            view.mBtnCadastrar.setEnabled(true);
            isEmpty = false;
        }
        else if(view.mEtSenha.getText().toString().trim().equals("")){
            view.showMessage("O campo de senha não pode ficar vazio.");
            view.mBtnCadastrar.setEnabled(true);
            isEmpty = false;
        }
        Log.d("Valor: ", "validateFields - isEmpty: " + isEmpty);
        return isEmpty;
    }

    /*private Subscription observeBtnArrowBack() {
        return view.observableBtnArrowBack()
                .subscribe(
                        __ -> model.backToCheckUsernameActivity(),
                        Throwable::printStackTrace
                );
    }*/

    public void onDestroy(){
        subscription.clear();
    }

}
