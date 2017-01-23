package pranav.apps.amazing.rxbus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import pranav.apps.amazing.rxbus.model.PostItem;

/**
 * Created by Pranav Gupta on 1/23/2017.
 */

public class EventBusBottomFragment2 extends Fragment{

    @Bind(R.id.bottom2_text)
    TextView bottom2;

    private EventBus _bus;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eventbus_bottom2,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        _bus = EventBus.getDefault();
        _bus.register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        _bus.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PostItem postItem){
        bottom2.setText("User: "+postItem.getName()+" "+"Views: "+postItem.getTotalView());
    }
}
