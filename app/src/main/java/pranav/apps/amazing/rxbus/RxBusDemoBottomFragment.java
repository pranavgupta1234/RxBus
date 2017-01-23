package pranav.apps.amazing.rxbus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.reactivestreams.Publisher;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subscribers.DefaultSubscriber;

/**
 * Created by Pranav Gupta on 1/21/2017.
 */

public class RxBusDemoBottomFragment extends Fragment{

    @Bind(R.id.demo_rxbus_tap_txt) TextView _tapEventTxtShow;
    @Bind(R.id.demo_rxbus_tap_count) TextView _tapEventCountShow;
    private RxBus _rxBus;
    private CompositeDisposable _disposables;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rxbus_bottom,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        _rxBus = ((MainActivity)getActivity()).getRxBusSingleton();
    }
    /** using composite disposables we can group observables and then apply some operator directly
     * connectable flowable means this flowable will only emit events when .connect() is made
     *
     * */

    @Override
    public void onStart() {
        super.onStart();
        _disposables = new CompositeDisposable();
        // this below declaration of tapEventEmitter makes it an observable which can be used to listen to bus events
        // and it can be converted to other flowables by applying operators
        ConnectableFlowable<Object> tapEventEmitter = _rxBus.asFlowable().publish();
        /** Our RxBus is emitting events in form of TapEvent(extends Object) and then here there are two disposables which are
         * listening to those events and subscribed to same rxbus
         * One of then logs each time an event is received
         * while the other buffers the value over 1 sec and then emit the result
         * When we subscribe to the bus as a flowable that means we want to listen the events posted in the bus i.e we have an
         * observable to the bus events
         *
         * */


        /**Parameters for subscribe operator:
         onNext - the Consumer<T> you have designed to accept emissions from the Publisher
         As relay was of <Object,Object> so it emits objects and here we just log something on each received item
         * */
        _disposables.add(tapEventEmitter.subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object event) throws Exception {
                if(event instanceof RxBusImplementation.TapEvent){
                    _showTapText();
                }
            }
        }));
        /** publish() takes an flowable and buffers the value over given time window and then passes flow to method inside subscribe
         *
         * */

        /** one simple way of implementation
         * firstly debounce (select last values in a particular time window of 1 sec)
         Flowable<Object> debouncedEmitter = tapEventEmitter.debounce(1, TimeUnit.SECONDS);
         buffer those debounced emitted item over 1 sec
         Flowable<List<Object>> debouncedBufferEmitter = tapEventEmitter.buffer(debouncedEmitter);

         _disposables.add(debouncedBufferEmitter
         .observeOn(AndroidSchedulers.mainThread())
         .subscribe(taps -> {
         _showTapCount(taps.size());
         }));
         * */
        _disposables.add(tapEventEmitter.publish(new Function<Flowable<Object>, Flowable<List<Object>> >() {
            @Override
            public Flowable<List<Object>> apply(Flowable<Object> objectFlowable) throws Exception {
                return objectFlowable.buffer(objectFlowable.debounce(1,TimeUnit.SECONDS));
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Object>>() {
                    @Override
                    public void accept(List<Object> objects) throws Exception {
                        _showTapCount(objects.size());
                    }
                })
        );
        /** here consumer works same as that of Action1 in case of an observer i.e a consumer is analogous to observer
         * */
        _disposables.add(tapEventEmitter.connect());

    }

    @Override
    public void onStop() {
        super.onStop();
        _disposables.clear();
    }

    // -----------------------------------------------------------------------------------
    // Helper to show the text via an animation

    private void _showTapText() {
        _tapEventTxtShow.setVisibility(View.VISIBLE);
        _tapEventTxtShow.setAlpha(1f);
        ViewCompat.animate(_tapEventTxtShow).alphaBy(-1f).setDuration(400);
    }

    private void _showTapCount(int size) {
        _tapEventCountShow.setText(String.valueOf(size));
        _tapEventCountShow.setVisibility(View.VISIBLE);
        _tapEventCountShow.setScaleX(1f);
        _tapEventCountShow.setScaleY(1f);
        ViewCompat.animate(_tapEventCountShow)
                .scaleXBy(-1f)
                .scaleYBy(-1f)
                .setDuration(800)
                .setStartDelay(100);
    }
}
