package pranav.apps.amazing.rxbus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pranav.apps.amazing.rxbus.model.PostItem;

/**
 * Created by Pranav Gupta on 1/23/2017.
 */

public class EventBusGreenRoBot extends Fragment{

    EventBus _bus ;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eventbus,container,false);
        ButterKnife.bind(this,view);
        return view;
    }
    @OnClick(R.id.post)
    void PostEvent(){
        _bus.post(new PostItem("Pranav",150));
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        _bus = EventBus.getDefault();
        _bus.register(this);

         getActivity().getSupportFragmentManager()
                 .beginTransaction()
                 .replace(R.id.top,new EventBusTopFragment())
                 .replace(R.id.bottom1,new EventBusBottomFragment1())
                 .replace(R.id.bottom2,new EventBusBottomFragment2())
                 .commit();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    void onEvent(PostItem postItem){

    }
}
