package pranav.apps.amazing.rxbus;

import com.jakewharton.rxrelay.PublishRelay;
import com.jakewharton.rxrelay.Relay;

import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.Flowable;

/**
 * Created by Pranav Gupta on 1/21/2017.
 */

public class RxBus {

    /** 1) Relays are RxJava types which are both an Observable and a Consumer.
      * Basically: A Subject except without the ability to call onComplete or onError.
     *
     * 2) Hence relay represent an Object i.e both an **Observable** and **Action1**
     *
     * 3) As an observable the below relay emits the second parameter while as Action1 one it receives the first parameter(if specifically
     * mentioned) (here it doesn't matter both are object i.e superclass of all class)
      * */

    /**4) PublicRelay.create returns a **Relay** that, **once** an Observer has subscribed, emits all subsequently observed items
     *  to the subscriber.
     * */
    /** 5) toSerialised wraps a Relay so that it is safe to call call from different threads.
     When you use an ordinary Relay as a Action1, you must take care not to call its Action1.call method from multiple threads,
     as this could lead to non-serialized calls, which violates the Observable contract and creates an ambiguity in the
     resulting Relay.
     *
     * */
    /** 7) basically rxRelay is open source lib maintained by Jake Wharton one can simply use subject available in rx java also
     *
     * */
    private final Relay<Object,Object> _rxBus = PublishRelay.create().toSerialized();

    /** This method calls the Action1 of relay i.e at this point of time it works as an observer
     * */
    public void send(Object o){
        _rxBus.call(o);
    }
    /** this returns an observable so can use rxbus to emit items to subscribed observers
     * */
    public Flowable<Object> asFlowable(){
        return RxJavaInterop.toV2Flowable(_rxBus.asObservable());
    }

    public boolean hasObservers(){
        return _rxBus.hasObservers();
    }

}
