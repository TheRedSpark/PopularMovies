package com.popularmovies.vpaliy.popularmoviesapp.ui.base;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.popularmovies.vpaliy.domain.configuration.ISortConfiguration;
import com.popularmovies.vpaliy.popularmoviesapp.ui.base.bus.RxBus;
import com.popularmovies.vpaliy.popularmoviesapp.ui.navigator.Navigator;
import io.reactivex.disposables.CompositeDisposable;
import javax.inject.Inject;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * A base class for activities
 */

public abstract class BaseActivity extends AppCompatActivity{

    @Inject
    protected Navigator navigator;

    @Inject
    protected RxBus eventBus;

    @Inject
    protected ISortConfiguration iSortConfiguration;

    protected CompositeDisposable disposables;

    /**
     * Handle the user events
     */
    public abstract void handleEvent(@NonNull Object event);

    /**
     * Initialize the dependencies
     */
    public abstract void inject();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        inject();
        disposables=new CompositeDisposable();
        super.onCreate(savedInstanceState);
    }

    @CallSuper @Override
    protected void onResume() {
        super.onResume();
        disposables.add(eventBus.asFlowable()
                .subscribe(this::processEvent));
    }

    private void processEvent(Object object){
        if(object!=null){
            handleEvent(object);
        }
    }

    @Override @CallSuper
    protected void onPause() {
        super.onPause();
        disposables.clear();
    }

    //just in case if the onPause() hasn't been called
    @CallSuper @Override
    protected void onStop(){
        super.onStop();
        disposables.clear();
    }
}
