package com.popularmovies.vpaliy.popularmoviesapp.ui.details.mvp.presenter;

import com.popularmovies.vpaliy.data.utils.scheduler.BaseSchedulerProvider;
import com.popularmovies.vpaliy.domain.model.MovieDetails;
import com.popularmovies.vpaliy.domain.repository.IDetailsRepository;
import com.popularmovies.vpaliy.popularmoviesapp.ui.details.mvp.contract.MovieCastContract;
import com.popularmovies.vpaliy.popularmoviesapp.ui.details.mvp.contract.MovieCastContract.View;
import rx.subscriptions.CompositeSubscription;
import javax.inject.Inject;
import com.popularmovies.vpaliy.popularmoviesapp.di.scope.ViewScope;
import android.support.annotation.NonNull;

@ViewScope
public class MovieCastPresenter
        implements MovieCastContract.Presenter {


    private View view;
    private final IDetailsRepository<MovieDetails> iRepository;
    private final CompositeSubscription subscriptions;
    private final BaseSchedulerProvider schedulerProvider;


    @Inject
    public MovieCastPresenter(@NonNull IDetailsRepository<MovieDetails> iRepository,
                              @NonNull BaseSchedulerProvider schedulerProvider){
        this.iRepository=iRepository;
        this.subscriptions=new CompositeSubscription();
        this.schedulerProvider=schedulerProvider;
    }

    @Override
    public void attachView(@NonNull View view) {
        this.view=view;
    }

    @Override
    public void start(int movieId) {
        subscriptions.clear();
        subscriptions.add(iRepository.get(movieId)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(this::processData,
                          this::handleError,
                          subscriptions::clear));
    }

    @Override
    public void stop() {
        view=null;
        subscriptions.clear();
    }

    private void handleError(@NonNull Throwable throwable){
        throwable.printStackTrace();
    }

    private void processData(@NonNull MovieDetails details){
        if(details.getCast()!=null){
            if(!details.getCast().isEmpty()) {
                view.showCast(details.getCast());
                return;
            }
        }
        view.showNoCastMessage();
    }

}