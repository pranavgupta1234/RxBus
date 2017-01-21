package pranav.apps.amazing.rxbus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Pranav Gupta on 1/21/2017.
 */

public class RxBusDemoTopFragment extends Fragment {

    private RxBus rxBus;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rxbus_top,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rxBus = ((MainActivity)getActivity()).getRxBusSingleton();
    }

    @OnClick(R.id.btn_demo_rxbus_tap)
    void OnTapEvent(){
        if(rxBus.hasObservers()){
            rxBus.send(new RxBusImplementation.TapEvent());
        }
    }

}
