package kent.rxjava_sample.rxjava;

import io.reactivex.Scheduler;

public interface SchedulerProvider {

    Scheduler ui();

    Scheduler computation();

    Scheduler trampoline();

    Scheduler newThread();

    Scheduler io();
}