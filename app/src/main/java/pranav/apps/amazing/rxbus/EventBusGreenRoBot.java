package pranav.apps.amazing.rxbus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * Created by Pranav Gupta on 1/23/2017.
 */

public class EventBusGreenRoBot extends Fragment{

    EventBus _bus ;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eventbus,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        _bus = EventBus.getDefault();

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

    }
}
