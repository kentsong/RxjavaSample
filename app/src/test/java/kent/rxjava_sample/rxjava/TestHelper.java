package kent.rxjava_sample.rxjava;

import io.reactivex.MaybeObserver;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;

import static org.mockito.Mockito.mock;

/**
 * Created by Kent on 2018/5/4.
 */

public class TestHelper {

    public static <T> Observer<T> mockObserver() {
        return mock(Observer.class);
    }

    public static <T> MaybeObserver<T> mockMaybeObserver() {
        return mock(MaybeObserver.class);
    }

    public static <T> SingleObserver<T> mockSingleObserver() {
        return mock(SingleObserver.class);
    }
}
