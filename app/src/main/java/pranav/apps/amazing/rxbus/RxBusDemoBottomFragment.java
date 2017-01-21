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
/**
 *
 Implementing an Event Bus With RxJava - RxBus

 Dec 24, 2014
 Originally posted this article on the Wedding Party tech blog

 This post has three parts:

 quick primer on what an event bus is
 implementing the event bus with RxJava
 parting thoughts on this approach

 “RxBus” is not going to be a library. Implementing an event bus with RxJava is so ridiculously easy that it doesn’t warrant the bloat of an independent library.
 Part 1: What is an event bus?

 Let’s talk about two concepts that seem similar: the Observer pattern and the Pub-sub pattern.
 Observer pattern

 This is a pattern of development in which your class or primary object (known as the Observable) notifies other interested classes or objects (known as Observers) with relevant information (events).
 Pub-sub pattern

 The objective of the pub-sub pattern is exactly the same as the Observer pattern viz. you want some other class to know of certain events taking place.

 There’s an important semantic difference between the Observer and Pub-sub patterns though: in the pub-sub pattern the focus is on “broadcasting” messages outside. The Observable here doesn’t want to know who the events are going out to, just that they’ve gone out. In other words the Observable (a.k.a Publisher) doesn’t want to know who the Observers (a.k.a Subscribers) are.
 Why the anonymity?

 It allows for this thing called “decoupling”, which is a good word in computer programming. You want to keep coupling as low as possible in your design.

 Typically, you would expect the publisher to have direct knowledge of each of the many subscribers that it needs to notify, so it can go about notifying each of them, once the “event” or message is ready. But with an event bus, the publisher is relieved of such duties and this independence helps, because the publisher and subscriber need not have logic coded in them that establish the dependencies between the two.

 In other words “consciously decouple” your code whenever you can*.
 How the anonymity?

 Ok, so a natural question with the pub-sub pattern is: how do you actually achieve that anonymity between publisher and subscriber? An easy way is to just get hold of a middleman and let that middleman take care of all the communication. An event bus is one such middleman.

 That’s it. An event bus is as simple as that.

 Two event bus libraries that are commonly used in Android are Otto and Green Robot’s EventBus. There are plenty of posts online that explain how to implement them in your app.
 Part 2: Implementing the event bus with RxJava

 I’ve been posting real world examples of using RxJava for Android in this github repo, so i’ll stick to posting the complete implementation in that repo. Here are the interesting parts of the implementation:
 implement_bus_1.java
 link

 // this is the middleman object
 public class RxBus {

 private final Subject<Object, Object> _bus = new SerializedSubject<>(PublishSubject.create());

 public void send(Object o) {
 _bus.onNext(o);
 }

 public Observable<Object> toObserverable() {
 return _bus;
 }
 }

 That’s it. You now have an event bus ready to use.

 Here’s how you post an event to the bus:
 implement_bus_2.java
 link

 @OnClick(R.id.btn_demo_rxbus_tap)
 public void onTapButtonClicked() {

 _rxBus.send(new TapEvent());
 }

 and here’s how you listen to those events from other fragments/services etc.
 implement_bus_3.java
 link

 // note that it is important to subscribe to the exact same _rxBus instance that was used to post the events
 _rxBus.toObserverable()
 .subscribe(new Action1<Object>() {
 @Override
 public void call(Object event) {

 if(event instanceof TapEvent) {
 _showTapText();

 }else if(event instanceof SomeOtherEvent) {
 _doSomethingElse();
 }
 }
 });

 In the example we post events from the top fragment (green part) and listen in (through the bus) from the bottom fragment (blue part).

 Simple RxBus example
 Part 3 - parting thoughts
 Dead events

 There are some cases where it’s useful to know if there are any observers currently listening to the bus. For e.g. if you use an event bus to handle your GCM push notifications) and don’t want to send a push notification if the app is in the foreground, then it’s important to listen to “dead events”.

 For e.g in a recent release for Wedding Party, we added “Messaging” to our app. If the user has the app open (thus having atleast 1 or more listeners to the bus) we don’t send push notifications but if they have the app in the background, then we send a push notification to let them know of chat messages. After an event is posted to the event bus, if no subscriber is listening, a dead event is returned. If we get back a dead event a push notification is shot out.

 How would you do this with the RxBus implementation?

 It’s pretty easy actually. Subjects have this helpful method on them hasObservers() that would tell us exactly just that. This was added in the 1.x release for RxJava, so you have to be on the latest version of RxAndroid (0.23.0 as of this writing) in order to see this method.
 So should I use RxBus instead of Otto/EventBus?

 If you simply want to use an event bus with your Android app, you’re probably better off with a library like Otto (which i highly recommend) or EventBus. Otto has a clean api driven by annotations and is probably far more simpler to use.

 If you’re familiar with Rx, already use RxJava in your app and want to get rid of an additional library, definitely try out the RxBus approach!

 Did i miss anything? Do you have more suggestions on improving the RxBus implementation? Follow the discussion on Reddit, Google Plus, feel free to tweet your comments @kaushikgopal or just raise an issue in the repo.

 Pssst. there are two bonus posts that delve a little more into the RxJava trickery I used to make the RxBus example a tad bit fancier. Stay tuned.


 conscious decoupling

  * */
