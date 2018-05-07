package kent.rxjava_sample.rxjava;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by TomazWang on 2018/03/28.
 *
 * @author Tomaz Wang
 * @since 2018/03/28
 **/

public class TestSchedulerProvider implements SchedulerProvider {

    @Override
    public Scheduler ui() {
        return Schedulers.trampoline();
    }

    @Override
    public Scheduler computation() {
        return Schedulers.trampoline();
    }

    @Override
    public Scheduler trampoline() {
        return Schedulers.trampoline();
    }

    @Override
    public Scheduler newThread() {
        return Schedulers.trampoline();
    }

    @Override
    public Scheduler io() {
        return Schedulers.trampoline();
    }
}
